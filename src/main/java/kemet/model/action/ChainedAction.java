package kemet.model.action;

import java.util.ArrayList;
import java.util.List;

import kemet.model.KemetGame;
import kemet.model.Validation;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

public class ChainedAction implements Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2573734854040221566L;

	private List<Action> actionChain = new ArrayList<Action>();

	private KemetGame game;

	private Action parent;

	public static Cache<ChainedAction> CACHE = new Cache<ChainedAction>(() -> new ChainedAction());
	
	public void initialize() {
		game = null;
		parent= null;
		actionChain.clear();
	}
	
	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {
		currentGame.validate(game);

		for (Action action : actionChain) {
			action.validate(this, currentGame);
		}
		
		if( expectedParent != parent ) {
			Validation.validationFailed("Action parent isn't as expected.");
		}
	}
	
	@Override
	public ChainedAction deepCacheClone() {
		// create the object
		ChainedAction clone = CACHE.create();

		// copy all objects
		clone.game = game;
		clone.parent = parent;

		// deep clone all owned objects
		// clone.AAA = AAA.deepCacheClone();

		// deep clone all owned lists
		clone.actionChain.clear();
		for (Action currentAction : actionChain) {
			Action deepCacheClone = currentAction.deepCacheClone();
			deepCacheClone.setParent(clone);
			clone.actionChain.add(deepCacheClone);
		}

		return clone;
	}

	@Override
	public void release() {
		// null all references
		game = null;
		parent = null;

		// clear all lists
		for (Action action : actionChain) {
			action.release();
		}
		actionChain.clear();

		CACHE.release(this);
	}

	@Override
	public void relink(KemetGame clone) {
		// relink game
		this.game = clone;

		// relink pointers

		// relink lists
		for (Action action : actionChain) {
			action.relink(clone);
		}
	}


	private ChainedAction() {

	}

	public static ChainedAction create(KemetGame game, Action parent) {
		ChainedAction create = CACHE.create();
		create.initialize();

		create.game = game;
		create.parent = parent;

		return create;
	}

	@Override
	public void setParent(Action parent) {
		this.parent = parent;
	}

	public KemetGame getGame() {
		return game;
	}

	public void add(Action action) {
		actionChain.add(action);
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {
		while (true) {
			if (actionChain.size() == 0) {
				return null;
			}

			Action nextAction = actionChain.get(0);
			PlayerChoicePick nextPlayerChoicePick = nextAction.getNextPlayerChoicePick();
			if (nextPlayerChoicePick != null) {
				return nextPlayerChoicePick;
			}
			nextAction.release();
			actionChain.remove(0);
		}
	}

	@Override
	public Action getParent() {
		return parent;
	}

	public void clear() {
		actionChain.clear();
	}
	
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {
		actionChain.get(0).fillCanonicalForm(cannonicalForm, playerIndex);
	}

}
