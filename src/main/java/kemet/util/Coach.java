/*
 */
package kemet.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;

import kemet.Options;
import kemet.ai.TrialPlayerAI;
import kemet.model.KemetGame;
import lombok.extern.log4j.Log4j2;

/**
 * Coach
 * 
 * @author Steve McDuff
 */
@Log4j2
public class Coach {

	/**
	 * Number of iterations to coach. In each iteration, {@link #numEps} games are
	 * played and then used to train the neural network.
	 */
	public int numIters = Options.COACH_NUMBER_OF_NN_TRAINING_ITERATIONS;

	/**
	 * Number of games to play before training the neural network.
	 */
	public int numEps = Options.COACH_NEURAL_NETWORK_TRAIN_GAME_COUNT;

	/**
	 * Number of steps at the beginning of a training game where the algorithm will
	 * try to explore more options.
	 */
	public int temperatureThreshold = Options.COACH_HIGH_EXPLORATION_MOVE_COUNT;

	/**
	 * Win percentage required to consider a neural network better than the
	 * precedent.
	 */
	public float updateThreshold = 0.55f;

	public int maxlenOfQueue = 200000;

	/**
	 * Number of simulated steps in MCTS for each move.
	 */
	public int simulationCount = Options.COACH_MCTS_SIMULATION_COUNT_PER_MOVE;

	/**
	 * Number of games to play in the arena to compare whether or not a new neural
	 * network is better than the previous one.
	 */
	public int arenaCompare = Options.COACH_ARENA_COMPARE_MATCH_COUNT;

	public static float cpuct = Options.COACH_CPUCT;

	public String checkpoint = "./temp/";
	public boolean load_model = false;
	public String loadFolder = "/dev/models/8x100x50/";
	public String loadFile = "best.pth.tar";

	public NeuralNet nnet;
	public NeuralNet pnet;

	public boolean skipFirstSelfPlay = false;
	public ArrayList<TrainExample> trainExamplesHistory = new ArrayList<>();
	public GameFactory gameFactory;

	public Coach(GameFactory gameFactory, NeuralNet nnet) {
		super();
		this.gameFactory = gameFactory;
		this.nnet = nnet;
		pnet = nnet.clone();
	}

	/**
	 * This function executes one episode of this.play, starting with player 1. As
	 * the game is played, each turn is added as a training example to
	 * trainExamples. The game is played till the game ends. After the game ends,
	 * the outcome of the game is used to assign values to each example in
	 * trainExamples.
	 * 
	 * 
	 * It uses a temp=1 if episodeStep < tempThreshold, and thereafter uses temp=0.
	 * 
	 * Returns: trainExamples: a list of examples of the form (canonicalBoard,pi,v)
	 * pi is the MCTS informed policy vector, v is +1 i
	 * 
	 * @return
	 */
	public List<TrainExample> executeEpisode() {

		MCTS mcts = new MCTS(null, nnet, cpuct, simulationCount); // reset the search tree

		List<TrainExample> trainExamples = new ArrayList<>();
		Game game = gameFactory.createGame();
		game.setPrintActivations(Options.PRINT_COACH_SEARCH_ACTIONS);
		mcts.setGame(game);
		int currentPlayer = game.getNextPlayer();
		int episodeStep = 0;

		while (true) {

			episodeStep += 1;

			// At temperature zero, do less exploration
			int temperature = 0;
			if (episodeStep < temperatureThreshold) {

				// At temperature 1, do more exploration during the first few moves
				temperature = 1;
			}

			currentPlayer = activateNextAction(game, mcts, trainExamples, temperature, currentPlayer);

			int gameEnded = game.getGameEnded(currentPlayer);

			if (gameEnded != 0) {
				adjustAllTrainingExampleValue(trainExamples, currentPlayer, gameEnded);
				break;
			}

		}

		mcts.printStats();

		return trainExamples;

	}

	public void adjustAllTrainingExampleValue(List<TrainExample> trainExamples, int currentPlayer,
			int isCurrentPlayerWinner) {
		for (TrainExample trainExample : trainExamples) {

			int currentPlayerMod = 1;
			if (trainExample.currentPlayer != currentPlayer) {
				currentPlayerMod = -1;
			}
			trainExample.valueV = isCurrentPlayerWinner * currentPlayerMod;
		}
	}

