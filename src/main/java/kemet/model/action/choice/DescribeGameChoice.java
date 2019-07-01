package kemet.model.action.choice;

import kemet.model.KemetGame;
import kemet.model.Player;

public class DescribeGameChoice extends PlayerChoice {
	
	public DescribeGameChoice(KemetGame game, Player player) {
		super(game, player);
	}

	public byte row;

	@Override
    public String describe() {
		
		return "Describe the game.";

	}

	@Override
	public void choiceActivate() {
		game.printEvent(game.toString());
	}

	@Override
	public int getIndex() {
		return ChoiceInventory.NON_AI_CHOICE;
	}

}
