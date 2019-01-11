package kemet.data.choice;

import kemet.data.state.State;
import kemet.model.Color;
import kemet.model.Game;
import kemet.model.Player;
import kemet.model.Tile;

public class InitialPyramidChoice extends PlayerChoice {

	public InitialPyramidChoice(Game game, Player player) {
		super(game, player);
	}

	public Color color;
	public byte endLevel;
	public Tile tile;

	@Override
	public State choiceActivate() {

		tile.pyramidColor = color;
		tile.setPyramidLevel(endLevel);
		return null;
	}

	public String describe() {

		return "initial " + color + " pyramid level " + endLevel + " on tile \"" + tile.name + "\"";

	}

}
