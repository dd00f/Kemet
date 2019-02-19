package kemet.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import kemet.Options;
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

	public Map<String, Map<Integer, Float>> valueAtBoardActionQsa; // stores Q values for s,a (as defined in the paper)
	public Map<String, Map<Integer, Integer>> boardActionHitCountNsa; // stores #times edge s (game CF) a (choice
																		// Index) was visited
	public Map<String, Integer> boardHitCountNs; // stores #times board s (game CF) was visited
	public Map<String, PolicyVector> choiceValuePredictionForBoardPs; // stores initial policy (returned by neural net)

	public Map<String, Integer> isBoardEndedEs; // stores game.getGameEnded ended for board s
	public Map<String, boolean[]> validMovesForBoardVs; // stores game.getValidMoves for board s
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

	}

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
	public PolicyVector getActionProbability(int temperature) {

		long starProbNano = System.nanoTime();

		// run the requested simulation count
		for (int i = 0; i < simulationCount; ++i) {
			long starSimNano = System.nanoTime();
			Game clone = game.clone();
			clone.setPrintActivations(Options.PRINT_MCTS_SEARCH_ACTIONS);
			search(clone);

			simulationTotalCount++;
			simulationTotalTimeNano += System.nanoTime() - starSimNano;
		}

		String gameString = game.stringRepresentation();

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

		if (temperature == 0) {
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

		return probabilitiesOfAllActions;
	}

	private float[] adjustActionHitCountTemperature(int temperature, int[] actionHitCounts) {
		float[] countsFloat = new float[actionHitCounts.length];
		if (temperature != 1) {
			int power = 1 / temperature;
			for (int i = 0; i < actionHitCounts.length; i++) {
				countsFloat[i] = (float) Math.pow(actionHitCounts[i], power);
			}
		} else {
			for (int i = 0; i < actionHitCounts.length; i++) {
				countsFloat[i] = (float) actionHitCounts[i];
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
	public float search(Game currentGame) {

		String boardS = currentGame.stringRepresentation();

		if (!isBoardEndedEs.containsKey(boardS)) {
			isBoardEndedEs.put(boardS, currentGame.getGameEnded(1));
		}

		Integer gameFinishedState = isBoardEndedEs.get(boardS);
		float boardValueV = 0;

		if (gameFinishedState != 0) {
			boardValueV = gameFinishedState;
		} else if (!choiceValuePredictionForBoardPs.containsKey(boardS)) {
			boardValueV = predictValueWithNeuralNet(currentGame, boardS);
		} else {

			int actionIndexOfNextSimulationA = findBestActionIndexFromPreviousSimulations(boardS);
			int playerIndex = currentGame.getNextPlayer();

			// Possible optimization : cache the game to reuse it.
			try {

				currentGame.activateAction(playerIndex, actionIndexOfNextSimulationA);

			} catch (Exception ex) {
				currentGame.setPrintActivations(true);
				currentGame.printDescribeGame();
				currentGame.printChoiceList();

				ex.printStackTrace();

				// patch the valid moves and redo another action
				validMovesForBoardVs.put(boardS, currentGame.getValidMoves());
				actionIndexOfNextSimulationA = findBestActionIndexFromPreviousSimulations(boardS);
				currentGame.activateAction(playerIndex, actionIndexOfNextSimulationA);
			}

			int nextPlayerIndex = currentGame.getNextPlayer();

			boardValueV = search(currentGame);
			if (nextPlayerIndex == -1) {
				// game ended, check if the current player won
				boardValueV = currentGame.getGameEnded(playerIndex);
			} else if (nextPlayerIndex != playerIndex) {
				// swap the board value as we switch players
				boardValueV = -boardValueV;
			}

			updateBoardActionValue(boardS, actionIndexOfNextSimulationA, boardValueV);

			incrementBoardHitCount(boardS);
			incrementBoardActionHitCount(boardS, actionIndexOfNextSimulationA);

		}

		// search always reverts the board value, likely assuming the player switches
		// between actions.
		return -boardValueV;

	}

	private int findBestActionIndexFromPreviousSimulations(String boardS) {
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
			log.error("findBestActionIndexFromPreviousSimulations return -1 index, valuid moves : {}",
					Arrays.toString(validMoves));
		}

		return bestActionIndex;
	}

	public void printStats() {

		if (Options.PRINT_MCTS_STATS) {

			log.info(
					"action count = {}, nnet call count = {}, average nnet call time micro = {}, all move masked count : {}",
					getActionProbabilityTotalCount, neuralNetTotalCount,
					neuralNetTotalTimeNano / (neuralNetTotalCount * 1000), allMovesMaskedCount);
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

	private float predictValueWithNeuralNet(Game currentGame, String boardS) {
		ByteCanonicalForm canonicalForm = currentGame.getCanonicalForm(currentGame.getNextPlayer());

		long starNano = System.nanoTime();

		Pair<PolicyVector, Float> predictActionAndValue = neuralNet.predict(canonicalForm);

		neuralNetTotalCount++;
		neuralNetTotalTimeNano += System.nanoTime() - starNano;

		PolicyVector allActionProbability = predictActionAndValue.getLeft();

		if (Options.PRINT_MCTS_FULL_PROBABILITY_VECTOR) {
			allActionProbability.printProbabilityVector();
		}

		float boardValueV = predictActionAndValue.getRight();

		choiceValuePredictionForBoardPs.put(boardS, allActionProbability);

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
			log.debug("All valid moves were masked, do workaround where all actions get equal probability.");
			allMovesMaskedCount++;

			allActionProbability.activateAllValidMoves(validMoves);
		}
		allActionProbability.normalize();

		printActionProbabilities(allActionProbability, boardValueV, currentGame);

		validMovesForBoardVs.put(boardS, validMoves);
		boardHitCountNs.put(boardS, 0);
		return boardValueV;
	}

	public void printActionProbabilities(PolicyVector allActionProbability, float value, Game currentGame) {
		if (Options.PRINT_MCTS_SEARCH_PROBABILITIES) {
			log.debug("Current board value : {}", value);

			allActionProbability.printActionProbabilities(currentGame);

		}

	}

	private void updateBoardActionValue(String boardS, int actoinIndexA, float newBoardActionValueV) {
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

		map.put(actoinIndexA, newValue);
	}

	public void incrementBoardActionHitCount(String s, int a) {
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

	private void incrementBoardHitCount(String boardS) {
		Integer numberHits = boardHitCountNs.get(boardS);
		if (numberHits == null) {
			boardHitCountNs.put(boardS, 1);
		} else {
			boardHitCountNs.put(boardS, numberHits + 1);
		}
	}

	private float adjustMoveValue(String boardS, int actionIndexA) {
		boolean preExists = false;
		float previousActionValue = 0;
		Map<Integer, Float> map = valueAtBoardActionQsa.get(boardS);
		if (map != null && map.containsKey(actionIndexA)) {
			previousActionValue = map.get(actionIndexA);
			preExists = true;
		}
		float cpuct = getCPuct();
		float currentActionValueU;

		PolicyVector policyVectorValuePrediction = choiceValuePredictionForBoardPs.get(boardS);
		Integer boardHitCount = boardHitCountNs.get(boardS);
		float actionValuePredictionFromPolicy = policyVectorValuePrediction.vector[actionIndexA];
		if (preExists) {
			Map<Integer, Integer> boardActionHitCounter = boardActionHitCountNsa.get(boardS);
			Integer boardAndActionHitCounter = boardActionHitCounter.get(actionIndexA);

			currentActionValueU = (float) (previousActionValue + cpuct * actionValuePredictionFromPolicy
					* Math.sqrt(boardHitCount) / (1 + boardAndActionHitCounter));
		} else {
			currentActionValueU = (float) (cpuct * actionValuePredictionFromPolicy
					* Math.sqrt(boardHitCount + EPSILON)); // Q = 0 ?
		}
		return currentActionValueU;
	}

	private float getCPuct() {
		return cPuct;
	}

	public void setGame(Game game2) {
		this.game = game2;
	}

}