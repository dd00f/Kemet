package kemet.data.choice;

import java.util.ArrayList;
import java.util.List;

import kemet.data.state.State;
import kemet.model.Army;
import kemet.model.Game;
import kemet.model.Player;
import kemet.model.action.BattleAction;

public class RecallArmyChoice extends PlayerChoice {

	public BattleAction battle;
	public boolean isAttacker;
	public boolean recall;

	public RecallArmyChoice(Game game, Player player) {
		super(game, player);
	}

	@Override
	public String describe() {
		Army army = battle.getArmy(isAttacker);

		String action = "Recall ";
		if (!recall) {
			action = "Don't recall ";
		}
		return action + army;
	}

	@Override
	public State choiceActivate() {
		if( isAttacker ) {
			battle.attackerRecall = recall;
		}
		else {
			battle.defenderRecall = recall;
		}
		return null;
	}

	public static void pickRecallOption(Game game, BattleAction battle, boolean attacker) {

		Army army = battle.getArmy(attacker);

		List<Choice> choiceList = new ArrayList<>();

		{
			RecallArmyChoice recallChoice = new RecallArmyChoice(game, army.owningPlayer);
			recallChoice.battle = battle;
			recallChoice.isAttacker = attacker;
			recallChoice.recall = true;

			choiceList.add(recallChoice);
		}
		{
			RecallArmyChoice recallChoice = new RecallArmyChoice(game, army.owningPlayer);
			recallChoice.battle = battle;
			recallChoice.isAttacker = attacker;
			recallChoice.recall = false;

			choiceList.add(recallChoice);
		}

		army.owningPlayer.actor.pickActionAndActivateOld(choiceList);
	}

}
