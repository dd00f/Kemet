package kemet.model.action.choice;

import java.util.logging.Logger;

import kemet.model.KemetGame;
import kemet.model.Player;

public class DescribeGameChoice extends PlayerChoice {
	
	public DescribeGameChoice(KemetGame game, Player player) {
		super(game, player);
	}

	public static final Logger LOGGER = Logger.getLogger(DescribeGameChoice.class.getName());

	public byte row;

	@Override
    public String describe() {
		
		return "Describe the game.";

	}

	@Override
	public void choiceActivate() {
		game.describeGame();
	}

	@Override
	public int getIndex() {
		return ChoiceInventory.NON_AI_CHOICE;
	}

}
