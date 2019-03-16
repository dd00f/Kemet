package kemet.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import kemet.Options;
import kemet.util.SearchPooler.GameInformation;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

/**
 * 
 * Monte Carlo Tree Search that stacks multiple search at once for batching
 * efficiency
 */
@Log4j2
public class StackingMCTS {

	// constant to increase the rate at which the algorithm will explore n
	public static final float EPSILON = 0.00000001f;

	public Game game;

	public List<TrainExample> trainExamples = new ArrayList<>();

	private float cPuct; // C? P? upper confidence T?

	// stores Q values for s,a (as defined in the paper)
	public Map<ByteCanonicalForm, Map<Integer, Float>> valueAtBoardActionQsa = new HashMap<>(); 

	// stores #times edges (game CF) a choice Index was visited
	public Map<ByteCanonicalForm, Map<Integer, Integer>> boardActionHitCountNsa = new HashMap<>(); 

	// stores #times board s (game CF) was visited
	public Map<ByteCanonicalForm, Integer> boardHitCountNs = new HashMap<>();

	// stores initial policy (returned by Neural network )
	public Map<ByteCanonicalForm, PolicyVector> choiceValuePredictionForBoardPs = new HashMap<>();

	public SearchPooler pooler;

	private long getActionProbabilityTotalCount = 0;
	private long getActionProbabilityTotalTimeNano = 0;
	private long simulationTotalCount = 0;
	private long simulationTotalTimeNano = 0;

	public StackingMCTS(Game game, SearchPooler inPooler, float cPuct) {
		this.game = game;
		this.pooler = inPooler;
		this.cPuct = cPuct;
	}

	private int maxSearchDepth;

	private long getActionProbabilityTotalDepth;

	private SearchData searchData;

