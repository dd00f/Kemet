package kemet.model.action.choice;

import java.util.List;

import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.action.Endable;
import kemet.model.action.EndableAction;

public class EndTurnChoice extends PlayerChoice {
	
	public Endable  endChoice = null;

	public EndTurnChoice(KemetGame game, Player player) {
		super(game, player);
	}

	@Override
	public String describe() {
		return "End Turn";
	}

	@Override
	public void choiceActivate() {
		if(endChoice != null ) {
			endChoice.end();
		}
	}
	
	public static void addEndTurnChoice(KemetGame game, Player player, List<Choice> choiceList, EndableAction end ) {
		EndTurnChoice endTurnChoice = new EndTurnChoice(game, player);
		endTurnChoice.endChoice = end;
		choiceList.add(endTurnChoice);
	}

	@Override
	public int getIndex() {
		return ChoiceInventory.PASS_CHOICE_INDEX;
	}

}
