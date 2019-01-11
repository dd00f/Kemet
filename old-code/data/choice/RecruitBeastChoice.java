package kemet.data.choice;

import java.util.List;

import kemet.data.state.State;
import kemet.model.Beast;
import kemet.model.Game;
import kemet.model.Player;
import kemet.model.action.RecruitAction;

public class RecruitBeastChoice  extends PlayerChoice {

	public RecruitBeastChoice(Game game, Player player) {
		super(game, player);
	}

	public Beast beast;
	private RecruitAction action;

	@Override
	public State choiceActivate() {

		action.beast = beast;
		return null;
	}

	public String describe() {

		if( beast == null ) {
			return "Recruit no beast";
		}
		return "Recruit beast \"" + beast.name;

	}

	public static void addRecruitBeastChoice(Game game, Player player, List<Choice> choiceList, RecruitAction action) {
		
		for (Beast beast : player.availableBeasts) {
			RecruitBeastChoice subChoice = new RecruitBeastChoice(game, player);
			subChoice.beast = beast;
			subChoice.action = action;

			choiceList.add(subChoice);
		}
		
		{
			
			RecruitBeastChoice subChoice = new RecruitBeastChoice(game, player);
			subChoice.beast = null;
			subChoice.action = action;

			choiceList.add(subChoice);
		}
	}

}
