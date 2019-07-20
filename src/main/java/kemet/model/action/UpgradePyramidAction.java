package kemet.model.action;

import java.util.List;

import kemet.model.Army;
import kemet.model.BoardInventory;
import kemet.model.Color;
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

public class UpgradePyramidAction extends DiCardAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2961911483446230497L;

	public Color color;
	public byte startLevel;
	public byte endLevel = -1;
	public byte powerCost;
	public Tile tile;
	public boolean freeLevel = false;

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {
		super.fillCanonicalForm(cannonicalForm, playerIndex);
		
		cannonicalForm.set(BoardInventory.STATE_UPGRADE_PYRAMID, player.getState(playerIndex));
		if (tile == null) {
			cannonicalForm.set(BoardInventory.STATE_PICK_TILE, player.getState(playerIndex));
		} else if (endLevel == -1) {
			cannonicalForm.set(BoardInventory.STATE_PICK_PYRAMID_LEVEL, player.getState(playerIndex));
			tile.setSelected(cannonicalForm, playerIndex, player.getState(playerIndex));
			cannonicalForm.set(BoardInventory.PICKED_LEVEL, player.getState(playerIndex));
		} else if (color == Color.NONE) {
			cannonicalForm.set(BoardInventory.STATE_PICK_PYRAMID_COLOR, player.getState(playerIndex));
			tile.setSelected(cannonicalForm, playerIndex, player.getState(playerIndex));
		}
	}

	public static Cache<UpgradePyramidAction> CACHE = new Cache<UpgradePyramidAction>(() -> new UpgradePyramidAction());

	@Override
	public void internalInitialize() {

		super.internalInitialize();
		tile = null;
		color = null;
		freeLevel = false;
		startLevel = 0;
		endLevel = -1;
		powerCost = 0;
	}

	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {
		super.validate(expectedParent, currentGame);

		currentGame.validate(tile);
	}

	private UpgradePyramidAction() {

	}

	@Override
	public void relink(KemetGame clone) {
		tile = clone.getTileByCopy(tile);

		super.relink(clone);
	}

	@Override
	public UpgradePyramidAction deepCacheClone() {

		UpgradePyramidAction clone = CACHE.create();

		copy(clone);

		return clone;
	}

	private void copy(UpgradePyramidAction clone) {

		clone.tile = tile;
		clone.color = color;
		clone.startLevel = startLevel;
		clone.endLevel = endLevel;
		clone.powerCost = powerCost;
		clone.tile = tile;
		clone.freeLevel = freeLevel;

		super.copy(clone);
	}

	@Override
	public void release() {

		clear();
		CACHE.release(this);
	}

	@Override
	public void clear() {

		tile = null;

		super.clear();
	}

	public static UpgradePyramidAction create(KemetGame game, Player player, Action parent) {
		UpgradePyramidAction create = CACHE.create();
		create.initialize();
		create.game = game;
		create.player = player;
		create.parent = parent;

		return create;
	}

	public void activateAction() {
		player.modifyPrayerPoints((byte) -powerCost, "pyramid upgrade");
		tile.pyramidColor = color;
		tile.setPyramidLevel(endLevel);
	}

	public class UpgradePyramidPickTileChoice extends PlayerChoice {

		public Tile pickTile;

		public UpgradePyramidPickTileChoice(KemetGame game, Player player, Tile tile) {
			super(game, player);
			this.pickTile = tile;
		}

		@Override
		public String describe() {
			if (pickTile.pyramidColor != Color.NONE) {

				return "upgrade " + pickTile.pyramidColor + " pyramid on tile \"" + pickTile.name + "\" of level "
						+ pickTile.getPyramidLevel();
			}
			return "create pyramid on tile \"" + pickTile.name + "\"";
		}

		@Override
		public void choiceActivate() {
			tile = pickTile;
			color = pickTile.pyramidColor;
			if (freeLevel) {
				endLevel = (byte) (tile.getPyramidLevel() + 1);

				if (color != Color.NONE) {
					// upgrading existing pyramid
					activateAction();
				}
			}
		}

		@Override
		public int getIndex() {
			return pickTile.getPickChoiceIndex(player.getIndex());
		}

	}

	public class UpgradePyramidPickColorChoice extends PlayerChoice {

		public Color pickColor;

		public UpgradePyramidPickColorChoice(KemetGame game, Player player, Color pickColor) {
			super(game, player);
			this.pickColor = pickColor;
		}

		@Override
		public String describe() {
			return "upgrade pyramid " + pickColor + " from level " + startLevel + " to " + endLevel + " on tile \""
					+ tile.name + "\" for " + powerCost + " prayer points.";
		}

		@Override
		public void choiceActivate() {

			color = pickColor;
			activateAction();
		}

		@Override
		public int getIndex() {
			return ChoiceInventory.PICK_COLOR_CHOICE + pickColor.ordinal();
		}

	}

	public class UpgradePyramidPickLevelChoice extends PlayerChoice {

		private byte pickEndLevel;
		private byte pickCost;
		private byte pickStartLevel;

		public UpgradePyramidPickLevelChoice(KemetGame game, Player player, byte startLevel, byte level, byte cost) {
			super(game, player);
			this.pickStartLevel = startLevel;
			this.pickEndLevel = level;
			this.pickCost = cost;
		}

		@Override
		public String describe() {
			return "upgrade pyramid from level " + pickStartLevel + " to " + pickEndLevel + " on tile \"" + tile.name
					+ "\" for " + pickCost + " prayer points.";

		}

		@Override
		public void choiceActivate() {
			startLevel = pickStartLevel;
			endLevel = pickEndLevel;
			powerCost = pickCost;

			if (color != Color.NONE) {
				// upgrading existing pyramid
				activateAction();
			}
		}

		@Override
		public int getIndex() {
			return ChoiceInventory.PICK_PYRAMID_LEVEL_CHOICE + pickEndLevel - 1;
		}

	}

	private void createAllPyramidLevelChoices(byte pyramidInitialLevel, List<Choice> choiceList) {
		for (byte i = 4; i > pyramidInitialLevel; --i) {
			byte cost = calculateCost(pyramidInitialLevel, i);
			if (player.getPrayerPoints() >= cost) {
				// enough prayer points available
				UpgradePyramidPickLevelChoice choice = new UpgradePyramidPickLevelChoice(game, player,
						pyramidInitialLevel, i, cost);
				choiceList.add(choice);
			}
		}
	}

	private byte calculateCost(byte pyramidInitialLevel, int i) {
		byte cost = 0;
		boolean hasSlaves = player.hasPower(PowerList.WHITE_2_SLAVE);
		for (byte j = (byte) (pyramidInitialLevel + 1); j <= i; j++) {
			cost += j;
			if (hasSlaves) {
				cost -= 1;
			}
		}

		cost = player.applyPriestOfRaBonus(cost);

		return cost;
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {
		
		PlayerChoicePick nextPlayerChoicePick = super.getNextPlayerChoicePick();
		if (nextPlayerChoicePick != null) {
			return nextPlayerChoicePick;
		}
		
		if (isEnded()) {
			return null;
		}

		if (tile == null) {
			// pick tile
			PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

			for (Tile currentCityTile : player.cityTiles) {
				if (currentCityTile.canUpgradePyramid()) {
					pick.choiceList.add(new UpgradePyramidPickTileChoice(game, player, currentCityTile));
				}
			}

			for (Army army : player.armyList) {
				// all other city districts currently controlled
				Tile armyTile = army.tile;
				if (armyTile.owningPlayer != null && armyTile.owningPlayer != player && armyTile.canUpgradePyramid()) {
					pick.choiceList.add(new UpgradePyramidPickTileChoice(game, player, armyTile));
				}
			}
			
			addGenericDiCardChoice(pick.choiceList);

			EndTurnChoice.addEndTurnChoice(game, player, pick.choiceList, this, ChoiceInventory.UPGRADE_NOTHING);
			return pick.validate();

		} else if (endLevel == -1) {
			// pick level
			PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

			createAllPyramidLevelChoices(tile.getPyramidLevel(), pick.choiceList);
			
			addGenericDiCardChoice(pick.choiceList);

			EndTurnChoice.addEndTurnChoice(game, player, pick.choiceList, this, ChoiceInventory.UPGRADE_NOTHING);

			return pick.validate();

		} else if (color == Color.NONE) {
			// pick color
			PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

			if (!player.hasPyramid(Color.BLACK)) {
				pick.choiceList.add(new UpgradePyramidPickColorChoice(game, player, Color.BLACK));
			}
			if (!player.hasPyramid(Color.BLUE)) {
				pick.choiceList.add(new UpgradePyramidPickColorChoice(game, player, Color.BLUE));
			}
			if (!player.hasPyramid(Color.WHITE)) {
				pick.choiceList.add(new UpgradePyramidPickColorChoice(game, player, Color.WHITE));
			}
			if (!player.hasPyramid(Color.RED)) {
				pick.choiceList.add(new UpgradePyramidPickColorChoice(game, player, Color.RED));
			}
			
			addGenericDiCardChoice(pick.choiceList);
			
			EndTurnChoice.addEndTurnChoice(game, player, pick.choiceList, this, ChoiceInventory.UPGRADE_NOTHING);

			return pick.validate();

		}
		return null;
	}


}
