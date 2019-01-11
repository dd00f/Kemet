
package kemet.model.action;

import java.util.List;

import kemet.Options;
import kemet.model.BoardInventory;
import kemet.model.KemetGame;
import kemet.model.Player;
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
		
		if( nextAction != null ) {
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
				
				if( Options.VALIDATE_GAME_BETWEEN_PICKS ) {
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
			if( row == 1 ) {
				return ChoiceInventory.PICK_ROW_ONE_MOVE;
			}
			return ChoiceInventory.PICK_ROW_TWO_MOVE;
		}

	}

	public class PrayerChoice extends PlayerChoice {

		public PrayerChoice(KemetGame game, Player player, byte row) {
			super(game, player);
			this.row = row;
		}

		public byte increasedPower = 2;
		public byte row;

		@Override
		public void choiceActivate() {
			player.modifyPrayerPoints(increasedPower, "prayer action");
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
			if( row == 2 ) {
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

			return "Upgrade Pyramid.";

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

	private void addAllActions(Player player, List<Choice> choiceList) {
		addRowOneActions(player, choiceList);
		addRowTwoActions(player, choiceList);
		addRowThreeActions(player, choiceList);
	}

	private void addRowThreeActions(Player player, List<Choice> choiceList) {
		if (!player.rowThreePrayUsed) {
			choiceList.add(new PrayerChoice(game, player, (byte) 3));
		}
		
		// TODO add options to purchase power tiles
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
