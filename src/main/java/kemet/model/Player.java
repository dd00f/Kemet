package kemet.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import kemet.ai.PlayerActor;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

public class Player implements Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 233743569229589016L;

	public static final Logger LOGGER = Logger.getLogger(Player.class.getName());

	public String name;

	/**
	 * zero based player index in the game
	 */
	public int index;
	public KemetGame game;

	public byte victoryPoints = 0;
	public byte battlePoints = 0;
	public byte templePermanentPoints = 0;
	public byte templeOccupationPoints = 0;
	public byte highLevelPyramidOccupationPoints = 0;
	public byte initiativeTokens = 0;
	private byte prayerPoints = 5;
	public byte maximumArmySize = 5;
	public byte fightBonus = 0;
	public byte attackBonus = 0;
	public byte defenseBonus = 0;
	public byte bloodBonus = 0;
	public byte shieldBonus = 0;
	public byte moveCapacity = 1;
	public byte availableArmyTokens = 12;
	public byte teleportCost = 2;
	public short armyCounter = 1;
	public boolean canTeleportFromObelisk = false;
	public boolean canBreachWalls = false;

	public List<Power> powerList = new ArrayList<>();
	public List<BattleCard> availableBattleCards = new ArrayList<>();
	public List<BattleCard> usedBattleCards = new ArrayList<>();
	public List<BattleCard> discardedBattleCards = new ArrayList<>();
	public List<DiCard> diCards = new ArrayList<>();
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

	public byte actionTokenLeft = 5;
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
		fightBonus = 0;
		attackBonus = 0;
		defenseBonus = 0;
		bloodBonus = 0;
		shieldBonus = 0;
		index = 0;
		moveCapacity = 1;
		availableArmyTokens = 12;
		teleportCost = 2;
		armyCounter = 1;
		canTeleportFromObelisk = false;
		canBreachWalls = false;

		powerList.clear();
		availableBattleCards.clear();
		usedBattleCards.clear();
		discardedBattleCards.clear();
		diCards.clear();
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
		clone.index = index;
		clone.cityFront = cityFront;

		clone.victoryPoints = victoryPoints;
		clone.battlePoints = battlePoints;
		clone.templePermanentPoints = templePermanentPoints;
		clone.templeOccupationPoints = templeOccupationPoints;
		clone.highLevelPyramidOccupationPoints = highLevelPyramidOccupationPoints;
		clone.initiativeTokens = initiativeTokens;
		clone.prayerPoints = prayerPoints;
		clone.maximumArmySize = maximumArmySize;
		clone.fightBonus = fightBonus;
		clone.attackBonus = attackBonus;
		clone.defenseBonus = defenseBonus;
		clone.bloodBonus = bloodBonus;
		clone.shieldBonus = shieldBonus;
		clone.moveCapacity = moveCapacity;
		clone.availableArmyTokens = availableArmyTokens;
		clone.teleportCost = teleportCost;
		clone.armyCounter = armyCounter;
		clone.canTeleportFromObelisk = canTeleportFromObelisk;
		clone.canBreachWalls = canBreachWalls;
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
		clone.diCards.clear();
		clone.diCards.addAll(diCards);
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
		diCards.clear();
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

	public void modifyPrayerPoints(byte modification, String reason) {
		if (modification == 0) {
			return;
		}

		byte initial = prayerPoints;
		prayerPoints += modification;
		if (prayerPoints > 11) {
			prayerPoints = 11;
		}
		if (prayerPoints < 0) {
			LOGGER.warning("Player " + name + " managed to reach " + prayerPoints + " prayer points.");
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
		return prayerPoints >= teleportCost;
	}

	public void useBattleCard(BattleCard card) {
		availableBattleCards.remove(card);
		usedBattleCards.add(card);

		validateCardCount();
	}

	public void checkToRecuperateAllBattleCards() {
		if (availableBattleCards.size() == 0) {
			if (game.printActivations) {
				game.printEvent("Player " + name + " recuperates all his battle cards.");
			}
			availableBattleCards.addAll(usedBattleCards);
			availableBattleCards.addAll(discardedBattleCards);
			discardedBattleCards.clear();
			usedBattleCards.clear();

			validateCardCount();
		}
	}

	public void discardBattleCard(BattleCard card) {
		availableBattleCards.remove(card);
		discardedBattleCards.add(card);

		validateCardCount();
	}

	public void addDawnToken() {
		initiativeTokens++;
	}

	public void addBattleVictoryPoint() {
		if (game.printActivations) {
			game.printEvent("Army " + name + " won a permanent battle victory point.");
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

		if ((countUsedActions() + actionTokenLeft) != 5) {
			Validation.validationFailed("action left doesn't equal 5");
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

	private void validateCardCount() {
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

	public void describePlayer() {
		if (game.printActivations) {
			game.printEvent("Player : " + name);
			game.printEvent("\t" + prayerPoints + " prayer points.");
			game.printEvent("\t" + victoryPoints + " victory points.");
			game.printEvent("\t" + templeOccupationPoints + " temple occupation points.");
			game.printEvent("\t" + templePermanentPoints + " temple permanent points.");
			game.printEvent("\t" + battlePoints + " battle points.");
			game.printEvent("\t" + highLevelPyramidOccupationPoints + " high level pyramid occupation points.");
			game.printEvent("\t" + initiativeTokens + " initiative tokens.");

			for (Army army : armyList) {
				army.describeArmy();
			}
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
		if (playerIndex == index) {
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
		if (targetPlayerIndex == index) {
			return 0;
		}

		// otherwise, increment our own index, unless we are bigger than the target
		// player index.

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

		if (targetPlayerIndex > index) {
			return index;
		}
		return index + 1;

	}

	public void fillCanonicalForm(ByteCanonicalForm canonicalForm, int playerIndex) {
		
		int canonicalPlayerIndex = getCanonicalPlayerIndex(playerIndex);
		
		canonicalForm.set(BoardInventory.PLAYER_VICTORY_POINTS + canonicalPlayerIndex, victoryPoints);
		canonicalForm.set(BoardInventory.PLAYER_BATTLE_POINTS + canonicalPlayerIndex, battlePoints);
		canonicalForm.set(BoardInventory.PLAYER_PRAYER_POINTS + canonicalPlayerIndex, prayerPoints);
		canonicalForm.set(BoardInventory.PLAYER_AVAILABLE_ARMY_TOKENS + canonicalPlayerIndex, availableArmyTokens);
		canonicalForm.set(BoardInventory.PLAYER_ROW_ONE_MOVE_USED + canonicalPlayerIndex, (byte) (rowOneMoveUsed ? 1: 0));
		canonicalForm.set(BoardInventory.PLAYER_ROW_ONE_RECRUIT_USED + canonicalPlayerIndex, (byte) (rowOneRecruitUsed ? 1: 0));
		canonicalForm.set(BoardInventory.PLAYER_ROW_TWO_MOVE_USED + canonicalPlayerIndex, (byte) (rowTwoMoveUsed ? 1: 0));
		canonicalForm.set(BoardInventory.PLAYER_ROW_TWO_UPGRADE_PYRAMID_USED + canonicalPlayerIndex, (byte) (rowTwoUpgradePyramidUsed ? 1: 0));
		canonicalForm.set(BoardInventory.PLAYER_ROW_TWO_PRAY_USED + canonicalPlayerIndex, (byte) (rowTwoPrayUsed ? 1: 0));
		canonicalForm.set(BoardInventory.PLAYER_ROW_THREE_PRAY_USED + canonicalPlayerIndex, (byte) (rowThreePrayUsed ? 1: 0));
		canonicalForm.set(BoardInventory.PLAYER_ROW_THREE_BUILD_WHITE_USED + canonicalPlayerIndex, (byte) (rowThreeBuildWhiteUsed ? 1: 0));
		canonicalForm.set(BoardInventory.PLAYER_ROW_THREE_BUILD_RED_USED + canonicalPlayerIndex, (byte) (rowThreeBuildRedUsed ? 1: 0));
		canonicalForm.set(BoardInventory.PLAYER_ROW_THREE_BUILD_BLUE_USED + canonicalPlayerIndex, (byte) (rowThreeBuildBlueUsed ? 1: 0));
		canonicalForm.set(BoardInventory.PLAYER_ROW_THREE_BUILD_BLACK_USED + canonicalPlayerIndex, (byte) (rowThreeBuildBlackUsed ? 1: 0));
		canonicalForm.set(BoardInventory.PLAYER_ACTION_TOKEN_LEFT + canonicalPlayerIndex, actionTokenLeft);
		canonicalForm.set(BoardInventory.PLAYER_TEMPLE_COUNT + canonicalPlayerIndex, templeOccupationPoints);
		
		for (BattleCard card : availableBattleCards) {
			canonicalForm.set(BoardInventory.PLAYER_BATTLE_CARD_AVALIABLE + canonicalPlayerIndex * BattleCard.INDEXER + card.index, (byte) 1);
		}
		
		for (BattleCard card : usedBattleCards) {
			canonicalForm.set(BoardInventory.PLAYER_BATTLE_CARD_AVALIABLE + canonicalPlayerIndex * BattleCard.INDEXER + card.index, (byte) -1);
		}
		
		byte discardCardStatus = -1;
		
		if( playerIndex != index ) {
			// make discarded cards appear available only for other players.
			discardCardStatus = 1;
		}
		for (BattleCard card : discardedBattleCards) {
			canonicalForm.set(BoardInventory.PLAYER_BATTLE_CARD_AVALIABLE + canonicalPlayerIndex * BattleCard.INDEXER + card.index, discardCardStatus);
		}

//		public byte templePermanentPoints = 0;
//		public byte highLevelPyramidOccupationPoints = 0;
//		public byte initiativeTokens = 0;
//		public byte maximumArmySize = 5;
//		public byte fightBonus = 0;
//		public byte attackBonus = 0;
//		public byte defenseBonus = 0;
//		public byte bloodBonus = 0;
//		public byte shieldBonus = 0;
//		public byte moveCapacity = 1;
//		public byte teleportCost = 2;
//		public boolean canTeleportFromObelisk = false;
//		public boolean canBreachWalls = false;
//
//		public List<DiCard> diCards = new ArrayList<>();
//		public List<Beast> availableBeasts = new ArrayList<>();
//		public boolean goldTokenUsed = false;
//		public boolean goldTokenAvailable = false;
//		public boolean silverTokenUsed = false;
//		public boolean silverTokenAvailable = false;

	}

}