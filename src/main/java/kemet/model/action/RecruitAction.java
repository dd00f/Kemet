package kemet.model.action;

import java.util.ArrayList;
import java.util.List;

import kemet.model.Army;
import kemet.model.Beast;
import kemet.model.BoardInventory;
import kemet.model.DiCardList;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.PowerList;
import kemet.model.Tile;
import kemet.model.Validation;
import kemet.model.action.choice.Choice;
import kemet.model.action.choice.ChoiceInventory;
import kemet.model.action.choice.EndTurnChoice;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

public class RecruitAction extends DiCardAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3010582557508632609L;

	public Tile tile;
	public Beast beast;
	public byte cost;
	public byte recruitSize = -1;
	public boolean allowPaidRecruit = true;
	public boolean canRecruitOnAnyArmy = false;
	public List<Tile> pickedTiles = new ArrayList<>();
	public ChainedAction battles;
	public byte freeRecruitLeft = -1;

	public static Cache<RecruitAction> CACHE = new Cache<RecruitAction>(() -> new RecruitAction());

	private RecruitAction() {

	}

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {

		super.fillCanonicalForm(cannonicalForm, playerIndex);

		if (allowPaidRecruit) {
			// cannonicalForm.set(BoardInventory.STATE_RECRUIT,
			// player.getState(playerIndex));

			player.setCanonicalState(cannonicalForm, BoardInventory.STATE_RECRUIT, playerIndex);

		} else {
			// cannonicalForm.set(BoardInventory.STATE_FREE_RECRUIT,
			// player.getState(playerIndex));

			player.setCanonicalState(cannonicalForm, BoardInventory.STATE_FREE_RECRUIT, playerIndex);

		}

		cannonicalForm.set(BoardInventory.FREE_RECRUIT_LEFT, freeRecruitLeft);

		if (isEnded()) {
			// trigger battle actions
			// battles.fillCanonicalForm(cannonicalForm, playerIndex);
			throw new IllegalStateException(
					"Should not be able to fill a canonical form on a recruit action that's ended");
		}

		for (Tile tile : pickedTiles) {
			// reverse selection for tiles that were already picked
			// tile.setSelectedSource(cannonicalForm, playerIndex, (byte)
			// -player.getState(playerIndex));

			// tile.setCanonicalSelected(cannonicalForm, player, playerIndex);
			int tileCanonicalIndex = BoardInventory.TILE_SELECTION_DONE + tile.getTileCanonicalIndex(playerIndex);
			cannonicalForm.set(tileCanonicalIndex, (byte) 1);
		}

		if (tile == null) {
			// pick tile
			// cannonicalForm.set(BoardInventory.STATE_PICK_TILE,
			// player.getState(playerIndex));

			player.setCanonicalState(cannonicalForm, BoardInventory.STATE_PICK_TILE, playerIndex);

		} else if (recruitSize < 0) {
			// pick size
			// cannonicalForm.set(BoardInventory.STATE_PICK_ARMY_SIZE,
			// player.getState(playerIndex));
			player.setCanonicalState(cannonicalForm, BoardInventory.STATE_PICK_ARMY_SIZE, playerIndex);

			// tile.setSelectedSource(cannonicalForm, playerIndex,
			// player.getState(playerIndex));
			tile.setCanonicalSelectedSource(cannonicalForm, player, playerIndex);

		} else if (playerHasBeastAvailable()) {
			// cannonicalForm.set(BoardInventory.STATE_PICK_BEAST,
			// player.getState(playerIndex));
			player.setCanonicalState(cannonicalForm, BoardInventory.STATE_PICK_BEAST, playerIndex);
			cannonicalForm.set(BoardInventory.PICKED_SIZE, recruitSize);
			// tile.setSelectedSource(cannonicalForm, playerIndex,
			// player.getState(playerIndex));
			tile.setCanonicalSelectedSource(cannonicalForm, player, playerIndex);
		}

	}

	@Override
	public void internalInitialize() {

		super.internalInitialize();
		tile = null;
		beast = null;
		cost = 0;
		freeRecruitLeft = -1;
		recruitSize = -1;
		pickedTiles.clear();
		battles = null;
		allowPaidRecruit = true;
		canRecruitOnAnyArmy = false;
	}

	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {
		currentGame.validate(game);
		currentGame.validate(player);
		currentGame.validate(tile);

		for (Tile pickedTile : pickedTiles) {
			currentGame.validate(pickedTile);
		}

		if (battles != null) {
			battles.validate(this, currentGame);
		}

		if (expectedParent != parent) {
			Validation.validationFailed("Action parent isn't as expected.");
		}
	}

	@Override
	public void relink(KemetGame clone) {

		super.relink(clone);
		tile = clone.getTileByCopy(tile);

		for (int i = 0; i < pickedTiles.size(); i++) {
			pickedTiles.set(i, game.getTileByCopy(pickedTiles.get(i)));
		}

		if (battles != null) {
			battles.relink(clone);
		}

	}

	@Override
	public RecruitAction deepCacheClone() {

		RecruitAction clone = CACHE.create();

		copy(clone);

		return clone;
	}

	private void copy(RecruitAction clone) {
		super.copy(clone);

		clone.tile = tile;
		clone.beast = beast;
		clone.cost = cost;
		clone.freeRecruitLeft = freeRecruitLeft;
		clone.recruitSize = recruitSize;
		clone.allowPaidRecruit = allowPaidRecruit;
		clone.canRecruitOnAnyArmy = canRecruitOnAnyArmy;

		clone.pickedTiles.clear();
		clone.pickedTiles.addAll(pickedTiles);

		clone.battles = battles.deepCacheClone();
		clone.battles.setParent(clone);

		super.copy(clone);
	}

	@Override
	public void release() {

		super.release();
		CACHE.release(this);
	}

	@Override
	public void clear() {
		super.clear();
		if (battles != null) {
			battles.release();
		}
		battles = null;
		tile = null;
		beast = null;
		pickedTiles.clear();

		super.clear();
	}

	public static RecruitAction create(KemetGame game, Player player, Action parent) {
		RecruitAction create = CACHE.create();
		create.initialize();
		create.game = game;
		create.player = player;
		create.parent = parent;
		create.battles = ChainedAction.create(game, create);

		return create;
	}

	public boolean canRecruitBeast() {

		if (player.availableBeasts.size() == 0) {
			// no beast to recruit
			return false;
		}

		byte finalArmySize = recruitSize;

		if (tile.getArmy() != null) {
			if (tile.getArmy().owningPlayer == player) {
				finalArmySize += tile.getArmy().armySize;
				if (tile.getArmy().beast != null) {
					// beast already present
					return false;
				}
			}
		}

		if (finalArmySize <= 0) {
			// no army on which to recruit
			return false;
		}

		return true;

	}

	public Army createArmy() {

		Army returnValue = null;

		if (tile.getArmy() != null) {
			if (tile.getArmy().owningPlayer == player) {
				returnValue = tile.getArmy();
			}
		}

		if (returnValue == null) {
			returnValue = player.createArmy();
		}

		player.modifyPrayerPoints((byte) -cost, "recruit action");

		freeRecruitLeft -= recruitSize;
		if (freeRecruitLeft < 0) {
			freeRecruitLeft = 0;
		}

		returnValue.recruit(recruitSize);
		if (beast != null) {
			returnValue.addBeast(beast);
		}
		return returnValue;
	}

	public boolean resultsInCombat() {
		if (tile.getArmy() != null) {
			if (tile.getArmy().owningPlayer != player) {
				return true;
			}
		}
		return false;
	}

	public boolean playerCanRecruitArmy() {
		if (player.availableArmyTokens <= 0) {
			return false;
		}

		if (player.getPrayerPoints() <= 0) {
			return false;
		}
		return true;
	}

	public boolean playerCanRecruitBeast() {
		if (player.availableBeasts.size() <= 0) {
			return false;
		}
		return true;
	}

	public class RecruitBeastChoice extends PlayerChoice {

		public RecruitBeastChoice(KemetGame game, Player player) {
			super(game, player);
		}

		public Beast pickBeast;

		@Override
		public void choiceActivate() {

			beast = pickBeast;
			activateAction();

		}

		@Override
		public String describe() {

			if (pickBeast == null) {
				return "Recruit no beast";
			}
			return "Recruit beast \"" + pickBeast.name;

		}

		@Override
		public int getIndex() {
			if (pickBeast == null) {
				return ChoiceInventory.KEEP_BEAST;
			}

			return pickBeast.index + ChoiceInventory.PICK_BEAST;
		}

	}

	public void addRecruitBeastChoice(List<Choice> choiceList) {

		for (Beast beast : player.availableBeasts) {
			RecruitBeastChoice subChoice = new RecruitBeastChoice(game, player);
			subChoice.pickBeast = beast;

			choiceList.add(subChoice);
		}

		{

			RecruitBeastChoice subChoice = new RecruitBeastChoice(game, player);
			subChoice.pickBeast = null;

			choiceList.add(subChoice);
		}
	}

	public void activateAction() {

		Army army = createArmy();

		if (resultsInCombat()) {

			army.moveToBattleTile(tile);

			BattleAction battle = BattleAction.create(game, battles);
			battle.attackingArmy = army;
			battle.defendingArmy = tile.getArmy();
			battle.tile = tile;

			// parent.stackPendingActionOnParent(battle);
			battles.add(battle);
		} else {
			army.moveToTile(tile);
		}

		pickedTiles.add(tile);

		reset();
	}

	private void reset() {
		tile = null;
		recruitSize = -1;
		beast = null;
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {

		PlayerChoicePick nextPlayerChoicePick = super.getNextPlayerChoicePick();
		if (nextPlayerChoicePick != null) {
			return nextPlayerChoicePick;
		}

		if (freeRecruitLeft < 0) {
			freeRecruitLeft = 0;
			if (player.hasPower(PowerList.BLUE_1_RECRUITING_SCRIBE_1)) {
				freeRecruitLeft += 2;
			}

			if (player.hasPower(PowerList.WHITE_4_PRIEST_OF_RA)) {
				freeRecruitLeft += 1;
			}
		}

		if (isEnded()) {
			// trigger battle actions
//			return battles.getNextPlayerChoicePick();

			moveAllBattledToParent();
			return null;
		}

		if (tile != null) {

			if (recruitSize < 0) {
				// pick size

				PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

				addRecruitArmySizeChoice(pick.choiceList);

//				addGenericDiCardChoice(pick.choiceList);
//				addDiCardChoice(pick.choiceList, DiCardList.ENLISTMENT.index);

				EndTurnChoice.addEndTurnChoice(game, player, pick.choiceList, this, ChoiceInventory.END_RECRUIT);
				return pick.validate();
			} else if (playerHasBeastAvailable()) {

				// pick beast
				PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

				addRecruitBeastChoice(pick.choiceList);

//				addGenericDiCardChoice(pick.choiceList);
//				addDiCardChoice(pick.choiceList, DiCardList.ENLISTMENT.index);

				EndTurnChoice.addEndTurnChoice(game, player, pick.choiceList, this, ChoiceInventory.END_RECRUIT);
				return pick.validate();
			}
		} else {

			// pick tile
			PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

			addRecruitPickTileChoice(pick.choiceList);

			if (pick.choiceList.size() == 0 || !player.canRecruit() || (!allowPaidRecruit && freeRecruitLeft == 0)) {

				// no valid choices left
				end();
				moveAllBattledToParent();
				return null;

			}

			addGenericDiCardChoice(pick.choiceList);
			addDiCardChoice(pick.choiceList, DiCardList.ENLISTMENT.index);

			EndTurnChoice.addEndTurnChoice(game, player, pick.choiceList, this, ChoiceInventory.END_RECRUIT);
			return pick.validate();
		}

		moveAllBattledToParent();
		return null;

	}

	private void moveAllBattledToParent() {
		List<Action> actionChain = battles.getActionChain();
		for (Action action : actionChain) {
			parent.stackPendingActionOnParent(action);
		}

		battles.clear();
	}

	private boolean playerHasBeastAvailable() {
		return player.availableBeasts.size() > 0;
	}

	public class RecruitArmySizeChoice extends PlayerChoice {

		public RecruitArmySizeChoice(KemetGame game, Player player) {
			super(game, player);
		}

		public byte pickArmySize;
		public byte pickCost;

		@Override
		public void choiceActivate() {

			recruitSize = pickArmySize;
			cost = pickCost;

			if (!playerCanRecruitBeastOnTile(recruitSize)) {
				activateAction();
			}
		}

		@Override
		public String describe() {

			return "Recruit army size " + pickArmySize + " for " + pickCost + " prayer points.";

		}

		@Override
		public int getIndex() {
			if (pickArmySize == 0) {
				return ChoiceInventory.PASS_RECRUIT_CHOICE_INDEX;
			}
			return ChoiceInventory.ARMY_SIZE_CHOICE + pickArmySize - 1;
		}

	}

	public boolean playerCanRecruitBeastOnTile(byte addedSize) {
		if (!playerHasBeastAvailable()) {
			return false;
		}
		byte totalSize = addedSize;
		Army army = tile.getArmy();
		if (army != null) {
			if (army.beast != null) {
				return false;
			}
			totalSize += army.armySize;
		}

		// need units to recruit a beast
		if (totalSize == 0) {
			return false;
		}

		return true;
	}

	public void addRecruitArmySizeChoice(List<Choice> choiceList) {

		byte max = player.maximumArmySize;
		byte min = 1;
		Army army = tile.getArmy();
		if (army != null && army.owningPlayer == player) {
			max -= army.armySize;
			min = 0;
		}

		max = (byte) Math.min(max, player.availableArmyTokens);

		if (allowPaidRecruit) {
			max = (byte) Math.min(max, player.getPrayerPoints() + freeRecruitLeft);
		} else {
			max = (byte) Math.min(max, freeRecruitLeft);
		}

		for (byte i = min; i <= max; ++i) {
			RecruitArmySizeChoice subChoice = new RecruitArmySizeChoice(game, player);
			subChoice.pickArmySize = i;

			byte cost = (byte) (i - freeRecruitLeft);
			if (cost < 0) {
				cost = 0;
			}

			subChoice.pickCost = cost;

			choiceList.add(subChoice);
		}
	}

	private boolean isTargetTileFriendlyAndFull(Tile tile) {
		if (tile.getArmy() != null && tile.getArmy().owningPlayer == player
				&& !tile.getArmy().playerCanRecruitOnArmy()) {
			return true;
		}
		return false;
	}

	public void addRecruitPickTileChoice(List<Choice> choiceList) {

		if (player.availableArmyTokens <= 0 && player.availableBeasts.size() == 0) {
			// no option to add
			return;
		}

		for (Tile tile : player.cityTiles) {

			checkToAddRecruitChoice(choiceList, tile);
		}

		if (player.hasPower(PowerList.BLACK_1_ENFORCED_RECRUITMENT) || canRecruitOnAnyArmy) {

			for (Army army : player.armyList) {
				if (!player.cityTiles.contains(army.tile) && army.tile != null) {
					checkToAddRecruitChoice(choiceList, army.tile);
				}
			}
		}
	}

	private void checkToAddRecruitChoice(List<Choice> choiceList, Tile tile) {
		if (isTargetTileFriendlyAndFull(tile)) {
			return;
		}

		if (pickedTiles.contains(tile)) {
			return;
		}

		RecruitPickTileChoice subChoice = new RecruitPickTileChoice(game, player);
		subChoice.pickTile = tile;
		choiceList.add(subChoice);
	}

	public class RecruitPickTileChoice extends PlayerChoice {

		public RecruitPickTileChoice(KemetGame game, Player player) {
			super(game, player);
		}

		public Tile pickTile;

		@Override
		public void choiceActivate() {

			tile = pickTile;
		}

		@Override
		public String describe() {

			return "Recruit on tile \"" + pickTile.name;

		}

		@Override
		public int getIndex() {
			return pickTile.getPickChoiceIndex(player.getIndex());
		}
	}

	@Override
	public void applyDiCard(int index) {
		if (index == DiCardList.ENLISTMENT.index) {
			freeRecruitLeft += 2;
		} else {
			super.applyDiCard(index);
		}
	}

	@Override
	public void enterSimulationMode(int playerIndex) {
		super.enterSimulationMode(playerIndex);

		if (battles != null) {
			battles.enterSimulationMode(playerIndex);
		}

	}

	@Override
	public void stackPendingActionOnParent(Action pendingAction) {
		parent.stackPendingActionOnParent(pendingAction);
	}
}
