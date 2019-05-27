
package kemet.model.action;

import java.util.List;

import kemet.Options;
import kemet.model.BoardInventory;
import kemet.model.Color;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Power;
import kemet.model.PowerList;
import kemet.model.Validation;
import kemet.model.action.choice.Choice;
import kemet.model.action.choice.ChoiceInventory;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

/**
 * InitializationPlayerRecruitAction
 * 
 * @author Steve McDuff
 */
public class PlayerActionTokenPick implements Action {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9034661106772891860L;

	private KemetGame game;
	private Player player;

	public Action nextAction;
	private Action parent;

	public static Cache<PlayerActionTokenPick> CACHE = new Cache<PlayerActionTokenPick>(
			() -> new PlayerActionTokenPick());

	private PlayerActionTokenPick() {

	}

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {
		cannonicalForm.set(BoardInventory.STATE_PICK_ACTION_TOKEN, player.getState(playerIndex));

		if (nextAction != null) {
			nextAction.fillCanonicalForm(cannonicalForm, playerIndex);
		}
	}

	@Override
	public void initialize() {
		game = null;
		player = null;
		nextAction = null;
		parent = null;
	}

	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {
		currentGame.validate(game);
		currentGame.validate(player);

		if (nextAction != null) {
			nextAction.validate(this, currentGame);
		}
		if (expectedParent != parent) {
			Validation.validationFailed("Action parent isn't as expected.");
		}
	}

	@Override
	public PlayerActionTokenPick deepCacheClone() {
		// create the object
		PlayerActionTokenPick clone = CACHE.create();

		// copy all objects
		clone.game = game;
		clone.player = player;
		clone.parent = parent;

		// deep clone all owned objects
		clone.nextAction = null;
		if (nextAction != null) {
			clone.nextAction = nextAction.deepCacheClone();
			clone.nextAction.setParent(clone);
		}
		return clone;
	}

	@Override
	public void release() {

		// release all owned objects
		if (nextAction != null) {
			nextAction.release();
		}

		// null all references
		game = null;
		player = null;
		nextAction = null;
		parent = null;

		CACHE.release(this);
	}

