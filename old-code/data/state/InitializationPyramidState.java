package kemet.data.state;

import java.util.ArrayList;
import java.util.List;

import kemet.data.choice.Choice;
import kemet.data.choice.InitialPyramidChoice;
import kemet.model.Color;
import kemet.model.Game;
import kemet.model.Player;
import kemet.model.Tile;

public class InitializationPyramidState implements State {

	public Game game;

	@Override
	public State triggerNextAction() {

		for (Player player : game.playerByInitiativeList) {
			createInitializePyramidChoice(player);
		}

		return this;
	}

	private void createInitializePyramidChoice(Player player) {
		List<Choice> choiceList = new ArrayList<>();

		Tile district1 = player.cityTiles.get(0);
		createInitialPyramid(district1, Color.RED, (byte) 1, player, choiceList);
		createInitialPyramid(district1, Color.BLACK, (byte) 1, player, choiceList);
		createInitialPyramid(district1, Color.BLUE, (byte) 1, player, choiceList);
		createInitialPyramid(district1, Color.WHITE, (byte) 1, player, choiceList);
		createInitialPyramid(district1, Color.RED, (byte) 2, player, choiceList);
		createInitialPyramid(district1, Color.BLACK, (byte) 2, player, choiceList);
		createInitialPyramid(district1, Color.BLUE, (byte) 2, player, choiceList);
		createInitialPyramid(district1, Color.WHITE, (byte) 2, player, choiceList);

		InitialPyramidChoice choice = (InitialPyramidChoice) player.actor.pickActionOld(choiceList);
		choice.activate();

		choiceList = new ArrayList<>();

		Tile district2 = player.cityTiles.get(1);
		createInitialPyramid(district2, Color.RED, (byte) 1, player, choiceList);
		createInitialPyramid(district2, Color.BLACK, (byte) 1, player, choiceList);
		createInitialPyramid(district2, Color.BLUE, (byte) 1, player, choiceList);
		createInitialPyramid(district2, Color.WHITE, (byte) 1, player, choiceList);

		InitialPyramidChoice choice2 = (InitialPyramidChoice) player.actor.pickActionOld(choiceList);
		choice2.activate();

		
		if( choice.endLevel == 1 ) {
			Tile district3 = player.cityTiles.get(2);
			createInitialPyramid(district3, Color.RED, (byte) 1, player, choiceList);
			createInitialPyramid(district3, Color.BLACK, (byte) 1, player, choiceList);
			createInitialPyramid(district3, Color.BLUE, (byte) 1, player, choiceList);
			createInitialPyramid(district3, Color.WHITE, (byte) 1, player, choiceList);

			InitialPyramidChoice choice3 = (InitialPyramidChoice) player.actor.pickActionOld(choiceList);
			choice3.activate();
		}
	}

	private void createInitialPyramid(Tile tile, Color color, byte i, Player player, List<Choice> choiceList) {
		InitialPyramidChoice choice = new InitialPyramidChoice(game, player);
		choice.color = color;
		choice.endLevel = i;
		choice.tile = tile;
		
		if( ! player.hasPyramid(color)) {
			
			choiceList.add(choice);
		}
		
	}

}
