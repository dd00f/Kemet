/*
 */
package kemet.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;

import kemet.Options;
import kemet.ai.TrialPlayerAI;
import kemet.model.KemetGame;
import kemet.model.action.choice.ChoiceInventory;
import kemet.util.SearchPooler.GameInformation;
import kemet.util.StackingMCTS.MctsBoardInformation;
import lombok.extern.log4j.Log4j2;

/**
 * Coach
 * 
 * @author Steve McDuff
 */
@Log4j2
public class Coach {

	private static final int PLAY_PRINT_INTERVAL = 20;

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

	public int maxlenOfQueue = Options.COACH_MAX_TRAINING_LIST_LENGTH;

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

//	private void activateFirstValidMoveOnError(Game game, int currentPlayer, boolean[] validMoves) {
//		int actionIndex;
//		for (int i = 0; i < validMoves.length; i++) {
//			boolean valid = validMoves[i];
//			if (valid) {
//				actionIndex = i;
//				log.error("Activated action index {} as a temporary fix.", actionIndex);
//				game.activateAction(currentPlayer, actionIndex);
//			}
//		}
//	}

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
			if (Options.COACH_USE_STACKING_MCTS) {
				runSelfTrainingStacking();
			} else {
				// runSelfTraining();
				throw new IllegalStateException();
			}
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

		this.nnet.train(trainExamples);

