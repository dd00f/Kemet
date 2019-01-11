package kemet.data.choice;

import kemet.data.state.State;
import kemet.model.Army;
import kemet.model.Game;
import kemet.model.Player;
import kemet.model.Tile;

public class InitialRecruitChoice extends PlayerChoice {

	public InitialRecruitChoice(Game game, Player player) {
		super(game, player);
	}

	public byte soldierCount;
	public Tile tile;

	@Override
	public State choiceActivate() {
		
		if( soldierCount > 0 ) {
			Army modifiedArmy = player.createArmy();
			modifiedArmy.moveToTile(tile);
			modifiedArmy.recruit(soldierCount);
		}

		return null;
	}

	public String describe() {
		return "Initial Recruit : recruit " + soldierCount + " soldiers " + "on tile \""
				+ tile.name + "\" creating \"" + player.getNextArmyName() + "\"";
	}


}
