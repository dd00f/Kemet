package kemet.data.choice;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import kemet.data.state.State;
import kemet.model.Army;
import kemet.model.Game;
import kemet.model.Player;
import kemet.model.action.BattleAction;
import kemet.model.action.RecruitAction;

public class RecruitChoice extends PlayerChoice {

	public RecruitChoice(Game game, Player player, byte row) {
		super(game, player);
		this.row = row;
	}

	public static final Logger LOGGER = Logger.getLogger(RecruitChoice.class.getName());

	public byte row;

	public String describe() {

		return "Recruit action row " + row;

	}

	@Override
	public State choiceActivate() {
		player.rowOneRecruitUsed = true;
		player.actionTokenLeft--;

		// trigger army selection choice
		List<Choice> choiceList = new ArrayList<>();

		RecruitAction action = new RecruitAction(game, player);
		action.player = player;
		while (!action.isEnded()) {
			choiceList.clear();

			if (action.playerCanRecruitArmy() || action.playerCanRecruitBeast()) {

				RecruitPickTileChoice.addRecruitPickTileChoice(game, player, choiceList, action);
				EndTurnChoice.addEndTurnChoice(game, player, choiceList, action);
				player.actor.pickActionAndActivateOld(choiceList);

				if (!action.isEnded()) {
					executeRecruitActionOnTile(action);
				}
			} else {
				System.out.println("Player " + player.name + " recruit action ended. No more capacity to recruit.");
				action.end();
			}
		}
		return null;
	}

	private void executeRecruitActionOnTile(RecruitAction action) {
		// trigger army selection choice
		List<Choice> choiceList = new ArrayList<>();

		choiceList.clear();
		RecruitArmySizeChoice.addRecruitArmySizeChoice(game, player, choiceList, action);
		player.actor.pickActionAndActivateOld(choiceList);

		if (action.canRecruitBeast()) {
			choiceList.clear();
			RecruitBeastChoice.addRecruitBeastChoice(game, player, choiceList, action);
			player.actor.pickActionAndActivateOld(choiceList);
		}

		Army army = action.createArmy();

		if (action.resultsInCombat()) {
			BattleAction battle = new BattleAction(game);
			battle.attackingArmy = army;
			battle.defendingArmy = action.tile.getArmy();
			battle.tile = action.tile;

			BattleFlow flow = new BattleFlow(game, battle);
			flow.triggerBattle();
		} else {
			army.moveToTile(action.tile);
		}
	}

}
