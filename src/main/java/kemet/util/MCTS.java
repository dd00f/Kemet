package kemet.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import kemet.Options;
import kemet.ai.TrialPlayerAI;
import kemet.model.KemetGame;
import lombok.extern.log4j.Log4j2;

/**
 * 
 * Monte Carlo Tree Search
 */
@Log4j2
public class MCTS {

	// constant to increase the rate at which the algorithm will explore n
	public static final float EPSILON = 0.00000001f;

	private Game game;
	private NeuralNet neuralNet;

	private float cPuct; // C? P? upper confidence T?

	public Map<ByteCanonicalForm, Map<Integer, Float>> valueAtBoardActionQsa; // stores Q values for s,a (as defined in
																				// the paper)
	public Map<ByteCanonicalForm, Map<Integer, Integer>> boardActionHitCountNsa; // stores #times edge s (game CF) a
																					// (choice
	// Index) was visited
	public Map<ByteCanonicalForm, Integer> boardHitCountNs; // stores #times board s (game CF) was visited
	public Map<ByteCanonicalForm, PolicyVector> choiceValuePredictionForBoardPs; // stores initial policy (returned by
																					// neural net)

	public Map<ByteCanonicalForm, Integer> isBoardEndedEs; // stores game.getGameEnded ended for board s
	public Map<ByteCanonicalForm, boolean[]> validMovesForBoardVs; // stores game.getValidMoves for board s
	public Map<ByteCanonicalForm, int[]> movesToReachBoard; // stores game.getValidMoves for board s

	public Map<ByteCanonicalForm, Pair<PolicyVector, Float>> preparedPredictions = new HashMap<>();

	private int simulationCount;

	private long getActionProbabilityTotalCount = 0;
	private long getActionProbabilityTotalTimeNano = 0;
	private long simulationTotalCount = 0;
	private long simulationTotalTimeNano = 0;
	private long neuralNetTotalCount = 0;
	private long neuralNetTotalTimeNano = 0;

	private int allMovesMaskedCount;

	public MCTS(Game game, NeuralNet neuralNet, float cPuct, int simulationCount) {
		this.game = game;
		this.neuralNet = neuralNet;
		this.cPuct = cPuct;
		this.simulationCount = simulationCount;

		valueAtBoardActionQsa = new HashMap<>();
		boardActionHitCountNsa = new HashMap<>();
		boardHitCountNs = new HashMap<>();
		choiceValuePredictionForBoardPs = new HashMap<>();
		isBoardEndedEs = new HashMap<>();
		validMovesForBoardVs = new HashMap<>();
		movesToReachBoard = new HashMap<>();

	}

	private int maxSearchDepth;

	private long getActionProbabilityTotalDepth;

	/**
	 * This function performs simulationCount simulations of MCTS starting from
	 * canonicalBoard.
	 * 
	 * @param gameCanonicalForm
	 * @param temperature       determine the propensity of exploring new actions
	 *                          through simulations. A high temperature will result
	 *                          in more randomized exploration. A low temperature
	 *                          will ensure predicted high value moves are selected
	 *                          first.
	 * 
	 * @return a policy vector where the probability of the ith action is
	 *         proportional to Nsa[(s,a)]**(1./temperature)
	 */
	public PolicyVector getActionProbability(float temperature) {

		long starProbNano = System.nanoTime();

		// run the requested simulation count
		maxSearchDepth = 1;
		for (int i = 0; i < simulationCount; ++i) {
			long starSimNano = System.nanoTime();
			Game clone = game.clone();
			clone.setPrintActivations(Options.PRINT_MCTS_SEARCH_ACTIONS);

			log.debug("---------- Search {} starting ----------------", i);
			search(clone, 1);
			log.debug("---------- Search {} finished, max depth {} ----------------", i, maxSearchDepth);

			simulationTotalCount++;
			simulationTotalTimeNano += System.nanoTime() - starSimNano;
		}

		ByteCanonicalForm gameString = game.getCanonicalForm(game.getNextPlayer());

		int actionSize = game.getActionSize();
		int[] actionHitCounts = new int[actionSize];
		Map<Integer, Integer> map = boardActionHitCountNsa.get(gameString);
		if (map == null) {
			map = new HashMap<>();
			boardActionHitCountNsa.put(gameString, map);
		}
		Set<Entry<Integer, Integer>> actionIndexCount = map.entrySet();
		for (Entry<Integer, Integer> entry : actionIndexCount) {
			actionHitCounts[entry.getKey()] = entry.getValue();
		}

		PolicyVector probabilitiesOfAllActions = new PolicyVector();

		if (temperature == 0.0) {
			int bestActionIndexA = getMaxIndex(actionHitCounts);
			probabilitiesOfAllActions.vector = new float[actionSize];
			probabilitiesOfAllActions.vector[bestActionIndexA] = 1;

		} else {
			float[] countsFloat = adjustActionHitCountTemperature(temperature, actionHitCounts);
			probabilitiesOfAllActions.vector = countsFloat;
			probabilitiesOfAllActions.normalize();
		}

		getActionProbabilityTotalCount++;
		getActionProbabilityTotalTimeNano += System.nanoTime() - starProbNano;
		getActionProbabilityTotalDepth += maxSearchDepth;

		return probabilitiesOfAllActions;
	}

