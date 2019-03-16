package kemet.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kemet.Options;
import kemet.ai.Simulation;
import kemet.model.action.GameAction;
import kemet.model.action.PlayerChoicePick;
import kemet.model.action.choice.Choice;
import kemet.model.action.choice.ChoiceInventory;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;
import kemet.util.Game;

public class KemetGame implements Model, Game {

	public static final int VICTORY_POINTS_OBJECTIVE = 8;

	private static final long serialVersionUID = 6738783849669850313L;

	private static final Logger LOGGER = LogManager.getLogger(KemetGame.class);

	public List<Tile> tileList = new ArrayList<>();
	public List<Player> playerByInitiativeList = new ArrayList<>();
	public List<DiCard> availableDiCardList = new ArrayList<>();
	public List<DiCard> discardedDiCardList = new ArrayList<>();
	public List<Power> availablePowerList = new ArrayList<>();
	public byte roundNumber = 0;
	public boolean victoryConditionTriggered = false;
	public byte victoryPointObjective = VICTORY_POINTS_OBJECTIVE;
	public Player winner = null;
	public GameAction action = null;
	public boolean printActivations = true;
	public int battleCount = 0;

	public int recordActionIndex = 0;
	public int[] actions = null;
	{
		if (Options.GAME_TRACK_MAX_ACTION_COUNT > 0) {
			actions = new int[Options.GAME_TRACK_MAX_ACTION_COUNT];
		}
	}

	private static Cache<KemetGame> GAME_CACHE = new Cache<KemetGame>(() -> new KemetGame());

	private PlayerChoicePick nextPlayerChoicePick;

	private ByteCanonicalForm canonicalForm;

	private boolean[] allValidMoves;

	public static KemetGame create() {
		KemetGame create = GAME_CACHE.create();
		create.initialize();
		return create;
	}

	private KemetGame() {
		initialize();
	}

	@Override
	public void initialize() {

		roundNumber = 0;
		victoryConditionTriggered = false;
		victoryPointObjective = 8;
		winner = null;
		action = GameAction.create(this);
		printActivations = true;

		tileList.clear();
		playerByInitiativeList.clear();
		availableDiCardList.clear();
		discardedDiCardList.clear();
		availablePowerList.clear();

		recordActionIndex = 0;
		if (actions != null) {
			Arrays.fill(actions, -1);
		}
	}

	@Override
	public KemetGame deepCacheClone() {

		KemetGame clone = GAME_CACHE.create();

		clone.tileList.clear();
		for (Tile tile : tileList) {
			clone.tileList.add((Tile) tile.deepCacheClone());
		}

		clone.playerByInitiativeList.clear();
		for (Player player : playerByInitiativeList) {
			clone.playerByInitiativeList.add((Player) player.deepCacheClone());
		}

		clone.availableDiCardList.clear();
		clone.availableDiCardList.addAll(availableDiCardList);

		clone.discardedDiCardList.clear();
		clone.discardedDiCardList.addAll(discardedDiCardList);

		clone.availablePowerList.clear();
		clone.availablePowerList.addAll(availablePowerList);

		clone.roundNumber = roundNumber;
		clone.victoryConditionTriggered = victoryConditionTriggered;
		clone.victoryPointObjective = victoryPointObjective;
		clone.printActivations = printActivations;
		clone.winner = clone.getPlayerByCopy(winner);

		clone.action = action.deepCacheClone();
		clone.action.relink(clone);

		clone.recordActionIndex = recordActionIndex;
		if (actions != null) {
			clone.actions = actions.clone();
		}

		// relink the structure
		for (Tile tile : clone.tileList) {
			tile.relink(clone);
		}

		for (Player player : clone.playerByInitiativeList) {
			player.relink(clone);
		}

		clone.allValidMoves = allValidMoves;
		clone.canonicalForm = canonicalForm;

		return clone;
	}

	@Override
	public void release() {
		if (Options.GAME_SKIP_RELEASE) {
			return;
		}

		// release owned objects
		for (Tile tile : tileList) {
			tile.release();
		}

		for (Player player : playerByInitiativeList) {
			player.release();
		}
		action.release();

		// clean links
		tileList.clear();
		playerByInitiativeList.clear();
		availableDiCardList.clear();
		discardedDiCardList.clear();
		availablePowerList.clear();
		winner = null;

		action = null;

		GAME_CACHE.release(this);
	}

