package kemet.data.state;

import java.util.ArrayList;
import java.util.List;

import kemet.data.choice.Choice;
import kemet.data.choice.InitialRecruitChoice;
import kemet.model.Game;
import kemet.model.Player;
import kemet.model.Tile;

public class InitializationRecruitState implements State {

	public Game game;

	@Override
	public State triggerNextAction() {

		for (Player player : game.playerByInitiativeList) {
			createInitializeRecruitChoice(player);
		}

		return null;
	}

	private void createInitializeRecruitChoice(Player player) {
		
		player.getClass();
		
		Tile district1 = player.cityTiles.get(0);
		Tile district2 = player.cityTiles.get(1);
		Tile district3 = player.cityTiles.get(2);

		byte minimum = 0;
		byte maximum = 5;

		List<Choice> choiceList = new ArrayList<>();

		for (int i = minimum; i <= maximum; ++i) {
			createInitialRecruitChoice(player, (byte) i, district1, choiceList);
		}

		InitialRecruitChoice choice = (InitialRecruitChoice) player.actor.pickActionOld(choiceList);
		choice.activate();

		minimum = (byte) (5 - choice.soldierCount);
		choiceList.clear();

		for (int i = minimum; i <= maximum; ++i) {
			createInitialRecruitChoice(player, (byte) i, district2, choiceList);
		}

		InitialRecruitChoice choice2 = (InitialRecruitChoice) player.actor.pickActionOld(choiceList);
		choice2.activate();

		byte leftOver = (byte) (10 - choice.soldierCount - choice2.soldierCount);

		if (leftOver > 0) {
			choiceList.clear();
			createInitialRecruitChoice(player, leftOver, district3, choiceList);
			
			InitialRecruitChoice choice3 = (InitialRecruitChoice) player.actor.pickActionOld(choiceList);
			choice3.activate();
		}

	}

	private void createInitialRecruitChoice(Player player, byte soldierCount, Tile tile, List<Choice> choiceList) {
		
		assert( tile != null );
		assert( player != null );
		
		InitialRecruitChoice choice = new InitialRecruitChoice(game, player);
		choice.soldierCount = soldierCount;
		choice.tile = tile;
		choiceList.add(choice);
	}

}