	private float[] adjustActionHitCountTemperature(float temperature, int[] actionHitCounts) {
		float[] countsFloat = new float[actionHitCounts.length];
		if (temperature != 1) {
			float power = 1.0f / temperature;
			for (int i = 0; i < actionHitCounts.length; i++) {
				countsFloat[i] = (float) Math.pow(actionHitCounts[i], power);
			}
		} else {
			for (int i = 0; i < actionHitCounts.length; i++) {
				countsFloat[i] = actionHitCounts[i];
			}
		}

		return countsFloat;
	}

	public static int getMaxIndex(int[] array) {
		int maxAt = 0;

		for (int i = 0; i < array.length; i++) {
			maxAt = array[i] > array[maxAt] ? i : maxAt;
		}
		return maxAt;
	}

	/**
	 * This function performs one iteration of MCTS. It is recursively called till a
	 * leaf node is found. The action chosen at each node is one that has the
	 * maximum upper confidence bound as in the paper.
	 * 
	 * Once a leaf node is found, the neural network is called to return an initial
	 * policy P and a value v for the state. This value is propagated up the search
	 * path. In case the leaf node is a terminal state, the outcome is propagated up
	 * the search path. The values of Ns, Nsa, Qsa are updated.
	 * 
	 * NOTE: the return values are the negative of the value of the current state.
	 * This is done since v is in [-1,1] and if v is the value of a state for the
	 * current player, then its value is -v for the other player.
	 * 
	 * @param canonicalBoard
	 * @returns the negative of the value of the current canonicalBoard
	 */
	public float search(Game currentGame, int depth) {

		if (maxSearchDepth < depth) {
			maxSearchDepth = depth;
		}

		final int currentPlayerIndex = currentGame.getNextPlayer();
		ByteCanonicalForm canonicalForm = currentGame.getCanonicalForm(currentPlayerIndex);
		// final String boardS = canonicalForm.toCanonicalString();

		log.debug("starting search at depth {} for player {}", depth, currentPlayerIndex);

		int gameEnded = currentGame.getGameEnded(currentPlayerIndex);
		if (!isBoardEndedEs.containsKey(canonicalForm)) {
			isBoardEndedEs.put(canonicalForm, gameEnded);
		}

		float boardValueV = 0;

		if (gameEnded != 0) {
			boardValueV = gameEnded;
		} else if (!choiceValuePredictionForBoardPs.containsKey(canonicalForm)) {
			boardValueV = predictValueWithNeuralNet(currentGame, canonicalForm, depth);
		} else {

			final int actionIndexOfNextSimulationA = findBestActionIndexFromPreviousSimulations(canonicalForm,
					currentGame);

			String describeAction = "";
			if (log.isDebugEnabled()) {
				describeAction = currentGame.describeAction(actionIndexOfNextSimulationA);
			}

//			try {

			currentGame.activateAction(currentPlayerIndex, actionIndexOfNextSimulationA);

//			} catch (Exception ex) {
//
//				boolean[] validMoves = validMovesForBoardVs.get(boardS);
//				checkForValidMoveMatch(boardS, validMoves, currentGame);
//
//				currentGame.setPrintActivations(true);
//				currentGame.printDescribeGame();
//				currentGame.printChoiceList();
//
//				ex.printStackTrace();
//
//				// patch the valid moves and redo another action
//				validMovesForBoardVs.put(boardS, currentGame.getValidMoves());
//				actionIndexOfNextSimulationA = findBestActionIndexFromPreviousSimulations(boardS, currentGame);
//				currentGame.activateAction(currentPlayerIndex, actionIndexOfNextSimulationA);
//			}

			final int playerForNextAction = currentGame.getNextPlayer();

			boardValueV = search(currentGame, depth + 1);

			if (playerForNextAction == -1) {
				// game ended, check if the current player won
				boardValueV = currentGame.getGameEnded(currentPlayerIndex);
				log.debug("Game ended, player {} board value is {} at depth {}", currentPlayerIndex, boardValueV,
						depth);

			} else if (playerForNextAction != currentPlayerIndex) {
				// swap the board value as we switch players
				String message = "Next action player index {} is different than current player index {}, flipping returned board value from {} to {} at depth {}";
				log.debug(message, playerForNextAction, currentPlayerIndex, boardValueV, -boardValueV, depth);
				boardValueV = -boardValueV;
			} else {
				log.debug("Next action player index {} is same player index {} at depth {}", playerForNextAction,
						currentPlayerIndex, depth);
			}

			updateBoardActionValue(canonicalForm, actionIndexOfNextSimulationA, boardValueV, depth, describeAction);

			incrementBoardHitCount(canonicalForm);
			incrementBoardActionHitCount(canonicalForm, actionIndexOfNextSimulationA);

		}

		// search always reverts the board value, likely assuming the player switches
		// between actions.

		log.debug("ending search at depth {} for player {} board value {}", depth, currentPlayerIndex, boardValueV);

		return boardValueV;

	}

//	private void checkForValidMoveMatch(String boardS, boolean[] oldValidMoves, Game currentGame) {
//
//		Valid v1 = new Valid();
//		v1.valid = oldValidMoves;
//		Valid v2 = new Valid();
//		v2.valid = currentGame.getValidMoves();
//
//		if (!v1.equals(v2)) {
//			log.error("Found two state that have the same canonical form but not the same valid legal moves.");
//			int[] is = movesToReachBoard.get(boardS);
//			log.error("Game 1 : " + Arrays.toString(is));
//			log.error("Game 2 : " + Arrays.toString(currentGame.getActivatedActions()));
//			log.error("Legal moves 1 : " + Arrays.toString(v1.valid));
//			log.error("Legal moves 2 : " + Arrays.toString(v2.valid));
//
//			throw new IllegalStateException();
//		}
//
//	}
//
//	@EqualsAndHashCode
//	public static class Valid {
//		public boolean[] valid;
//	}

