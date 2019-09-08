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
import kemet.model.Player;
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

	public static final String TEMPORARY_NNET_SAVE_POINT_NAME = "temp.pth.tar";

	public static int PLAY_PRINT_INTERVAL = 200;

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

	public int ignoredGameCount = 0;

	public StackedArena arena;

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
			int playerInExample = trainExample.currentPlayer;
			if (playerInExample != currentPlayer) {
				currentPlayerMod = -1;
			}
			int trainExampleValue = isCurrentPlayerWinner * currentPlayerMod;
			trainExample.valueV = trainExampleValue;
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

			String durationStr = formatDuration(duration);
			String totalTimeStr = formatDuration(totalTime);
			String timeLeftStr = formatDuration(timeLeft);

			log.info(j + "/" + numIters + " | coach learn time " + durationStr + " | total " + totalTimeStr + " | ETA "
					+ timeLeftStr);

			start = now;
		}

	}

	public void pitBestVsBasic() {

		pickArenaWinnerStacking(0);

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
		deleteTrainExamples(j - 5);

		// shuffle examples before training
		List<TrainExample> trainExamples = new ArrayList<>(trainExamplesHistory);
		Collections.shuffle(trainExamples);

		// training new network, keeping a copy of the old one
		nnet.saveCheckpoint(this.checkpoint, TEMPORARY_NNET_SAVE_POINT_NAME);
		pnet.loadCheckpoint(this.checkpoint, TEMPORARY_NNET_SAVE_POINT_NAME);

		nnet.train(trainExamples);

		if (Options.COACH_USE_STACKING_MCTS) {

			if (Options.COACH_BLIND_LEARN) {
				blindLearn(j);
			} else {
				pickArenaWinnerStacking(j);
			}

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

	private void blindLearn(int iteration) {

		StackedArena arena = new StackedArena();
		arena.createSearchPooler();

		arena.acceptNewModel(iteration);
	}

	private void pickArenaWinnerStacking(int iteration) {

		long totalTime = 0;
		long end = System.currentTimeMillis();
		long start = end;

		int matchCount = Options.COACH_ARENA_COMPARE_MATCH_COUNT;

		log.info("Starting arena comparison with previous neural network. Using {} games with {} simulation per move.",
				matchCount, Options.COACH_MCTS_SIMULATION_COUNT_PER_MOVE);

		initiateArena(matchCount);

//		int TODO_GET_BACK_TO_NORMAL;
		arena.runGames();
		arena.pickArenaWinner(iteration);
//		arena.acceptNewModel(iteration);

		// bookkeeping + plot progress
		long now = System.currentTimeMillis();
		// eps_time.update(now - end);

		end = now;
		long duration = end - start;
		totalTime += duration;

		if (numEps > 0) {
			String durationStr = formatDuration(totalTime);

			String avgTime = formatDuration(totalTime / matchCount);
			log.info("total {} to run {} games in parallel. Avg time per game : {} ms", durationStr, matchCount,
					avgTime);
		}
	}

	public void initiateArena(int matchCount) {
		arena = new StackedArena();
		arena.createSearchPooler();
		arena.createGames(matchCount);
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
		public int firstPlayerWin;
		public int secondPlayerWin;

		public void pickArenaWinner(int iteration) {
			float newWinCountfloat = newVictory;
			float previousWinCountfloat = previousVictory;
			log.info("NEW/PREV WINS : {} / {} ; DRAWS : {}, First player win : {}, Second player win : {}", newVictory,
					previousVictory, 0, firstPlayerWin, secondPlayerWin);
			if (newWinCountfloat + previousWinCountfloat > 0
					&& newWinCountfloat / (newWinCountfloat + previousWinCountfloat) < updateThreshold) {
				log.info("REJECTING NEW MODEL");
				newPooler.neuralNet.loadCheckpoint(checkpoint, TEMPORARY_NNET_SAVE_POINT_NAME);
			} else {
				acceptNewModel(iteration);
			}
		}

		public void acceptNewModel(int iteration) {
			log.info("ACCEPTING NEW MODEL");
			newPooler.neuralNet.saveCheckpoint(checkpoint, getCheckpointFile(iteration));
			newPooler.neuralNet.saveCheckpoint(checkpoint, "best.pth.tar");

			saveTrainExamples(getBestExampleFileName());
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

					previousMcts.game.setPrintActivations(Options.PRINT_ARENA_GAME_EVENTS);
					int nextPlayer = previousMcts.game.getNextPlayer();

					StackingMCTS nextPlayerMcts = null;
					boolean swapped = j % 2 == 1;
					nextPlayerMcts = getNextMcts(previousMcts, newMcts, nextPlayer, swapped);

					try {
						activateActionOnGameCheckForEnd(temperature, j, previousMcts, newMcts, nextPlayerMcts, swapped);
					} catch (Exception ex) {
						log.error("Unexpected error in Coach.StackedArena.activateActionOnGameCheckForEnd"
								+ newMcts.game.toString(), ex);
						newMCTSList[j] = null;
						previousMCTSList[j] = null;
					}
				}
			}

			previousPooler.cleanup();
			newPooler.cleanup();

			return remainingGameCount;
		}

		public void activateActionOnGameCheckForEnd(float temperature, int j, StackingMCTS previousMcts,
				StackingMCTS newMcts, StackingMCTS nextPlayerMcts, boolean swapped) {
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

					try {
						nextPlayerMcts.finishSearch();
					} catch (Exception ex) {
						log.error("Unexpected error in Coach.StackedArena.finishSearch" + newMcts.game.toString(), ex);
						newMCTSList[j] = null;
						previousMCTSList[j] = null;
					}
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

					try {
						previousMcts.game.setPrintActivations(Options.PRINT_ARENA_GAME_SIMULATIONS);
						nextPlayerMcts.startSearch();
					} catch (Exception ex) {
						log.error("Unexpected error in Coach.StackedArena.startSearch " + newMcts.game.toString(), ex);
						newMCTSList[j] = null;
						previousMCTSList[j] = null;
					}
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
					String durationStr = formatDuration(duration);
					String prefix = "Arena play " + episodeStep + " steps | took " + durationStr + " | ";
					newPooler.printStats(prefix + " new  ");
					previousPooler.printStats(prefix + " prev ");
				}

				if (episodeStep > Options.GAME_TRACK_MAX_ACTION_COUNT) {
					break;
				}
			}

			long duration = System.currentTimeMillis() - start;
			String durationStr = formatDuration(duration);
			String prefix = "Arena finished " + episodeStep + " steps | took " + durationStr + " | ";
			newPooler.printStats(prefix + " new  ");
			previousPooler.printStats(prefix + " prev ");

		}

		public void createGames(int matchCount) {

			remainingGameCount = matchCount;

			previousMCTSList = new StackingMCTS[matchCount];
			newMCTSList = new StackingMCTS[matchCount];

			for (int k = 0; k < matchCount; k++) {
				Game createGame = gameFactory.createGame();

				if (createGame instanceof KemetGame) {
					KemetGame kg = (KemetGame) createGame;
					if (k % 2 == 1) {
						// swapped, new goes first
						kg.getPlayerByIndex(0).name = "new";
						kg.getPlayerByIndex(1).name = "old";

					} else {
						// old goes first
						kg.getPlayerByIndex(0).name = "old";
						kg.getPlayerByIndex(1).name = "new";
					}
				}

				createGame.setPrintActivations(Options.PRINT_ARENA_GAME_EVENTS);
				StackingMCTS newMcts = new StackingMCTS(createGame, newPooler, cpuct);
				StackingMCTS previousMcts = new StackingMCTS(createGame, previousPooler, cpuct);
				// reset the search tree
				previousMCTSList[k] = previousMcts;
				newMCTSList[k] = newMcts;
			}
		}

		public void createSearchPooler() {
			previousPooler = new SearchPooler(pnet);

			newPooler = new SearchPooler(nnet);
		}

		private void incrementVictoryCount(boolean swapped, int playerZeroScore) {

			if (playerZeroScore > 0) {
				firstPlayerWin++;
			} else {
				secondPlayerWin++;
			}

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

		log.info("START : Self Training Stacking of {} games with {} simulations per move", numEps,
				Options.COACH_MCTS_SIMULATION_COUNT_PER_MOVE);

		SearchPooler pooler = new SearchPooler(nnet);

		StackingMCTS[] gameList = new StackingMCTS[numEps];

		List<TrainExample> trainExamples = new ArrayList<>();

		createGames(pooler, gameList);

		playPooledGames(pooler, gameList, trainExamples);

		// bookkeeping + plot progress
		long now = System.currentTimeMillis();
		// eps_time.update(now - end);

		end = now;
		long duration = end - start;
		totalTime += duration;

		if (numEps > 0) {
			String durationStr = formatDuration(totalTime);
			String gameStr = formatDuration(totalTime / numEps);
			log.info("END   total {} to run {} games in parallel. Avg time per game : {}", durationStr, numEps,
					gameStr);
		}

		// save the iteration examples to the history
		trainExamplesHistory.addAll(trainExamples);
	}

	public static String formatDuration(long duration) {
		long milliseconds = duration % 1000;
		long seconds = (duration / 1000) % 60;
		long minutes = (duration / (1000 * 60)) % 60;
		long hours = (duration / (1000 * 60 * 60)) % 24;
		long days = (duration / (1000 * 60 * 60 * 24));
		String format = "";
//		if (days > 0) {
//			format = String.format("%1$02dd%2$02dh", days, hours);
//		} else if (hours > 0) {
//			format = String.format("%1$02dh%2$02dm", hours, minutes);
//		} else if (minutes > 0) {
//			format = String.format("%1$02dm%2$02ds", minutes, seconds);
//		} else {
//			format = String.format("%1$02ds%2$04dms", seconds, milliseconds);
//		}

		if (days > 0) {
			format = String.format("%1$01dd %2$02dh", days, hours);
		} else if (hours > 0) {
			format = String.format("%1$01dh %2$02dm", hours, minutes);
		} else if (minutes > 0) {
			format = String.format("%1$01dm %2$02ds", minutes, seconds);
		} else {
			format = String.format("%1$01ds %2$04dms", seconds, milliseconds);
		}

		return format;
	}

	private void playPooledGames(SearchPooler pooler, StackingMCTS[] gameList, List<TrainExample> trainExamples) {
		int episodeStep = 0;

		while (hasRemainingGame(gameList)) {

			episodeStep++;

			runPooledGameAction(pooler, gameList, trainExamples, episodeStep);

			if (episodeStep % PLAY_PRINT_INTERVAL == 0) {
				pooler.printStats("Self play " + episodeStep + " | ");
			}

			if (episodeStep > Options.GAME_TRACK_MAX_ACTION_COUNT) {

				int count = 0;
				for (int i = 0; i < gameList.length; i++) {
					StackingMCTS stackingMCTS = gameList[i];
					if (stackingMCTS != null) {
						count++;
//						stackingMCTS.game.setPrintActivations(true);
//						stackingMCTS.game.printDescribeGame();
					}
				}

				try {
					String message = "Reached " + Options.GAME_TRACK_MAX_ACTION_COUNT + " steps in " + count + " game.";
					log.error(message);
//					throw new IllegalStateException(
//							message);
				} catch (Exception ex) {
					log.error(ex);
				}
				break;
			}

		}

		pooler.printStats("Self play finished " + episodeStep + " | ignored game count : " + ignoredGameCount + " | ");
		ignoredGameCount = 0;
	}

	private boolean hasRemainingGame(StackingMCTS[] gameList) {
		for (int i = 0; i < gameList.length; i++) {
			StackingMCTS stackingMCTS = gameList[i];
			if (stackingMCTS != null) {
				return true;
			}
		}
		return false;
	}

	private void createGames(SearchPooler pooler, StackingMCTS[] gameList) {
		for (int k = 0; k < numEps; k++) {
			Game createGame = gameFactory.createGame();
			createGame.setPrintActivations(Options.PRINT_COACH_SEARCH_ACTIONS);
			StackingMCTS mcts = new StackingMCTS(createGame, pooler, cpuct);
			// reset the search tree
			gameList[k] = mcts;
		}
	}

	private void runPooledGameAction(SearchPooler pooler, StackingMCTS[] gameList, List<TrainExample> trainExamples,
			int episodeStep) {
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
				activateActionCheckForEnd(gameList, trainExamples, temperature, j, mcts);
			}
		}

		pooler.cleanup();

	}

	public void activateActionCheckForEnd(StackingMCTS[] gameList, List<TrainExample> trainExamples, int temperature,
			int j, StackingMCTS mcts) {
		try {
			activateActionOnGame(temperature, mcts);

			mcts.cleanupOldCycles();
			mcts.incrementCycle();

			// purge ended games
			checkIfGameEnded(gameList, j, mcts, trainExamples);

		} catch (Exception ex) {
			// scrap games that generated errors
			log.error("Unexpected error in Coach.runPooledGameAction" + mcts.game.toString(), ex);
			gameList[j] = null;
		}
	}

	private void checkIfGameEnded(StackingMCTS[] gameList, int i, StackingMCTS mcts, List<TrainExample> trainExamples) {
		Game game = mcts.game;
		if (game.isGameEnded()) {

			gameList[i] = null;

			if (gameHasHighScoreWinner(game)) {

				int playerIndex = 0;
				int playerZeroScore = game.getGameEnded(playerIndex);

				if (Options.COACH_PRINT_GAME_AFTER_SELF_TRAINING) {
					game.setPrintActivations(true);
					game.printDescribeGame();
					log.info("Adjusted Train examples with player index {} and with score {}", playerIndex,
							playerZeroScore);
				}

				adjustAllTrainingExampleValue(mcts.trainExamples, playerIndex, playerZeroScore);

				trainExamples.addAll(mcts.trainExamples);

			} else {
				log.debug("ignored game {} because it had a low winning score", i);
				ignoredGameCount++;
			}
		}
	}

	private boolean gameHasHighScoreWinner(Game game) {
		if (game instanceof KemetGame) {
			KemetGame kg = (KemetGame) game;
			List<Player> playerByInitiativeList = kg.playerByInitiativeList;
			for (Player player : playerByInitiativeList) {
				if (player.victoryPoints >= 7) {
					return true;
				}
			}
			return false;
		}
		return true;
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
			log.error("Valid moves in the mcts board memory : "
					+ ChoiceInventory.printValidMoves(mctsBoardInformation.validMoves));

			log.error(canonicalForm.printCanonicalForm());

			// activateFirstValidMoveOnError(game, currentPlayer, validMoves);
			throw ex;
		}
	}

	private void runPooledSimulation(SearchPooler pooler, StackingMCTS[] gameList) {
		startSearch(gameList);

		pooler.fetchAllPendingPredictions();

		finishSearch(gameList);

		if (Options.COACH_VALIDATE_ALL_NNET_PREDICTION_USED) {
			validateAllPredictionsUsed(pooler);
		}

	}

	private void validateAllPredictionsUsed(SearchPooler pooler) {
		Collection<GameInformation> values = pooler.providedPredictions.values();
		for (GameInformation gameInformation : values) {
			if (gameInformation.usedCount == 0) {
				if (gameInformation.game != null) {
					gameInformation.game.setPrintActivations(true);
					gameInformation.game.printDescribeGame();
				}
				log.error("Found a neural net prediction that wasn't used. \n" + gameInformation.byteCanonicalForm);
				gameInformation.usedCount++;
			}
		}
	}

	private void finishSearch(StackingMCTS[] gameList) {
		for (int j = 0; j < gameList.length; j++) {
			StackingMCTS game = gameList[j];
			if (game != null) {
				finishSearchOnGame(gameList, j, game);
			}
		}
	}

	private void finishSearchOnGame(StackingMCTS[] gameList, int j, StackingMCTS game) {
		try {
			game.finishSearch();
		} catch (Exception ex) {
			// scrap games that generated errors

			gameList[j].game.setPrintActivations(true);
			gameList[j].game.printDescribeGame();
			log.error("Unexpected error in Coach.finishSearch" + game.game.toString(), ex);
			gameList[j] = null;
		}
	}

	private void startSearch(StackingMCTS[] gameList) {
		for (int j = 0; j < gameList.length; j++) {
			StackingMCTS game = gameList[j];
			startSearchOnGame(gameList, j, game);
		}
	}

	private void startSearchOnGame(StackingMCTS[] gameList, int j, StackingMCTS game) {
		if (game != null) {
			try {
				game.startSearch();
			} catch (Exception ex) {
				// scrap games that generated errors
				gameList[j].game.setPrintActivations(true);
				gameList[j].game.printDescribeGame();
				log.error("Unexpected error in Coach.startSearch" + game.game.toString(), ex);
				gameList[j] = null;
			}
		}
	}

	private void trimTrainingExamples() {
		if (trainExamplesHistory.size() > maxlenOfQueue) {
			log.info("len(trainExamplesHistory) =" + trainExamplesHistory.size() + " bigger than " + maxlenOfQueue
					+ " => remove the oldest trainExamples");
			int toIndex = trainExamplesHistory.size() - 1;
			int fromIndex = trainExamplesHistory.size() - maxlenOfQueue - 1;
			trainExamplesHistory = new ArrayList<>(trainExamplesHistory.subList(fromIndex, toIndex));

		}
	}

	public String getBestExampleFileName() {
		return this.checkpoint + "best.pth.tar" + ".examples";
	}

	public String getCheckpointFile(int iteration) {
		return "checkpoint_" + padNumberToString(iteration, 5) + "pth.tar";
	}

	public static String padNumberToString(int number, int paddingCharacterCount) {

		String string = Integer.toString(number);
		int padLeft = paddingCharacterCount - string.length();

		for (int i = 0; i < padLeft; ++i) {
			string = "0" + string;
		}
		return string;
	}

	public void saveTrainExamples(int iteration) {
		File folder = new File(checkpoint);
		if (!folder.exists()) {
			folder.mkdir();
		}

		String filePath = folder + "/" + getCheckpointFile(iteration) + ".examples";
		saveTrainExamples(filePath);
	}

	public void deleteTrainExamples(int iteration) {
		File folder = new File(checkpoint);
		if (!folder.exists()) {
			folder.mkdir();
		}

		String filePath = folder + "/" + getCheckpointFile(iteration) + ".examples";
		File inputFile = new File(filePath);
		if (inputFile.exists()) {
			try {
				inputFile.delete();
			} catch (Exception ex) {
				log.error(ex);
			}
		}
	}

	public void saveTrainExamples(String filePath) {
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
