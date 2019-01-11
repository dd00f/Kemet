
package kemet.model.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kemet.model.KemetGame;
import kemet.model.Validation;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

/**
 * DoneAction
 * 
 * @author Steve McDuff
 */
public class DoneAction implements Action
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 2588431812133856280L;
	
	private static final Logger LOGGER = LogManager.getLogger( DoneAction.class);

	
	private Action parent;
	
	public static Cache<DoneAction> CACHE = new Cache<DoneAction>(() -> new DoneAction());

	public static DoneAction create(Action parent)
    {
        DoneAction create = CACHE.create();
        create.initialize();
        create.parent = parent;
        return create;
    }

	public void initialize() {
		parent= null;
	}
	
	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {
		
		if( expectedParent != parent ) {
			Validation.validationFailed("Action parent isn't as expected.");
		}
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

	@Override
	public DoneAction deepCacheClone() {
		// create the object
		DoneAction clone = CACHE.create();

		// copy all objects
		clone.parent = parent;


		return clone;
	}

	@Override
	public void release() {
		// null all references
		parent = null;

		CACHE.release(this);
	}

	@Override
	public void relink(KemetGame clone) {


	}

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {
		LOGGER.warn("Shouldn't be able to reach this code");
		
	}


}
