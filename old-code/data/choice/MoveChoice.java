package kemet.data.choice;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import kemet.data.state.State;
import kemet.model.Game;
import kemet.model.Player;

public class MoveChoice extends PlayerChoice {
	
	public MoveChoice(Game game, Player player, byte row) {
		super(game, player);
		this.row = row;
	}

	public static final Logger LOGGER = Logger.getLogger(MoveChoice.class.getName());

	public byte row;

	public String describe() {
		
		return "Move action, row " + row;

	}

	@Override
	public State choiceActivate() {
		if( row == 1 ) {
			player.rowOneMoveUsed = true;
		}
		else {
			player.rowTwoMoveUsed = true;
		}
		player.actionTokenLeft--;
		
		// trigger army selection choice
		List<Choice> choiceList = new ArrayList<>();

		ArmyPickMoveChoice.addArmyPickMoveChoice(game, player, choiceList);
		EndTurnChoice.addEndTurnChoice(game, player, choiceList, null);
		
		return player.actor.pickActionAndActivateOld(choiceList);
	}



}