	@Override
	public void relink(KemetGame clone) {
		// relink game
		this.game = clone;

		// relink pointers
		player = clone.getPlayerByCopy(player);

		// release all owned objects
		if (nextAction != null) {
			nextAction.relink(clone);
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
	public void setParent(Action parent) {
		this.parent = parent;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {
		if (nextAction != null) {
			PlayerChoicePick nextPlayerChoicePick = nextAction.getNextPlayerChoicePick();
			if (nextPlayerChoicePick == null) {
				game.checkForWinningCondition();

				if (Options.VALIDATE_GAME_BETWEEN_PICKS) {
					game.validate();
				}
			}
			return nextPlayerChoicePick;
		}

		// player won at the beginning of his turn.
		if (game.didPlayerWin(player)) {
			return null;
		}

		PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

		List<Choice> choiceList = pick.choiceList;

		if (player.actionTokenLeft == 1) {
			if (!player.isRowOneUsed()) {
				addRowOneActions(player, choiceList);
			} else if (!player.isRowTwoUsed()) {
				addRowTwoActions(player, choiceList);
			} else if (!player.isRowThreeUsed()) {
				addRowThreeActions(player, choiceList);
			} else {
				addAllActions(player, choiceList);
			}

		} else if (player.actionTokenLeft == 2 && player.getUsedRowCount() == 1) {
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

		return pick.validate();

	}

	public class MoveChoice extends PlayerChoice {

		public MoveChoice(KemetGame game, Player player, byte row) {
			super(game, player);
			this.row = row;
		}

		public byte row;

		@Override
		public String describe() {

			return "Move action, row " + row;
		}

		@Override
		public void choiceActivate() {
			if (row == 1) {
				player.rowOneMoveUsed = true;
			} else {
				player.rowTwoMoveUsed = true;
			}
			player.actionTokenLeft--;

			nextAction = ArmyMoveAction.create(game, player, PlayerActionTokenPick.this);

		}

		@Override
		public int getIndex() {
			if (row == 1) {
				return ChoiceInventory.PICK_ROW_ONE_MOVE;
			}
			return ChoiceInventory.PICK_ROW_TWO_MOVE;
		}

	}

	public class PrayerChoice extends PlayerChoice {

		public PrayerChoice(KemetGame game, Player player, byte row) {
			super(game, player);
			this.row = row;

			if (player.hasPower(PowerList.WHITE_1_PRIEST_1)) {
				increasedPower = 3;
			}
		}

		public byte increasedPower = 2;
		public byte row;

		@Override
		public void choiceActivate() {
			player.modifyPrayerPoints(increasedPower, "prayer action");

			if (player.hasPower(PowerList.BLACK_4_DIVINE_STRENGTH)) {
				player.modifyPrayerPoints((byte) 1, PowerList.BLACK_4_DIVINE_STRENGTH.toString());
			}

			if (row == 2) {
				player.rowTwoPrayUsed = true;
			} else {
				player.rowThreePrayUsed = true;
			}
			player.actionTokenLeft--;

			nextAction = DoneAction.create(PlayerActionTokenPick.this);
		}

		@Override
		public String describe() {

			return "Prayer action, row " + row + ", with " + player.getPrayerPoints() + " prayer points, pray for "
					+ increasedPower + " prayer points";

		}

		@Override
		public int getIndex() {
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
			player.actionTokenLeft--;

			nextAction = UpgradePyramidAction.create(game, player, PlayerActionTokenPick.this);

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

		public RecruitChoice(KemetGame game, Player player, byte row) {
			super(game, player);
			this.row = row;
		}

		public byte row;

		@Override
		public String describe() {

			return "Recruit action row " + row;

		}

		@Override
		public void choiceActivate() {
			player.rowOneRecruitUsed = true;
			player.actionTokenLeft--;

			nextAction = RecruitAction.create(game, player, PlayerActionTokenPick.this);

		}

		@Override
		public int getIndex() {
			return ChoiceInventory.PICK_ROW_ONE_RECRUIT;
		}

	}

	public class BuyPowerChoice extends PlayerChoice {

		public Power power;

		public BuyPowerChoice(KemetGame game, Player player, Power power) {
			super(game, player);
			this.power = power;
		}

		public byte row;

		@Override
		public String describe() {
			return "Buy Power action row 3 : " + power + " for " + getPowerCost() + " prayer points.";
		}

		@Override
		public void choiceActivate() {
			player.actionTokenLeft--;

			if (power.color == Color.WHITE) {
				player.rowThreeBuildWhiteUsed = true;
			} else if (power.color == Color.RED) {
				player.rowThreeBuildRedUsed = true;
			} else if (power.color == Color.BLACK) {
				player.rowThreeBuildBlackUsed = true;
			} else if (power.color == Color.BLUE) {
				player.rowThreeBuildBlueUsed = true;
			}

			// pay the cost
			byte cost = getPowerCost();
			player.modifyPrayerPoints(cost, "Buy power " + power);

			// move the power
			game.movePowerToPlayer(player, power);

			nextAction = power.createNextAction(player, PlayerActionTokenPick.this, game);
		}

		private byte getPowerCost() {
			return player.getPowerCost(power);
		}

		@Override
		public int getIndex() {
			return power.getActionIndex();
		}

	}

	private void addAllActions(Player player, List<Choice> choiceList) {
		addRowOneActions(player, choiceList);
		addRowTwoActions(player, choiceList);
		addRowThreeActions(player, choiceList);
	}

	private void addRowThreeActions(Player player, List<Choice> choiceList) {
		if (!player.rowThreePrayUsed) {
			choiceList.add(new PrayerChoice(game, player, (byte) 3));
		}

		addAllPowerBuyOptions(player, choiceList);
	}

	private void addAllPowerBuyOptions(Player currentPlayer, List<Choice> choiceList) {
		List<Power> availablePowerList = game.availablePowerList;
		for (Power power : availablePowerList) {
			if (playerCanBuyPower(currentPlayer, power)) {
				BuyPowerChoice choice = new BuyPowerChoice(game, currentPlayer, power);
				choiceList.add(choice);
			}
		}

	}

	private boolean playerCanBuyPower(Player currentPlayer, Power power) {
		if (power == null) {
			return false;
		}

		if (!playerHasPyramidForPower(currentPlayer, power)) {
			return false;
		}

		if (!playerHasActionAvailableForPower(currentPlayer, power)) {
			return false;
		}

		if (!playerHasPrayerAvailableForPower(currentPlayer, power)) {
			return false;
		}

		if (playerHasPowerAlready(currentPlayer, power)) {
			return false;
		}

		return true;
	}

	private boolean playerHasPowerAlready(Player currentPlayer, Power power) {
		return currentPlayer.hasPower(power);
	}

	private boolean playerHasPrayerAvailableForPower(Player currentPlayer, Power power) {
		byte powerCost = currentPlayer.getPowerCost(power);
		return currentPlayer.getPrayerPoints() >= -powerCost;
	}

	private boolean playerHasActionAvailableForPower(Player currentPlayer, Power power) {
		if (power.color.equals(Color.WHITE)) {
			return !currentPlayer.rowThreeBuildWhiteUsed;
		}
		if (power.color.equals(Color.BLACK)) {
			return !currentPlayer.rowThreeBuildBlackUsed;
		}
		if (power.color.equals(Color.RED)) {
			return !currentPlayer.rowThreeBuildRedUsed;
		}
		if (power.color.equals(Color.BLUE)) {
			return !currentPlayer.rowThreeBuildBlueUsed;
		}

		throw new IllegalStateException("Power without a valid color found.");
	}

	private boolean playerHasPyramidForPower(Player currentPlayer, Power power) {
		byte level = currentPlayer.getPyramidLevel(power.color);
		if (power.level > level) {
			return false;
		}

		return true;
	}

	private void addRowTwoActions(Player player, List<Choice> choiceList) {
		if (!player.rowTwoMoveUsed) {
			choiceList.add(new MoveChoice(game, player, (byte) 2));
		}
		if (!player.rowTwoPrayUsed) {
			choiceList.add(new PrayerChoice(game, player, (byte) 2));
		}
		if (!player.rowTwoUpgradePyramidUsed) {
			choiceList.add(new UpgradePyramidChoice(game, player));
		}
	}

	private void addRowOneActions(Player player, List<Choice> choiceList) {
		if (!player.rowOneMoveUsed) {
			choiceList.add(new MoveChoice(game, player, (byte) 1));
		}
		if (!player.rowOneRecruitUsed) {
			choiceList.add(new RecruitChoice(game, player, (byte) 1));
		}
	}

	@Override
	public Action getParent() {
		return parent;
	}
}
