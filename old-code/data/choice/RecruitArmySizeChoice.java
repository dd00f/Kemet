package kemet.data.choice;

import java.util.List;

import kemet.data.state.State;
import kemet.model.Army;
import kemet.model.Game;
import kemet.model.Player;
import kemet.model.action.RecruitAction;

public class RecruitArmySizeChoice extends PlayerChoice {

	public RecruitArmySizeChoice(Game game, Player player) {
		super(game, player);
	}

	public byte armySize;
	public byte cost;
	private RecruitAction action;

	@Override
	public State choiceActivate() {

		action.recruitSize = armySize;
		action.cost = cost;
		return null;
	}

	public String describe() {

		return "Recruit army size " + armySize + " for " + cost + " prayer points.";

	}

	public static void addRecruitArmySizeChoice(Game game, Player player, List<Choice> choiceList, RecruitAction action) {

		byte max = player.maximumArmySize;
		byte min = 1;
		Army army = action.tile.getArmy();
		if (army != null && army.owningPlayer == player) {
			max -= army.armySize;
			min = 0;
		}

		max = (byte) Math.min(max, player.availableArmyTokens);
		max = (byte) Math.min(max, player.getPrayerPoints());

		for (byte i = min; i <= max; ++i) {
			RecruitArmySizeChoice subChoice = new RecruitArmySizeChoice(game, player);
			subChoice.armySize = i;
			subChoice.action = action;
			subChoice.cost = i;

			choiceList.add(subChoice);
		}
	}

}