	public int activateNextAction(Game game, MCTS mcts, List<TrainExample> trainExamples, int temperature,
			int currentPlayer) {

		ByteCanonicalForm canonicalBoard = game.getCanonicalForm(currentPlayer);

		PolicyVector actionProbabilityPi = mcts.getActionProbability(temperature);
		
		int actionIndex = actionProbabilityPi.pickRandomAction();
		if( Options.COACH_USE_MANUAL_AI ) {
			KemetGame kg = (KemetGame) game;
			TrialPlayerAI ai = new TrialPlayerAI(kg.getPlayerByIndex(currentPlayer), kg);
			ai.print = false;
			actionIndex = ai.pickAction(kg.action.getNextPlayerChoicePick()).getIndex();
			
			actionProbabilityPi.boostActionIndex(actionIndex);
		}
		

		boolean[] validMoves = game.getValidMoves();
		trainExamples
				.add(new TrainExample(canonicalBoard, currentPlayer, actionProbabilityPi, 0, validMoves));

		if (Options.PRINT_COACH_SEARCH_PROBABILITIES) {
			actionProbabilityPi.printActionProbabilities(game);
		}



		try {
			game.activateAction(currentPlayer, actionIndex);
			currentPlayer = game.getNextPlayer();
		} catch (Exception ex) {
			log.error("Activate action failed", ex);
			
			activateFirstValidMoveOnError(game, currentPlayer, validMoves);
		}
		return currentPlayer;
	}

	private void activateFirstValidMoveOnError(Game game, int currentPlayer, boolean[] validMoves) {
		int actionIndex;
		for (int i = 0; i < validMoves.length; i++) {
			boolean valid = validMoves[i];
			if( valid ) {
				actionIndex = i;
				log.error("Activated action index {} as a temporary fix.", actionIndex);
				game.activateAction(currentPlayer, actionIndex);
			}
		}
	}



	/**
	 * Performs numIters iterations with numEps episodes of this.play in each
	 * iteration. After every iteration, it retrains neural network with examples in
	 * trainExamples (which has a maximium length of maxlenofQueue). It then pits
	 * the new neural network against the old one and accepts it only if it wins >=
	 * updateThreshold fraction of games.
	 * 
	 * @return
	 */
	public void learn() {

		long totalTime = 0;
		long end = System.currentTimeMillis();
		long start = end;

		for (int j = 1; j < numIters + 1; j++) {
			runTrainingIteration(j);

			// bookkeeping + plot progress
			long now = System.currentTimeMillis();
			// eps_time.update(now - end);

			end = now;
			long duration = end - start;
			totalTime += duration;
			long average = totalTime / j;
			long timeLeft = average * (numIters - j);

			log.info(j + "/" + numIters + " | coach learn time " + duration + "ms | total " + totalTime + "ms | ETA "
					+ timeLeft + "ms");

			start = now;
		}

	}

	public void runTrainingIteration(int j) {
		log.info("------ITER " + j + "------");
		// examples of the iteration

		if (!skipFirstSelfPlay || j > 1) {
			runSelfTraining();
		}

		trimTrainingExamples();

		// backup history to a file
		// NB! the examples were collected using the model from the previous iteration,
		// so (i-1)
		saveTrainExamples(j - 1);

		// shuffle examples before training
		List<TrainExample> trainExamples = new ArrayList<>(trainExamplesHistory);
		Collections.shuffle(trainExamples);

		// training new network, keeping a copy of the old one
		nnet.saveCheckpoint(this.checkpoint, "temp.pth.tar");
		pnet.loadCheckpoint(this.checkpoint, "temp.pth.tar");

		MCTS previousMcts = new MCTS(null, pnet, cpuct, simulationCount);
		this.nnet.train(trainExamples);
		MCTS newMcts = new MCTS(null, nnet, cpuct, simulationCount);

		Arena arena = playArenaGames(previousMcts, newMcts);

		pickArenaWinner(j, arena);
	}

	private void runSelfTraining() {
		List<TrainExample> iterationTrainExamples = new ArrayList<>(); // = deque([],
																		// maxlen=this.args.maxlenOfQueue)

		long totalTime = 0;
		long end = System.currentTimeMillis();
		long start = end;
		for (int k = 1; k <= numEps; k++) {
			List<TrainExample> executeEpisode = executeEpisode();
			iterationTrainExamples.addAll(executeEpisode);

			// bookkeeping + plot progress
			long now = System.currentTimeMillis();
			// eps_time.update(now - end);

			end = now;
			long duration = end - start;
			totalTime += duration;
			long average = totalTime / k;
			long timeLeft = average * (numEps - k);

			log.info(k + "/" + numEps + " | eps time " + duration + "ms | total " + totalTime + "ms | ETA " + timeLeft
					+ "ms");

			start = now;

		}

		// save the iteration examples to the history
		trainExamplesHistory.addAll(iterationTrainExamples);
	}

