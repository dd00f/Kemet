package kemet.model.action;

import java.util.List;

import kemet.model.Army;
import kemet.model.BoardInventory;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.PowerList;
import kemet.model.Tile;
import kemet.model.Validation;
import kemet.model.action.choice.Choice;
import kemet.model.action.choice.ChoiceInventory;
import kemet.model.action.choice.EndTurnChoice;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ArmyMoveAction extends EndableAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3412984242415127309L;

	private KemetGame game;
	private Player player;

	public boolean firstMove = true;
	public Tile destinationTile;
	public byte movementLeft = 1;
	public Army army;
	public BattleAction battle;
	public boolean isTeleport = false;
	public byte powerCost = 0;

	private Action parent;

	public static Cache<ArmyMoveAction> CACHE = new Cache<ArmyMoveAction>(() -> new ArmyMoveAction());

	private ArmyMoveAction() {

	}

	@Override
	public void internalInitialize() {
		game = null;
		player = null;
		firstMove = true;
		destinationTile = null;
		movementLeft = 1;
		army = null;
		battle = null;
		isTeleport = false;
		powerCost = 0;

		parent = null;
	}

	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {
		currentGame.validate(game);
		currentGame.validate(player);
		currentGame.validate(destinationTile);
		currentGame.validate(army);

		if (battle != null) {
			battle.validate(this, currentGame);
		}
		if (expectedParent != parent) {
			Validation.validationFailed("Action parent isn't as expected.");
		}
	}

	@Override
	public void relink(KemetGame clone) {
		this.game = clone;
		player = clone.getPlayerByCopy(player);
		army = clone.getArmyByCopy(army);
		destinationTile = clone.getTileByCopy(destinationTile);
		if (battle != null) {
			battle.relink(clone);
		}
		super.relink(clone);

	}

	@Override
	public ArmyMoveAction deepCacheClone() {

		ArmyMoveAction clone = CACHE.create();

		copy(clone);

		return clone;
	}

	private void copy(ArmyMoveAction clone) {
		clone.game = game;
		clone.player = player;
		clone.firstMove = firstMove;
		clone.destinationTile = destinationTile;
		clone.movementLeft = movementLeft;
		clone.army = army;
		clone.battle = battle;
		if (battle != null) {
			clone.battle = battle.deepCacheClone();
			clone.battle.setParent(clone);
		}
		clone.isTeleport = isTeleport;
		clone.powerCost = powerCost;
		clone.parent = parent;

		super.copy(clone);
	}

	@Override
	public void release() {
		if (battle != null) {
			battle.release();
		}
		clear();
		CACHE.release(this);
	}

	@Override
	public void clear() {
		game = null;
		player = null;
		destinationTile = null;
		army = null;
		battle = null;
		parent = null;

		super.clear();
	}

	public static ArmyMoveAction create(KemetGame game, Player player, Action parent) {

		ArmyMoveAction create = CACHE.create();
		create.initialize();

		create.game = game;
		create.player = player;
		create.parent = parent;
		create.movementLeft = player.moveCapacity;

		return create;
	}

	@Override
	public Action getParent() {
		return parent;
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

	public class TileMoveChoice extends PlayerChoice {

		public Tile pickDestinationTile;
		public boolean pickIsTeleport;
		public byte pickPowerCost;

		public TileMoveChoice(KemetGame game, Player player) {
			super(game, player);
		}

		@Override
		public String describe() {
			StringBuilder builder = new StringBuilder();
			builder.append("Moving \"").append(army).append("\"");
			if (army.tile != null) {
				builder.append(" from tile ").append(army.tile.name);
			}

			builder.append(" to tile ").append(pickDestinationTile);
			builder.append(".");

			if (pickDestinationTile.getArmy() != null) {
				if (pickDestinationTile.getArmy().owningPlayer == player) {
					builder.append(" Containing friendly army ");
				} else {
					builder.append(" Containing enemy army ");
				}
				builder.append(pickDestinationTile.getArmy());
				builder.append(".");
			}

			if (pickIsTeleport) {
				builder.append(" Using teleportation for " + pickPowerCost + " prayer points.");
			}

			builder.append(" ");
			builder.append(movementLeft);
			builder.append(" movement left.");
			return builder.toString();
		}

		@Override
		public void choiceActivate() {
			destinationTile = pickDestinationTile;
			powerCost = pickPowerCost;
			isTeleport = pickIsTeleport;
		}

		@Override
		public int getIndex() {
			return pickDestinationTile.getPickChoiceIndex(player.index);
		}

	}

	public void addArmyTileMoveChoice(List<Choice> choiceList) {
		for (Tile tile : army.tile.connectedTiles) {

			if (isTargetTileFriendlyAndFull(player, tile, army)) {
				continue;
			}

			TileMoveChoice subChoice = new TileMoveChoice(game, player);
			subChoice.pickDestinationTile = tile;
			subChoice.pickIsTeleport = false;

			if (tile.isWalledByEnemy(player) && !firstMove && !player.hasPower(PowerList.RED_2_OPEN_GATE)) {
				// moving into a city tile with walls
				log.debug("Army {} can't move to tile {} because it has walls and it isn't the first move.", army,
						tile.name);
				continue;
			}

			choiceList.add(subChoice);

		}

		if (armyCanTeleport()) {
			for (Tile tile : game.tileList) {
				if (tile.hasObelisk) {

					if (isTargetTileFriendlyAndFull(player, tile, army)) {
						continue;
					}

					TileMoveChoice subChoice = new TileMoveChoice(game, player);
					subChoice.pickDestinationTile = tile;
					subChoice.pickIsTeleport = true;
					subChoice.pickPowerCost = player.getTeleportCost();
					choiceList.add(subChoice);
				}
			}
		}
	}

	private boolean armyCanTeleport() {

		if (!player.canTeleport()) {
			// not enough power points to teleport
			return false;
		}

		if (army.tile.getPyramidLevel() > 0) {
			return true;
		}

		if (army.tile.hasObelisk && player.hasPower(PowerList.RED_2_TELEPORT)) {
			return true;
		}

		return false;
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

	public class ArmyPickMoveChoice extends PlayerChoice {

		public ArmyPickMoveChoice(KemetGame game, Player player) {
			super(game, player);
		}

		public Army armyPick;

		@Override
		public void choiceActivate() {

			army = armyPick;
			movementLeft = army.getMoveCount();

		}

		@Override
		public String describe() {

			return "move " + armyPick;

		}

		@Override
		public int getIndex() {
			return armyPick.tile.getPickChoiceIndex(player.index);
		}

	}

	public void addArmyPickMoveChoice(List<Choice> choiceList) {
		for (Army army : player.armyList) {
			ArmyPickMoveChoice subChoice = new ArmyPickMoveChoice(game, player);
			subChoice.armyPick = army;
			choiceList.add(subChoice);
		}
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {
		if (isEnded()) {
			return null;
		}

		if (battle != null) {
			return battle.getNextPlayerChoicePick();
		}

		if (army == null) {
			// pick army
			PlayerChoicePick pick = new PlayerChoicePick(game, player, this);
			addArmyPickMoveChoice(pick.choiceList);
			EndTurnChoice.addEndTurnChoice(game, player, pick.choiceList, this);
			return pick.validate();
		}

		if (movementLeft > 0) {
			// pick tile until move capacity is done
			if (destinationTile == null) {

				PlayerChoicePick pick = new PlayerChoicePick(game, player, this);
				addArmyTileMoveChoice(pick.choiceList);

				if (pick.choiceList.size() == 0) {
					// no possible destination tile (usually because move landed on a island )
					return null;
				}

				EndTurnChoice.addEndTurnChoice(game, player, pick.choiceList, this);
				return pick.validate();
			} else {
				PlayerChoicePick pick = new PlayerChoicePick(game, player, this);
				addArmySizeMoveChoice(pick.choiceList);
				EndTurnChoice.addEndTurnChoice(game, player, pick.choiceList, this);
				return pick.validate();
			}

		}

		return null;

	}

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {

		if (isEnded()) {
			return;
		}

		cannonicalForm.set(BoardInventory.STATE_MOVE, player.getState(playerIndex));
		cannonicalForm.set(BoardInventory.MOVES_LEFT, movementLeft);
		cannonicalForm.set(BoardInventory.IS_FIRST_MOVE, (byte) (firstMove ? 1 : 0));

		if (battle != null) {
			battle.fillCanonicalForm(cannonicalForm, playerIndex);

		}

		if (army == null) {
			// pick army source tile
			cannonicalForm.set(BoardInventory.STATE_PICK_SOURCE_TILE, player.getState(playerIndex));
		} else if (movementLeft > 0) {
			army.tile.setSelectedSource(cannonicalForm, playerIndex, player.getState(playerIndex));

			// pick tile until move capacity is done
			if (destinationTile == null) {
				// pick destination tile
				cannonicalForm.set(BoardInventory.STATE_PICK_TILE, player.getState(playerIndex));
			}

			else {
				// pick army size to move
				cannonicalForm.set(BoardInventory.STATE_PICK_ARMY_SIZE, player.getState(playerIndex));
				destinationTile.setSelected(cannonicalForm, playerIndex, player.getState(playerIndex));
			}
		}
	}

	public void addArmySizeMoveChoice(List<Choice> choiceList) {

		// if friendly army, limit size & beast choice
		byte sourceArmySize = army.armySize;
		if (destinationTile.getArmy() != null && destinationTile.getArmy().owningPlayer == player) {

			int destinationRemainingCapacity = player.maximumArmySize - destinationTile.getArmy().armySize;
			byte maxSize = (byte) Math.min(sourceArmySize, destinationRemainingCapacity);

			addAllArmySizeMoveChoice(choiceList, (byte) 1, maxSize, false);
			if (army.beast != null && destinationTile.getArmy() == null) {
				// add options to move beast
				addAllArmySizeMoveChoice(choiceList, (byte) 0, sourceArmySize, true);
			}

		} else {
			addAllArmySizeMoveChoice(choiceList, (byte) 1, sourceArmySize, false);
			if (army.beast != null) {
				addAllArmySizeMoveChoice(choiceList, (byte) 0, sourceArmySize, true);
			}
		}
	}

	public void addAllArmySizeMoveChoice(List<Choice> choiceList, byte minSize, byte maxSize, boolean moveBeast) {
		for (byte i = minSize; i <= maxSize; ++i) {
			ArmySizeMoveChoice subChoice = new ArmySizeMoveChoice(game, player);
			subChoice.moveSoldierCount = i;
			subChoice.moveBeast = moveBeast;
			choiceList.add(subChoice);
		}
	}

	public class ArmySizeMoveChoice extends PlayerChoice {

		public boolean moveBeast;
		public byte moveSoldierCount;

		public ArmySizeMoveChoice(KemetGame game, Player player) {
			super(game, player);
		}

		@Override
		public String describe() {
			String armyModified = "Moving army \"" + army.name + "\" " + moveSoldierCount + " soldiers";
			if (moveBeast) {
				armyModified += " and the beast " + army.beast;
			} else if (army.beast != null) {
				armyModified += ", leaving behind the beast " + army.beast;
			}
			int remainingArmy = army.armySize - moveSoldierCount;
			if (remainingArmy > 0) {
				armyModified += ", leaving " + remainingArmy + " soldiers";
			}
			return armyModified;
		}

		@Override
		public void choiceActivate() {

			// trigger the cost
			player.modifyPrayerPoints((byte) -powerCost, "move teleportation");

			Army armyThatKeepsMoving = null;

			if (destinationTile.getArmy() != null) {
				// there is an army at the destination

				if (destinationTile.getArmy().owningPlayer == army.owningPlayer) {
					// player merging army
					army.transferSoldiersToArmy(destinationTile.getArmy(), moveSoldierCount);
					if (moveBeast) {
						army.transferBeastToArmy(destinationTile.getArmy());
					}
					army.checkToDisbandArmy();

					armyThatKeepsMoving = destinationTile.getArmy();

				} else {
					// battle about to begin
					Tile previousTile = army.tile;
					army.moveToTile(null);
					createArmyLeftBehind(previousTile);
					boolean battleContinues = true;
					boolean battleCancelledAttackerDestroyed = false;

					if (army.owningPlayer.hasPower(PowerList.BLACK_2_DEDICATION_TO_BATTLE)) {
						army.owningPlayer.modifyPrayerPoints((byte) 2,
								PowerList.BLACK_2_DEDICATION_TO_BATTLE.toString());

						if (army.owningPlayer.hasPower(PowerList.BLACK_4_DIVINE_STRENGTH)) {
							army.owningPlayer.modifyPrayerPoints((byte) 1,
									PowerList.BLACK_4_DIVINE_STRENGTH.toString());
						}

					
					}
					
					if (destinationTile.getArmy().owningPlayer.hasPower(PowerList.BLACK_3_DEADLY_TRAP)) {

						byte enemyArmySize = army.armySize;
						byte damageDone = (byte) Math.min(1, enemyArmySize);
						army.bleedArmy(damageDone, PowerList.BLACK_3_DEADLY_TRAP.toString());

						if (damageDone == enemyArmySize) {
							battleCancelledAttackerDestroyed = true;
							army.destroyArmy();
						}
					}

					if (army.owningPlayer.hasPower(PowerList.RED_4_INITIATIVE)) {

						byte enemyArmySize = destinationTile.getArmy().armySize;
						byte damageDone = (byte) Math.min(2, enemyArmySize);
						destinationTile.getArmy().bleedArmy(damageDone, PowerList.RED_4_INITIATIVE.toString());

						if (damageDone == enemyArmySize) {
							battleContinues = false;
							destinationTile.getArmy().destroyArmy();
						}
					}

					if (battleContinues) {
						battle = BattleAction.create(game, ArmyMoveAction.this);
						battle.attackingArmy = army;
						battle.defendingArmy = destinationTile.getArmy();
						battle.tile = destinationTile;
					} else if ( ! battleCancelledAttackerDestroyed ) {
						army.moveToTile(destinationTile);
						armyThatKeepsMoving = army;
					}

				}
			} else {
				// remove army from previous tile
				Tile previousTile = army.tile;

				army.moveToTile(null);
				createArmyLeftBehind(previousTile);

				army.moveToTile(destinationTile);
				armyThatKeepsMoving = army;
			}

			if (!isTeleport) {
				// normal move
				movementLeft--;
			}

			if (armyThatKeepsMoving != null && movementLeft > 0 || powerCost > 0) {

				army = armyThatKeepsMoving;
				powerCost = 0;
				isTeleport = false;
				destinationTile = null;
				firstMove = false;

			}

		}

		public void createArmyLeftBehind(Tile previousTile) {
			// moving to empty territory
			byte soldierLeft = (byte) (army.armySize - moveSoldierCount);

			if (soldierLeft == 0) {
				// nobody left behind, simple move
				if (!moveBeast) {
					army.returnBeastToPlayer();
				}

			} else {
				// create new army for what's left behind
				Army createArmy = player.createArmy();
				army.transferSoldiersToArmy(createArmy, soldierLeft);
				createArmy.moveToTile(previousTile);
				if (moveBeast) {
					army.transferBeastToArmy(createArmy);
				}
			}
		}

		@Override
		public int getIndex() {

			if (moveSoldierCount == 0) {
				log.error("Move army size of 0 isn't valid yet in the AI choices");
			}

			return ChoiceInventory.ARMY_SIZE_CHOICE + moveSoldierCount - 1;
		}

	}

	@Override
	public void setParent(Action parent) {
		this.parent = parent;
	}

}
