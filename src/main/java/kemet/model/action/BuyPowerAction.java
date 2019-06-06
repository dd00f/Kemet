
package kemet.model.action;

import java.util.List;

import kemet.model.Color;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Power;
import kemet.model.Validation;
import kemet.model.action.choice.Choice;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

/**
 * BuyPowerAction
 * 
 * @author Steve McDuff
 */
public class BuyPowerAction implements Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6525066429860592023L;
	
	private KemetGame game;
	private Player player;

	public Action nextAction;
	private Action parent;
	
	public Color color;
	public byte costBoost;

	public static Cache<BuyPowerAction> CACHE = new Cache<BuyPowerAction>(
			() -> new BuyPowerAction());

	private BuyPowerAction() {

	}

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {
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
		costBoost = 0;
		color = null;
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
	public BuyPowerAction deepCacheClone() {
		// create the object
		BuyPowerAction clone = CACHE.create();

		// copy all objects
		clone.game = game;
		clone.player = player;
		clone.parent = parent;
		clone.color = color;
		clone.costBoost = costBoost;

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
	public void setParent(Action parent) {
		this.parent = parent;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {
		
		if( nextAction != null ) {

			return nextAction.getNextPlayerChoicePick();
		}
		
		
		PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

		List<Choice> choiceList = pick.choiceList;
		
		addAllPowerBuyOptions(player, choiceList, color);
		
		return pick;
	}


	@Override
	public Action getParent() {
		return parent;
	}
	

	private void addAllPowerBuyOptions(Player currentPlayer, List<Choice> choiceList, Color filterColor) {
		List<Power> availablePowerList = game.availablePowerList;
		for (Power power : availablePowerList) {
			if( power == null ) {
				continue;
			}
			
			if(! power.color.equals(filterColor)) {
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
		byte powerCost = (byte) (currentPlayer.getPowerCost(power) + costBoost);
		return currentPlayer.getPrayerPoints() >= -powerCost;
	}

	private boolean playerHasPyramidForPower(Player currentPlayer, Power power) {
		byte level = currentPlayer.getPyramidLevel(power.color);
		if (power.level > level) {
			return false;
		}

		return true;
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
			player.modifyPrayerPoints(cost, "Buy power " + power);

			// move the power
			game.movePowerToPlayer(player, power);
			
			nextAction = power.createNextAction(player, BuyPowerAction.this, game);
			
		}

		private byte getPowerCost() {
			return (byte) (player.getPowerCost(power) + costBoost);
		}

		@Override
		public int getIndex() {
			return power.getActionIndex();
		}

	}
}