	private int findBestActionIndexFromPreviousSimulations(ByteCanonicalForm boardS, Game game) {
		boolean[] validMoves = validMovesForBoardVs.get(boardS);

		float bestActionValue = Float.NEGATIVE_INFINITY;
		int bestActionIndex = -1;

		// pick the action with the highest upper confidence bound
		for (int actionIndexA = 0; actionIndexA < validMoves.length; actionIndexA++) {
			boolean valid = validMoves[actionIndexA];
			if (valid) {

				// adjust the move value of everything that we hit during simulations.
				float currentActionValueU = adjustMoveValue(boardS, actionIndexA);

				if (currentActionValueU > bestActionValue) {
					bestActionValue = currentActionValueU;
					bestActionIndex = actionIndexA;
				}
			}
		}

		if (bestActionIndex == -1) {
			log.error("findBestActionIndexFromPreviousSimulations return -1 index, valid moves : {}",
					Arrays.toString(validMoves));
		}

		return bestActionIndex;
	}

	public void printStats() {

		if (Options.PRINT_MCTS_STATS) {

			String infoMessage = "action count = {}, avg action time micro = {}, nnet call count = {}, average nnet call time micro = {}, all move masked count : {}, unused prediction count : {}, avg depth per search : {}";
			log.info(infoMessage, getActionProbabilityTotalCount,
					getActionProbabilityTotalTimeNano / (getActionProbabilityTotalCount * 1000), neuralNetTotalCount,
					neuralNetTotalTimeNano / (neuralNetTotalCount * 1000), allMovesMaskedCount,
					preparedPredictions.size(), getActionProbabilityTotalDepth / getActionProbabilityTotalCount);
			log.debug("simulationTotalCount = {}", simulationTotalCount);
			log.debug("neuralNetTotalCount = {}", neuralNetTotalCount);
			log.debug("getActionProbabilityTotalTimeNano = {}", getActionProbabilityTotalTimeNano);
			log.debug("simulationTotalTimeNano = {}", simulationTotalTimeNano);
			log.debug("neuralNetTotalTimeNano = {}", neuralNetTotalTimeNano);

			log.debug("avg getActionProbabilityTime Micro = {}",
					getActionProbabilityTotalTimeNano / (getActionProbabilityTotalCount * 1000));
			log.debug("avg simulationTime Micro = {}", simulationTotalTimeNano / (simulationTotalCount * 1000));
			log.debug("avg neuralNetTime Micro = {}", neuralNetTotalTimeNano / (neuralNetTotalCount * 1000));

		}
	}