	public void validate() {

		for (Player player : playerByInitiativeList) {
			player.validate(this);
		}

		for (Tile tile : tileList) {
			tile.validate(this);
		}

		action.validate(null, this);

		validate(winner);

	}

	public void validate(Player player) {
		if (player != null) {
			Player playerByCopy = getPlayerByCopy(player);
			if (playerByCopy != player) {
				Validation.validationFailed("Player is wrong copy " + player.name);
			}
		}
	}

	public void validate(Army army) {
		if (army != null) {
			Army armyByCopy = getArmyByCopy(army);
			if (armyByCopy != army) {
				Validation.validationFailed("Army is wrong copy " + army.name);
			}
		}
	}

	@Override
	public void describeGame(StringBuilder builder) {

		builder.append("Game turn ");
		builder.append(this.roundNumber);
		builder.append("\n");
		builder.append("Actions : \n\t");
		builder.append(Arrays.toString(getActivatedActions()));
		builder.append("\n");
		for (Player player : playerByInitiativeList) {
			player.describePlayer(builder);
		}
	}

	public void findWinner() {

		printDescribeGame();

		List<Player> playerList = new ArrayList<>(playerByInitiativeList);
		byte vp = playerList.stream().max(Comparator.comparing(ft -> ft.victoryPoints)).get().victoryPoints;
		List<Player> filter = playerList.stream().filter(player -> player.victoryPoints >= vp)
				.collect(Collectors.toList());

		long count = filter.size();
		if (count > 1) {
			if (printActivations) {
				printEvent("Tie for VP " + vp);
			}

			byte battlePoint = filter.stream().max(Comparator.comparing(ft -> ft.battlePoints)).get().battlePoints;
			filter = filter.stream().filter(player -> player.battlePoints >= battlePoint).collect(Collectors.toList());

			count = filter.size();
			if (count > 1) {
				printEvent("Tie for Battle VP " + battlePoint);

				for (Player orderedPlayer : playerByInitiativeList) {
					if (filter.contains(orderedPlayer)) {
						winner = orderedPlayer;
						printEvent(winner.name + " won at the end of day. Earliest to act");
						break;
					}
				}
			} else {
				winner = filter.iterator().next();
				printEvent(winner.name + " won at the end of day. Most Battle VP " + vp);
			}
		} else {
			winner = filter.iterator().next();
			printEvent(winner.name + " won at the end of day. Most VP " + vp);

		}
	}

	public void checkForWinningCondition() {
		if (!victoryConditionTriggered) {
			for (Player player : playerByInitiativeList) {
				if (player.victoryPoints >= victoryPointObjective) {
					if (printActivations) {
						printEvent(player.name + " triggered the end game condition of " + victoryPointObjective
								+ " victory points.");
					}
					victoryConditionTriggered = true;
				}
			}
		}
	}

	public Player getPlayerByCopy(Player copy) {

		if (copy == null) {
			return null;
		}
		return getPlayerByName(copy.name);
	}

	public Player getPlayerByName(String name) {

		for (Player player : playerByInitiativeList) {
			if (player.name.equals(name)) {
				return player;
			}
		}
		return null;
	}

	public Player getPlayerByIndex(int index) {

		for (Player player : playerByInitiativeList) {
			if (player.index == index) {
				return player;
			}
		}
		return null;
	}

	public Tile getTileByPlayerAndDistrictIndex(int playerIndex, int districtIndex) {
		for (Tile tile : tileList) {
			if (tile.districtIndex == districtIndex && tile.owningPlayer.index == playerIndex) {
				return tile;
			}
		}
		return null;
	}

	@Override
	public void playbackGame(int[] actions) {

	}

	public boolean didPlayerWin(Player player) {

		if (player.victoryPoints >= victoryPointObjective) {
			for (Player otherPlayer : playerByInitiativeList) {
				if (otherPlayer != player && otherPlayer.victoryPoints >= player.victoryPoints) {
					return false;
				}
			}

			winner = player;

			printDescribeGame();
			if (printActivations) {
				printEvent(player.name + " won the game at the beginning of his turn with " + player.victoryPoints
						+ " victory points.");
			}
			return true;
		}
		return false;
	}