		if (Options.COACH_USE_STACKING_MCTS) {

			pickArenaWinnerStacking(pnet, nnet, j);

		} else {

			throw new IllegalStateException();
//			MCTS previousMcts = new MCTS(null, pnet, cpuct, simulationCount);
//			MCTS newMcts = new MCTS(null, nnet, cpuct, simulationCount);
//
//			Arena arena = playArenaGames(previousMcts, newMcts);
//
//			pickArenaWinner(j, arena);
		}
	}

	private void pickArenaWinnerStacking(NeuralNet pnet2, NeuralNet nnet2, int iteration) {

		long totalTime = 0;
		long end = System.currentTimeMillis();
		long start = end;

		int matchCount = Options.COACH_ARENA_COMPARE_MATCH_COUNT;

		StackedArena arena = new StackedArena();
		arena.createSearchPooler(pnet2, nnet2);
		arena.createGames(matchCount);

		arena.runGames();

		arena.pickArenaWinner(iteration);

		// bookkeeping + plot progress
		long now = System.currentTimeMillis();
		// eps_time.update(now - end);

		end = now;
		long duration = end - start;
		totalTime += duration;

		if (numEps > 0) {
			log.info("total {} ms to run {} games in parallel. Avg time per game : {} ms", totalTime, matchCount,
					totalTime / matchCount);
		}
	}

	public class StackedArena {
		public SearchPooler previousPooler;
		public SearchPooler newPooler;
		public StackingMCTS[] previousMCTSList;
		public StackingMCTS[] newMCTSList;
		public int remainingGameCount;
		public int episodeStep;

		public int previousVictory;
		public int newVictory;

		private void pickArenaWinner(int iteration) {
			float newWinCountfloat = newVictory;
			float previousWinCountfloat = previousVictory;
			log.info("NEW/PREV WINS : {} / {} ; DRAWS : {}", newVictory, previousVictory, 0);
			if (newWinCountfloat + previousWinCountfloat > 0
					&& newWinCountfloat / (newWinCountfloat + previousWinCountfloat) < updateThreshold) {
				log.info("REJECTING NEW MODEL");
				newPooler.neuralNet.loadCheckpoint(checkpoint, "temp.pth.tar");
			} else {
				log.info("ACCEPTING NEW MODEL");
				newPooler.neuralNet.saveCheckpoint(checkpoint, getCheckpointFile(iteration));
				newPooler.neuralNet.saveCheckpoint(checkpoint, "best.pth.tar");

				saveTrainExamples(getBestExampleFileName());
			}
		}

		private int runPooledArenaAction() {

			// At temperature zero, do less exploration
			float temperature = 0;
			if (episodeStep < temperatureThreshold) {

				// At temperature 1, do more exploration during the first few moves
				temperature = 0.5f;
			}

			for (int i = 0; i < simulationCount; ++i) {
				runPooledArenaSimulation();
			}

			for (int j = 0; j < previousMCTSList.length; j++) {
				StackingMCTS previousMcts = previousMCTSList[j];
				StackingMCTS newMcts = newMCTSList[j];

				if (previousMcts != null) {

					int nextPlayer = previousMcts.game.getNextPlayer();

					StackingMCTS nextPlayerMcts = null;
					boolean swapped = j % 2 == 1;
					nextPlayerMcts = getNextMcts(previousMcts, newMcts, nextPlayer, swapped);

					activateActionOnGame(temperature, nextPlayerMcts);

					previousMcts.cleanupOldCycles();
					newMcts.cleanupOldCycles();

					previousMcts.incrementCycle();
					newMcts.incrementCycle();

					if (nextPlayerMcts.game.isGameEnded()) {
						previousMCTSList[j] = null;
						newMCTSList[j] = null;
						remainingGameCount--;

						int playerZeroScore = nextPlayerMcts.game.getGameEnded(0);

						incrementVictoryCount(swapped, playerZeroScore);

						// newMcts.printStats();
						// previousMcts.printStats();

						if (Options.PRINT_ARENA_GAME_END) {
							nextPlayerMcts.game.setPrintActivations(true);
							nextPlayerMcts.game.printDescribeGame();
						}
					}
				}
			}

			previousPooler.cleanup();
			newPooler.cleanup();

			return remainingGameCount;
		}

		private void runPooledArenaSimulation() {

			startSearch();

			newPooler.fetchAllPendingPredictions();
			previousPooler.fetchAllPendingPredictions();

			finishSearch();
		}

		private void finishSearch() {
			for (int j = 0; j < previousMCTSList.length; j++) {
				StackingMCTS previousMcts = previousMCTSList[j];
				StackingMCTS newMcts = newMCTSList[j];

				if (previousMcts != null) {

					int nextPlayer = previousMcts.game.getNextPlayer();

					StackingMCTS nextPlayerMcts = null;
					boolean swapped = j % 2 == 1;
					nextPlayerMcts = getNextMcts(previousMcts, newMcts, nextPlayer, swapped);

					nextPlayerMcts.finishSearch();
				}
			}
		}

		private void startSearch() {
			for (int j = 0; j < previousMCTSList.length; j++) {
				StackingMCTS previousMcts = previousMCTSList[j];
				StackingMCTS newMcts = newMCTSList[j];

				if (previousMcts != null) {

					int nextPlayer = previousMcts.game.getNextPlayer();

					StackingMCTS nextPlayerMcts = null;
					boolean swapped = j % 2 == 1;
					nextPlayerMcts = getNextMcts(previousMcts, newMcts, nextPlayer, swapped);

					nextPlayerMcts.startSearch();
				}
			}
		}

		public void runGames() {

			long start = System.currentTimeMillis();

			while (remainingGameCount > 0) {

				episodeStep++;

				remainingGameCount = runPooledArenaAction();

				if (episodeStep % PLAY_PRINT_INTERVAL == 0) {
					long duration = System.currentTimeMillis() - start;
					String prefix = "Arena play " + episodeStep + " steps | took " + duration + "ms | ";
					newPooler.printStats(prefix + " new  ");
					previousPooler.printStats(prefix + " prev ");
				}
			}

			long duration = System.currentTimeMillis() - start;
			String prefix = "Arena finished " + episodeStep + " steps | took " + duration + "ms | ";
			newPooler.printStats(prefix + " new  ");
			previousPooler.printStats(prefix + " prev ");

		}

		public void createGames(int matchCount) {

			remainingGameCount = matchCount;

			previousMCTSList = new StackingMCTS[matchCount];
			newMCTSList = new StackingMCTS[matchCount];

			for (int k = 0; k < matchCount; k++) {
				Game createGame = gameFactory.createGame();
				createGame.setPrintActivations(Options.PRINT_ARENA_GAME_EVENTS);
				StackingMCTS newMcts = new StackingMCTS(createGame, newPooler, cpuct);
				StackingMCTS previousMcts = new StackingMCTS(createGame, previousPooler, cpuct);
				// reset the search tree
				previousMCTSList[k] = previousMcts;
				newMCTSList[k] = newMcts;
			}
		}

		public void createSearchPooler(NeuralNet pnet2, NeuralNet nnet2) {
			previousPooler = new SearchPooler(pnet2);

			newPooler = new SearchPooler(nnet2);
		}

		private void incrementVictoryCount(boolean swapped, int playerZeroScore) {
			if (!swapped) {
				// swapped
				if (playerZeroScore > 0) {
					// player zero won
					log.debug("Victory attributed to old neural net. Swapped : {}", swapped);
					previousVictory++;
				} else {
					// player one won
					log.debug("Victory attributed to new neural net. Swapped : {}", swapped);
					newVictory++;
				}
			} else {
				// swapped
				if (playerZeroScore > 0) {
					// player zero won
					log.debug("Victory attributed to new neural net. Swapped : {}", swapped);
					newVictory++;
				} else {
					// player one won
					log.debug("Victory attributed to old neural net. Swapped : {}", swapped);
					previousVictory++;
				}
			}
		}

		private StackingMCTS getNextMcts(StackingMCTS previousMcts, StackingMCTS newMcts, int nextPlayer,
				boolean swapped) {
			StackingMCTS nextPlayerMcts;
			if (swapped) {
				// swapped
				if (nextPlayer == 0) {
					nextPlayerMcts = newMcts;
				} else {
					nextPlayerMcts = previousMcts;
				}
			} else {
				// not swapped
				if (nextPlayer == 0) {
					nextPlayerMcts = previousMcts;
				} else {
					nextPlayerMcts = newMcts;
				}
			}
			return nextPlayerMcts;
		}

	}

	public void runSelfTrainingStacking() {

		long totalTime = 0;
		long end = System.currentTimeMillis();
		long start = end;

		log.info("START : Self Training Stacking of {} games", numEps);

		SearchPooler pooler = new SearchPooler(nnet);

		StackingMCTS[] gameList = new StackingMCTS[numEps];

		List<TrainExample> trainExamples = new ArrayList<>();

		for (int k = 0; k < numEps; k++) {
			Game createGame = gameFactory.createGame();
			createGame.setPrintActivations(Options.PRINT_COACH_SEARCH_ACTIONS);
			StackingMCTS mcts = new StackingMCTS(createGame, pooler, cpuct);
			// reset the search tree
			gameList[k] = mcts;
		}

		int remainingGameCount = numEps;

		int episodeStep = 0;

		while (remainingGameCount > 0) {

			episodeStep++;

			remainingGameCount = runPooledGameAction(pooler, gameList, trainExamples, remainingGameCount, episodeStep);

			if (episodeStep % PLAY_PRINT_INTERVAL == 0) {
				pooler.printStats("Self play " + episodeStep + " | ");

				if (episodeStep > 1500) {
					try {
						throw new IllegalStateException("Reached 1500 steps in a game.");
					} catch (Exception ex) {

					}
					break;
				}
			}

		}

		pooler.printStats("Self play finished " + episodeStep + " | ");

		// bookkeeping + plot progress
		long now = System.currentTimeMillis();
		// eps_time.update(now - end);

		end = now;
		long duration = end - start;
		totalTime += duration;

		if (numEps > 0) {
			log.info("END   total {} ms to run {} games in parallel. Avg time per game : {} ms", totalTime, numEps,
					totalTime / numEps);
		}

		// save the iteration examples to the history
		trainExamplesHistory.addAll(trainExamples);
	}

	private int runPooledGameAction(SearchPooler pooler, StackingMCTS[] gameList, List<TrainExample> trainExamples,
			int remainingGameCount, int episodeStep) {
		// At temperature zero, do less exploration
		int temperature = 0;
		if (episodeStep < temperatureThreshold) {

			// At temperature 1, do more exploration during the first few moves
			temperature = 1;
		}

		for (int i = 0; i < simulationCount; ++i) {
			runPooledSimulation(pooler, gameList);
		}

		for (int j = 0; j < gameList.length; j++) {
			StackingMCTS mcts = gameList[j];
			if (mcts != null) {
				try {
					activateActionOnGame(temperature, mcts);

					mcts.cleanupOldCycles();
					mcts.incrementCycle();

					// purge ended games
					remainingGameCount = checkIfGameEnded(gameList, remainingGameCount, j, mcts, trainExamples);

				} catch (Exception ex) {
					// scrap games that generated errors
					log.error("Unexpected error in Coach.runPooledGameAction", ex);
					remainingGameCount--;
					gameList[j] = null;
				}
			}
		}

		pooler.cleanup();

		return remainingGameCount;
	}

	private int checkIfGameEnded(StackingMCTS[] gameList, int remainingGameCount, int i, StackingMCTS mcts,
			List<TrainExample> trainExamples) {
		if (mcts.game.isGameEnded()) {
			gameList[i] = null;
			remainingGameCount--;

			int playerIndex = 0;
			int playerZeroScore = mcts.game.getGameEnded(playerIndex);

			adjustAllTrainingExampleValue(mcts.trainExamples, playerIndex, playerZeroScore);

			trainExamples.addAll(mcts.trainExamples);

			if (Options.COACH_PRINT_GAME_AFTER_SELF_TRAINING) {
				mcts.game.setPrintActivations(true);
				mcts.game.printDescribeGame();
				log.info("Adjusted Train examples with player index {} and with score {}", playerIndex,
						playerZeroScore);
			}
		}
		return remainingGameCount;
	}

	private void activateActionOnGame(float temperature, StackingMCTS mcts) {
		PolicyVector actionProbabilityAfterSearch = mcts.getActionProbabilityAfterSearch(temperature);

		Game game = mcts.game;
		int currentPlayer = game.getNextPlayer();
		ByteCanonicalForm canonicalForm = game.getCanonicalForm(currentPlayer);

		int actionIndex = actionProbabilityAfterSearch.pickRandomAction();
		if (Options.COACH_USE_MANUAL_AI) {
			KemetGame kg = (KemetGame) game;
			TrialPlayerAI ai = new TrialPlayerAI(kg.getPlayerByIndex(currentPlayer), kg);
			ai.print = false;
			actionIndex = ai.pickAction(kg.action.getNextPlayerChoicePick()).getIndex();

			actionProbabilityAfterSearch.boostActionIndex(actionIndex);
		}

		boolean[] validMoves = game.getValidMoves();
		TrainExample trainExample = new TrainExample(canonicalForm, currentPlayer, actionProbabilityAfterSearch, 0,
				validMoves);
		mcts.trainExamples.add(trainExample);

		if (Options.PRINT_COACH_SEARCH_PROBABILITIES) {
			actionProbabilityAfterSearch.printActionProbabilities(game);
		}

		try {
			game.activateAction(currentPlayer, actionIndex);
		} catch (Exception ex) {
			log.error("Activate action failed", ex);
			
			StringBuilder build = new StringBuilder();
			game.describeGame(build);
			
			log.error(build.toString());
			
			// check MCTS for valid moves
			MctsBoardInformation mctsBoardInformation = mcts.boardInformationMemory.get(canonicalForm);
			log.error("Valid moves in the mcts board memory : " + ChoiceInventory.printValidMoves(mctsBoardInformation.validMoves));
			
			log.error(canonicalForm.printCanonicalForm());
			
			// activateFirstValidMoveOnError(game, currentPlayer, validMoves);
			throw ex;
		}
	}

	private void runPooledSimulation(SearchPooler pooler, StackingMCTS[] gameList) {
		startSearch(gameList);

		pooler.fetchAllPendingPredictions();

		finishSearch(gameList);
		
		// TODO remove this.
		int TODOREMOVE;
		Collection<GameInformation> values = pooler.providedPredictions.values();
		for (GameInformation gameInformation : values) {
			if( gameInformation.usedCount == 0 ) {
				gameInformation.game.printDescribeGame();
				throw new IllegalStateException("Found a neural net prediction that wasn't used. " );
			}
		}
		
	}

	private void finishSearch(StackingMCTS[] gameList) {
		for (int j = 0; j < gameList.length; j++) {
			StackingMCTS game = gameList[j];
			if (game != null) {
				try {
					game.finishSearch();
				} catch (Exception ex) {
					// scrap games that generated errors
					gameList[j] = null;
				}
			}
		}
	}

	private void startSearch(StackingMCTS[] gameList) {
		for (int j = 0; j < gameList.length; j++) {
			StackingMCTS game = gameList[j];
			if (game != null) {
				try {
					game.startSearch();
				} catch (Exception ex) {
					// scrap games that generated errors
					gameList[j] = null;
				}
			}
		}
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

	private String getBestExampleFileName() {
		return this.checkpoint + "best.pth.tar" + ".examples";
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

		String bestExampleFileName = getBestExampleFileName();
		File examplesFile = new File(bestExampleFileName);

		if (!examplesFile.exists()) {
			log.warn("File with train example not found : " + examplesFile.getAbsolutePath());

		} else {
			log.info("File with trainExamples found. Read it. {}", bestExampleFileName);
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
