package kemet.data.state;

import java.util.ArrayList;
import java.util.List;

import kemet.data.choice.Choice;
import kemet.data.choice.MoveChoice;
import kemet.data.choice.PrayerChoice;
import kemet.data.choice.RecruitChoice;
import kemet.data.choice.UpgradePyramidChoice;
import kemet.model.Game;
import kemet.model.Player;

public class DayState implements State {

	public Game game;

	@Override
	public State triggerNextAction() {

		for (int i = 0; i < 5; ++i) {
			// five player turn
			for (Player player : game.playerByInitiativeList) {
				State newState = createPlayerActionTurn(player);
				if (newState != null) {
					return newState;
				}
			}
		}

		return null;
	}

	private State createPlayerActionTurn(Player player) {

		if (game.didPlayerWin(player)) {
			return new PlayerWonState(player);
		}

		List<Choice> choiceList = createPlayerChoiceList(player);
		Choice pickAction = player.actor.pickActionOld(choiceList);
		State newState = pickAction.activate();
		if (newState != null) {
			newState.triggerNextAction();
		}
		
		game.checkForWinningCondition();
		game.validate();
		return null;
	}



	private List<Choice> createPlayerChoiceList(Player player) {
		List<Choice> choiceList = new ArrayList<>(); 
				
		if (player.actionTokenLeft == 1) {
			if( ! player.isRowOneUsed() ) {
				addRowOneActions(player, choiceList);
			}
			else if( ! player.isRowTwoUsed() ) {
				addRowTwoActions(player, choiceList);
			}
			else if( ! player.isRowThreeUsed() ) {
				addRowThreeActions(player, choiceList);
			}
			else {
				addAllActions(player, choiceList);
			}

		} else if (player.actionTokenLeft == 2 && player.getUsedRowCount() == 1 ) {
			if( ! player.isRowOneUsed() ) {
				addRowOneActions(player, choiceList);
			}
			if( ! player.isRowTwoUsed() ) {
				addRowTwoActions(player, choiceList);
			}
			if( ! player.isRowThreeUsed() ) {
				addRowThreeActions(player, choiceList);
			}

		} else {
			addAllActions(player, choiceList);
			
		}

		return choiceList;
	}

	private void addAllActions(Player player, List<Choice> choiceList) {
		addRowOneActions(player, choiceList);
		addRowTwoActions(player, choiceList);
		addRowThreeActions(player, choiceList);
	}

	private void addRowThreeActions(Player player, List<Choice> choiceList) {
		if( ! player.rowThreePrayUsed ) {
			choiceList.add(new PrayerChoice(game, player, (byte) 3));
		}
	}

	private void addRowTwoActions(Player player, List<Choice> choiceList) {
		if( ! player.rowTwoMoveUsed ) {
			choiceList.add(new MoveChoice(game, player, (byte) 2));
		}
		if( ! player.rowTwoPrayUsed ) {
			choiceList.add(new PrayerChoice(game, player, (byte) 2));
		}
		if( ! player.rowTwoUpgradePyramidUsed ) {
			choiceList.add(new UpgradePyramidChoice(game, player));
		}
	}

	private void addRowOneActions(Player player, List<Choice> choiceList) {
		if( ! player.rowOneMoveUsed ) {
			choiceList.add(new MoveChoice(game, player, (byte) 1));
		}
		if( ! player.rowOneRecruitUsed ) {
			choiceList.add(new RecruitChoice(game, player, (byte) 1));
		}

	}

}
