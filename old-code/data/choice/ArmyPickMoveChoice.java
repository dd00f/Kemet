package kemet.data.choice;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import kemet.data.state.State;
import kemet.model.Army;
import kemet.model.Game;
import kemet.model.Player;

public class ArmyPickMoveChoice extends PlayerChoice {
	
	public ArmyPickMoveChoice(Game game, Player player) {
		super(game, player);
	}

	public static final Logger LOGGER = Logger.getLogger(ArmyPickMoveChoice.class.getName());


	public Army army;

	@Override
	public State choiceActivate() {
		
		// trigger army selection choice
		List<Choice> choiceList = new ArrayList<>();

		ArmyTileMoveChoice.addArmyTileMoveChoice(game, player, army, choiceList, true, army.getMoveCount());
		EndTurnChoice.addEndTurnChoice(game, player, choiceList, null);
		
		return player.actor.pickActionAndActivateOld(choiceList);

	}

	public String describe() {
		
		return "move " + army;

	}

	public static void addArmyPickMoveChoice(Game game, Player player, List<Choice> choiceList) {
		for (Army army : player.armyList) {
			ArmyPickMoveChoice subChoice = new ArmyPickMoveChoice(game, player);
			subChoice.army = army;
			choiceList.add(subChoice);
		}
	}

}
