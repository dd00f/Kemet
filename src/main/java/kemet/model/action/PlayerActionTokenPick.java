
package kemet.model.action;

import java.util.List;

import kemet.Options;
import kemet.model.ActionList;
import kemet.model.BoardInventory;
import kemet.model.Color;
import kemet.model.DiCardList;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.PowerList;
import kemet.model.Validation;
import kemet.model.action.choice.Choice;
import kemet.model.action.choice.ChoiceInventory;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;
import lombok.extern.log4j.Log4j2;

/**
 * PlayerActionTokenPick
 * 
 * @author Steve McDuff
 */
@Log4j2
public class PlayerActionTokenPick extends DiCardAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9034661106772891860L;

	public ChainedAction nextAction;
	public ChainedAction overridingAction;
	private boolean donePicking = false;
	private boolean firstPick = true;
	private boolean mainTokenPicked = false;

	private int actionIndex1;
	private int actionIndex2;
	private int actionIndex3;

	public static Cache<PlayerActionTokenPick> CACHE = new Cache<PlayerActionTokenPick>(
			() -> new PlayerActionTokenPick());

	private PlayerActionTokenPick() {

	}

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {

		super.fillCanonicalForm(cannonicalForm, playerIndex);

		cannonicalForm.set(BoardInventory.STATE_PICK_ACTION_TOKEN, player.getState(playerIndex));

		if (mainTokenPicked) {
			cannonicalForm.set(BoardInventory.MAIN_TOKEN_PICKED, player.getState(playerIndex));
		}

		// Picked & pending actions aren't reflected in the game canonical state.
		// need indicator to know what is the 1st & 2nd, 3rd pending action.
		// need indicator to know which action is executing : 1st,2nd,3rd

		int selectedCount = getSelectedActionCount();
		int pendingCount = getPendingActionCount();
		int offset = selectedCount - pendingCount;
		int canonicalPendingActionIndex1 = -1;
		int canonicalPendingActionIndex2 = -1;
		int canonicalPendingActionIndex3 = -1;

		if (offset == 0) {
			canonicalPendingActionIndex1 = actionIndex1;
			canonicalPendingActionIndex2 = actionIndex2;
			canonicalPendingActionIndex3 = actionIndex3;
		} else if (offset == 1) {
			canonicalPendingActionIndex1 = actionIndex2;
			canonicalPendingActionIndex2 = actionIndex3;
		} else if (offset == 2) {
			canonicalPendingActionIndex1 = actionIndex3;
		} else {
			if (overridingAction == null || overridingAction.size() == 0) {
				throw new IllegalStateException(
						"How can the offset be bigger than 2 when there can only be 3 actions ? Offset " + offset);
			}
		}

		if (canonicalPendingActionIndex1 >= 0) {
			int offset1 = BoardInventory.PICKED_ACTION_IN_ORDER + canonicalPendingActionIndex1;
			cannonicalForm.set(offset1, player.getState(playerIndex));
		}
		if (canonicalPendingActionIndex2 >= 0) {
			int offset2 = BoardInventory.PICKED_ACTION_IN_ORDER + ActionList.TOTAL_ACTION_COUNT
					+ canonicalPendingActionIndex2;
			cannonicalForm.set(offset2, player.getState(playerIndex));
		}
		if (canonicalPendingActionIndex3 >= 0) {
			int offset3 = BoardInventory.PICKED_ACTION_IN_ORDER + (ActionList.TOTAL_ACTION_COUNT * 2)
					+ canonicalPendingActionIndex3;
			cannonicalForm.set(offset3, player.getState(playerIndex));
		}

		if (overridingAction != null && overridingAction.size() > 0) {
			overridingAction.fillCanonicalForm(cannonicalForm, playerIndex);
		} else if (donePicking) {
			if (nextAction != null) {
				nextAction.fillCanonicalForm(cannonicalForm, playerIndex);
			}
		}
	}