	private float predictValueWithNeuralNet(Game currentGame, ByteCanonicalForm canonicalForm, int depth) {

		Pair<PolicyVector, Float> predictActionAndValue = null;

		if (Options.MCTS_PREPARE_PREDICTIONS && depth <= Options.MCTS_PREPARE_PREDICTION_MAX_DEPTH) {
			predictActionAndValue = fetchPreparedPredictions(canonicalForm);

			if (predictActionAndValue == null) {
				preparePredictionsFromGame(currentGame);
				predictActionAndValue = fetchPreparedPredictions(canonicalForm);
			}
		} else {
			long starNano = System.nanoTime();
			predictActionAndValue = neuralNet.predict(canonicalForm);
			neuralNetTotalCount++;
			neuralNetTotalTimeNano += System.nanoTime() - starNano;
		}

		PolicyVector allActionProbability = predictActionAndValue.getLeft();

		if (Options.PRINT_MCTS_FULL_PROBABILITY_VECTOR) {
			allActionProbability.printProbabilityVector();
		}

		float boardValueV = predictActionAndValue.getRight();

		if (Options.MCTS_PREDICT_VALUE_WITH_SIMULATION) {
			int nextPlayer = currentGame.getNextPlayer();
			boardValueV = currentGame.getSimpleValue(nextPlayer, boardValueV);
		}

		boolean[] validMoves = currentGame.getValidMoves();
		allActionProbability.maskInvalidMoves(validMoves);

		float sum = allActionProbability.sum();
		if (sum <= 0) {
			/*
			 * # if all valid moves were masked make all valid moves equally probable
			 * 
			 * # NB! All valid moves may be masked if either your NNet architecture is
			 * insufficient or you've get overfitting or something else. # If you have got
			 * dozens or hundreds of these messages you should pay attention to your NNet
			 * and/or training process.
			 */
			log.info("All valid moves were masked, do workaround where all actions get equal probability.");
			allMovesMaskedCount++;

			allActionProbability.activateAllValidMoves(validMoves);
		}

		if (Options.MCTS_USE_MANUAL_AI) {
			KemetGame kg = (KemetGame) game;

			int nextPlayer = currentGame.getNextPlayer();
			TrialPlayerAI ai = new TrialPlayerAI(kg.getPlayerByIndex(nextPlayer), kg);
			ai.print = false;
			int actionIndex = ai.pickAction(kg.action.getNextPlayerChoicePick()).getIndex();

			allActionProbability.boostActionIndex(actionIndex);
		}

		allActionProbability.normalize();

		choiceValuePredictionForBoardPs.put(canonicalForm, allActionProbability);

		printActionProbabilities(allActionProbability, boardValueV, currentGame);

		validMovesForBoardVs.put(canonicalForm, validMoves);
		if (Options.MCTS_VALIDATE_MOVE_FOR_BOARD) {
			movesToReachBoard.put(canonicalForm, currentGame.getActivatedActions());
		}
		boardHitCountNs.put(canonicalForm, 0);
		return boardValueV;
	}