	private void trimTrainingExamples() {
		if (trainExamplesHistory.size() > maxlenOfQueue) {
			log.info("len(trainExamplesHistory) =" + trainExamplesHistory.size() + " bigger than " + maxlenOfQueue
					+ " => remove the oldest trainExamples");
			int toIndex = trainExamplesHistory.size() - 1;
			int fromIndex = trainExamplesHistory.size() - maxlenOfQueue;
			trainExamplesHistory = new ArrayList<>(trainExamplesHistory.subList(fromIndex, toIndex));

		}
	}

	private void pickArenaWinner(int j, Arena arena) {
		float newWinCountfloat = arena.getNewWinCount();
		float previousWinCountfloat = arena.getPreviousWinCount();
		int drawCount = arena.getDrawCount();
		log.info("NEW/PREV WINS : {} / {} ; DRAWS : {}", arena.getNewWinCount(), arena.getPreviousWinCount(), drawCount);
		if (newWinCountfloat + previousWinCountfloat > 0
				&& newWinCountfloat / (newWinCountfloat + previousWinCountfloat) < this.updateThreshold) {
			log.info("REJECTING NEW MODEL");
			this.nnet.loadCheckpoint(this.checkpoint, "temp.pth.tar");
		} else {
			log.info("ACCEPTING NEW MODEL");
			this.nnet.saveCheckpoint(this.checkpoint, this.getCheckpointFile(j));
			this.nnet.saveCheckpoint(this.checkpoint, "best.pth.tar");
			
			
			File modelFile = new File(loadFolder + loadFile);

			saveTrainExamples(getBestExampleFileName());
		}
	}

	private String getBestExampleFileName() {
		return this.checkpoint + "best.pth.tar" + ".examples";
	}

	private Arena playArenaGames(MCTS pmcts, MCTS nmcts) {
		log.info("PITTING AGAINST PREVIOUS VERSION");

		Player previousPlayer = new Player() {

			public String name = "old";

			@Override
			public int getActionProbability(Game game) {
				validatePlayer(game, name);

				pmcts.setGame(game);
				return pmcts.getActionProbability(0).getMaximumMove();
			}

			@Override
			public void printStats() {
				pmcts.printStats();

			}
		};

		Player newPlayer = new Player() {

			public String name = "new";

			@Override
			public int getActionProbability(Game game) {
				validatePlayer(game, name);

				nmcts.setGame(game);
				return nmcts.getActionProbability(0).getMaximumMove();
			}

			@Override
			public void printStats() {
				nmcts.printStats();

			}

		};

		Arena arena = new Arena(previousPlayer, newPlayer, gameFactory);

		arena.playGames(this.arenaCompare);
		return arena;
	}

	private void validatePlayer(Game game, String expectedName) {
		if (Options.COACH_VALIDATE_PLAYER_NAME) {
			String playerName = game.getPlayerName(game.getNextPlayer());
			if (!playerName.equals(expectedName)) {
				throw new IllegalArgumentException("player name = " + playerName + ", expected " + expectedName);
			}
		}
	}

	public String getCheckpointFile(int iteration) {
		return "checkpoint_" + iteration + "pth.tar";
	}

	public void saveTrainExamples(int iteration) {
		File folder = new File(checkpoint);
		if (!folder.exists()) {
			folder.mkdir();
		}

		String filePath = folder + "/" + getCheckpointFile(iteration) + ".examples";
		saveTrainExamples(filePath);
	}

	private void saveTrainExamples(String filePath) {
		File inputFile = new File(filePath);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(inputFile);
			SerializationUtils.serialize(trainExamplesHistory, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void loadTrainExamples() {

		File examplesFile = new File(getBestExampleFileName());

		if (!examplesFile.exists()) {
			log.warn("File with train example not found : " + examplesFile.getAbsolutePath());

		} else {
			log.info("File with trainExamples found. Read it.");
			FileInputStream fileInputStream;
			try {
				fileInputStream = new FileInputStream(examplesFile);
				trainExamplesHistory = SerializationUtils.deserialize(fileInputStream);
				IOUtils.closeQuietly(fileInputStream);
				// skipFirstSelfPlay = true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}

		}

	}
}
