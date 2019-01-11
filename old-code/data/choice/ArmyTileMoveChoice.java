package kemet.data.choice;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import kemet.data.state.State;
import kemet.model.Army;
import kemet.model.Game;
import kemet.model.Player;
import kemet.model.Tile;

public class ArmyTileMoveChoice extends PlayerChoice {

	public ArmyTileMoveChoice(Game game, Player player) {
		super(game, player);
	}

	public static final Logger LOGGER = Logger.getLogger(ArmyTileMoveChoice.class.getName());

	public boolean firstMove = false;
	public Tile destinationTile;
	public byte movementLeft = 0;
	public boolean isTeleport = false;
	public Army army;
	public byte powerCost = 0;

	@Override
	public State choiceActivate() {

		// trigger army selection choice
		List<Choice> choiceList = new ArrayList<>();

		ArmySizeMoveChoice.addArmySizeMoveChoice(this, choiceList);
		EndTurnChoice.addEndTurnChoice(game, player, choiceList, null);

		return player.actor.pickActionAndActivateOld(choiceList);
	}

	public String describe() {

		StringBuilder builder = new StringBuilder();
		builder.append("Moving \"").append(army).append("\"");
		if (army.tile != null) {
			builder.append(" from tile ").append(army.tile.name);
		}

		builder.append(" to tile ").append(destinationTile);
		builder.append(".");

		if (destinationTile.getArmy() != null) {
			if (destinationTile.getArmy().owningPlayer == player) {
				builder.append(" Containing friendly army ");
			} else {
				builder.append(" Containing enemy army ");
			}
			builder.append(destinationTile.getArmy());
			builder.append(".");
		}

		if (isTeleport) {
			builder.append(" Using teleportation for " + powerCost + " prayer points.");
		}

		builder.append(" ");
		builder.append(movementLeft);
		builder.append(" movement left.");
		return builder.toString();

	}

	public static void addArmyTileMoveChoice(Game game, Player player, Army army, List<Choice> choiceList,
			boolean firstMove, byte remainingMoveCount) {
		for (Tile tile : army.tile.connectedTiles) {

			if (isTargetTileFriendlyAndFull(player, tile, army)) {
				continue;
			}

			ArmyTileMoveChoice subChoice = new ArmyTileMoveChoice(game, player);
			subChoice.army = army;
			subChoice.destinationTile = tile;
			subChoice.firstMove = firstMove;
			subChoice.movementLeft = remainingMoveCount;
			subChoice.isTeleport = false;

			if (tile.isWalledByEnemy(player) && !firstMove) {
				// moving into a city tile with walls
				LOGGER.info("Army " + army + " can't move to tile " + tile.name
						+ " because it has walls and it isn't the first move.");
				continue;
			}

			choiceList.add(subChoice);

		}

		if (army.tile.getPyramidLevel() > 0 && player.canTeleport()) {
			for (Tile tile : game.tileList) {
				if (tile.hasObelisk) {

					if (isTargetTileFriendlyAndFull(player, tile, army)) {
						continue;
					}

					ArmyTileMoveChoice subChoice = new ArmyTileMoveChoice(game, player);
					subChoice.army = army;
					subChoice.destinationTile = tile;
					subChoice.powerCost = player.teleportCost;
					subChoice.firstMove = firstMove;
					subChoice.movementLeft = remainingMoveCount;
					subChoice.isTeleport = true;
					choiceList.add(subChoice);
				}
			}
		}
	}

	private static boolean isTargetTileFriendlyAndFull(Player player, Tile tile, Army armyToMove) {
		if (tile.getArmy() != null && tile.getArmy().owningPlayer == player && tile.getArmy().isArmySizeFull()) {

			if (armyToMove.beast == null || tile.getArmy().beast != null) {
				// nothing can move to friendly army tile
				return true;
			}
		}
		return false;
	}


}
