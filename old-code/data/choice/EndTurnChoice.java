package kemet.data.choice;

import java.util.List;

import kemet.data.state.State;
import kemet.model.Game;
import kemet.model.Player;
import kemet.model.action.Endable;
import kemet.model.action.EndableAction;

public class EndTurnChoice extends PlayerChoice {
	
	public Endable  endChoice = null;

	public EndTurnChoice(Game game, Player player) {
		super(game, player);
	}

	@Override
	public String describe() {
		return "End Turn";
	}

	@Override
	public State choiceActivate() {
		if(endChoice != null ) {
			endChoice.end();;
		}
		return null;
	}
	
	public static void addEndTurnChoice(Game game, Player player, List<Choice> choiceList, EndableAction end ) {
		EndTurnChoice endTurnChoice = new EndTurnChoice(game, player);
		endTurnChoice.endChoice = end;
		choiceList.add(endTurnChoice);
	}

}
