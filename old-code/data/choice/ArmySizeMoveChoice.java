package kemet.data.choice;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import kemet.data.state.State;
import kemet.model.Army;
import kemet.model.Game;
import kemet.model.Player;
import kemet.model.Tile;
import kemet.model.action.BattleAction;

public class ArmySizeMoveChoice extends PlayerChoice {

	public ArmySizeMoveChoice(Game game, Player player) {
		super(game, player);
	}

	public static final Logger LOGGER = Logger.getLogger(ArmySizeMoveChoice.class.getName());

	public ArmyTileMoveChoice moveChoice;
	public boolean moveBeast;
	public byte moveSoldierCount = 0;

	@Override
	public State choiceActivate() {

		// trigger the cost
		player.modifyPrayerPoints((byte) -moveChoice.powerCost, "move teleportation");

		Army armyThatKeepsMoving = null;

		if (moveChoice.destinationTile.getArmy() != null) {
			// there is an army at the destination

			if (moveChoice.destinationTile.getArmy().owningPlayer == moveChoice.army.owningPlayer) {
				// player merging army
				moveChoice.army.transferSoldiersToArmy(moveChoice.destinationTile.getArmy(), moveSoldierCount);
				if (moveBeast) {
					moveChoice.army.transferBeastToArmy(moveChoice.destinationTile.getArmy());
				}
				moveChoice.army.checkToDisbandArmy();

				armyThatKeepsMoving = moveChoice.destinationTile.getArmy();

			} else {
				// battle about to begin
				Tile previousTile = moveChoice.army.tile;
				moveChoice.army.moveToTile(null);
				createArmyLeftBehind(previousTile);

				BattleAction battle = new BattleAction(game);
				battle.attackingArmy = moveChoice.army;
				battle.defendingArmy = moveChoice.destinationTile.getArmy();
				battle.tile = moveChoice.destinationTile;

				BattleFlow flow = new BattleFlow(game, battle);
				flow.triggerBattle();

			}
		} else {
			// remove army from previous tile
			Tile previousTile = moveChoice.army.tile;

			moveChoice.army.moveToTile(null);
			createArmyLeftBehind(previousTile);

			moveChoice.army.moveToTile(moveChoice.destinationTile);
			armyThatKeepsMoving = moveChoice.army;
		}

		if (armyThatKeepsMoving != null && (moveChoice.movementLeft > 1 || moveChoice.powerCost > 0)) {

			byte moveLeft = moveChoice.movementLeft;
			if (moveChoice.powerCost == 0) {
				// normal move
				moveLeft--;
			}

			// trigger next move selection
			List<Choice> choiceList = new ArrayList<>();

			ArmyTileMoveChoice.addArmyTileMoveChoice(game, player, armyThatKeepsMoving, choiceList, false, moveLeft);

			EndTurnChoice.addEndTurnChoice(game, player, choiceList, null);

			player.actor.pickActionAndActivateOld(choiceList);

		}

		return null;

	}

	public void createArmyLeftBehind(Tile previousTile) {
		// moving to empty territory
		byte soldierLeft = (byte) (moveChoice.army.armySize - moveSoldierCount);

		if (soldierLeft == 0) {
			// nobody left behind, simple move
			if (!moveBeast) {
				moveChoice.army.returnBeastToPlayer();
			}

		} else {
			// create new army for what's left behind
			Army createArmy = player.createArmy();
			moveChoice.army.transferSoldiersToArmy(createArmy, soldierLeft);
			createArmy.moveToTile(previousTile);
			if (moveBeast) {
				moveChoice.army.transferBeastToArmy(createArmy);
			}
		}
	}

	public String describe() {

		String armyModified = "Moving army " + moveChoice.army.name + " " + moveSoldierCount + " soldiers";
		if (moveBeast) {
			armyModified += " and the beast " + moveChoice.army.beast;
		} else if (moveChoice.army.beast != null) {
			armyModified += ", leaving behind the beast " + moveChoice.army.beast;
		}
		int remainingArmy = moveChoice.army.armySize - moveSoldierCount;
		if (remainingArmy > 0) {
			armyModified += ", leaving " + remainingArmy + " soldiers";
		}
		return armyModified;
	}

	public static void addArmySizeMoveChoice(ArmyTileMoveChoice moveChoice, List<Choice> choiceList) {

		// if friendly army, limit size & beast choice
		byte sourceArmySize = moveChoice.army.armySize;
		if (moveChoice.destinationTile.getArmy() != null
				&& moveChoice.destinationTile.getArmy().owningPlayer == moveChoice.player) {

			int destinationRemainingCapacity = moveChoice.player.maximumArmySize
					- moveChoice.destinationTile.getArmy().armySize;
			byte maxSize = (byte) Math.min(sourceArmySize, destinationRemainingCapacity);

			addAllArmySizeMoveChoice(moveChoice, choiceList, (byte) 1, maxSize, false);
			if (moveChoice.army.beast != null && moveChoice.destinationTile.getArmy() == null) {
				// add options to move beast
				addAllArmySizeMoveChoice(moveChoice, choiceList, (byte) 0, sourceArmySize, true);
			}

		} else {
			addAllArmySizeMoveChoice(moveChoice, choiceList, (byte) 1, sourceArmySize, false);
			if (moveChoice.army.beast != null) {
				addAllArmySizeMoveChoice(moveChoice, choiceList, (byte) 0, sourceArmySize, true);
			}
		}
	}

	public static void addAllArmySizeMoveChoice(ArmyTileMoveChoice moveChoice, List<Choice> choiceList, byte minSize,
			byte maxSize, boolean moveBeast) {
		for (byte i = minSize; i <= maxSize; ++i) {
			ArmySizeMoveChoice subChoice = new ArmySizeMoveChoice(moveChoice.game, moveChoice.player);
			subChoice.moveChoice = moveChoice;
			subChoice.moveSoldierCount = i;
			subChoice.moveBeast = moveBeast;
			choiceList.add(subChoice);
		}
	}

}