	@Override
	public void printDescribeGame() {
		if (printActivations) {
			printEvent(toString());
		}
	}

	public boolean hasWinner() {
		return winner != null;
	}

	@Override
	public void setPrintActivations(boolean printActivations) {
		this.printActivations = printActivations;
	}

	public byte incrementTurnCount() {
		roundNumber++;

		if (printActivations) {
			printEvent("");
			printEvent("Beginning of round " + roundNumber);
			printEvent("");
		}
		return roundNumber;
	}

	public boolean isFirstTurn() {
		return roundNumber > 1;
	}

	public void provideNightTemplePrayerPoints() {
		for (Player player : playerByInitiativeList) {
			List<Army> armyList = new ArrayList<>(player.armyList);
			for (Army army : armyList) {
				army.tile.activateTemple();
			}
		}
	}

	public void provideNightTempleVictoryPoints() {
		for (Player player : playerByInitiativeList) {
			byte controlledTempleCount = player.getControlledPaireableTempleCount();
			if (controlledTempleCount > 1) {
				player.addTemplePermanentVictoryPoint("the occupation of " + controlledTempleCount + " temples");
			}
		}
	}

	public void activateNightPowers() {

	}

	public void provideNightDiCards() {

	}

	public void provideNightPrayerPoints() {
		for (Player player : playerByInitiativeList) {
			player.modifyPrayerPoints((byte) 2, "night state");
		}

	}

	public void resetAvailableActions() {
		for (Player player : playerByInitiativeList) {
			player.resetAvailableActions();
		}

	}

	public void printEvent(String event) {
		if (printActivations) {
			LOGGER.info(event);
		}
	}

	public Tile getTileByName(String tileName) {
		for (Tile tile : tileList) {
			if (tile.name.equals(tileName)) {
				return tile;
			}
		}
		return null;
	}

	public Tile getTileByCopy(Tile tile) {
		if (tile == null) {
			return null;
		}
		return getTileByName(tile.name);
	}

	public Army getArmyByCopy(Army army) {
		if (army == null) {
			return null;
		}
		return getPlayerByCopy(army.owningPlayer).getArmyByCopy(army);
	}

	public void validate(KemetGame game) {
		if (game != this) {
			Validation.validationFailed("Game is wrong copy");
		}
	}

	public void validate(Tile tile) {
		Tile tileByCopy = getTileByCopy(tile);
		if (tileByCopy != tile) {
			Validation.validationFailed("Tile is wrong copy " + tile.name);
		}

	}

	@Override
	public void getInitialBoard() {

	}

	@Override
	public Pair<Integer, Integer> getBoardSize() {
		return new ImmutablePair<Integer, Integer>(BoardInventory.TOTAL_STATE_COUNT, 1);
	}

	@Override
	public int getActionSize() {
		return ChoiceInventory.TOTAL_CHOICE;
	}

	@Override
	public Game getNextState(int player, int actionIndex) {

		KemetGame deepCacheClone = deepCacheClone();
		deepCacheClone.activateAction(player, actionIndex);
		return deepCacheClone;
	}
	
	private static final int[] EMPTY = new int[] {};

	@Override
	public int[] getActivatedActions() {
		if( actions == null ) {
			return EMPTY;
		}
		return Arrays.copyOf(actions, recordActionIndex);
	}

	@Override
	public void replayMultipleActions(int[] actions) {
		for (int i = 0; i < actions.length; i++) {
			int j = actions[i];
			activateAction(getNextPlayer(), j);
		}
	}

	public void activateAction(Choice choice) {
		if (recordActionIndex < Options.GAME_TRACK_MAX_ACTION_COUNT) {
			actions[recordActionIndex++] = choice.getIndex();
		}

		choice.activate();

		// reset the cached choice to null
		nextPlayerChoicePick = null;
		canonicalForm = null;
		allValidMoves = null;
	}

	@Override
	public void activateAction(int player, int actionIndex) {

		if (actionIndex == -1) {
			return;
		}

		PlayerChoicePick currentPlayerChoicePick = getNextPlayerChoicePick();

		if (currentPlayerChoicePick.player.index != player) {
			LOGGER.warn("Next player index for action doesn't match");
		}

		for (Choice choice : currentPlayerChoicePick.choiceList) {
			if (choice.getIndex() == actionIndex) {

				activateAction(choice);
				return;
			}
		}

		String message = "Unable to find action that matches index : " + actionIndex;
		LOGGER.error(message);
		throw new IllegalArgumentException(message);
	}

