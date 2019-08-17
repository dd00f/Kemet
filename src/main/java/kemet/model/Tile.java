package kemet.model;

import java.util.ArrayList;
import java.util.List;

import kemet.model.action.choice.ChoiceInventory;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

public class Tile implements Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8210157314973707401L;

	public String name;
	/**
	 * zero based tile index in the game
	 */
	public int index;
	public boolean hasObelisk;
	public boolean isWalled;
	public Color pyramidColor = Color.NONE;
	private byte pyramidLevel = 0;
	public Player owningPlayer = null;
	private Army army = null;
	private Army battleArmy = null;
	public boolean hasTemple = false;
	public byte templeBonusPrayer = 0;
	public byte templeBonusPoints = 0;
	public byte templeArmyCost = 0;
	public boolean templePaireable = true;
	public byte districtIndex = -1;
	public List<Tile> connectedTiles = new ArrayList<>();
	public Player currentScoringPlayer = null;
	public KemetGame game = null;

	private static Cache<Tile> CACHE = new Cache<Tile>(() -> new Tile());

	public static Tile create(int index) {
		Tile create = CACHE.create();
		create.initialize();
		create.index = index;
		return create;
	}

	private Tile() {

	}

	@Override
	public void initialize() {
		name = null;
		hasObelisk = false;
		pyramidColor = Color.NONE;
		game = null;

		pyramidLevel = 0;
		owningPlayer = null;
		army = null;
		battleArmy = null;
		hasTemple = false;
		isWalled = false;
		templeBonusPrayer = 0;
		templeBonusPoints = 0;
		templeArmyCost = 0;
		index = 0;
		districtIndex = -1;
		templePaireable = true;
		connectedTiles.clear();
		currentScoringPlayer = null;
	}

	public void relink(KemetGame game) {

		this.game = game;

		for (int i = 0; i < connectedTiles.size(); i++) {
			connectedTiles.set(i, game.getTileByCopy(connectedTiles.get(i)));
		}

		currentScoringPlayer = game.getPlayerByCopy(currentScoringPlayer);

		owningPlayer = game.getPlayerByCopy(owningPlayer);

		army = game.getArmyByCopy(army);
		battleArmy = game.getArmyByCopy(battleArmy);
	}

	@Override
	public Model deepCacheClone() {

		Tile clone = CACHE.create();

		clone.connectedTiles.clear();
		clone.connectedTiles.addAll(connectedTiles);

		clone.game = game;
		clone.name = name;
		clone.hasObelisk = hasObelisk;
		clone.pyramidColor = pyramidColor;
		clone.pyramidLevel = pyramidLevel;
		clone.isWalled = isWalled;

		clone.hasTemple = hasTemple;
		clone.templeBonusPrayer = templeBonusPrayer;
		clone.templeBonusPoints = templeBonusPoints;
		clone.templeArmyCost = templeArmyCost;
		clone.templePaireable = templePaireable;
		clone.index = index;
		clone.districtIndex = districtIndex;

		clone.currentScoringPlayer = currentScoringPlayer;
		clone.owningPlayer = owningPlayer;
		clone.army = army;
		clone.battleArmy = battleArmy;

		return clone;
	}

	@Override
	public void release() {

		connectedTiles.clear();

		owningPlayer = null;

		army = null;
		battleArmy = null;
		currentScoringPlayer = null;

		CACHE.release(this);
	}

	public void setPyramidLevel(byte level) {
		this.pyramidLevel = level;
		checkToUpdatePyramidScoringPlayer();
	}

	private void checkToUpdateTempleScoringPlayer() {
		if (hasTemple) {
			Player scoringPlayer = getTempleScoringPlayer();
			if (scoringPlayer != currentScoringPlayer) {

				if (currentScoringPlayer != null) {
					currentScoringPlayer.decreaseTempleOccupationPoints(this);
				}
				if (scoringPlayer != null) {
					scoringPlayer.increaseTempleOccupationPoints(this);
				}
				currentScoringPlayer = scoringPlayer;
			}
		}
	}

	private Player getTempleScoringPlayer() {
		if (army != null) {
			return army.owningPlayer;
		}
		return null;
	}

	private void checkToUpdatePyramidScoringPlayer() {
		if (owningPlayer != null) {
			Player scoringPlayer = getPyramidScoringPlayer();
			if (scoringPlayer != currentScoringPlayer) {

				if (currentScoringPlayer != null) {
					currentScoringPlayer.decreasePyramidOccupationPoints(this);
				}
				if (scoringPlayer != null) {
					scoringPlayer.increasePyramidOccupationPoints(this);
				}
				currentScoringPlayer = scoringPlayer;
			}
		}
	}

	private Player getPyramidScoringPlayer() {
		if (pyramidLevel >= 4) {
			if (army != null) {
				return army.owningPlayer;
			}
			return owningPlayer;
		}
		return null;
	}

	public void setBattleArmy(Army battleArmy) {
		this.battleArmy = battleArmy;
	}

	public Army getBattleArmy() {
		return battleArmy;
	}

	public void setArmy(Army army) {
		this.army = army;
		checkToUpdatePyramidScoringPlayer();
		checkToUpdateTempleScoringPlayer();
	}

	public boolean hasPyramid() {
		return pyramidLevel > 0;
	}

	public void activateTemple() {
		if (army != null && hasTemple) {
			army.owningPlayer.modifyPrayerPoints(templeBonusPrayer, "bonus of " + name);
			if (templeBonusPoints != 0) {
				army.owningPlayer.addTemplePermanentVictoryPoint("temple bonus point of " + name);
			}

			// temple activation not done during the day.
//			if (army.owningPlayer.hasPower(PowerList.BLACK_4_DIVINE_STRENGTH)) {
//				army.owningPlayer.modifyPrayerPoints((byte) 1, PowerList.BLACK_4_DIVINE_STRENGTH.toString());
//			}

			if (templeArmyCost > 0) {
				army.bleedArmy(templeArmyCost, "army cost of " + name);
				army.checkToDisbandArmy();
			}
		}
	}

	@Override
	public String toString() {
		return name;
	}

	public String describe() {
		StringBuilder build = new StringBuilder();
		return describe(build);
	}

	public String describe(StringBuilder build) {
		build.append(name);
		if (pyramidColor != Color.NONE) {
			build.append(" with pyramid ");
			build.append(pyramidColor);
			build.append(" of level ");
			build.append(pyramidLevel);
		}

		if (hasTemple) {
			build.append(" with temple of ");
			build.append(templeBonusPrayer);
			build.append(" bonus prayer points.");
		}
		return build.toString();
	}

	public boolean isWalledByEnemy(Player player) {
		boolean retVal = false;
		if (isWalled) {
			if (army != null) {
				// if territory is occupied, check army match
				retVal = army.owningPlayer != player;
			} else {
				retVal = owningPlayer != player;
			}
		}

		return retVal;
	}

	public Army getArmy() {
		return army;
	}

	public byte getPyramidLevel() {
		return pyramidLevel;
	}

	public boolean canUpgradePyramid() {
		return pyramidLevel != 4 && isWalled;
	}

	public void validate(KemetGame currentGame) {
		currentGame.validate(owningPlayer);
		currentGame.validate(army);
		currentGame.validate(currentScoringPlayer);

		for (Tile tile : connectedTiles) {
			currentGame.validate(tile);
		}
	}

	public int getPickChoiceIndex(int playerIndex) {
		return ChoiceInventory.PICK_TILE_CHOICE + getTileCanonicalIndex(playerIndex);
	}

	public int getEscapeChoiceIndex(int playerIndex) {
		return ChoiceInventory.ESCAPE_TILE_CHOICE + getTileCanonicalIndex(playerIndex);
	}

	public void setSelected(ByteCanonicalForm cannonicalForm, int playerIndex, byte value) {
		int tileCanonicalIndex = getTileCanonicalIndex(playerIndex);
		cannonicalForm.set(BoardInventory.TILE_SELECTED + tileCanonicalIndex, value);
	}

	public void setSelectedSource(ByteCanonicalForm cannonicalForm, int playerIndex, byte value) {
		int tileCanonicalIndex = getTileCanonicalIndex(playerIndex);
		cannonicalForm.set(BoardInventory.TILE_SOURCE_SELECTED + tileCanonicalIndex, value);
	}

	public int getTileCanonicalIndex(int targetPlayerIndex) {
		if (districtIndex == -1) {
			return index;
		}

		int canonicalPlayerIndex = owningPlayer.getCanonicalPlayerIndex(targetPlayerIndex);
		Tile tileByPlayerAndDistrictIndex = game.getTileByPlayerAndDistrictIndex(canonicalPlayerIndex, districtIndex);
		return tileByPlayerAndDistrictIndex.index;
	}

	public void fillCanonicalForm(ByteCanonicalForm canonicalForm, int playerIndex) {

		// the current player must always have its tiles at the same place for AI
		// training
		int tileCanonicalIndex = getTileCanonicalIndex(playerIndex);

		fillTileArmy(canonicalForm, playerIndex, tileCanonicalIndex, army);
		fillTileArmy(canonicalForm, playerIndex, tileCanonicalIndex, battleArmy);

		if (pyramidColor != Color.NONE) {
			if (pyramidColor == Color.RED) {
				canonicalForm.set(BoardInventory.TILE_RED_PYRAMID_LEVEL + tileCanonicalIndex, pyramidLevel);
			} else if (pyramidColor == Color.BLUE) {
				canonicalForm.set(BoardInventory.TILE_BLUE_PYRAMID_LEVEL + tileCanonicalIndex, pyramidLevel);
			} else if (pyramidColor == Color.WHITE) {
				canonicalForm.set(BoardInventory.TILE_WHITE_PYRAMID_LEVEL + tileCanonicalIndex, pyramidLevel);
			} else if (pyramidColor == Color.BLACK) {
				canonicalForm.set(BoardInventory.TILE_BLACK_PYRAMID_LEVEL + tileCanonicalIndex, pyramidLevel);
			}
		}

	}

	private void fillTileArmy(ByteCanonicalForm canonicalForm, int playerIndex, int tileCanonicalIndex, Army tileArmy) {
		if (tileArmy != null) {
			int owningPlayerCanonicalIndex = tileArmy.owningPlayer.getCanonicalPlayerIndex(playerIndex);

			int startArmySizeOffset = BoardInventory.TILE_PLAYER_ARMY_SIZE;
			int armySizePlayerOffset = owningPlayerCanonicalIndex * BoardInventory.TILE_COUNT;
			int armySizePlayerTileOffset = startArmySizeOffset + armySizePlayerOffset + tileCanonicalIndex;
			canonicalForm.set(armySizePlayerTileOffset, tileArmy.armySize);

			if (tileArmy.beast != null) {

				int startOffset = BoardInventory.BEAST_POSITION;
				int playerOffset = armySizePlayerOffset * BeastList.BEAST_INDEXER;
				int beastOffset = tileArmy.beast.index * BoardInventory.TILE_COUNT;
				int finalOffset = startOffset + playerOffset + beastOffset + index;

				canonicalForm.set(finalOffset, (byte) 1);
			}

		}
	}

}