	private void preparePredictionsFromGame(Game currentGame) {
		List<ByteCanonicalForm> preparedBoards = new ArrayList<>();

		preparePredictionsFromGame(currentGame, preparedBoards, Options.MCTS_PREPARE_PREDICTION_DEPTH);

		int size = preparedBoards.size();
		long starNano = System.nanoTime();
		Pair<PolicyVector, Float>[] predict = neuralNet.predict(preparedBoards);
		neuralNetTotalCount += size;
		long duration = System.nanoTime() - starNano;
		neuralNetTotalTimeNano += duration;
		log.debug("Ran {} predictions on neural network, took {} nano", size, duration);

		for (int i = 0; i < predict.length; i++) {
			Pair<PolicyVector, Float> pair = predict[i];
			ByteCanonicalForm board = preparedBoards.get(i);
			preparedPredictions.put(board, pair);
		}

	}

	private void preparePredictionsFromGame(Game currentGame, List<ByteCanonicalForm> preparedBoards, int depthLeft) {
		int nextPlayer = currentGame.getNextPlayer();
		ByteCanonicalForm canonicalForm = currentGame.getCanonicalForm(nextPlayer);
		preparedBoards.add(canonicalForm);

		if (depthLeft > 0) {
			int nextDepth = depthLeft - 1;
			boolean[] validMoves = currentGame.getValidMoves();
			for (int i = 0; i < validMoves.length; i++) {
				boolean b = validMoves[i];
				if (b) {
					Game clone = currentGame.clone();
					clone.activateAction(nextPlayer, i);
					if (!clone.isGameEnded()) {
						preparePredictionsFromGame(clone, preparedBoards, nextDepth);
					}
				}
			}
		}
	}

	private Pair<PolicyVector, Float> fetchPreparedPredictions(ByteCanonicalForm boardS) {
		Pair<PolicyVector, Float> remove = preparedPredictions.remove(boardS);
		return remove;
	}

	public static void printActionProbabilities(PolicyVector allActionProbability, float value, Game currentGame) {
		if (Options.PRINT_MCTS_SEARCH_PROBABILITIES) {
			if (log.isDebugEnabled()) {
				log.debug("Current board value : {} for player {}", value, currentGame.getNextPlayer());
			}
			// allActionProbability.printActionProbabilities(currentGame);
		}
	}

	private float updateBoardActionValue(ByteCanonicalForm boardS, int actoinIndexA, float newBoardActionValueV,
			int depth, String describeAction) {
		boolean preExists = false;
		float previousBoardActionValueQ = 0;
		Map<Integer, Float> map = valueAtBoardActionQsa.get(boardS);

		if (map == null) {
			map = new HashMap<Integer, Float>();
			valueAtBoardActionQsa.put(boardS, map);
		}

		if (map.containsKey(actoinIndexA)) {
			previousBoardActionValueQ = map.get(actoinIndexA);
			preExists = true;
		}

		float newValue = newBoardActionValueV;
		if (preExists) {
			// update the value of the current board+action
			Integer currentBoardActionHitCountNsa = boardActionHitCountNsa.get(boardS).get(actoinIndexA);

			// merge the new board value with the old one proportionally
			newValue = (currentBoardActionHitCountNsa * previousBoardActionValueQ + newBoardActionValueV)
					/ (currentBoardActionHitCountNsa + 1);
		}

		log.debug("Adjusted action {} from value {} merged with {} resulting value {} at depth {}", describeAction,
				previousBoardActionValueQ, newBoardActionValueV, newValue, depth);

		map.put(actoinIndexA, newValue);
		return newValue;
	}

