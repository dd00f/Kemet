package kemet.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

/**
 * 
 * Monte Carlo Tree Search
 */
public class MCTS {

	// constant to increase the rate at which the algorithm will explore n
	public static final float EPSILON = 0.00000001f;
	
	private Game game;
	private NeuralNet neuralNet;
	private float cPuct; // C? P? upper confidence T?

	private Map<String, Map<Integer, Float>> valueAtBoardActionQsa; // stores Q values for s,a (as defined in the paper)
	private Map<String, Map<Integer, Integer>> boardActionHitCountNsa; // stores #times edge s (game CF) a (choice
																		// Index) was visited
	private Map<String, Integer> boardHitCountNs; // stores #times board s (game CF) was visited
	private Map<String, PolicyVector> choiceValuePredictionForBoardPs; // stores initial policy (returned by neural net)

	private Map<String, Integer> isBoardEndedEs; // stores game.getGameEnded ended for board s
	private Map<String, boolean[]> validMovesForBoardVs; // stores game.getValidMoves for board s
	private int simulationCount;

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
	public PolicyVector getActionProbability(ByteCanonicalForm gameCanonicalForm, int temperature, int playerIndex) {

		// run the requested simulation count
		for (int i = 0; i < simulationCount; ++i) {
			search(gameCanonicalForm, playerIndex);
		}

		String gameString = game.stringRepresentation();

		int actionSize = game.getActionSize();
		int[] actionHitCounts = new int[actionSize];
		Set<Entry<Integer, Integer>> actionIndexCount = boardActionHitCountNsa.get(gameString).entrySet();
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

		return probabilitiesOfAllActions;
	}

	private float[] adjustActionHitCountTemperature(int temperature, int[] actionHitCounts) {
		float[] countsFloat = new float[actionHitCounts.length];
		if (temperature != 1) {
			for (int i = 0; i < actionHitCounts.length; i++) {
				countsFloat[i] = (float) Math.pow(actionHitCounts[i], 1 / temperature);
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
	public float search(ByteCanonicalForm canonicalBoard, int playerIndex) {

		String boardS = game.stringRepresentation();

		if (!isBoardEndedEs.containsKey(boardS)) {
			isBoardEndedEs.put(boardS, game.getGameEnded(1));
		}

		Integer gameFinishedState = isBoardEndedEs.get(boardS);
		float boardValueV = 0;

		if (gameFinishedState != 0) {
			boardValueV = gameFinishedState;
		} else if (!choiceValuePredictionForBoardPs.containsKey(boardS)) {
			boardValueV = predictValueWithNeuralNet(canonicalBoard, boardS);
		} else {

			int actionIndexOfNextSimulationA = findBestActionIndexFromPreviousSimulations(boardS);

			// Possible optimization : cache the game to reuse it.
			Game nextState = game.getNextState(1, actionIndexOfNextSimulationA);
			int nextPlayerIndex = nextState.getNextPlayer();

			ByteCanonicalForm nextBoardS = nextState.getCanonicalForm(nextPlayerIndex);

			boardValueV = search(nextBoardS, nextPlayerIndex);
			
			if( nextPlayerIndex != playerIndex ) {
				// swap the board value as we switch players
				boardValueV = -boardValueV;
			}

			updateBoardActionValue(boardS, actionIndexOfNextSimulationA, boardValueV);

			incrementBoardHitCount(boardS);
		}

		// search always reverts the board value, likely assuming the player switches between actions.
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
		return bestActionIndex;
	}

	private float predictValueWithNeuralNet(ByteCanonicalForm canonicalBoard, String boardS) {
		Pair<PolicyVector, Float> predictActionAndValue = neuralNet.predict(canonicalBoard);
		PolicyVector allActionProbability = predictActionAndValue.getLeft();
		float boardValueV = predictActionAndValue.getRight();

		choiceValuePredictionForBoardPs.put(boardS, allActionProbability);

		boolean[] validMoves = game.getValidMoves();
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
			System.out.println("All valid moves were masked, do workaround.");

			allActionProbability.activateAllValidMoves(validMoves);
		}
		allActionProbability.normalize();

		validMovesForBoardVs.put(boardS, validMoves);
		boardHitCountNs.put(boardS, 0);
		return boardValueV;
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

		if (preExists) {
			currentActionValueU = (float) (previousActionValue
					+ cpuct * choiceValuePredictionForBoardPs.get(boardS).vector[actionIndexA]
							* Math.sqrt(boardHitCountNs.get(boardS))
							/ (1 + boardActionHitCountNsa.get(boardS).get(actionIndexA)));
		} else {
			currentActionValueU = (float) (cpuct * choiceValuePredictionForBoardPs.get(boardS).vector[actionIndexA]
					* Math.sqrt(boardHitCountNs.get(boardS) + EPSILON)); // Q = 0 ?
		}
		return currentActionValueU;
	}

	private float getCPuct() {
		return cPuct;
	}

}