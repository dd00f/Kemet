package kemet.model;

import java.util.ArrayList;
import java.util.List;

import kemet.ai.PlayerActor;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Player implements Model {

	public static final int ACTION_TOKEN_COUNT = 5;

	private static final int INITIAL_ARMY_TOKEN = 12;

	public static final int MAXIMUM_PRAYER_POINTS = 11;

	/**
	 * 
	 */
	private static final long serialVersionUID = 233743569229589016L;

	public String name;

	/**
	 * zero based player index in the game
	 */
	private int index;
	public KemetGame game;

	public byte victoryPoints = 0;
	public byte battlePoints = 0;
	public byte templePermanentPoints = 0;
	public byte templeOccupationPoints = 0;
	public byte highLevelPyramidOccupationPoints = 0;
	public byte initiativeTokens = 0;
	private byte prayerPoints = 5;
	public byte maximumArmySize = 5;
	public byte strengthBonus = 0;
	public byte attackBonus = 0;
	public byte defenseBonus = 0;
	public byte damageBonus = 0;
	public byte shieldBonus = 0;
	public byte moveCapacity = 1;
	public byte availableArmyTokens = INITIAL_ARMY_TOKEN;
	public short armyCounter = 1;

	public List<Power> powerList = new ArrayList<>();
	public List<BattleCard> availableBattleCards = new ArrayList<>();
	public List<BattleCard> usedBattleCards = new ArrayList<>();
	public List<BattleCard> discardedBattleCards = new ArrayList<>();
	public byte[] diCards = new byte[DiCardList.TOTAL_DI_CARD_TYPE_COUNT];
	public List<Beast> availableBeasts = new ArrayList<>();

	public boolean rowOneMoveUsed = false;
	public boolean rowOneRecruitUsed = false;
	public boolean rowTwoMoveUsed = false;
	public boolean rowTwoUpgradePyramidUsed = false;
	public boolean rowTwoPrayUsed = false;
	public boolean rowThreePrayUsed = false;
	public boolean rowThreeBuildWhiteUsed = false;
	public boolean rowThreeBuildRedUsed = false;
	public boolean rowThreeBuildBlueUsed = false;
	public boolean rowThreeBuildBlackUsed = false;

	public byte actionTokenLeft = ACTION_TOKEN_COUNT;
	public boolean goldTokenUsed = false;
	public boolean goldTokenAvailable = false;
	public boolean silverTokenUsed = false;
	public boolean silverTokenAvailable = false;

	public Tile cityFront = null;
	public List<Tile> cityTiles = new ArrayList<>();
	public List<Army> armyList = new ArrayList<>();
	public List<Army> destroyedArmyList = new ArrayList<>();

	public PlayerActor actor;

	private static Cache<Player> CACHE = new Cache<Player>(() -> new Player());

	public static Player create() {
		Player create = CACHE.create();
		create.initialize();
		return create;
	}

	private Player() {
		initialize();
	}

	public boolean hasPower(Power powerToCheck) {
		if (powerToCheck == null) {
			return false;
		}

		for (Power power : powerList) {
			if (power.name.equals(powerToCheck.name)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void initialize() {

		name = null;
		game = null;
		cityFront = null;
		victoryPoints = 0;
		battlePoints = 0;
		templePermanentPoints = 0;
		templeOccupationPoints = 0;
		highLevelPyramidOccupationPoints = 0;
		initiativeTokens = 0;
		prayerPoints = 5;
		maximumArmySize = 5;
		strengthBonus = 0;
		attackBonus = 0;
		defenseBonus = 0;
		damageBonus = 0;
		shieldBonus = 0;
		setIndex(0);
		moveCapacity = 1;
		availableArmyTokens = 12;
		armyCounter = 1;

		powerList.clear();
		availableBattleCards.clear();
		usedBattleCards.clear();
		discardedBattleCards.clear();
		DiCardList.fillArray(diCards, (byte) 0);
		availableBeasts.clear();
		cityTiles.clear();
		armyList.clear();
		destroyedArmyList.clear();

		rowOneMoveUsed = false;
		rowOneRecruitUsed = false;
		rowTwoMoveUsed = false;
		rowTwoUpgradePyramidUsed = false;
		rowTwoPrayUsed = false;
		rowThreePrayUsed = false;
		rowThreeBuildWhiteUsed = false;
		rowThreeBuildRedUsed = false;
		rowThreeBuildBlueUsed = false;
		rowThreeBuildBlackUsed = false;

		actionTokenLeft = 5;
		goldTokenUsed = false;
		goldTokenAvailable = false;
		silverTokenUsed = false;
		silverTokenAvailable = false;

		createBattleCards();
	}

	@Override
	public Model deepCacheClone() {

		Player clone = CACHE.create();

		clone.game = game;
		clone.name = name;
		clone.actor = actor;
		clone.setIndex(index);
		clone.cityFront = cityFront;

		clone.victoryPoints = victoryPoints;
		clone.battlePoints = battlePoints;
		clone.templePermanentPoints = templePermanentPoints;
		clone.templeOccupationPoints = templeOccupationPoints;
		clone.highLevelPyramidOccupationPoints = highLevelPyramidOccupationPoints;
		clone.initiativeTokens = initiativeTokens;
		clone.prayerPoints = prayerPoints;
		clone.maximumArmySize = maximumArmySize;
		clone.strengthBonus = strengthBonus;
		clone.attackBonus = attackBonus;
		clone.defenseBonus = defenseBonus;
		clone.damageBonus = damageBonus;
		clone.shieldBonus = shieldBonus;
		clone.moveCapacity = moveCapacity;
		clone.availableArmyTokens = availableArmyTokens;
		clone.armyCounter = armyCounter;
		clone.rowOneMoveUsed = rowOneMoveUsed;
		clone.rowOneRecruitUsed = rowOneRecruitUsed;
		clone.rowTwoMoveUsed = rowTwoMoveUsed;
		clone.rowTwoUpgradePyramidUsed = rowTwoUpgradePyramidUsed;
		clone.rowTwoPrayUsed = rowTwoPrayUsed;
		clone.rowThreePrayUsed = rowThreePrayUsed;
		clone.rowThreeBuildWhiteUsed = rowThreeBuildWhiteUsed;
		clone.rowThreeBuildRedUsed = rowThreeBuildRedUsed;
		clone.rowThreeBuildBlueUsed = rowThreeBuildBlueUsed;
		clone.rowThreeBuildBlackUsed = rowThreeBuildBlackUsed;
		clone.actionTokenLeft = actionTokenLeft;
		clone.goldTokenUsed = goldTokenUsed;
		clone.goldTokenAvailable = goldTokenAvailable;
		clone.silverTokenUsed = silverTokenUsed;
		clone.silverTokenAvailable = silverTokenAvailable;
		clone.availableArmyTokens = availableArmyTokens;
		clone.availableArmyTokens = availableArmyTokens;

		clone.cityTiles.clear();
		clone.cityTiles.addAll(cityTiles);
		clone.armyList.clear();

		for (Army army : armyList) {
			clone.armyList.add(army.deepCacheClone());
		}

		clone.destroyedArmyList.clear();

		for (Army army : destroyedArmyList) {
			clone.destroyedArmyList.add(army.deepCacheClone());
		}

		clone.powerList.clear();
		clone.powerList.addAll(powerList);
		clone.availableBattleCards.clear();
		clone.availableBattleCards.addAll(availableBattleCards);
		clone.usedBattleCards.clear();
		clone.usedBattleCards.addAll(usedBattleCards);
		clone.usedBattleCards.clear();
		clone.usedBattleCards.addAll(usedBattleCards);
		clone.discardedBattleCards.clear();
		clone.discardedBattleCards.addAll(discardedBattleCards);

		DiCardList.copyArray(diCards, clone.diCards);
		clone.availableBeasts.clear();
		clone.availableBeasts.addAll(availableBeasts);

		return clone;
	}

	@Override
	public void release() {

		cityFront = null;
		game = null;
		for (Army army : armyList) {
			army.release();
		}

		for (Army army : destroyedArmyList) {
			army.release();
		}

		cityTiles.clear();
		armyList.clear();
		destroyedArmyList.clear();
		powerList.clear();
		availableBattleCards.clear();
		usedBattleCards.clear();
		usedBattleCards.clear();
		discardedBattleCards.clear();
		availableBeasts.clear();

		CACHE.release(this);
	}

	public void relink(KemetGame game) {
		this.game = game;
		cityFront = game.getTileByCopy(cityFront);
		for (int i = 0; i < cityTiles.size(); i++) {
			cityTiles.set(i, game.getTileByCopy(cityTiles.get(i)));
		}

		for (Army army : armyList) {
			army.relink(game);
		}

		for (Army army : destroyedArmyList) {
			army.relink(game);
		}
	}

	private void createBattleCards() {
		availableBattleCards.add(BattleCard.SACRIFICIAL_CHARGE_CARD);
		availableBattleCards.add(BattleCard.DEFENSIVE_RETREAT_CARD);
		availableBattleCards.add(BattleCard.PHALANX_DEFENSE_CARD);
		availableBattleCards.add(BattleCard.CAVALRY_BLITZ_CARD);
		availableBattleCards.add(BattleCard.CHARIOT_RAID_CARD);
		availableBattleCards.add(BattleCard.SHIELD_PUSH_CARD);
		availableBattleCards.add(BattleCard.MIXED_TACTICS_CARD);
		availableBattleCards.add(BattleCard.FERVENT_PURGE_CARD);
	}

	public boolean isSamePlayer(Player otherPlayer) {
		return name.equals(otherPlayer.name);
	}

	public Army createArmy() {

		Army modifiedArmy = null;

		if (destroyedArmyList.size() > 0) {
			modifiedArmy = destroyedArmyList.remove(destroyedArmyList.size() - 1);
			String armyName = modifiedArmy.name;
			modifiedArmy.initialize();
			modifiedArmy.name = armyName;
		} else {
			modifiedArmy = Army.create();
			modifiedArmy.name = getNextArmyName();
			this.armyCounter++;
		}

		modifiedArmy.owningPlayer = this;
		armyList.add(modifiedArmy);

		return modifiedArmy;
	}

	public String getNextArmyName() {
		return this.name + " army " + this.armyCounter;
	}

	public byte getPrayerPoints() {
		return prayerPoints;
	}

	public void modifyPrayerPoints(byte modification, Object reason) {
		if (modification == 0) {
			return;
		}

		byte initial = prayerPoints;
		prayerPoints += modification;
		if (prayerPoints > MAXIMUM_PRAYER_POINTS) {
			prayerPoints = MAXIMUM_PRAYER_POINTS;
		}
		if (prayerPoints < 0) {
			log.warn("Player {} managed to reach {} prayer points.", name, prayerPoints);
			prayerPoints = 0;
		}
		if (game.printActivations) {
			game.printEvent("Player " + name + " modified prayer points by " + modification + " due to " + reason
					+ ". From " + initial + " to " + prayerPoints + " prayer points.");
		}
	}

	public boolean hasPyramid(Color color) {

		for (Tile tile : cityTiles) {
			if (tile.pyramidColor == color) {
				return true;
			}
		}

		for (Army army : armyList) {
			if (army.tile != null && army.tile.pyramidColor == color) {
				return true;
			}
		}

		return false;
	}

	public byte getUsedRowCount() {
		byte retVal = 0;
		if (isRowOneUsed()) {
			retVal++;
		}
		if (isRowTwoUsed()) {
			retVal++;
		}
		if (isRowThreeUsed()) {
			retVal++;
		}
		return retVal;
	}

	public boolean isRowOneUsed() {
		return rowOneMoveUsed || rowOneRecruitUsed;
	}

	public boolean isRowTwoUsed() {
		return rowTwoMoveUsed || rowTwoPrayUsed || rowTwoUpgradePyramidUsed;
	}

	public boolean isRowThreeUsed() {
		return rowThreeBuildBlackUsed || rowThreeBuildBlueUsed || rowThreeBuildRedUsed || rowThreeBuildWhiteUsed
				|| rowThreePrayUsed;
	}

	public boolean canTeleport() {
		return prayerPoints >= getTeleportCost();
	}

	public void useBattleCard(BattleCard card) {
		availableBattleCards.remove(card);
		usedBattleCards.add(card);

		validateCardCount();
	}

	public void checkToRecuperateAllBattleCards() {

		// half of all cards are used
		if (usedBattleCards.size() >= BattleCard.CARD_COUNT / 2) {
			if (game.printActivations) {
				game.printEvent("Player " + name + " recuperates all his battle cards.");
			}
			recuperateAllBattleCards();
		}
	}

	public void recuperateAllBattleCards() {
		availableBattleCards.addAll(usedBattleCards);
		availableBattleCards.addAll(discardedBattleCards);
		discardedBattleCards.clear();
		usedBattleCards.clear();

		validateCardCount();
	}

	public void discardBattleCard(BattleCard card) {
		availableBattleCards.remove(card);
		discardedBattleCards.add(card);

		validateCardCount();
	}

	public void addDawnToken() {
		initiativeTokens++;
	}

	public void addBattleVictoryPoint(String reason) {
		if (game.printActivations) {
			game.printEvent("Army " + name + " won a permanent battle victory point : " + reason);
		}
		victoryPoints++;
		battlePoints++;
	}

	public void resetAvailableActions() {

		rowOneMoveUsed = false;
		rowOneRecruitUsed = false;
		rowTwoMoveUsed = false;
		rowTwoUpgradePyramidUsed = false;
		rowTwoPrayUsed = false;
		rowThreePrayUsed = false;
		rowThreeBuildWhiteUsed = false;
		rowThreeBuildRedUsed = false;
		rowThreeBuildBlueUsed = false;
		rowThreeBuildBlackUsed = false;

		actionTokenLeft = 5;
		goldTokenUsed = false;
		silverTokenUsed = false;
	}

	public byte getControlledPaireableTempleCount() {
		byte templeCount = 0;
		for (Army army : armyList) {
			if (army.tile != null && army.tile.hasTemple && army.tile.templePaireable) {
				templeCount++;
			}
		}
		return templeCount;

	}

	public byte countUsedActions() {
		byte retVal = 0;

		if (rowOneMoveUsed) {
			retVal++;
		}
		if (rowOneRecruitUsed) {
			retVal++;
		}
		if (rowTwoMoveUsed) {
			retVal++;
		}
		if (rowTwoUpgradePyramidUsed) {
			retVal++;
		}
		if (rowTwoPrayUsed) {
			retVal++;
		}
		if (rowThreePrayUsed) {
			retVal++;
		}
		if (rowThreeBuildWhiteUsed) {
			retVal++;
		}
		if (rowThreeBuildRedUsed) {
			retVal++;
		}
		if (rowThreeBuildBlueUsed) {
			retVal++;
		}
		if (rowThreeBuildBlackUsed) {
			retVal++;
		}
		return retVal;
	}

	public void addTemplePermanentVictoryPoint(String reason) {
		if (game.printActivations) {
			game.printEvent(name + " gained one permanent temple victory point due to " + reason);
		}
		victoryPoints++;
		templePermanentPoints++;
	}

	public void validate(KemetGame currentGame) {
		currentGame.validate(this);

		currentGame.validate(game);

		validateCardCount();

		for (Army army : armyList) {
			currentGame.validate(army);
			if (army.owningPlayer != this) {
				Validation.validationFailed("Army owner is wrong player " + name + " for army " + army.name);
			}
		}

		for (Tile tile : cityTiles) {
			currentGame.validate(tile);
			if (tile.owningPlayer != this) {
				Validation.validationFailed("Tile owner is wrong player " + name + " for tile " + tile.name);
			}
		}

		int usedActions = countUsedActions() + actionTokenLeft;
		int availableActions = 5;
		if (silverTokenAvailable) {
			availableActions += 1;
			if (!silverTokenUsed) {
				usedActions += 1;
			}
		}

		if (usedActions != availableActions) {
			Validation.validationFailed("Used actions " + usedActions
					+ " is not equal to the number of available actions " + availableActions);
		}

		if (prayerPoints < 0) {
			Validation.validationFailed("Prayer points below zero.");
		}

		if (prayerPoints > 11) {
			Validation.validationFailed("Prayer points above 11.");
		}

		for (Army army : armyList) {
			if (army.tile == null) {
				// army in transition
				// Validation.validationFailed("Army without tile found " + army.name);
			} else {
				if (army != army.tile.getArmy()) {
					Validation.validationFailed("Army " + army.name + " thinks its on tile " + army.tile.name
							+ " but tile doesnt think so.");
				}
			}

			if (army.armySize <= 0) {
				Validation.validationFailed("Army size zero found " + army.name);
			}

			if (army.armySize > maximumArmySize) {
				Validation.validationFailed("Army size bigger than max " + army.name + " of size " + army.armySize);
			}
		}

	}

	public void validateCardCount() {
		int cardCount = availableBattleCards.size() + usedBattleCards.size() + discardedBattleCards.size();
		if (cardCount != 8) {
			Validation.validationFailed("Battle card count not equals 8, is " + cardCount);
		}
	}

	public void increasePyramidOccupationPoints(Tile tile) {
		if (game.printActivations) {
			game.printEvent(name + " now occupies level " + tile.getPyramidLevel() + " pyramid on tile " + tile.name
					+ ". Gained one temporary victory points.");
		}
		highLevelPyramidOccupationPoints++;
		victoryPoints++;

	}

	public void decreasePyramidOccupationPoints(Tile tile) {
		if (game.printActivations) {
			game.printEvent(name + " no longer occupies level " + tile.getPyramidLevel() + " pyramid on tile "
					+ tile.name + ". Lost one temporary victory points.");
		}
		highLevelPyramidOccupationPoints--;
		victoryPoints--;

	}

	public void increaseTempleOccupationPoints(Tile tile) {
		if (game.printActivations) {
			game.printEvent(
					name + " now occupies temple on tile " + tile.name + ". Gained one temporary victory points.");
		}
		templeOccupationPoints++;
		victoryPoints++;

	}

	public void decreaseTempleOccupationPoints(Tile tile) {
		if (game.printActivations) {
			game.printEvent(name + " no longer occupies a temple on tile " + tile.name
					+ ". Lost one temporary victory points.");
		}
		templeOccupationPoints--;
		victoryPoints--;

	}

	public void describePlayer(StringBuilder builder) {
		builder.append("Player : " + name);
		builder.append("\n");
		builder.append("\t" + prayerPoints + " prayer points.");
		builder.append("\n");
		builder.append("\t" + victoryPoints + " victory points.");
		builder.append("\n");
		builder.append("\t" + templeOccupationPoints + " temple occupation points.");
		builder.append("\n");
		builder.append("\t" + templePermanentPoints + " temple permanent points.");
		builder.append("\n");
		builder.append("\t" + battlePoints + " battle points.");
		builder.append("\n");
		builder.append("\t" + highLevelPyramidOccupationPoints + " high level pyramid occupation points.");
		builder.append("\n");
		builder.append("\t" + initiativeTokens + " initiative tokens.");
		builder.append("\n");

		builder.append("\tPyramids : ");
		boolean first = true;
		for (Tile tile : cityTiles) {
			if (tile.getPyramidLevel() > 0) {
				if (first) {
					first = false;
				} else {
					builder.append(", ");
				}

				builder.append(tile.pyramidColor);
				builder.append(" ");
				builder.append(tile.getPyramidLevel());
			}
		}
		builder.append("\n");

		builder.append("\tPowers : ");
		first = true;
		for (Power power : powerList) {
			if (first) {
				first = false;
			} else {
				builder.append(", ");
			}

			builder.append(power.name);
		}
		builder.append("\n");

		builder.append("\tAvailable Beasts : ");
		first = true;
		for (Beast beast : availableBeasts) {
			if (first) {
				first = false;
			} else {
				builder.append(", ");
			}

			builder.append(beast.name);
		}
		builder.append("\n");

		builder.append("\tUsed Battle Cards  : ");
		for (BattleCard used : usedBattleCards) {
			builder.append(" ");
			builder.append(used.index);
		}
		builder.append("\n");

		builder.append("\tDiscard Battle Cards  : ");
		for (BattleCard used : discardedBattleCards) {
			builder.append(" ");
			builder.append(used.index);
		}
		builder.append("\n");

		builder.append("\tAvailable Battle Cards  : ");
		for (BattleCard used : availableBattleCards) {
			builder.append(" ");
			builder.append(used.index);
		}
		builder.append("\n");

		for (Army army : armyList) {
			army.describeArmy(builder);
		}

	}

	public boolean canRecruit() {
		return availableArmyTokens > 0 || availableBeasts.size() > 0;
	}

	public BattleCard getBattleCard(String cardName) {
		for (BattleCard card : availableBattleCards) {
			if (card.name.equals(cardName)) {
				return card;
			}
		}
		return null;
	}

	public void recoverAllDiscardedBattleCards() {
		availableBattleCards.addAll(discardedBattleCards);
		discardedBattleCards.clear();

		validateCardCount();
	}

	public void recoverAllUsedBattleCards() {
		availableBattleCards.addAll(usedBattleCards);
		usedBattleCards.clear();

		validateCardCount();
	}

	public void returnUsedBattleCard(BattleCard attackingUsedBattleCard) {
		availableBattleCards.add(attackingUsedBattleCard);
		usedBattleCards.remove(attackingUsedBattleCard);

		validateCardCount();
	}

	public Army getArmyByCopy(Army armyCopy) {
		if (armyCopy == null) {
			return null;
		}

		for (Army army : armyList) {
			if (army.name.equals(armyCopy.name)) {
				return army;
			}
		}

		for (Army army : destroyedArmyList) {
			if (army.name.equals(armyCopy.name)) {
				return army;
			}
		}
		return null;
	}

	public byte getState(int playerIndex) {
		if (playerIndex == getIndex()) {
			return 1;
		}
		return -1;
	}

	/**
	 * 
	 * @param targetPlayerIndex index of the player for who the canonical form is
	 *                          generated.
	 * @return the adjusted index of the current player so that the target player is
	 *         always at index zero.
	 */
	public int getCanonicalPlayerIndex(int targetPlayerIndex) {
		if (targetPlayerIndex == getIndex()) {
			return 0;
		}

		// otherwise, increment our own index, unless we are bigger than the target
		// player index.

		// 2 player game
		// target : 0, current 0 - return 0
		// target : 0, current 1 - return 1
		// target : 1, current 0 - return 1
		// target : 1, current 1 - return 0

		// 3 player game example,
		// target : 0, current 0 - return 0
		// target : 0, current 1 - return 1
		// target : 0, current 2 - return 2
		// target : 1, current 0 - return 1
		// target : 1, current 1 - return 0
		// target : 1, current 2 - return 2
		// target : 2, current 0 - return 1
		// target : 2, current 1 - return 2
		// target : 2, current 2 - return 0

		if (targetPlayerIndex < getIndex()) {
			return getIndex();
		}
		return getIndex() + 1;

	}

	@Override
	public String toString() {
		return "Player " + name + " index " + getIndex();
	}

	public void fillCanonicalForm(ByteCanonicalForm canonicalForm, int playerIndex) {

		int canonicalPlayerIndex = getCanonicalPlayerIndex(playerIndex);

		canonicalForm.set(BoardInventory.PLAYER_VICTORY_POINTS + canonicalPlayerIndex, victoryPoints);
		canonicalForm.set(BoardInventory.PLAYER_BATTLE_POINTS + canonicalPlayerIndex, battlePoints);
		canonicalForm.set(BoardInventory.PLAYER_PRAYER_POINTS + canonicalPlayerIndex, prayerPoints);
		canonicalForm.set(BoardInventory.PLAYER_AVAILABLE_ARMY_TOKENS + canonicalPlayerIndex, availableArmyTokens);
		canonicalForm.set(BoardInventory.PLAYER_ROW_ONE_MOVE_USED + canonicalPlayerIndex,
				(byte) (rowOneMoveUsed ? 1 : 0));
		canonicalForm.set(BoardInventory.PLAYER_ROW_ONE_RECRUIT_USED + canonicalPlayerIndex,
				(byte) (rowOneRecruitUsed ? 1 : 0));
		canonicalForm.set(BoardInventory.PLAYER_ROW_TWO_MOVE_USED + canonicalPlayerIndex,
				(byte) (rowTwoMoveUsed ? 1 : 0));
		canonicalForm.set(BoardInventory.PLAYER_ROW_TWO_UPGRADE_PYRAMID_USED + canonicalPlayerIndex,
				(byte) (rowTwoUpgradePyramidUsed ? 1 : 0));
		canonicalForm.set(BoardInventory.PLAYER_ROW_TWO_PRAY_USED + canonicalPlayerIndex,
				(byte) (rowTwoPrayUsed ? 1 : 0));
		canonicalForm.set(BoardInventory.PLAYER_ROW_THREE_PRAY_USED + canonicalPlayerIndex,
				(byte) (rowThreePrayUsed ? 1 : 0));
		canonicalForm.set(BoardInventory.PLAYER_ROW_THREE_BUILD_WHITE_USED + canonicalPlayerIndex,
				(byte) (rowThreeBuildWhiteUsed ? 1 : 0));
		canonicalForm.set(BoardInventory.PLAYER_ROW_THREE_BUILD_RED_USED + canonicalPlayerIndex,
				(byte) (rowThreeBuildRedUsed ? 1 : 0));
		canonicalForm.set(BoardInventory.PLAYER_ROW_THREE_BUILD_BLUE_USED + canonicalPlayerIndex,
				(byte) (rowThreeBuildBlueUsed ? 1 : 0));
		canonicalForm.set(BoardInventory.PLAYER_ROW_THREE_BUILD_BLACK_USED + canonicalPlayerIndex,
				(byte) (rowThreeBuildBlackUsed ? 1 : 0));
		canonicalForm.set(BoardInventory.PLAYER_ACTION_TOKEN_LEFT + canonicalPlayerIndex, actionTokenLeft);
		canonicalForm.set(BoardInventory.PLAYER_TEMPLE_COUNT + canonicalPlayerIndex, templeOccupationPoints);
		canonicalForm.set(BoardInventory.PLAYER_DAWN_TOKEN + canonicalPlayerIndex, initiativeTokens);
		canonicalForm.set(BoardInventory.PLAYER_ORDER + canonicalPlayerIndex * BoardInventory.PLAYER_COUNT
				+ game.getPlayerOrder(getIndex()), (byte) 1);

		if (silverTokenAvailable) {
			canonicalForm.set(BoardInventory.PLAYER_SILVER_TOKEN_USED + canonicalPlayerIndex,
					(byte) (silverTokenUsed ? 0 : 1));
		}
		if (goldTokenAvailable) {
			canonicalForm.set(BoardInventory.PLAYER_GOLD_TOKEN_USED + canonicalPlayerIndex,
					(byte) (goldTokenUsed ? 0 : 1));
		}

		for (BattleCard card : availableBattleCards) {
			canonicalForm.set(getCardStatusIndex(canonicalPlayerIndex, card), (byte) 1);
		}

		for (BattleCard card : usedBattleCards) {
			canonicalForm.set(getCardStatusIndex(canonicalPlayerIndex, card), (byte) -1);
		}

		for (Beast beast : availableBeasts) {
			int beastAvailableIndex = BoardInventory.BEAST_AVAILABLE + BeastList.BEAST_INDEXER * canonicalPlayerIndex
					+ beast.index;
			canonicalForm.set(beastAvailableIndex, (byte) 1);
		}

		byte discardCardStatus = -1;

		if (playerIndex != getIndex()) {
			// make discarded cards appear available only for other players.
			discardCardStatus = 1;
		} else {
			// Fill DI cards only if we are the current player, hide for other players
			DiCardList.fillCanonicalForm(diCards, canonicalForm, BoardInventory.CURRENT_PLAYER_DI);
		}

		for (BattleCard card : discardedBattleCards) {
			canonicalForm.set(getCardStatusIndex(canonicalPlayerIndex, card), discardCardStatus);
		}

		for (Power power : powerList) {
			int powerIndex = (BoardInventory.PLAYER_POWERS + power.index * BoardInventory.PLAYER_COUNT
					+ canonicalPlayerIndex);
			canonicalForm.set(powerIndex, (byte) 1);
		}

		// fill DI card count
		canonicalForm.set(BoardInventory.DI_CARD_PER_PLAYER + canonicalPlayerIndex, DiCardList.sumArray(diCards));

	}

	public static int getCardStatusIndex(int canonicalPlayerIndex, BattleCard card) {
		return BoardInventory.PLAYER_BATTLE_CARD_AVALIABLE + canonicalPlayerIndex * BattleCard.INDEXER + card.index;
	}

	public void enterSimulationMode(int playerIndex) {
		if (getIndex() != playerIndex) {
			// return all discard battle cards for simulations to act as if those cards were
			// available.
			recoverAllDiscardedBattleCards();
		}

	}

	public void removeInitiativeToken(byte tokenCount) {
		initiativeTokens -= tokenCount;
		if (initiativeTokens < 0) {
			log.error("Initiative token was consumed that lead to a resulting count of {}", initiativeTokens);
			initiativeTokens = 0;
		}

	}

	public byte getPyramidLevel(Color color) {
		byte level = 0;
		for (Tile cityTile : cityTiles) {
			if (cityTile.pyramidColor.equals(color)) {
				byte pyramidLevel = cityTile.getPyramidLevel();
				if (pyramidLevel > level) {
					level = pyramidLevel;
				}
			}
		}

		for (Army army : armyList) {
			Tile tile = army.tile;
			if (tile != null && tile.pyramidColor.equals(color)) {
				byte pyramidLevel = tile.getPyramidLevel();
				if (pyramidLevel > level) {
					level = pyramidLevel;
				}
			}
		}

		return level;
	}

	public byte getPowerCost(Power power) {
		byte returnValue = (byte) -power.level;
		if (hasPower(PowerList.WHITE_1_PRIESTESS_1)) {
			returnValue += 1;
		}

		if (hasPower(PowerList.WHITE_4_PRIEST_OF_RA)) {
			returnValue += 1;
		}

		if (returnValue > 0) {
			returnValue = 0;
		}

		return returnValue;
	}

	public byte getNightPrayerPoints() {
		byte points = 2;
		if (hasPower(PowerList.WHITE_2_GREAT_PRIEST)) {
			points += 2;
		}

		if (hasPower(PowerList.WHITE_4_PRIEST_OF_AMON)) {
			points += 5;
		}

		return points;
	}

	public byte applyPriestOfRaBonus(byte cost) {
		if (cost > 0 && hasPower(PowerList.WHITE_4_PRIEST_OF_RA)) {
			cost -= 1;
		}
		return cost;
	}

	public byte getTeleportCost() {
		byte cost = 2;
		cost = applyPriestOfRaBonus(cost);
		cost = applyStargateBonus(cost);
		return cost;
	}

	private byte applyStargateBonus(byte cost) {
		if (cost > 0 && hasPower(PowerList.RED_1_STARGATE)) {
			cost -= 1;
		}
		return cost;
	}

	public boolean canUseGoldToken() {
		return goldTokenAvailable && !goldTokenUsed;
	}

	public boolean canUseSilverToken() {
		return silverTokenAvailable && !silverTokenUsed;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String describePlayer() {
		StringBuilder build = new StringBuilder();
		describePlayer(build);
		return build.toString();
	}

}
