
package kemet.model.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Validation;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

/**
 * PrayAction
 * 
 * @author Steve McDuff
 */
public class PrayAction implements Action
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8302298681069845516L;


	private static final Logger LOGGER = LogManager.getLogger( PrayAction.class);

	
	private Action parent;

	private Player player;

	private byte increasedPower;
	
	public static Cache<PrayAction> CACHE = new Cache<PrayAction>(() -> new PrayAction());

	public static PrayAction create(Action parent, Player player, byte increasedPower)
    {
        PrayAction create = CACHE.create();
        create.initialize();
        create.parent = parent;
        create.player = player;
        create.increasedPower = increasedPower;
        return create;
    }

	@Override
	public void initialize() {
		parent= null;
		player = null;
		increasedPower = 0;
	}
	
	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {
		
		currentGame.validate(player);
		
		if( expectedParent != parent ) {
			Validation.validationFailed("Action parent isn't as expected.");
		}
	}
	
    @Override
    public PlayerChoicePick getNextPlayerChoicePick()
    {
    	player.modifyPrayerPoints(increasedPower, "prayer action");
    	
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
	public PrayAction deepCacheClone() {
		// create the object
		PrayAction clone = CACHE.create();

		// copy all objects
		clone.parent = parent;
		clone.player = player;
		clone.increasedPower = increasedPower;

		return clone;
	}

	@Override
	public void release() {
		// null all references
		parent = null;
		player = null;

		CACHE.release(this);
	}

	@Override
	public void relink(KemetGame clone) {
		player = clone.getPlayerByCopy(player);
	}

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {
		LOGGER.warn("PrayAction fillCanonicalForm Shouldn't be able to reach this code");
		
	}

	@Override
	public void enterSimulationMode(int playerIndex) {

	}
	
	@Override
	public void stackPendingActionOnParent(Action pendingAction) {
		parent.stackPendingActionOnParent(pendingAction);
	}
}
