package kemet.data.choice;

import java.util.ArrayList;
import java.util.List;

import kemet.data.state.State;
import kemet.model.Color;
import kemet.model.Game;
import kemet.model.Player;
import kemet.model.Tile;

public class UpgradePyramidChoice extends PlayerChoice {

	public UpgradePyramidChoice(Game game, Player player) {
		super(game, player);
	}

	public boolean redUsed = false;
	public boolean blueUsed = false;
	public boolean blackUsed = false;
	public boolean whiteUsed = false;

	@Override
	public State choiceActivate() {
		
		player.rowTwoUpgradePyramidUsed = true;
		player.actionTokenLeft--;
		
		List<Choice> choiceList = new ArrayList<>();

		Tile district1 = player.cityTiles.get(0);
		Tile district2 = player.cityTiles.get(1);
		Tile district3 = player.cityTiles.get(2);

		usePyramidColor(district1);
		usePyramidColor(district2);
		usePyramidColor(district3);
		
		createPyramidUpgradeChoiceForTile(district1, choiceList);
		createPyramidUpgradeChoiceForTile(district2, choiceList);
		createPyramidUpgradeChoiceForTile(district3, choiceList);
		EndTurnChoice.addEndTurnChoice(game, player, choiceList, null);
		
		return player.actor.pickActionAndActivateOld(choiceList);
	}

	private void createPyramidUpgradeChoiceForTile(Tile district1, List<Choice> choiceList) {
		if( district1.pyramidColor == Color.NONE ) {
			createAllPyramidChoices(district1, choiceList);
		}
		else {
			createAllColorPyramidChoices(district1, district1.pyramidColor, district1.getPyramidLevel(), choiceList);
		}
	}

	private void createAllColorPyramidChoices(Tile district1, Color pyramidColor, byte pyramidInitialLevel,
			List<Choice> choiceList) {
		for( byte i=4; i>pyramidInitialLevel;--i) {
			byte cost = calculateCost( pyramidInitialLevel, i);
			if( player.getPrayerPoints() >= cost ) {
				// enough prayer points available
				UpgradePyramidPickChoice choice = new UpgradePyramidPickChoice(game, player);
				choice.color = pyramidColor;
				choice.endLevel = i;
				choice.powerCost = cost;
				choice.startLevel = pyramidInitialLevel;
				choice.tile = district1;
				choiceList.add(choice);
			}
		}
	}

	private byte calculateCost(byte pyramidInitialLevel, int i) {
		byte cost = 0;
		for( byte j=(byte) (pyramidInitialLevel+1); j<= i; j++) {
			cost += j;
		}
		return cost;
	}

	private void createAllPyramidChoices(Tile district1, List<Choice> choiceList) {
		if(! whiteUsed ) {
			createAllColorPyramidChoices(district1, Color.WHITE, (byte) 0, choiceList);
		}
		if(! redUsed ) {
			createAllColorPyramidChoices(district1, Color.RED, (byte) 0, choiceList);
		}
		if(! blackUsed ) {
			createAllColorPyramidChoices(district1, Color.BLACK, (byte) 0, choiceList);
		}
		if(! blueUsed ) {
			createAllColorPyramidChoices(district1, Color.BLUE, (byte) 0, choiceList);
		}
	}

	private void usePyramidColor(Tile district1) {
		switch (district1.pyramidColor) {
		case BLUE:
			blueUsed = true;
			break;
		case BLACK:
			blackUsed = true;
			break;
		case WHITE:
			whiteUsed = true;
			break;
		case RED:
			redUsed = true;
			break;
		default:
			break;
		}
	}

	public String describe() {

		return "Upgrade Pyramid.";

	}

}
