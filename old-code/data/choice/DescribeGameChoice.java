package kemet.data.choice;

import java.util.logging.Logger;

import kemet.data.state.State;
import kemet.model.Game;
import kemet.model.Player;

public class DescribeGameChoice extends PlayerChoice {
	
	public DescribeGameChoice(Game game, Player player) {
		super(game, player);
	}

	public static final Logger LOGGER = Logger.getLogger(DescribeGameChoice.class.getName());

	public byte row;

	public String describe() {
		
		return "Describe the game.";

	}

	@Override
	public State choiceActivate() {
		game.describeGame();
		return null;
	}

}
