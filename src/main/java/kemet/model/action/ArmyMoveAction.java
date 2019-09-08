package kemet.model.action;

import java.util.ArrayList;
import java.util.List;

import kemet.model.Army;
import kemet.model.BeastList;
import kemet.model.BoardInventory;
import kemet.model.DiCardList;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.PowerList;
import kemet.model.Tile;
import kemet.model.action.choice.Choice;
import kemet.model.action.choice.ChoiceInventory;
import kemet.model.action.choice.EndTurnChoice;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ArmyMoveAction extends DiCardAction implements DiCardExecutor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3412984242415127309L;

	public boolean firstMove = true;
	public Tile destinationTile;
	public byte movementLeft = 1;
	public Army army;
	public BattleAction overridingAction;
//	public ChainedAction temporaryAction;
	public boolean isTeleport = false;
	public boolean freeTeleport = false;
	public boolean freeBreachWalls = false;
	public boolean escapePicked = false;
	public boolean escapeTilePicked = false;
	public byte powerCost = 0;

	public static Cache<ArmyMoveAction> CACHE = new Cache<ArmyMoveAction>(() -> new ArmyMoveAction());

	private ArmyMoveAction() {

	}

	@Override
	public void internalInitialize() {
		super.internalInitialize();

		firstMove = true;
		destinationTile = null;
		movementLeft = 1;
		army = null;
		overridingAction = null;
//		temporaryAction = null;
		isTeleport = false;
		freeTeleport = false;
		freeBreachWalls = false;
		escapePicked = false;
		escapeTilePicked = false;
		powerCost = 0;
	}

	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {
		super.validate(expectedParent, currentGame);
		currentGame.validate(destinationTile);
		currentGame.validate(army);

		if (overridingAction != null) {
			overridingAction.validate(this, currentGame);
		}
//		if (temporaryAction != null) {
//			temporaryAction.validate(this, currentGame);
//		}
	}

	@Override
	public void relink(KemetGame clone) {

		army = clone.getArmyByCopy(army);
		destinationTile = clone.getTileByCopy(destinationTile);
		if (overridingAction != null) {
			overridingAction.relink(clone);
		}
//		if (temporaryAction != null) {
//			temporaryAction.relink(clone);
//		}
		super.relink(clone);

	}

	@Override
	public ArmyMoveAction deepCacheClone() {

		ArmyMoveAction clone = CACHE.create();

		copy(clone);

		return clone;
	}

	private void copy(ArmyMoveAction clone) {

		clone.firstMove = firstMove;
		clone.destinationTile = destinationTile;
		clone.movementLeft = movementLeft;
		clone.army = army;
		clone.overridingAction = overridingAction;
		if (overridingAction != null) {
			clone.overridingAction = overridingAction.deepCacheClone();
			clone.overridingAction.setParent(clone);
		}

//		clone.temporaryAction = temporaryAction;
//		if (temporaryAction != null) {
//			clone.temporaryAction = temporaryAction.deepCacheClone();
//			clone.temporaryAction.setParent(clone);
//		}

		clone.isTeleport = isTeleport;
		clone.powerCost = powerCost;
		clone.freeTeleport = freeTeleport;
		clone.freeBreachWalls = freeBreachWalls;
		clone.escapePicked = escapePicked;
		clone.escapeTilePicked = escapeTilePicked;

		super.copy(clone);
	}

	@Override
	public void release() {
		if (overridingAction != null) {
			overridingAction.release();
		}

//		if (temporaryAction != null) {
//			temporaryAction.release();
//		}

		super.release();

		CACHE.release(this);
	}

	@Override
	public void clear() {
		destinationTile = null;
		army = null;
		overridingAction = null;
//		temporaryAction = null;

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
			return pickDestinationTile.getPickChoiceIndex(player.getIndex());
		}

	}

	public class EscapeTileMoveChoice extends PlayerChoice {

		public Tile pickDestinationTile;

		public EscapeTileMoveChoice(KemetGame game, Player player) {
			super(game, player);
		}

		@Override
		public String describe() {
			StringBuilder builder = new StringBuilder();
			Army currentArmy = overridingAction.defendingArmy;
			builder.append("Escape battle with \"").append(currentArmy).append("\"");
			if (currentArmy.tile != null) {
				builder.append(" from tile ").append(currentArmy.tile.name);
			}

			builder.append(" to tile ").append(pickDestinationTile);
			builder.append(".");

			return builder.toString();
		}

		@Override
		public void choiceActivate() {
			overridingAction.defendingArmy.moveToTile(pickDestinationTile);
			overridingAction.attackingArmy.moveToTile(overridingAction.tile);
			escapeTilePicked = true;
			overridingAction = null;
			end();
		}

		@Override
		public int getIndex() {
			return pickDestinationTile.getEscapeChoiceIndex(player.getIndex());
		}

	}

	public class EscapeChoice extends PlayerChoice {

		private boolean escape;

		public EscapeChoice(KemetGame game, Player player, boolean escape) {
			super(game, player);
			this.escape = escape;
		}

		@Override
		public String describe() {
			if (escape) {
				return "Use DI card : " + DiCardList.ESCAPE.toString();
			}
			return "Do not use DI card : " + DiCardList.ESCAPE.toString();
		}

		@Override
		public void choiceActivate() {
			if (escape) {
				createVetoAction(DiCardList.ESCAPE.index, player);
			} else {
				escapePicked = true;
				escapeTilePicked = true;
			}
		}

		@Override
		public int getIndex() {
			if (escape) {
				return ChoiceInventory.ACTIVATE_DI_CARD + DiCardList.ESCAPE.index;
			}
			return ChoiceInventory.SKIP_ESCAPE;
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

			applyKhnumSphinxCostToMove(tile, subChoice);

			if (tile.isWalledByEnemy(player) && !firstMove && !armyHasPowerToBypassWalls()) {
				// moving into a city tile with walls
				log.debug("Army {} can't move to tile {} because it has walls and it isn't the first move.", army,
						tile.name);
				continue;
			}

			if (playerCanAffordCost(subChoice.pickPowerCost)) {
				choiceList.add(subChoice);
			}
		}

		if (armyCanTeleport()) {
			for (Tile tile : game.tileList) {
				if (tile.hasObelisk) {

					if (isTargetTileFriendlyAndFull(player, tile, army)) {
						continue;
					}

					if (tile.index == army.tile.index) {
						// already there.
						continue;
					}

					TileMoveChoice subChoice = new TileMoveChoice(game, player);
					subChoice.pickDestinationTile = tile;
					subChoice.pickIsTeleport = true;

					if (freeTeleport) {
						subChoice.pickPowerCost = 0;
					} else {
						subChoice.pickPowerCost = player.getTeleportCost();
					}

					applyKhnumSphinxCostToMove(tile, subChoice);
					if (playerCanAffordCost(subChoice.pickPowerCost)) {
						choiceList.add(subChoice);
					}
				}
			}
		}
	}

	private boolean playerCanAffordCost(byte pickPowerCost) {
		return player.getPrayerPoints() >= pickPowerCost;
	}

	private void applyKhnumSphinxCostToMove(Tile tile, TileMoveChoice subChoice) {
		Army destinationTileArmy = tile.getArmy();
		if (destinationTileArmy != null && destinationTileArmy.beast == BeastList.BLACK_2_KHNUM_SPHINX) {

			if (army.beast == BeastList.BLUE_2_DEEP_DESERT_SNAKE) {
				log.debug("Cost to move to tile {} didn't change by Khnum's Sphinx due to Deep DesertSnake", tile);
			} else {

				byte extraCost = player.applyPriestOfRaBonus((byte) 2);
				log.debug("Cost to move to tile {} increased by {} due to Khnum's Sphinx", tile, extraCost);
				subChoice.pickPowerCost += extraCost;
			}
		}
	}

	private boolean armyHasPowerToBypassWalls() {
		return player.hasPower(PowerList.RED_2_OPEN_GATE) || army.beast == BeastList.RED_4_PHOENIX || freeBreachWalls;
	}

	private boolean armyCanTeleport() {

		if (freeTeleport) {
			return true;
		}

		if (!player.canTeleport()) {
			// not enough power points to teleport
			return false;
		}

		if (army.tile.getPyramidLevel() > 0) {
			return true;
		}

		if (army.tile.hasObelisk) {
			if (player.hasPower(PowerList.RED_2_TELEPORT)) {
				return true;
			}
			if (army.beast == BeastList.BLACK_3_GRIFFIN_SPHINX) {
				return true;
			}
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
			Tile tile = armyPick.tile;
			int index = player.getIndex();
			int pickChoiceIndex = tile.getPickChoiceIndex(index);
			return pickChoiceIndex;
		}

	}

	public void addArmyPickMoveChoice(List<Choice> choiceList) {
		for (Army army : player.armyList) {
			if (army.tile == null) {
				throw new IllegalStateException("Army " + army + " has empty tile during army picking time." + game.toString());
			}

			ArmyPickMoveChoice subChoice = new ArmyPickMoveChoice(game, player);
			subChoice.armyPick = army;
			choiceList.add(subChoice);
		}
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {

		PlayerChoicePick nextPlayerChoicePick = super.getNextPlayerChoicePick();
		if (nextPlayerChoicePick != null) {
			return nextPlayerChoicePick;
		}

		if (overridingAction != null) {

			if (!escapeTilePicked) {
				Player defender = overridingAction.defendingArmy.owningPlayer;

				if (!escapePicked) {
					// offer escape choice
					if (defenderCanEscapeBattle()) {

						PlayerChoicePick pick = new PlayerChoicePick(game, defender, this);
						pick.choiceList.add(new EscapeChoice(game, defender, true));
						pick.choiceList.add(new EscapeChoice(game, defender, false));

						return pick;

					}
					escapeTilePicked = true;
					escapePicked = true;
				} else {
					// ask for a tile

					PlayerChoicePick pick = new PlayerChoicePick(game, defender, this);

					List<Tile> battleEscapeTileChoice = getBattleEscapeTileChoice();
					for (Tile tile : battleEscapeTileChoice) {
						EscapeTileMoveChoice escape = new EscapeTileMoveChoice(game, defender);
						escape.pickDestinationTile = tile;
						pick.choiceList.add(escape);
					}

					if (pick.choiceList.size() == 1) {
						pick.choiceList.get(0).activate();
					} else {
						return pick;
					}
				}
			}

//			if (overridingAction != null) {
//				nextPlayerChoicePick = overridingAction.getNextPlayerChoicePick();
//				if (nextPlayerChoicePick != null) {
//					return nextPlayerChoicePick;
//				}
//			}
		}

//		if (temporaryAction != null) {
//			nextPlayerChoicePick = temporaryAction.getNextPlayerChoicePick();
//			if (nextPlayerChoicePick == null) {
//				temporaryAction = null;
//			} else {
//				return nextPlayerChoicePick;
//			}
//		}

		if (isEnded()) {
			moveBattleToParent();
			return null;
		}

		if (army == null) {
			// pick army
			PlayerChoicePick pick = new PlayerChoicePick(game, player, this);
			addArmyPickMoveChoice(pick.choiceList);

			addGenericDiCardChoice(pick.choiceList);

			addDiCardChoice(pick.choiceList, DiCardList.ENLISTMENT.index);

			EndTurnChoice.addEndTurnChoice(game, player, pick.choiceList, this,
					ChoiceInventory.ZERO_ARMY_SIZE_CHOICE_INDEX);
			return pick.validate();
		}

		if (movementLeft > 0) {
			// pick tile until move capacity is done
			if (destinationTile == null) {

				PlayerChoicePick pick = new PlayerChoicePick(game, player, this);
				addArmyTileMoveChoice(pick.choiceList);

				if (pick.choiceList.size() == 0) {
					// no possible destination tile (usually because move landed on a island )
					moveBattleToParent();
					return null;
				}

				addGenericDiCardChoice(pick.choiceList);

				addDiCardChoice(pick.choiceList, DiCardList.SWIFTNESS.index);
				addDiCardChoice(pick.choiceList, DiCardList.TELEPORTATION.index);
				addDiCardChoice(pick.choiceList, DiCardList.OPEN_GATES.index);

				EndTurnChoice.addEndTurnChoice(game, player, pick.choiceList, this,
						ChoiceInventory.ZERO_ARMY_SIZE_CHOICE_INDEX);
				return pick.validate();
			}

			PlayerChoicePick pick = new PlayerChoicePick(game, player, this);
			addArmySizeMoveChoice(pick.choiceList);
			EndTurnChoice.addEndTurnChoice(game, player, pick.choiceList, this,
					ChoiceInventory.ZERO_ARMY_SIZE_CHOICE_INDEX);
			return pick.validate();

		}

		moveBattleToParent();
		return null;

	}

	private void moveBattleToParent() {
		if (overridingAction != null) {
			parent.stackPendingActionOnParent(overridingAction);
			overridingAction = null;
		}
	}

	private boolean defenderCanEscapeBattle() {
		if (overridingAction.defendingArmy.owningPlayer.diCards[DiCardList.ESCAPE.index] <= 0) {
			return false;
		}

		// check if there is a tile to move to
		List<Tile> battleEscapeTileChoice = getBattleEscapeTileChoice();
		if (battleEscapeTileChoice.size() == 0) {
			return false;
		}

		return true;
	}

	private List<Tile> getBattleEscapeTileChoice() {
		List<Tile> escapeTiles = new ArrayList<>();
		List<Tile> connectedTiles = overridingAction.tile.connectedTiles;
		for (Tile tile : connectedTiles) {
			// tile is empty
			if (tile.getArmy() == null) {
				escapeTiles.add(tile);
			}
		}
		return escapeTiles;
	}

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {

		super.fillCanonicalForm(cannonicalForm, playerIndex);

		if (!isEnded()) {
			player.setCanonicalState(cannonicalForm, BoardInventory.STATE_MOVE, playerIndex);
			cannonicalForm.set(BoardInventory.MOVES_LEFT, movementLeft);
			cannonicalForm.set(BoardInventory.IS_FIRST_MOVE, (byte) (firstMove ? 1 : 0));

			if (escapePicked) {
				cannonicalForm.set(BoardInventory.ESCAPE_PICKED, (byte) 1);
			}

			if (escapeTilePicked) {
				cannonicalForm.set(BoardInventory.ESCAPE_TILE_PICKED, (byte) 1);
			}

			if (freeBreachWalls) {
				cannonicalForm.set(BoardInventory.MOVE_FREE_BREACH_WALL, (byte) 1);
			}
			if (freeTeleport) {
				cannonicalForm.set(BoardInventory.MOVE_FREE_TELEPORT, (byte) 1);
			}
		} else {
//			throw new IllegalStateException(
//					"Should not be able to fill canonical state on ArmyMoveAction if isEnded() is true ");
		}

		if (overridingAction != null) {

			if (!escapeTilePicked) {
				Player defender = overridingAction.defendingArmy.owningPlayer;

				if (!escapePicked) {
					// offer escape choice
					if (defenderCanEscapeBattle()) {
						defender.setCanonicalState(cannonicalForm, BoardInventory.STATE_PICK_ESCAPE, playerIndex);

						// cannonicalForm.set(BoardInventory.STATE_PICK_ESCAPE,
						// defender.getState(playerIndex));
						return;
					}
				} else {
					// ask for a tile
					defender.setCanonicalState(cannonicalForm, BoardInventory.STATE_ESCAPE_SELECT_TILE, playerIndex);

					// cannonicalForm.set(BoardInventory.STATE_ESCAPE_SELECT_TILE,
					// defender.getState(playerIndex));
					return;
				}
			}

//			overridingAction.fillCanonicalForm(cannonicalForm, playerIndex);
//		} else if (temporaryAction != null && temporaryAction.size() > 0) {
//			temporaryAction.fillCanonicalForm(cannonicalForm, playerIndex);
		} else if (!isEnded()) {

			if (army == null) {
				// pick army source tile
				player.setCanonicalState(cannonicalForm, BoardInventory.STATE_PICK_SOURCE_TILE, playerIndex);

				// cannonicalForm.set(BoardInventory.STATE_PICK_SOURCE_TILE,
				// player.getState(playerIndex));
			} else if (movementLeft > 0) {
				Tile currentArmyTile = army.tile;
				if (currentArmyTile != null) {

					currentArmyTile.setCanonicalSelectedSource(cannonicalForm, player, playerIndex);
				}

				// pick tile until move capacity is done
				if (destinationTile == null) {
					// pick destination tile
					player.setCanonicalState(cannonicalForm, BoardInventory.STATE_PICK_TILE, playerIndex);
					// cannonicalForm.set(BoardInventory.STATE_PICK_TILE,
					// player.getState(playerIndex));
				}

				else {
					// pick army size to move
					player.setCanonicalState(cannonicalForm, BoardInventory.STATE_PICK_ARMY_SIZE, playerIndex);
					// cannonicalForm.set(BoardInventory.STATE_PICK_ARMY_SIZE,
					// player.getState(playerIndex));
					destinationTile.setCanonicalSelected(cannonicalForm, player, playerIndex);
				}
			}
		}
	}

	public void addArmySizeMoveChoice(List<Choice> choiceList) {

		// if friendly army, limit size & beast choice
		byte sourceArmySize = army.armySize;
		Army destinationTileArmy = destinationTile.getArmy();
		if (destinationTileArmy != null && destinationTileArmy.owningPlayer == player) {

			int destinationRemainingCapacity = player.maximumArmySize - destinationTileArmy.armySize;
			byte maxSize = (byte) Math.min(sourceArmySize, destinationRemainingCapacity);

			addAllArmySizeMoveChoice(choiceList, (byte) 1, maxSize, false);
			if (army.beast != null && destinationTileArmy.beast == null) {
				// add options to move beast
				addAllArmySizeMoveChoice(choiceList, (byte) 0, maxSize, true);
			}

		} else {
			addAllArmySizeMoveChoice(choiceList, (byte) 1, sourceArmySize, false);
			if (army.beast != null) {
				addAllArmySizeMoveChoice(choiceList, (byte) 1, sourceArmySize, true);
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
				armyModified += " and the beast " + army.beast.name;
			} else if (army.beast != null) {
				armyModified += ", leaving behind the beast " + army.beast.name;
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

			Army destinationArmy = destinationTile.getArmy();
			if (destinationArmy != null) {
				// there is an army at the destination

				if (destinationArmy.owningPlayer == army.owningPlayer) {
					// player merging army
					army.transferSoldiersToArmy(destinationArmy, moveSoldierCount);
					if (moveBeast) {
						army.transferBeastToArmy(destinationArmy);
					}
					army.checkToDisbandArmy();

					armyThatKeepsMoving = destinationArmy;

				} else {
					// battle about to begin
					Tile previousTile = army.tile;
					army.moveToTile(null);
					army.moveToBattleTile(destinationTile);

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

					if (isDamageDoneFromDeadlyTrap(destinationArmy)) {

						byte enemyArmySize = army.armySize;
						byte damageDone = (byte) Math.min(1, enemyArmySize);
						army.bleedArmy(damageDone, PowerList.BLACK_3_DEADLY_TRAP.toString());

						if (damageDone == enemyArmySize) {
							battleContinues = false;
							battleCancelledAttackerDestroyed = true;
							addRecruitBeastFromRemovedArmy(army);
							army.destroyArmy();
							movementLeft = 0;
							end();
						}
					}

					if (isDamageInflictedFromInitiativePower()) {

						byte enemyArmySize = destinationArmy.armySize;
						byte damageDone = (byte) Math.min(2, enemyArmySize);
						destinationArmy.bleedArmy(damageDone, PowerList.RED_4_INITIATIVE.toString());

						if (damageDone == enemyArmySize) {
							battleContinues = false;
							addRecruitBeastFromRemovedArmy(destinationArmy);
							destinationArmy.destroyArmy();
						}
					}

					if (battleContinues) {
						BattleAction battleAction = BattleAction.create(game, ArmyMoveAction.this);
						battleAction.attackingArmy = army;
						battleAction.defendingArmy = destinationArmy;
						battleAction.tile = destinationTile;
						overridingAction = battleAction;
						end();
					} else if (!battleCancelledAttackerDestroyed) {
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

			if (isTeleport) {
				freeTeleport = false;
			} else {
				// normal move
				movementLeft--;
			}

			if (armyThatKeepsMoving != null && movementLeft > 0 || powerCost > 0) {
				army = armyThatKeepsMoving;
				firstMove = false;
			}

			powerCost = 0;
			isTeleport = false;
			destinationTile = null;
		}

		private boolean isDamageDoneFromDeadlyTrap(Army destinationArmy) {
			if (!destinationArmy.owningPlayer.hasPower(PowerList.BLACK_3_DEADLY_TRAP)) {
				return false;
			}

			if (army.beast == BeastList.BLACK_4_DEVOURER) {

				if (destinationArmy.beast == BeastList.BLUE_2_DEEP_DESERT_SNAKE) {
					log.debug(
							"Deadly trap damage done to army {}, Devourer couldn't avoid it due to Deep Desert Snake.",
							army);
					return true;
				}

				log.debug("Deadly trap damage to army {} avoided due to Devourer.", army);
				return false;
			}

			log.debug("Deadly trap damage done to army {} .", army);
			return true;
		}

		private boolean isDamageInflictedFromInitiativePower() {
			boolean hasPower = army.owningPlayer.hasPower(PowerList.RED_4_INITIATIVE);
			if (!hasPower) {
				return false;
			}

			Army destinationArmy = destinationTile.getArmy();
			if (destinationArmy.beast == BeastList.BLACK_4_DEVOURER) {

				if (destinationArmy.beast == BeastList.BLUE_2_DEEP_DESERT_SNAKE) {
					log.debug(
							"Initiative damage done to army {}. It couldn't be avoided by Devourer due to Deep Desert Snake.",
							army);
					return true;
				}

				log.debug("Initiative damage is avoided on army {} because it has the Devourer beast.",
						destinationArmy);
				return false;
			}

			log.debug("Initiative damage done to army {} .", army);

			return true;
		}

		public void createArmyLeftBehind(Tile previousTile) {
			// moving to empty territory
			byte soldierLeft = (byte) (army.armySize - moveSoldierCount);

			if (soldierLeft == 0) {
				// nobody left behind, simple move
				if (!moveBeast) {
					addRecruitBeastFromRemovedArmy(army);
					army.returnBeastToPlayer();
				}

			} else {
				// create new army for what's left behind
				Army createArmy = player.createArmy();
				army.transferSoldiersToArmy(createArmy, soldierLeft);
				createArmy.moveToTile(previousTile);
				if (!moveBeast) {
					army.transferBeastToArmy(createArmy);
				}
			}
		}

		@Override
		public int getIndex() {

			if (moveSoldierCount == 0) {

				if (moveBeast) {
					return ChoiceInventory.ONLY_BEAST_MOVE;
				}
				log.error("Move army size of 0 isn't valid");
				return ChoiceInventory.ZERO_ARMY_SIZE_CHOICE_INDEX;

			}

			if (moveBeast) {
				return ChoiceInventory.ARMY_SIZE_WITH_BEAST_CHOICE + moveSoldierCount - 1;
			}

			return ChoiceInventory.ARMY_SIZE_CHOICE + moveSoldierCount - 1;
		}

	}

	public void addRecruitBeastFromRemovedArmy(Army removedArmy) {
		if (removedArmy.beast != null) {

			BeastRecruitAction createBeastRecruitAction = BeastRecruitAction.create(game, removedArmy.owningPlayer,
					this, removedArmy.beast);

			addTemporaryAction(createBeastRecruitAction);
		}
	}

	private void addTemporaryAction(Action temporaryActionToAdd) {
//		if (temporaryAction == null) {
//			temporaryAction = ChainedAction.create(game, this);
//		}
//		temporaryActionToAdd.setParent(temporaryAction);
//
//		temporaryAction.add(temporaryActionToAdd);

		parent.stackPendingActionOnParent(temporaryActionToAdd);
	}

	@Override
	public void applyDiCard(int index) {

		if (index == DiCardList.SWIFTNESS.index) {
			movementLeft++;
		} else if (index == DiCardList.TELEPORTATION.index) {
			freeTeleport = true;
		} else if (index == DiCardList.OPEN_GATES.index) {
			freeBreachWalls = true;
		} else if (index == DiCardList.ESCAPE.index) {
			escapePicked = true;
		} else {
			super.applyDiCard(index);
		}
	}

	@Override
	public void enterSimulationMode(int playerIndex) {

		if (overridingAction != null) {
			overridingAction.enterSimulationMode(playerIndex);
		}
//		if (temporaryAction != null) {
//			temporaryAction.enterSimulationMode(playerIndex);
//		}
		super.enterSimulationMode(playerIndex);

	}

	@Override
	public void stackPendingActionOnParent(Action pendingAction) {
//		addTemporaryAction(pendingAction);
		parent.stackPendingActionOnParent(pendingAction);
	}
}
