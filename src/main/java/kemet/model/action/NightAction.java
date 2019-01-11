package kemet.model.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kemet.model.KemetGame;
import kemet.model.Validation;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

public class NightAction implements Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5798076013017724149L;
	
	private static final Logger LOGGER = LogManager.getLogger( NightAction.class);

	private KemetGame game;

	private Action parent;

	public static Cache<NightAction> CACHE = new Cache<NightAction>(() -> new NightAction());
	
	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {
		LOGGER.warn("Shouldn't be able to reach this code");

	}	

	@Override
	public void initialize() {
		game = null;
		parent = null;

	}

	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {
		currentGame.validate(game);

		if (expectedParent != parent) {
			Validation.validationFailed("Action parent isn't as expected.");
		}
	}

	@Override
	public NightAction deepCacheClone() {
		// create the object
		NightAction clone = CACHE.create();

		// copy all objects
		clone.game = game;
		clone.parent = parent;

		return clone;
	}

	@Override
	public void release() {
		// null all references
		game = null;
		parent = null;

		CACHE.release(this);
	}

	@Override
	public void relink(KemetGame clone) {
		// relink game
		this.game = clone;
	}

	private NightAction() {

	}

	public static NightAction create(KemetGame game, Action parent) {
		NightAction create = CACHE.create();
		create.initialize();
		create.game = game;
		create.parent = parent;
		return create;
	}

	public KemetGame getGame() {
		return game;
	}

	@Override
	public void setParent(Action parent) {
		this.parent = parent;
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {

		game.resetAvailableActions();

		game.provideNightPrayerPoints();

		game.provideNightTempleVictoryPoints();

		game.provideNightTemplePrayerPoints();

		game.provideNightDiCards();

		game.activateNightPowers();

		game.describeGame();

		return null;
	}

	@Override
	public Action getParent() {
		return parent;
	}

}
