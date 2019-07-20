
package kemet.model.action;

import java.util.List;

import kemet.model.BoardInventory;
import kemet.model.Color;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Power;
import kemet.model.Validation;
import kemet.model.action.choice.Choice;
import kemet.model.action.choice.ChoiceInventory;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

/**
 * BuyPowerAction
 * 
 * @author Steve McDuff
 */
public class BuyPowerAction extends DiCardAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6525066429860592023L;

	public Action nextAction;

	public Color color;
	public byte costBoost;

	public static Cache<BuyPowerAction> CACHE = new Cache<BuyPowerAction>(() -> new BuyPowerAction());

	private BuyPowerAction() {

	}

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {

		cannonicalForm.set(BoardInventory.STATE_BUY_POWER_COLOR + color.ordinal(), player.getState(playerIndex));

		if (nextAction != null) {
			nextAction.fillCanonicalForm(cannonicalForm, playerIndex);
		}
	}

	@Override
	public void internalInitialize() {

		nextAction = null;
		costBoost = 0;
		color = null;
		super.internalInitialize();
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
		super.validate(expectedParent, currentGame);
	}

	@Override
	public BuyPowerAction deepCacheClone() {
		// create the object
		BuyPowerAction clone = CACHE.create();

		copy(clone);

		return clone;
	}

	private void copy(BuyPowerAction clone) {
		// copy all objects
		clone.color = color;
		clone.costBoost = costBoost;

		// deep clone all owned objects
		clone.nextAction = null;
		if (nextAction != null) {
			clone.nextAction = nextAction.deepCacheClone();
			clone.nextAction.setParent(clone);
		}
		super.copy(clone);
	}

	@Override
	public void release() {

		// release all owned objects
		if (nextAction != null) {
			nextAction.release();
		}

		// null all references
		nextAction = null;

		super.release();

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

		super.relink(clone);
	}

	public static BuyPowerAction create(KemetGame game, Player player, Action parent, Color color, byte costBoost) {
		BuyPowerAction create = CACHE.create();
		create.initialize();

		create.game = game;
		create.player = player;
		create.parent = parent;
		create.color = color;
		create.costBoost = costBoost;

		return create;
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {

		PlayerChoicePick nextPlayerChoicePick = super.getNextPlayerChoicePick();
		if (nextPlayerChoicePick != null) {
			return nextPlayerChoicePick;
		}

		if (nextAction != null) {

			return nextAction.getNextPlayerChoicePick();
		}

		PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

		List<Choice> choiceList = pick.choiceList;

		addAllPowerBuyOptions(player, choiceList, color);

		// nothing to do, action is done
		if (choiceList.size() == 0) {
			return null;
		}
		
		addGenericDiCardChoice(choiceList);

		BuyNothingChoice nothing = new BuyNothingChoice(game, player);
		choiceList.add(nothing);

		return pick;
	}

	private void addAllPowerBuyOptions(Player currentPlayer, List<Choice> choiceList, Color filterColor) {
		List<Power> availablePowerList = game.availablePowerList;
		for (Power power : availablePowerList) {
			if (power == null) {
				continue;
			}

			if (!power.color.equals(filterColor)) {
				continue;
			}

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
		byte powerCost = (byte) (currentPlayer.getPowerCost(power) - costBoost);
		return currentPlayer.getPrayerPoints() >= -powerCost;
	}

	private boolean playerHasPyramidForPower(Player currentPlayer, Power power) {
		byte level = currentPlayer.getPyramidLevel(power.color);
		if (power.level > level) {
			return false;
		}

		return true;
	}

	public class BuyNothingChoice extends PlayerChoice {

		public BuyNothingChoice(KemetGame game, Player player) {
			super(game, player);
		}

		@Override
		public String describe() {
			return "Buy nothing";
		}

		@Override
		public void choiceActivate() {

			nextAction = DoneAction.create(parent);

		}

		@Override
		public int getIndex() {
			return ChoiceInventory.BUY_NOTHING;
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

			// trigger the power purchase

			// pay the cost
			byte cost = getPowerCost();
			player.modifyPrayerPoints(cost, power);

			// move the power
			game.movePowerToPlayer(player, power);

			nextAction = power.createNextAction(player, BuyPowerAction.this, game);

		}

		private byte getPowerCost() {
			return (byte) (player.getPowerCost(power) - costBoost);
		}

		@Override
		public int getIndex() {
			return power.getActionIndex();
		}

	}
}
