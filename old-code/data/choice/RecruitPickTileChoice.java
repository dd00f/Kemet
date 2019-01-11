package kemet.data.choice;

import java.util.List;

import kemet.data.state.State;
import kemet.model.Game;
import kemet.model.Player;
import kemet.model.Tile;
import kemet.model.action.RecruitAction;

public class RecruitPickTileChoice  extends PlayerChoice {

	public RecruitPickTileChoice(Game game, Player player) {
		super(game, player);
	}

	public Tile tile;
	private RecruitAction action;

	@Override
	public State choiceActivate() {

		action.tile = tile;
		return null;
	}

	public String describe() {

		return "Recruit on tile \"" + tile.name;

	}
	
	private static boolean isTargetTileFriendlyAndFull(Player player, Tile tile) {
		if (tile.getArmy() != null && tile.getArmy().owningPlayer == player && ! tile.getArmy().playerCanRecruitOnArmy()) {
			return true;
		}
		return false;
	}	
	
	public static void addRecruitPickTileChoice(Game game, Player player, List<Choice> choiceList, RecruitAction action) {
		
		if( player.availableArmyTokens <= 0 && player.availableBeasts.size() == 0 ) {
			// no option to add
			return;
		}
		
		for (Tile tile : player.cityTiles) {

			if (isTargetTileFriendlyAndFull(player, tile)) {
				continue;
			}

			RecruitPickTileChoice subChoice = new RecruitPickTileChoice(game, player);
			subChoice.tile = tile;
			subChoice.action = action;

			choiceList.add(subChoice);
		}
	}

}