	private PlayerChoicePick getNextPlayerChoicePick() {
		if (nextPlayerChoicePick == null) {
			nextPlayerChoicePick = action.getNextPlayerChoicePick();
		}

		return nextPlayerChoicePick;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		describeGame(builder);
		return builder.toString();
	}

	@Override
	public String describeAction(int i) {
		PlayerChoicePick nextPlayerChoicePick = getNextPlayerChoicePick();

		for (Choice choice : nextPlayerChoicePick.choiceList) {
			if (choice.getIndex() == i) {
				return choice.toString();
			}
		}
		return "invalid choice index " + i;
	}

	@Override
	public void printChoiceList() {
		PlayerChoicePick nextPlayerChoicePick = getNextPlayerChoicePick();

		for (Choice choice : nextPlayerChoicePick.choiceList) {
			printEvent(choice.toString());
		}
	}

	@Override
	public int getNextPlayer() {
		PlayerChoicePick nextPlayerChoicePick = getNextPlayerChoicePick();
		if (nextPlayerChoicePick == null) {
			return -1;
		}
		return nextPlayerChoicePick.player.index;
	}

	@Override
	public boolean[] getValidMoves() {
		if (allValidMoves == null) {
			allValidMoves = new boolean[ChoiceInventory.TOTAL_CHOICE];

			PlayerChoicePick nextPlayerChoicePick = getNextPlayerChoicePick();

			if (nextPlayerChoicePick != null) {
				List<Choice> choiceList = nextPlayerChoicePick.choiceList;
				for (Choice choice : choiceList) {
					allValidMoves[choice.getIndex()] = true;
				}
			} else {
				LOGGER.error("No next player choices for getValidMoves(), game must be ended.");
			}
		}

		return allValidMoves;
	}

	@Override
	public boolean isGameEnded() {
		getNextPlayerChoicePick();

		return winner != null;
	}

	@Override
	public int getGameEnded(int playerIndex) {
		getNextPlayerChoicePick();

		if (winner == null) {
			return 0;
		}
		if (winner.index == playerIndex) {
			return 1;
		}
		return -1;
	}

	@Override
	public ByteCanonicalForm getCanonicalForm(int playerIndex) {

		if (canonicalForm == null) {
			// ensure the board is moved to the latest state
			getNextPlayerChoicePick();

			canonicalForm = new KemetByteCanonicalForm(BoardInventory.TOTAL_STATE_COUNT);
			canonicalForm.set(BoardInventory.ROUND_NUMBER, roundNumber);

			// ACTION STATE
			action.fillCanonicalForm(canonicalForm, playerIndex);

			// TILE
			for (Tile tile : tileList) {
				tile.fillCanonicalForm(canonicalForm, playerIndex);
			}

			// PLAYER
			for (Player player : playerByInitiativeList) {
				player.fillCanonicalForm(canonicalForm, playerIndex);
			}

			canonicalForm.finalize();
		}

		return canonicalForm;
	}

	@Override
	public List<Game> getSymmetries(int playerIndex) {
		// ???
		return new ArrayList<>();
	}

//	@Override
//	public String stringRepresentation(int playerIndex) {
//		ByteCanonicalForm canonicalForm2 = getCanonicalForm(playerIndex);
//		return canonicalForm2.toCanonicalString();
//	}

	@Override
	public Game clone() {
		return deepCacheClone();
	}

	@Override
	public String getPlayerName(int playerIndex) {
		return getPlayerByIndex(playerIndex).name;
	}

	@Override
	public void setPlayerName(int playerIndex, String name) {
		getPlayerByIndex(playerIndex).name = name;
	}

	@Override
	public float getSimpleValue(int playerIndex, float predictedValue) {

		Player playerByIndex = getPlayerByIndex(playerIndex);

		Player opponent = getPlayerByIndex(Math.abs(playerIndex - 1));

		float pointDifference = Simulation.calculatePlayerScore(playerByIndex)
				- Simulation.calculatePlayerScore(opponent);
		float victoryPointsObjective = VICTORY_POINTS_OBJECTIVE * 1000;
		pointDifference = pointDifference / victoryPointsObjective;

		return pointDifference;
	}

}