	public PolicyVector getActionProbabilityAfterSearch(float temperature) {
		long starProbNano = System.nanoTime();

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

	public static class SearchDataLayer {
		public int currentPlayer = -1;
		public int actionIndex = -1;
		public ByteCanonicalForm byteCanonicalForm;
		public String actionDescription = "";
	}

	public class SearchData {

		public List<SearchDataLayer> searchLayers = new ArrayList<>(Options.COACH_MCTS_SIMULATION_COUNT_PER_MOVE / 3);

		public boolean searchFinished = false;
		public Game game;

		public int getDepth() {
			return searchLayers.size();
		}

		public void finalizeSearch(final float valueV, PolicyVector allActionProbability,
				ByteCanonicalForm canonicalForm) {
			// reverse iterate boards
			int size = getDepth();

			if (maxSearchDepth < size) {
				maxSearchDepth = size;
			}

			SearchDataLayer nextDataLayer = null;
			float currentValue = valueV;

			if (allActionProbability != null) {
				choiceValuePredictionForBoardPs.put(canonicalForm, allActionProbability);
			}

			for (int i = size - 1; i >= 0; --i) {

				SearchDataLayer searchDataLayer = searchLayers.get(i);

				if (nextDataLayer == null) {
					// game ended
					log.debug("Last move of simulation, player {} board value is {} at depth {}",
							searchDataLayer.currentPlayer, currentValue, i);

				} else if (searchDataLayer.currentPlayer != nextDataLayer.currentPlayer) {
					// swap the board value as we switch players
					String message = "Next action player index {} is different than current player index {}, flipping returned board value from {} to {} at depth {}";
					log.debug(message, nextDataLayer.currentPlayer, searchDataLayer.currentPlayer, currentValue,
							-currentValue, i);
					currentValue = -currentValue;
				} else {
					log.debug("Next action player index {} is same player index {} at depth {}",
							nextDataLayer.currentPlayer, searchDataLayer.currentPlayer, i);
				}

				updateBoardActionValue(searchDataLayer.byteCanonicalForm, searchDataLayer.actionIndex, currentValue, i,
						searchDataLayer.actionDescription);

				incrementBoardHitCount(searchDataLayer.byteCanonicalForm);
				incrementBoardActionHitCount(searchDataLayer.byteCanonicalForm, searchDataLayer.actionIndex);

				nextDataLayer = searchDataLayer;

			}

		}

		public Game getGame() {
			return game;
		}

		public void addPendingPrediction(Game currentGame) {
			int nextPlayer = currentGame.getNextPlayer();
			ByteCanonicalForm canonicalForm = currentGame.getCanonicalForm(nextPlayer);
			boardHitCountNs.put(canonicalForm, 0);
			pooler.addPendingPrediction(currentGame, canonicalForm);

			if (Options.MCTS_PREPARE_PREDICTIONS && getDepth() <= Options.MCTS_PREPARE_PREDICTION_MAX_DEPTH) {
				preparePredictionsFromGame(currentGame, Options.MCTS_PREPARE_PREDICTION_DEPTH);
			}
		}
	}

	public void startSearch() {
		searchData = new SearchData();
		searchData.game = game.clone();
		runSearchUntilNeuralNetPredict();
	}

	public void finishSearch() {
		if (!searchData.game.isGameEnded()) {

			int nextPlayer = searchData.game.getNextPlayer();
			ByteCanonicalForm canonicalForm = searchData.game.getCanonicalForm(nextPlayer);
			GameInformation providedPrediction = pooler.providedPredictions.get(canonicalForm);
			PolicyVector allActionProbability = providedPrediction.policy;

			if (allActionProbability == null) {
				throw new NullPointerException("required prediction not found");
			}

			float boardValueV = providedPrediction.boardValue;

			searchData.finalizeSearch(boardValueV, allActionProbability, canonicalForm);

			printActionProbabilities(allActionProbability, boardValueV, searchData.game);
		}

		searchData = null;
	}

	public void runSearchUntilNeuralNetPredict() {

		int currentPlayerIndex = searchData.getGame().getNextPlayer();
		long start = System.nanoTime();

		while (!searchData.searchFinished) {

			Game currentGame = searchData.getGame();
			int gameEnded = currentGame.getGameEnded(currentPlayerIndex);
			if (gameEnded != 0) {
				searchData.searchFinished = true;
				searchData.finalizeSearch(gameEnded, null, null);

			} else {
				currentPlayerIndex = currentGame.getNextPlayer();
				ByteCanonicalForm canonicalForm = currentGame.getCanonicalForm(currentPlayerIndex);

				if (!choiceValuePredictionForBoardPs.containsKey(canonicalForm)) {

					// query neural network
					searchData.addPendingPrediction(currentGame);
					searchData.searchFinished = true;
				} else {
					// increase the search depth
					final int actionIndexOfNextSimulationA = findBestActionIndexFromPreviousSimulations(canonicalForm,
							currentGame);

					String describeAction = "";
					if (log.isDebugEnabled()) {
						describeAction = currentGame.describeAction(actionIndexOfNextSimulationA);
					}

					SearchDataLayer newLayer = new SearchDataLayer();
					newLayer.actionDescription = describeAction;
					newLayer.actionIndex = actionIndexOfNextSimulationA;
					newLayer.byteCanonicalForm = canonicalForm;
					newLayer.currentPlayer = currentPlayerIndex;
					searchData.searchLayers.add(newLayer);

					activateActionOnGame(currentGame, currentPlayerIndex, canonicalForm, actionIndexOfNextSimulationA);

				}
			}
		}

		simulationTotalCount++;
		long duration = System.nanoTime() - start;
		simulationTotalTimeNano += duration;

	}

	private void activateActionOnGame(Game currentGame, final int currentPlayerIndex, ByteCanonicalForm canonicalForm,
			int actionIndexOfNextSimulationA) {
		try {
			currentGame.activateAction(currentPlayerIndex, actionIndexOfNextSimulationA);

		} catch (Exception ex) {

			GameInformation gameInformation = pooler.providedPredictions.get(canonicalForm);
			checkForValidMoveMatch(gameInformation, currentGame);

			currentGame.setPrintActivations(true);
			currentGame.printDescribeGame();
			currentGame.printChoiceList();

			ex.printStackTrace();

			// patch the valid moves and redo another action
			gameInformation.validMoves = currentGame.getValidMoves();
			actionIndexOfNextSimulationA = findBestActionIndexFromPreviousSimulations(canonicalForm, currentGame);
			currentGame.activateAction(currentPlayerIndex, actionIndexOfNextSimulationA);
		}
	}

	public void checkForValidMoveMatch(GameInformation gameInformation, Game currentGame) {

		Valid v1 = new Valid();
		v1.valid = gameInformation.validMoves;
		Valid v2 = new Valid();
		v2.valid = currentGame.getValidMoves();

		if (!v1.equals(v2)) {
			log.error("Found two state that have the same canonical form but not the same valid legal moves.");
			log.error("Game 1 : " + Arrays.toString(gameInformation.movesToReachBoard));
			log.error("Game 2 : " + Arrays.toString(currentGame.getActivatedActions()));
			log.error("Legal moves 1 : " + Arrays.toString(v1.valid));
			log.error("Legal moves 2 : " + Arrays.toString(v2.valid));

			throw new IllegalStateException();
		}

	}

	@EqualsAndHashCode
	public static class Valid {
		public boolean[] valid;
	}

	private int findBestActionIndexFromPreviousSimulations(ByteCanonicalForm boardS, Game game) {
		boolean[] validMoves = pooler.providedPredictions.get(boardS).validMoves;

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

			if (getActionProbabilityTotalCount > 0) {
				String infoMessage = "action count = {}, avg action time micro = {}, avg depth per search : {}";
				log.info(infoMessage, getActionProbabilityTotalCount,
						getActionProbabilityTotalTimeNano / (getActionProbabilityTotalCount * 1000),
						getActionProbabilityTotalDepth / getActionProbabilityTotalCount);

				log.debug("avg getActionProbabilityTime Micro = {}",
						getActionProbabilityTotalTimeNano / (getActionProbabilityTotalCount * 1000));

			}

			log.debug("simulationTotalCount = {}", simulationTotalCount);
			log.debug("getActionProbabilityTotalTimeNano = {}", getActionProbabilityTotalTimeNano);
			log.debug("simulationTotalTimeNano = {}", simulationTotalTimeNano);

			if (simulationTotalCount > 0) {
				log.debug("avg simulationTime Micro = {}", simulationTotalTimeNano / (simulationTotalCount * 1000));
			}

		}

	}

