package kemet.data.choice;

import kemet.data.state.State;
import kemet.model.Color;
import kemet.model.Game;
import kemet.model.Player;
import kemet.model.Tile;

public class UpgradePyramidPickChoice extends PlayerChoice {

	public UpgradePyramidPickChoice(Game game, Player player) {
		super(game, player);
	}

	public Color color;
	public byte startLevel;
	public byte endLevel;
	public byte powerCost;
	public Tile tile;

	@Override
	public State choiceActivate() {
		player.modifyPrayerPoints((byte) -powerCost, "pyramid upgrade");
		tile.pyramidColor = color;
		tile.setPyramidLevel(endLevel);

		return null;
	}

	public String describe() {

		return "upgrade " + color + " pyramid from level " + startLevel + " to "
				+ endLevel + " on tile \"" + tile.name + "\" for " + powerCost + " prayer points.";

	}

}
