package kemet.model.action;

import kemet.model.BoardInventory;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Tile;
import kemet.model.Validation;
import kemet.model.action.choice.ChoiceInventory;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

public class TempleDecisionAction implements Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7403467206973571543L;

	private KemetGame game;

	private Player player;

	private Action parent;

	public Tile templeTile;

	@Override
	public void initialize() {

		game = null;

		parent = null;
		player = null;
		templeTile = null;

	}

	public static Cache<TempleDecisionAction> CACHE = new Cache<TempleDecisionAction>(() -> new TempleDecisionAction());

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
		this.templeTile = clone.getTileByCopy(templeTile);
	}

	@Override
	public TempleDecisionAction deepCacheClone() {

		TempleDecisionAction clone = CACHE.create();

		clone.game = game;
		clone.parent = parent;
		clone.player = player;
		clone.templeTile = templeTile;

		return clone;
	}

	@Override
	public void release() {
		game = null;
		parent = null;
		player = null;
		templeTile = null;

		CACHE.release(this);

	}

	private TempleDecisionAction() {

	}

	public static TempleDecisionAction create(KemetGame game, Action parent, Player player, Tile templeTile) {
		TempleDecisionAction create = CACHE.create();
		create.initialize();

		create.game = game;
		create.parent = parent;
		create.player = player;
		create.templeTile = templeTile;

		return create;
	}

	public KemetGame getGame() {
		return game;
	}

	@Override
	public Action getParent() {
		return parent;
	}

	public class TempleChoice extends PlayerChoice {

		public boolean activateTemple;

		public TempleChoice(KemetGame game, Player player) {
			super(game, player);
		}

		@Override
		public String describe() {
			if( activateTemple ) {
				return "Activate temple : " + templeTile;
			}
			return "Do not activate temple on : " + templeTile;
		}

		@Override
		public void choiceActivate() {
			if( activateTemple ) {
				templeTile.activateTemple();
			}
			templeTile = null;
		}

		@Override
		public int getIndex() {
			if( activateTemple ) {
				return ChoiceInventory.ACTIVATE_OPTIONAL_TEMPLE;
			}
			return ChoiceInventory.DONT_ACTIVATE_OPTIONAL_TEMPLE;
		}
	}

	public PlayerChoicePick addTempleChoice(Player player) {

		PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

		TempleChoice activate = new TempleChoice(game, player);
		activate.activateTemple = true;

		TempleChoice dontActivate = new TempleChoice(game, player);
		dontActivate.activateTemple = false;
		
		pick.choiceList.add(activate);
		pick.choiceList.add(dontActivate);

		return pick;
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {

		if (templeTile != null) {
			return addTempleChoice(player).validate();
		}

		// action is over
		return null;
	}

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {

		// cannonicalForm.set(BoardInventory.STATE_ACTIVATE_OPTIONAL_TEMPLE, player.getState(playerIndex));
		
		player.setCanonicalState(cannonicalForm, BoardInventory.STATE_ACTIVATE_OPTIONAL_TEMPLE, playerIndex);

	}

	@Override
	public void setParent(Action parent) {
		this.parent = parent;
	}

	@Override
	public void enterSimulationMode(int playerIndex) {

	}

	@Override
	public void stackPendingActionOnParent(Action pendingAction) {
		parent.stackPendingActionOnParent(pendingAction);
	}
}