	private void preparePredictionsFromGame(Game currentGame, int depthLeft) {
		int nextPlayer = currentGame.getNextPlayer();
		ByteCanonicalForm canonicalForm = currentGame.getCanonicalForm(nextPlayer);
		pooler.addPendingPrediction(currentGame, canonicalForm);

		if (depthLeft > 0) {
			int nextDepth = depthLeft - 1;
			boolean[] validMoves = currentGame.getValidMoves();
			for (int i = 0; i < validMoves.length; i++) {
				boolean b = validMoves[i];
				if (b) {
					Game clone = currentGame.clone();
					clone.activateAction(nextPlayer, i);
					if (!clone.isGameEnded()) {
						preparePredictionsFromGame(clone, nextDepth);
					}
				}
			}
		}
	}

//	private Pair<PolicyVector, Float> fetchPreparedPredictions(ByteCanonicalForm boardS) {
//		Pair<PolicyVector, Float> remove = preparedPredictions.remove(boardS);
//		return remove;
//	}

	public void printActionProbabilities(PolicyVector allActionProbability, float value, Game currentGame) {
		if (Options.PRINT_MCTS_SEARCH_PROBABILITIES) {
			if (log.isInfoEnabled()) {
				log.info("Current board value : {} for player {}", value, currentGame.getNextPlayer());
			}
			allActionProbability.printActionProbabilities(currentGame);
		}
	}

	private float updateBoardActionValue(ByteCanonicalForm boardS, int actionIndexA, float newBoardActionValueV,
			int depth, String describeAction) {
		boolean preExists = false;
		float previousBoardActionValueQ = 0;
		Map<Integer, Float> map = valueAtBoardActionQsa.get(boardS);

		if (map == null) {
			map = new HashMap<Integer, Float>();
			valueAtBoardActionQsa.put(boardS, map);
		}

		if (map.containsKey(actionIndexA)) {
			previousBoardActionValueQ = map.get(actionIndexA);
			preExists = true;
		}

		float newValue = newBoardActionValueV;
		if (preExists) {
			// update the value of the current board+action
			Integer currentBoardActionHitCountNsa = boardActionHitCountNsa.get(boardS).get(actionIndexA);

			// merge the new board value with the old one proportionally
			newValue = (currentBoardActionHitCountNsa * previousBoardActionValueQ + newBoardActionValueV)
					/ (currentBoardActionHitCountNsa + 1);
		}

		log.debug("Adjusted action {} from value {} merged with {} resulting value {} at depth {}", describeAction,
				previousBoardActionValueQ, newBoardActionValueV, newValue, depth);

		map.put(actionIndexA, newValue);
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

}