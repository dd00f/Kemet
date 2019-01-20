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

import kemet.data.TwoPlayerGame;

/**
 * Coach
 * 
 * @author Steve McDuff
 */
public class Coach {

	public int numIters = 1000;
	public int numEps = 100;
	public int temperatureThreshold = 15;
	public float updateThreshold = 0.6f;
	public int maxlenOfQueue = 200000;
	public int simulationCount = 25;
	public int arenaCompare = 40;
	public static float cpuct = 1;

	public String checkpoint = "./temp/";
	public boolean load_model = false;
	public String loadFolder = "/dev/models/8x100x50/";
	public String loadFile = "best.pth.tar";

	public int numItersForTrainExamplesHistory = 20;

	public Game game;

	public NeuralNet nnet;
	public NeuralNet pnet;
	public MCTS mcts;

	public boolean skipFirstSelfPlay = false;
	public ArrayList<TrainExample> trainExamplesHistory = new ArrayList<>();
	public int currentPlayer;

	public Coach(Game game, NeuralNet nnet) {
		super();
		this.game = game;
		this.nnet = nnet;
		pnet = nnet.clone();
		mcts = new MCTS(game, nnet, cpuct, simulationCount);
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
		List<TrainExample> trainExamples = new ArrayList<>();
		game = TwoPlayerGame.createGame();
		currentPlayer = game.getNextPlayer();
		int episodeStep = 0;

		while (true) {

			episodeStep += 1;

			ByteCanonicalForm canonicalBoard = game.getCanonicalForm(currentPlayer);
			int temperature = 0;
			if (episodeStep < temperatureThreshold) {
				temperature = 1;
			}

			PolicyVector actionProbabilityPi = mcts.getActionProbability(canonicalBoard, temperature, currentPlayer);

			trainExamples.add(new TrainExample(canonicalBoard, currentPlayer, actionProbabilityPi, 0));

			int actionIndex = pickRandomAction(actionProbabilityPi);

			game.getNextState(currentPlayer, actionIndex);
			currentPlayer = game.getNextPlayer();

			int gameEnded = game.getGameEnded(currentPlayer);
			if (gameEnded != 0) {
				for (TrainExample trainExample : trainExamples) {

					int currentPlayerMod = 1;
					if (trainExample.currentPlayer != currentPlayer) {
						currentPlayerMod = -1;
					}
					trainExample.valueV = gameEnded * currentPlayerMod;
				}
				break;

			}

		}
		return trainExamples;

	}

	public Random random = new Random();

	private int pickRandomAction(PolicyVector actionProbabilityPi) {
		float nextFloat = random.nextFloat();
		int action = 0;
		float[] vector = actionProbabilityPi.vector;
		for (int i = 0; i < vector.length; i++) {
			float f = vector[i];
			if (f > 0) {
				nextFloat -= f;
				if (nextFloat <= 0) {
					action = i;
					break;
				}
			}
		}
		return action;

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

		for (int j = 1; j < numIters + 1; j++) {
			runTrainingIteration(j);
		}

	}

	private void runTrainingIteration(int j) {
		print("------ITER " + j + "------");
		// examples of the iteration

		if (!skipFirstSelfPlay || j > 1) {
			List<TrainExample> iterationTrainExamples = new ArrayList<>(); // = deque([],
																			// maxlen=this.args.maxlenOfQueue)

			long totalTime = 0;
			long end = System.currentTimeMillis();
			long start = end;
			for (int k = 1; k <= numEps; k++) {
				mcts = new MCTS(game, nnet, cpuct, simulationCount); // reset the search tree
				iterationTrainExamples.addAll(executeEpisode());

				// bookkeeping + plot progress
				long now = System.currentTimeMillis();
				// eps_time.update(now - end);
				
				end = now;
				long duration = end - start;
				totalTime += duration;
				long average = totalTime / k;
				long timeLeft = average * (numEps - k);

				print(k + "/" + numEps + " | eps time " + duration + "ms | total " + totalTime + "ms | ETA "
						+ timeLeft + "ms");
				

				start = now;

			}

			// save the iteration examples to the history
			trainExamplesHistory.addAll(iterationTrainExamples);
		}

		if (trainExamplesHistory.size() > numItersForTrainExamplesHistory) {
			print("len(trainExamplesHistory) =" + trainExamplesHistory.size()
					+ " => remove the oldest trainExamples");
			trainExamplesHistory.remove(0);

		}

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
		MCTS pmcts = new MCTS(this.game, this.pnet, cpuct, simulationCount);

		this.nnet.train(trainExamples);
		MCTS nmcts = new MCTS(this.game, this.nnet, cpuct, simulationCount);

		print("PITTING AGAINST PREVIOUS VERSION");

		Player previousPlayer = new Player() {
			@Override
			public int getActionProbability(ByteCanonicalForm gameCanonicalForm) {
				return pmcts.getActionProbability(gameCanonicalForm, 0, 1).getMaximumMove();
			}
		};

		Player newPlayer = new Player() {
			@Override
			public int getActionProbability(ByteCanonicalForm gameCanonicalForm) {
				return nmcts.getActionProbability(gameCanonicalForm, 0, 1).getMaximumMove();
			}

		};

		Arena arena = new Arena(previousPlayer, newPlayer, game);

		arena.playGames(this.arenaCompare);

		int newWinCount = arena.getNewWinCount();
		int previousWinCount = arena.getPreviousWinCount();
		int drawCount = arena.getDrawCount();
		print(
				String.format("NEW/PREV WINS : %d / %d ; DRAWS : %d", newWinCount, previousWinCount, drawCount));
		if (newWinCount + previousWinCount > 0
				&& newWinCount / (newWinCount + previousWinCount) < this.updateThreshold) {
			print("REJECTING NEW MODEL");
			this.nnet.loadCheckpoint(this.checkpoint, "temp.pth.tar");
		} else {
			print("ACCEPTING NEW MODEL");
			this.nnet.saveCheckpoint(this.checkpoint, this.getCheckpointFile(j));
			this.nnet.saveCheckpoint(this.checkpoint, "best.pth.tar");
		}
	}

	private void print(String string) {
		System.out.println(string);
	}

	public String getCheckpointFile(int iteration) {
		return "checkpoint_" + iteration + "pth.tar";
	}

	public void saveTrainExamples(int iteration) {
		File folder = new File(checkpoint);
		if (!folder.exists()) {
			folder.mkdir();
		}

		File inputFile = new File(folder + getCheckpointFile(iteration) + ".examples");
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
		File modelFile = new File(loadFolder + loadFile);

		File examplesFile = new File(modelFile.getAbsolutePath() + ".examples");

		if (!examplesFile.exists()) {
			print("File with train example not found.");
			// r = input("File with trainExamples not found. Continue? [y|n]")
			// if r != "y":
			// sys.exit()
		} else {
			print("File with trainExamples found. Read it.");
			FileInputStream fileInputStream;
			try {
				fileInputStream = new FileInputStream(examplesFile);
				trainExamplesHistory = SerializationUtils.deserialize(fileInputStream);
				IOUtils.closeQuietly(fileInputStream);
				skipFirstSelfPlay = true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(-1);
			}

		}

	}
}