//	private boolean hasBattleActionLeft() {
//		if (nextAction != null) {
//			List<Action> actionChain = nextAction.getActionChain();
//			if (actionChain.size() > 0) {
//				Action remainingAction = actionChain.get(0);
//				if (remainingAction instanceof BattleAction) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}

	private int getPendingActionCount() {
		int count = 0;
		if (nextAction != null) {
			count = nextAction.size();
//			List<Action> actionChain = nextAction.getActionChain();
//			for (Action action : actionChain) {
//				if (!(action instanceof BattleAction)) {
//					count++;
//				}
//			}
		}
		return count;
	}

	private int getSelectedActionCount() {
		if (actionIndex3 >= 0) {
			return 3;
		}
		if (actionIndex2 >= 0) {
			return 2;
		}
		if (actionIndex1 >= 0) {
			return 1;
		}
		return 0;
	}

	@Override
	public void internalInitialize() {

		super.internalInitialize();

		nextAction = null;
		overridingAction = null;
		donePicking = false;
		firstPick = true;
		mainTokenPicked = false;

		actionIndex1 = -1;
		actionIndex2 = -1;
		actionIndex3 = -1;
	}

	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {

		super.validate(expectedParent, currentGame);

		if (expectedParent != parent) {
			Validation.validationFailed("Action parent isn't as expected.");
		}
	}

	@Override
	public PlayerActionTokenPick deepCacheClone() {
		// create the object
		PlayerActionTokenPick clone = CACHE.create();

		copy(clone);

		return clone;
	}

	private void copy(PlayerActionTokenPick clone) {

		super.copy(clone);

		// copy all objects
		clone.donePicking = donePicking;
		clone.firstPick = firstPick;
		clone.mainTokenPicked = mainTokenPicked;

		clone.actionIndex1 = actionIndex1;
		clone.actionIndex2 = actionIndex2;
		clone.actionIndex3 = actionIndex3;

		// deep clone all owned objects
		clone.nextAction = null;
		if (nextAction != null) {
			clone.nextAction = nextAction.deepCacheClone();
			clone.nextAction.setParent(clone);
		}
		// deep clone all owned objects
		clone.overridingAction = null;
		if (overridingAction != null) {
			clone.overridingAction = overridingAction.deepCacheClone();
			clone.overridingAction.setParent(clone);
		}
	}

	@Override
	public void release() {

		super.release();

		// release all owned objects
		if (nextAction != null) {
			nextAction.release();
		}

		// null all references
		nextAction = null;

		// release all owned objects
		if (overridingAction != null) {
			overridingAction.release();
		}

		// null all references
		overridingAction = null;

		CACHE.release(this);
	}

	@Override
	public void relink(KemetGame clone) {

		super.relink(clone);

		// release all owned objects
		if (nextAction != null) {
			nextAction.relink(clone);
		}
		// release all owned objects
		if (overridingAction != null) {
			overridingAction.relink(clone);
		}
	}

	public static PlayerActionTokenPick create(KemetGame game, Player player, Action parent) {
		PlayerActionTokenPick create = CACHE.create();
		create.initialize();

		create.game = game;
		create.player = player;
		create.parent = parent;

		return create;
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {

		PlayerChoicePick nextPlayerChoicePick = super.getNextPlayerChoicePick();
		if (nextPlayerChoicePick != null) {
			return nextPlayerChoicePick.validate();
		}

		// check for override first
		if (overridingAction != null && overridingAction.size() > 0) {
			nextPlayerChoicePick = overridingAction.getNextPlayerChoicePick();
			if (nextPlayerChoicePick != null) {
				return nextPlayerChoicePick.validate();
			}
		}

		if (donePicking) {
			if (nextAction != null) {
				List<Action> actionChain = nextAction.getActionChain();

				while (actionChain.size() != 0) {
					Action nextAction = actionChain.get(0);
					nextPlayerChoicePick = nextAction.getNextPlayerChoicePick();
					if (nextPlayerChoicePick != null) {
						return nextPlayerChoicePick.validate();
					}
					nextAction.release();
					actionChain.remove(0);

					// check for any overriding action that may have been created.
					if (overridingAction != null && overridingAction.size() > 0) {
						nextPlayerChoicePick = overridingAction.getNextPlayerChoicePick();
						if (nextPlayerChoicePick != null) {
							return nextPlayerChoicePick.validate();
						}
					}
				}

				// check for any overriding action one last time
				if (overridingAction != null && overridingAction.size() > 0) {
					nextPlayerChoicePick = overridingAction.getNextPlayerChoicePick();
					if (nextPlayerChoicePick != null) {
						return nextPlayerChoicePick.validate();
					}
				}
			}
		} else {

			// player won at the beginning of his turn.
			if (game.didPlayerWin(player)) {
				return null;
			}

			PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

			List<Choice> choiceList = pick.choiceList;

			if (!mainTokenPicked || player.canUseSilverToken()) {
				if (getPlayerActionTokenLeft() == 1) {
					if (!player.isRowOneUsed()) {
						addRowOneActions(player, choiceList);
					} else if (!player.isRowTwoUsed()) {
						addRowTwoActions(player, choiceList);
					} else if (!player.isRowThreeUsed()) {
						addRowThreeActions(player, choiceList);
					} else {
						addAllActions(player, choiceList);
					}

				} else if (getPlayerActionTokenLeft() == 2 && player.getUsedRowCount() == 1) {
					if (!player.isRowOneUsed()) {
						addRowOneActions(player, choiceList);
					}
					if (!player.isRowTwoUsed()) {
						addRowTwoActions(player, choiceList);
					}
					if (!player.isRowThreeUsed()) {
						addRowThreeActions(player, choiceList);
					}

				} else {
					addAllActions(player, choiceList);
				}
			}

			addAllGoldTokenActions(player, choiceList);

			addGenericDiCardChoice(choiceList);

			addDiCardChoice(choiceList, DiCardList.ENLISTMENT.index);

			if (mainTokenPicked) {
				choiceList.add(new DonePickingChoice(game, player));
			}

			return pick.validate();

		}

		game.checkForWinningCondition();

		if (Options.VALIDATE_GAME_BETWEEN_PICKS) {
			game.validate();
		}

		return null;

	}

	private void addAllGoldTokenActions(Player currentPlayer, List<Choice> choiceList) {
		if (currentPlayer.canUseGoldToken()) {
			boolean hasDivineWill = currentPlayer.hasPower(PowerList.BLUE_4_DIVINE_WILL);
			boolean hasForcedMarch = currentPlayer.hasPower(PowerList.BLACK_3_FORCED_MARCH);
			if (hasDivineWill || hasForcedMarch) {
				choiceList.add(new MoveChoice(game, player, (byte) 0, true));
			}
			if (hasDivineWill) {
				// add gold recruit
				choiceList.add(new RecruitChoice(game, player, (byte) 0, true));
			}
			if (currentPlayer.hasPower(PowerList.BLACK_1_DARK_RITUAL)) {
				// add pray
				choiceList.add(new PrayerChoice(game, player, (byte) 0, true));
			}

			addAllTwinCeremonyPowerBuyOptions(currentPlayer, choiceList);
		}

	}

	private int getPlayerActionTokenLeft() {
		int tokenLeft = player.actionTokenLeft;

		if (player.canUseSilverToken()) {
			tokenLeft += 1;
		}
		return tokenLeft;
	}

	public class MoveChoice extends PlayerChoice {

		private boolean isGold;

		public MoveChoice(KemetGame game, Player player, byte row, boolean isGold) {
			super(game, player);
			this.row = row;
			this.isGold = isGold;
		}

		public byte row;

		@Override
		public String describe() {

			if (isGold) {
				return "Move action, gold token";
			}
			return "Move action, row " + row;
		}

		@Override
		public void choiceActivate() {
			if (!isGold) {
				if (row == 1) {
					player.rowOneMoveUsed = true;
				} else {
					player.rowTwoMoveUsed = true;
				}
			}

			ArmyMoveAction action = ArmyMoveAction.create(game, player, PlayerActionTokenPick.this);
			addNextAction(action, isGold, ActionList.ACTION_MOVE);

		}

		@Override
		public int getIndex() {
			if (isGold) {
				return ChoiceInventory.PICK_GOLD_MOVE;
			}
			if (row == 1) {
				return ChoiceInventory.PICK_ROW_ONE_MOVE;
			}
			return ChoiceInventory.PICK_ROW_TWO_MOVE;
		}

	}

	public class PrayerChoice extends PlayerChoice {

		private boolean isGold;

		public PrayerChoice(KemetGame game, Player player, byte row, boolean isGold) {
			super(game, player);
			this.row = row;
			this.isGold = isGold;

			increasedPower = player.getPrayActionPowerIncrease();
		}

		public byte increasedPower = 2;
		public byte row;

		@Override
		public void choiceActivate() {

			if (!isGold) {
				if (row == 2) {
					player.rowTwoPrayUsed = true;
				} else {
					player.rowThreePrayUsed = true;
				}
			}

			PrayAction action = PrayAction.create(PlayerActionTokenPick.this, player, increasedPower);
			addNextAction(action, isGold, ActionList.ACTION_PRAY);
		}

		@Override
		public String describe() {

			if (isGold) {
				return "Prayer action, gold token, with " + player.getPrayerPoints() + " prayer points, pray for "
						+ increasedPower + " prayer points";
			}
			return "Prayer action, row " + row + ", with " + player.getPrayerPoints() + " prayer points, pray for "
					+ increasedPower + " prayer points";

		}

		@Override
		public int getIndex() {
			if (isGold) {
				return ChoiceInventory.PICK_GOLD_PRAY;
			}
			if (row == 2) {
				return ChoiceInventory.PICK_ROW_TWO_PRAY;
			}
			return ChoiceInventory.PICK_ROW_THREE_PRAY;
		}

	}

	public class UpgradePyramidChoice extends PlayerChoice {

		public UpgradePyramidChoice(KemetGame game, Player player) {
			super(game, player);
		}

		@Override
		public void choiceActivate() {

			player.rowTwoUpgradePyramidUsed = true;

			UpgradePyramidAction action = UpgradePyramidAction.create(game, player, PlayerActionTokenPick.this);
			addNextAction(action, false, ActionList.ACTION_UPGRADE_PYRAMID);

		}

		@Override
		public String describe() {

			return "Upgrade Pyramid row two.";

		}

		@Override
		public int getIndex() {
			return ChoiceInventory.PICK_ROW_TWO_UPGRADE_PYRAMID;
		}
	}

	public class RecruitChoice extends PlayerChoice {

		private boolean isGold;

		public RecruitChoice(KemetGame game, Player player, byte row, boolean isGold) {
			super(game, player);
			this.row = row;
			this.isGold = isGold;
		}

		public byte row;

		@Override
		public String describe() {

			if (isGold) {
				return "Recruit action gold token";
			}
			return "Recruit action row " + row;

		}

		@Override
		public void choiceActivate() {
			if (!isGold) {
				player.rowOneRecruitUsed = true;
			}

			RecruitAction action = RecruitAction.create(game, player, PlayerActionTokenPick.this);
			addNextAction(action, isGold, ActionList.ACTION_RECRUIT);

		}

		@Override
		public int getIndex() {
			if (isGold) {
				return ChoiceInventory.PICK_GOLD_RECRUIT;
			}
			return ChoiceInventory.PICK_ROW_ONE_RECRUIT;
		}

	}

	public class BuyPowerChoice extends PlayerChoice {

		public Color color;
		public boolean isGold;

		public BuyPowerChoice(KemetGame game, Player player, Color color, boolean isGold) {
			super(game, player);
			this.color = color;
			this.isGold = isGold;
		}

		public byte row;

		@Override
		public String describe() {
			if (isGold) {
				return "Buy Power with Gold Token : " + color;
			}
			return "Buy Power action row 3 : " + color;
		}

		@Override
		public void choiceActivate() {
			byte costBoost = 0;
			if (isGold) {
				costBoost = 1;
			} else {
				if (color == Color.WHITE) {
					player.rowThreeBuildWhiteUsed = true;
				} else if (color == Color.RED) {
					player.rowThreeBuildRedUsed = true;
				} else if (color == Color.BLACK) {
					player.rowThreeBuildBlackUsed = true;
				} else if (color == Color.BLUE) {
					player.rowThreeBuildBlueUsed = true;
				} else {
					log.error("unknown color to buy power {}", color);
				}
			}

			BuyPowerAction action = BuyPowerAction.create(game, player, PlayerActionTokenPick.this, color, costBoost);

			int actionIndexOffset = getActionIndexOffset();
			addNextAction(action, isGold, actionIndexOffset);
		}

		public int getActionIndexOffset() {
			if (isGold) {
				return ActionList.ACTION_REPEAT_BUY_POWER + color.ordinal();
			}
			return ActionList.ACTION_BUY_POWER + color.ordinal();
		}

		@Override
		public int getIndex() {
			return getBuyPowerActionIndex(color);
		}

	}

	public class DonePickingChoice extends PlayerChoice {

		public DonePickingChoice(KemetGame game, Player player) {
			super(game, player);
		}

		@Override
		public String describe() {
			return "Done picking actions.";
		}

		@Override
		public void choiceActivate() {
			donePicking = true;
		}

		@Override
		public int getIndex() {
			return ChoiceInventory.PASS_TOKEN_PICK;
		}

	}

	public static int getBuyPowerActionIndex(Color color) {

		if (color == Color.WHITE) {
			return ChoiceInventory.PICK_ROW_THREE_BUILD_WHITE;
		} else if (color == Color.RED) {
			return ChoiceInventory.PICK_ROW_THREE_BUILD_RED;
		} else if (color == Color.BLACK) {
			return ChoiceInventory.PICK_ROW_THREE_BUILD_BLACK;
		} else if (color == Color.BLUE) {
			return ChoiceInventory.PICK_ROW_THREE_BUILD_BLUE;
		}
		log.error("unknown color to buy power {}", color);

		throw new IllegalStateException();

	}

	private void addAllActions(Player player, List<Choice> choiceList) {
		addRowOneActions(player, choiceList);
		addRowTwoActions(player, choiceList);
		addRowThreeActions(player, choiceList);
	}

	public void addNextAction(Action action, boolean goldToken, int actionIndexOffset) {

		if (actionIndex1 < 0) {
			actionIndex1 = actionIndexOffset;
		} else if (actionIndex2 < 0) {
			actionIndex2 = actionIndexOffset;
		} else if (actionIndex3 < 0) {
			actionIndex3 = actionIndexOffset;
		} else {
			game.printDescribeGame();
			throw new IllegalStateException("A player managed to get 4 actions in one turn. ");
		}

		firstPick = false;

		if (goldToken) {
			player.goldTokenUsed = true;
		} else {
			if (mainTokenPicked) {
				player.silverTokenUsed = true;
			} else {
				player.actionTokenLeft--;
				mainTokenPicked = true;
			}
		}

		if (nextAction == null) {
			nextAction = ChainedAction.create(game, this);
		}

		action.setParent(nextAction);
		nextAction.add(action);

		if (!mainTokenPicked || player.canUseGoldToken() || player.canUseSilverToken()) {
			// can keep on picking actions
		} else {
			donePicking = true;
		}
	}

	private void addRowThreeActions(Player player, List<Choice> choiceList) {
		if (!player.rowThreePrayUsed) {
			choiceList.add(new PrayerChoice(game, player, (byte) 3, false));
		}

		addAllPowerBuyOptions(player, choiceList);
	}

	private void addAllPowerBuyOptions(Player currentPlayer, List<Choice> choiceList) {

		if (!player.rowThreeBuildWhiteUsed && playerHasPyramidColor(currentPlayer, Color.WHITE)) {
			choiceList.add(new BuyPowerChoice(game, player, Color.WHITE, false));
		}

		if (!player.rowThreeBuildRedUsed && playerHasPyramidColor(currentPlayer, Color.RED)) {
			choiceList.add(new BuyPowerChoice(game, player, Color.RED, false));
		}

		if (!player.rowThreeBuildBlackUsed && playerHasPyramidColor(currentPlayer, Color.BLACK)) {
			choiceList.add(new BuyPowerChoice(game, player, Color.BLACK, false));
		}

		if (!player.rowThreeBuildBlueUsed && playerHasPyramidColor(currentPlayer, Color.BLUE)) {
			choiceList.add(new BuyPowerChoice(game, player, Color.BLUE, false));
		}

	}

	private void addAllTwinCeremonyPowerBuyOptions(Player currentPlayer, List<Choice> choiceList) {

		boolean canBuyWithGoldToken = currentPlayer.canUseGoldToken()
				&& currentPlayer.hasPower(PowerList.BLACK_2_TWIN_CEREMONY);

		if (!canBuyWithGoldToken) {
			return;
		}

		if (player.rowThreeBuildWhiteUsed && playerHasPyramidColor(currentPlayer, Color.WHITE)) {
			choiceList.add(new BuyPowerChoice(game, player, Color.WHITE, true));
		}

		if (player.rowThreeBuildRedUsed && playerHasPyramidColor(currentPlayer, Color.RED)) {
			choiceList.add(new BuyPowerChoice(game, player, Color.RED, true));
		}

		if (player.rowThreeBuildBlackUsed && playerHasPyramidColor(currentPlayer, Color.BLACK)) {
			choiceList.add(new BuyPowerChoice(game, player, Color.BLACK, true));
		}

		if (player.rowThreeBuildBlueUsed && playerHasPyramidColor(currentPlayer, Color.BLUE)) {
			choiceList.add(new BuyPowerChoice(game, player, Color.BLUE, true));
		}

	}

	private boolean playerHasPyramidColor(Player currentPlayer, Color color) {
		byte level = currentPlayer.getPyramidLevel(color);

		return level > 0;
	}

	private void addRowTwoActions(Player player, List<Choice> choiceList) {
		if (!player.rowTwoMoveUsed) {
			choiceList.add(new MoveChoice(game, player, (byte) 2, false));
		}
		if (!player.rowTwoPrayUsed) {
			choiceList.add(new PrayerChoice(game, player, (byte) 2, false));
		}
		if (!player.rowTwoUpgradePyramidUsed) {
			choiceList.add(new UpgradePyramidChoice(game, player));
		}
	}

	private void addRowOneActions(Player player, List<Choice> choiceList) {
		if (!player.rowOneMoveUsed) {
			choiceList.add(new MoveChoice(game, player, (byte) 1, false));
		}
		if (!player.rowOneRecruitUsed) {
			choiceList.add(new RecruitChoice(game, player, (byte) 1, false));
		}
	}

	@Override
	public void enterSimulationMode(int playerIndex) {

		super.enterSimulationMode(playerIndex);

		// release all owned objects
		if (nextAction != null) {
			nextAction.enterSimulationMode(playerIndex);
		}

		// release all owned objects
		if (overridingAction != null) {
			overridingAction.enterSimulationMode(playerIndex);
		}
	}

	@Override
	public void stackPendingActionOnParent(Action pendingAction) {
		if (overridingAction == null) {
			overridingAction = ChainedAction.create(game, this);
		}

		pendingAction.setParent(overridingAction);
		overridingAction.add(pendingAction);

	}
}
