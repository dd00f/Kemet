package kemet.model.action;

import java.util.logging.Logger;

import kemet.model.BattleCard;
import kemet.model.BoardInventory;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Validation;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RemoveBattleCardAction implements Action {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5934665242964291185L;


	public static final Logger LOGGER = Logger.getLogger(RemoveBattleCardAction.class.getName());


	private KemetGame game;

	private Player player;
	
	private Action parent;

	public BattleCard newBattleCard;
	public BattleCard removedBattleCard;
	

	@Override
	public void initialize() {

		game = null;

		parent = null;
		player = null;

		newBattleCard = null;
		removedBattleCard = null;

	}

	public static Cache<RemoveBattleCardAction> CACHE = new Cache<RemoveBattleCardAction>(() -> new RemoveBattleCardAction());

	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {
		currentGame.validate(game);
		currentGame.validate(player);

		if (expectedParent != parent) {
			Validation.validationFailed("Action parent isn't as expected.");
		}
	}

	@Override
	public void relink(KemetGame clone) {
		this.game = clone;
		this.player = clone.getPlayerByCopy(player);
	}

	@Override
	public RemoveBattleCardAction deepCacheClone() {

		RemoveBattleCardAction clone = CACHE.create();

		clone.game = game;
		clone.parent = parent;
		clone.newBattleCard = newBattleCard;
		clone.removedBattleCard = removedBattleCard;
		clone.player = player;

		return clone;
	}

	@Override
	public void release() {
		game = null;
		parent = null;
		player = null;
		newBattleCard = null;
		removedBattleCard = null;
		
		CACHE.release(this);

	}

	private RemoveBattleCardAction() {

	}

	public static RemoveBattleCardAction create(KemetGame game, Action parent, Player player) {
		RemoveBattleCardAction create = CACHE.create();
		create.initialize();

		create.game = game;
		create.parent = parent;
		create.player = player;

		return create;
	}

	public KemetGame getGame() {
		return game;
	}

	@Override
	public Action getParent() {
		return parent;
	}

	public class PickBattleCardChoice extends PlayerChoice {

		public BattleCard card;

		public PickBattleCardChoice(KemetGame game, Player player) {
			super(game, player);
		}

		@Override
		public String describe() {
			return "Remove battle card " + card;
		}

		@Override
		public void choiceActivate() {
			removedBattleCard = card;
			player.availableBattleCards.remove(card);
			player.availableBattleCards.add(newBattleCard);
			player.validateCardCount();
		}

		@Override
		public int getIndex() {
			return card.getPickChoiceIndex();
		}
	}

	public PlayerChoicePick addPickBattleCardChoice(Player player) {

		PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

		for (BattleCard card : player.availableBattleCards) {
			PickBattleCardChoice pickBattleCardChoice = new PickBattleCardChoice(game, player);
			pickBattleCardChoice.card = card;
			pick.choiceList.add(pickBattleCardChoice);
		}

		return pick;
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {


		if (removedBattleCard == null) {
			return addPickBattleCardChoice(player).validate();
		}
		
		// action is over
		return null;
	}

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {

		cannonicalForm.set(BoardInventory.STATE_PICK_BATTLECARD_TO_REMOVE, player.getState(playerIndex));

	}


	@Override
	public void setParent(Action parent) {
		this.parent = parent;
	}

}
