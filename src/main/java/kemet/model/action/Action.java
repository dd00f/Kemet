package kemet.model.action;

import kemet.model.KemetGame;
import kemet.model.Model;
import kemet.util.ByteCanonicalForm;

public interface Action extends Model {
	
	
	/**
	 * Get the next player choice pick.
	 * @return the next player choice pick. Null if the action is over.
	 */
	public PlayerChoicePick getNextPlayerChoicePick();
	
	public Action getParent();
	
	public void relink(KemetGame clone);
	
	public void setParent(Action parent);
	
	public void validate(Action expectedParent, KemetGame currentGame);

	@Override
	public Action deepCacheClone();
	
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex);

	public void enterSimulationMode(int playerIndex);
	
	public void stackPendingActionOnParent(Action pendingAction);
	
	
	
}
