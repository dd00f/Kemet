package kemet.model.action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kemet.Options;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.PowerList;
import kemet.model.Validation;
import kemet.model.action.choice.Choice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class GameAction implements Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5798076013017724149L;
	
	public boolean initialized = false;
	
	public ChainedAction chainedActions;


	public static Cache<GameAction> CACHE = new Cache<GameAction>(() -> new GameAction());
	
	@Override
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
		clone.chainedActions = chainedActions.deepCacheClone();
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
            
            if( game.roundNumber > Options.GAME_TURN_LIMIT ) {
            	game.victoryConditionTriggered = true;
            	game.findWinner();
                return null;
            }
        	
            chainedActions.add(NightAction.create(game, chainedActions));
            
            addBlue4ReinforcementsAction();
            
            addWhite3HandOfGodAction();
            
            if (!game.isFirstTurn()) {
            	chainedActions.add(DawnAction.create(game, chainedActions));
            }
            chainedActions.add(createDayAction(game, chainedActions));
            nextPlayerChoicePick = chainedActions.getNextPlayerChoicePick();
        }
        
        if( Options.VALIDATE_PLAYER_CHOICE_PICK_INDEX ) {
        	validatePlayerChoicePickIndex(nextPlayerChoicePick);
        }

        return nextPlayerChoicePick;
    }

	private void addBlue4ReinforcementsAction() {
		
		for (Player player : chainedActions.getGame().playerByInitiativeList) {
			
			if( player.hasPower(PowerList.BLUE_4_REINFORCEMENTS)) {
				RecruitAction action = RecruitAction.create(chainedActions.getGame(), player, chainedActions);
				action.allowPaidRecruit = false;
				action.freeRecruitLeft = 4;
				action.canRecruitOnAnyArmy = true;
				chainedActions.add(action);
			}
		}
	}

	private void addWhite3HandOfGodAction() {
		
		for (Player player : chainedActions.getGame().playerByInitiativeList) {
			
			if( player.hasPower(PowerList.WHITE_3_HAND_OF_GOD)) {
				UpgradePyramidAction action = UpgradePyramidAction.create(chainedActions.getGame(), player, chainedActions);
				action.freeLevel = true;
				chainedActions.add(action);
			}
		}
	}

	private void validatePlayerChoicePickIndex(PlayerChoicePick nextPlayerChoicePick) {
		
		List<Choice> choiceList = nextPlayerChoicePick.choiceList;
		Set<Integer> indexes = new HashSet<>();
		
		for (Choice choice : choiceList) {
			int index = choice.getIndex();
			if( indexes.contains(index)) {
				String message = "two choices have the same index " + index + " " + choiceList;
				log.error(message);
				throw new IllegalStateException(message);
			}
			
		}
		
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
