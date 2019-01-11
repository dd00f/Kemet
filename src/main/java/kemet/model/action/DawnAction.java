package kemet.model.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kemet.model.KemetGame;
import kemet.model.Validation;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

public class DawnAction implements Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5798076013017724149L;

	private static final Logger LOGGER = LogManager.getLogger( DawnAction.class);

	
	private KemetGame game;

	private Action parent;


	public static Cache<DawnAction> CACHE = new Cache<DawnAction>(() -> new DawnAction());
	
	private DawnAction() {
		
	}
	
	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {
		LOGGER.warn("Shouldn't be able to reach this code");

	}	
	
	public void initialize() {
		game = null;
		parent= null;
	}
	
	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {
		currentGame.validate(game);

		
		if( expectedParent != parent ) {
			Validation.validationFailed("Action parent isn't as expected.");
		}
	}
	
	@Override
	public DawnAction deepCacheClone() {
		// create the object
		DawnAction clone = CACHE.create();

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

	
	public static DawnAction create(KemetGame game, Action parent) {
		DawnAction create = CACHE.create();
		create.initialize();
		create.game = game;
		create.parent = parent;
		return create;
	}

	public KemetGame getGame() {
		return game;
	}

    @Override
    public PlayerChoicePick getNextPlayerChoicePick()
    {
        return null;
    }

	@Override
	public Action getParent() {
		return parent;
	}
	@Override
	public void setParent(Action parent) {
		this.parent = parent;
	}

}
