package kemet.data.choice;

import java.util.List;
import java.util.logging.Logger;

import kemet.data.state.State;
import kemet.model.Army;
import kemet.model.Game;
import kemet.model.Player;
import kemet.model.Tile;

public class ArmyRetreatTileMoveChoice extends PlayerChoice {

	public ArmyRetreatTileMoveChoice(Game game, Player player) {
		super(game, player);
	}

	public static final Logger LOGGER = Logger.getLogger(ArmyRetreatTileMoveChoice.class.getName());

	public Tile destinationTile;
	public Army army;

	@Override
	public State choiceActivate() {
		army.moveToTile(destinationTile);
		return null;
	}

	public String describe() {

		StringBuilder builder = new StringBuilder();
		builder.append("Retreat army \"").append(army).append("\"");
		if (army.tile != null) {
			builder.append(" from tile ").append(army.tile.name);
		}

		builder.append(" to tile ").append(destinationTile);
		builder.append(".");

		return builder.toString();

	}

	public static void addArmyTileRetreatMoveChoice(Game game, Player player, Army army, Tile sourceTile,
			List<Choice> choiceList) {
		for (Tile tile : sourceTile.connectedTiles) {

			if (tile.getArmy() != null) {
				// can't retreat on occupied tile, even friendly
				continue;
			}

			ArmyRetreatTileMoveChoice subChoice = new ArmyRetreatTileMoveChoice(game, player);
			subChoice.army = army;
			subChoice.destinationTile = tile;

			if (tile.isWalledByEnemy(player)) {
				// moving into a city tile with walls
				LOGGER.info("Army " + army + " can't retreat to tile " + tile.name + " because it has walls.");
				continue;
			}

			choiceList.add(subChoice);
		}
	}


}
