package kemet.model.action;

import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Validation;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

public class GameAction implements Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5798076013017724149L;
	
	public boolean initialized = false;
	
	public ChainedAction chainedActions;


	public static Cache<GameAction> CACHE = new Cache<GameAction>(() -> new GameAction());
	
	public void initialize() {
		initialized = false;
		chainedActions = null;
	}
	
	
	@Override
	public GameAction deepCacheClone() {
		// create the object
		GameAction clone = CACHE.create();

		// copy all objects
		clone.initialized = initialized;

		// deep clone all owned objects
		clone.chainedActions = (ChainedAction) chainedActions.deepCacheClone();
		clone.chainedActions.setParent(clone);

		return clone;
	}
	
	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {
		chainedActions.validate(this, currentGame);

		
		if( expectedParent != null ) {
			Validation.validationFailed("Action parent isn't as expected.");
		}
	}

	@Override
	public void release() {
		// null all references
		chainedActions.release();
		chainedActions = null;

		CACHE.release(this);
	}

	@Override
	public void relink(KemetGame clone) {
		chainedActions.relink(clone);
	}

	
	public static GameAction create(KemetGame game) {
		GameAction create = CACHE.create();
		create.initialize();
		create.chainedActions = ChainedAction.create(game, create);
		return create;
	}
	
    @Override
    public PlayerChoicePick getNextPlayerChoicePick()
    {
    	KemetGame game = chainedActions.getGame();
    	
        if( ! initialized ) {
        	
    		for (Player player : game.playerByInitiativeList)
            {
    			chainedActions.add(InitializationPlayerPyramidAction.create(game, player, chainedActions) );
            }
            for (Player player : game.playerByInitiativeList)
            {
            	chainedActions.add(InitializationPlayerRecruitAction.create(game, player, chainedActions) );
            }

            initialized = true;
        }

        if (game.hasWinner()) {
            return null;
        }
        
        PlayerChoicePick nextPlayerChoicePick = chainedActions.getNextPlayerChoicePick();
        if( nextPlayerChoicePick == null ) {
            // start a new turn when we're out of options
            if (game.victoryConditionTriggered) {
            	game.findWinner();
                return null;
            }


            game.incrementTurnCount();
        	
            chainedActions.add(NightAction.create(game, this));
            if (!game.isFirstTurn()) {
            	chainedActions.add(DawnAction.create(game, this));
            }
            chainedActions.add(createDayAction(game, chainedActions));
            nextPlayerChoicePick = chainedActions.getNextPlayerChoicePick();
        }

        return nextPlayerChoicePick;
    }

	private Action createDayAction(KemetGame game, Action parent) {
		ChainedAction action = ChainedAction.create(game, parent);
		
        for (int i = 0; i < 5; ++i)
        {
            // five player turn
            for (Player player : game.playerByInitiativeList)
            {
            	action.add(PlayerActionTokenPick.create(game, player, action));
            }
        }
		
		return action;
	}

	@Override
	public Action getParent() {
		return null;
	}

	@Override
	public void setParent(Action parent) {
		
		
	}


	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {
		chainedActions.fillCanonicalForm(cannonicalForm, playerIndex);
	}


}