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
import kemet.util.CopyableRandom;
import kemet.util.Game;
import kemet.util.StackingMCTS;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class KemetGame implements Model, Game {

	public static final String AVAILABLE_DI_CARDS = "Available DI cards";
	public static final String DISCARDED_DI_CARDS = "Discarded DI cards";

	public static final int VICTORY_POINTS_OBJECTIVE = 8;

	private static final long serialVersionUID = 6738783849669850313L;

	private static final Logger LOGGER = LogManager.getLogger(KemetGame.class);

	public List<Tile> tileList = new ArrayList<>();
	public List<Player> playerByInitiativeList = new ArrayList<>();
	public byte[] availableDiCardList = new byte[DiCardList.TOTAL_DI_CARD_TYPE_COUNT];
	public byte[] discardedDiCardList = new byte[DiCardList.TOTAL_DI_CARD_TYPE_COUNT];
	public List<Power> availablePowerList = new ArrayList<>();
	public byte roundNumber = 0;
	public boolean victoryConditionTriggered = false;
	public byte victoryPointObjective = VICTORY_POINTS_OBJECTIVE;
	public Player winner = null;
	public GameAction action = null;
	public boolean printActivations = true;
	public int battleCount = 0;
	public int simulatedPlayerIndex = -1;

	public int recordActionIndex = 0;
	public int[] actions = null;
	{
		if (Options.GAME_TRACK_MAX_ACTION_COUNT > 0) {
			actions = new int[Options.GAME_TRACK_MAX_ACTION_COUNT];
		}
	}

	private static Cache<KemetGame> GAME_CACHE = new Cache<KemetGame>(() -> new KemetGame());

	public PlayerChoicePick nextPlayerChoicePick;

	public ByteCanonicalForm canonicalForm;

	private boolean[] allValidMoves;
	
	public CopyableRandom random = new CopyableRandom();

	public StackingMCTS simulationMcts;

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
		simulationMcts = null;
		action = GameAction.create(this);
		printActivations = true;
		battleCount = 0;
		simulatedPlayerIndex = -1;

		tileList.clear();
		playerByInitiativeList.clear();
		DiCardList.initializeGame(availableDiCardList);
		DiCardList.fillArray(discardedDiCardList, (byte) 0);
		availablePowerList.clear();

		PowerList.initializeGame(this);

		recordActionIndex = 0;
		if (actions != null) {
			Arrays.fill(actions, -1);
		}
	}

	@Override
	public KemetGame deepCacheClone() {

		KemetGame clone = GAME_CACHE.create();
		
		clone.random.copyFrom(random);

		clone.tileList.clear();
		for (Tile tile : tileList) {
			clone.tileList.add((Tile) tile.deepCacheClone());
		}

		clone.playerByInitiativeList.clear();
		for (Player player : playerByInitiativeList) {
			clone.playerByInitiativeList.add((Player) player.deepCacheClone());
		}

		DiCardList.copyArray(availableDiCardList, clone.availableDiCardList);
		DiCardList.copyArray(discardedDiCardList, clone.discardedDiCardList);

		clone.availablePowerList.clear();
		clone.availablePowerList.addAll(availablePowerList);

		clone.roundNumber = roundNumber;
		clone.victoryConditionTriggered = victoryConditionTriggered;
		clone.victoryPointObjective = victoryPointObjective;
		clone.printActivations = printActivations;

		clone.battleCount = battleCount;
		clone.simulatedPlayerIndex = simulatedPlayerIndex;

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
		clone.simulationMcts = simulationMcts;

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
		availablePowerList.clear();
		winner = null;

		action = null;

		simulationMcts = null;

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

		validateDiCardCount();
	}

	private void validateDiCardCount() {
		int count = 0;

		count += DiCardList.sumArray(availableDiCardList);
		count += DiCardList.sumArray(discardedDiCardList);

		for (Player player : playerByInitiativeList) {
			count += DiCardList.sumArray(player.diCards);
		}

		if (count != DiCardList.TOTAL_DI_COUNT) {
			Validation.validationFailed(
					"Total DI card in play is wrong. Expected " + DiCardList.TOTAL_DI_COUNT + " but got " + count);
		}

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
			if (player.getIndex() == index) {
				return player;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param index The player by initiative index, starting at zero to
	 *              number_of_player -1.
	 * @return The player.
	 */
	public Player getPlayerByInitiativeIndex(int index) {
		return playerByInitiativeList.get(index);
	}

	public Tile getTileByPlayerAndDistrictIndex(int playerIndex, int districtIndex) {
		for (Tile tile : tileList) {
			if (tile.districtIndex == districtIndex && tile.owningPlayer.getIndex() == playerIndex) {
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
				if (otherPlayer != player && otherPlayer.victoryPoints > player.victoryPoints) {
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
		return roundNumber <= 1;
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

	public void provideNightDiCards() {

		for (Player player : playerByInitiativeList) {
			DiCardList.moveRandomDiCard(availableDiCardList, player.diCards, AVAILABLE_DI_CARDS, player.name,
					"Night DI Card", this, true);

			if (player.hasPower(PowerList.WHITE_2_DIVINE_BOON)) {
				DiCardList.moveRandomDiCard(availableDiCardList, player.diCards, AVAILABLE_DI_CARDS, player.name,
						PowerList.WHITE_2_DIVINE_BOON.name, this, true);
			}

			if (player.hasPower(PowerList.WHITE_4_MUMMY)) {
				DiCardList.moveRandomDiCard(availableDiCardList, player.diCards, AVAILABLE_DI_CARDS, player.name,
						PowerList.WHITE_4_MUMMY.name, this, true);
			}
		}

	}

	public void provideNightPrayerPoints() {
		for (Player player : playerByInitiativeList) {

			byte points = player.getNightPrayerPoints();

			player.modifyPrayerPoints(points, "night state");
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
		if (actions == null) {
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
		int index = choice.getIndex();

		if (recordActionIndex < Options.GAME_TRACK_MAX_ACTION_COUNT && index >= 0) {
			if (isSimulation()) {
				actions[recordActionIndex++] = index + 1000;
			} else {
				actions[recordActionIndex++] = index;
			}
		}

		resetCachedChoices();

		choice.activate();

		resetCachedChoices();

	}

	public boolean isSimulation() {
		return simulatedPlayerIndex != -1;
	}

	public void resetCachedChoices() {
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

		if (currentPlayerChoicePick.player.getIndex() != player) {
			LOGGER.warn("Next player index for action doesn't match");
		}

		for (Choice choice : currentPlayerChoicePick.choiceList) {
			if (choice.getIndex() == actionIndex) {

				activateAction(choice);
				return;
			}
		}

		String message = "Unable to find action that matches index : " + actionIndex + "\n" + "Choice List :"
				+ printChoiceList(currentPlayerChoicePick) + "\nPlayer :\n"
				+ currentPlayerChoicePick.player.describePlayer();
		LOGGER.error(message);
		throw new IllegalArgumentException(message);
	}

	private String printChoiceList(PlayerChoicePick currentPlayerChoicePick) {
		StringBuilder build = new StringBuilder();
		List<Choice> choiceList = currentPlayerChoicePick.choiceList;
		for (Choice choice : choiceList) {
			build.append("\n\t");
			build.append(choice.toString());
		}
		return build.toString();
	}

	public PlayerChoicePick getNextPlayerChoicePick() {
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
		return nextPlayerChoicePick.player.getIndex();
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
		if (winner.getIndex() == playerIndex) {
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

			// DI CARDS
			DiCardList.fillCanonicalForm(discardedDiCardList, canonicalForm, BoardInventory.DI_DISCARD);

			canonicalForm.finalize();
		}

		return canonicalForm;
	}

	@Override
	public List<Game> getSymmetries(int playerIndex) {
		return new ArrayList<>();
	}

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

	@Override
	public void enterSimulationMode(int playerIndex, StackingMCTS mcts) {
		resetCachedChoices();

		simulatedPlayerIndex = playerIndex;
		simulationMcts = mcts;

		for (Player player : playerByInitiativeList) {
			player.enterSimulationMode(playerIndex);
		}
	}

	/**
	 * 
	 * @param index the player index
	 * @return the order of the player in the game, zero based.
	 */
	public int getPlayerOrder(int index) {
		for (int i = 0; i < playerByInitiativeList.size(); ++i) {
			if (playerByInitiativeList.get(i).getIndex() == index) {
				return i;
			}
		}

		log.error("Couldn't determine player order of player index {}", index);
		return -1;
	}

	public void movePowerToPlayer(Player player, Power power) {
		if (power == null) {
			return;
		}

		Power removedPower = availablePowerList.set(power.index, null);
		if (removedPower == null) {
			log.error("Attempted to mover power {} to player {} but it's already gone.", power, player);
			throw new IllegalStateException("Invalid power to move");
		}

		player.powerList.add(power);

		power.applyToPlayer(player);

	}

	public void resetDiCards() {
		
		resetCachedChoices();
		
		if (printActivations) {
			printEvent("DI Card reset triggered.");
		}

		for (Player player : playerByInitiativeList) {
			DiCardList.fillArray(player.diCards, (byte) 0);
		}
		DiCardList.fillArray(discardedDiCardList, (byte) 0);
		DiCardList.initializeGame(availableDiCardList);
	}

	public void moveDiscardedDiCardsToAvailableDiCardList() {
		if (printActivations) {
			printEvent(
					"Moving all discarded DI cards back to available DI card stack because available DI cards stack was empty.");
		}

		boolean oldPrintActivation = printActivations;
		printActivations = false;
		DiCardList.moveAllDiCard(discardedDiCardList, availableDiCardList, "Discard DI cards", AVAILABLE_DI_CARDS,
				"Reset DI cards.", this);
		printActivations = oldPrintActivation;
	}

	public void giveDiCardToPlayer(DiCard cardToMove, Player redPlayer) {
		DiCardList.moveDiCard(availableDiCardList, redPlayer.diCards, cardToMove.index, AVAILABLE_DI_CARDS,
				redPlayer.name, "Give DI card", this);

	}
}