	public void incrementBoardActionHitCount(ByteCanonicalForm s, int a) {
		Map<Integer, Integer> map = boardActionHitCountNsa.get(s);
		if (map == null) {
			map = new HashMap<Integer, Integer>();
			boardActionHitCountNsa.put(s, map);
		}

		int hitCount = 1;
		if (map.containsKey(a)) {
			hitCount = map.get(a) + 1;
		}
		map.put(a, hitCount);
	}

	private void incrementBoardHitCount(ByteCanonicalForm boardS) {
		Integer numberHits = boardHitCountNs.get(boardS);
		if (numberHits == null) {
			boardHitCountNs.put(boardS, 1);
		} else {
			boardHitCountNs.put(boardS, numberHits + 1);
		}
	}

	private float adjustMoveValue(ByteCanonicalForm boardS, int actionIndexA) {

		float cpuct = getCPuct();
		float currentActionValueU;

		PolicyVector policyVectorValuePrediction = choiceValuePredictionForBoardPs.get(boardS);
		Integer boardHitCount = boardHitCountNs.get(boardS);
		float actionValuePredictionFromPolicy = policyVectorValuePrediction.vector[actionIndexA];

		Map<Integer, Float> map = valueAtBoardActionQsa.get(boardS);
		if (map != null && map.containsKey(actionIndexA)) {
			float previousActionValue = 0;
			previousActionValue = map.get(actionIndexA);

			Map<Integer, Integer> boardActionHitCounter = boardActionHitCountNsa.get(boardS);
			Integer boardAndActionHitCounter = boardActionHitCounter.get(actionIndexA);

			currentActionValueU = getAdjustedActionValueForSearch(cpuct, boardHitCount, actionValuePredictionFromPolicy,
					previousActionValue, boardAndActionHitCounter);
		} else {
			currentActionValueU = (float) (cpuct * actionValuePredictionFromPolicy
					* Math.sqrt(boardHitCount + EPSILON)); // Q = 0 ?
		}
		return currentActionValueU;
	}

	public static float getAdjustedActionValueForSearch(float cpuct, Integer boardHitCount,
			float actionValuePredictionFromPolicy, float previousActionValue, Integer boardAndActionHitCounter) {
		float currentActionValueU;
		currentActionValueU = (float) (previousActionValue
				+ cpuct * actionValuePredictionFromPolicy * Math.sqrt(boardHitCount) / (1 + boardAndActionHitCounter));
		return currentActionValueU;
	}

	private float getCPuct() {
		return cPuct;
	}

	public void setGame(Game game2) {
		this.game = game2;
	}

	public void setCpuct(float d) {
		cPuct = d;

	}

	public void printCurrentBoardProbability() {

		ByteCanonicalForm gameString = game.getCanonicalForm(game.getNextPlayer());
		
		Pair<PolicyVector, Float> predict = neuralNet.predict(gameString);
		PolicyVector policy = predict.getKey();
		Float value = predict.getValue();
		
		value += 1;
		value /= 2;
		value *= 100;
		
		log.info("Neural net chance to win : {} %", value);
		
		Map<Integer, Float> valueAtActionQ = valueAtBoardActionQsa.get(gameString);
		Set<Entry<Integer, Float>> entrySet = valueAtActionQ.entrySet();
		for (Entry<Integer, Float> entry : entrySet) {
			Integer key = entry.getKey();
			Float predictedActionValue = entry.getValue();
			float nnetPredict = policy.vector[key];
			log.info("Action index {} | mcts predicted value {} | nnet predicted value {} | action : {}", key, predictedActionValue, nnetPredict, game.describeAction(key));
		}
		
	}

}