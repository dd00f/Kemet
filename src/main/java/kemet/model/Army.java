package kemet.model;

import java.util.logging.Logger;

import kemet.util.Cache;

public class Army implements Model {

	private static final long serialVersionUID = -4443577609612057529L;

	public static final Logger LOGGER = Logger.getLogger(Army.class.getName());

	public String name;
	public Beast beast = null;
	public byte armySize = 0;
	public Player owningPlayer;
	public Tile tile;

	private static Cache<Army> CACHE = new Cache<Army>(() -> new Army());

	private Army() {

	}

	@Override
	public void initialize() {

		name = null;
		beast = null;
		armySize = 0;
		owningPlayer = null;
		tile = null;

	}

	public static Army create() {
		Army create = CACHE.create();
		create.initialize();
		return create;
	}

	@Override
	public Army deepCacheClone() {

		Army clone = CACHE.create();

		clone.name = name;
		clone.beast = beast;
		clone.armySize = armySize;
		clone.owningPlayer = owningPlayer;
		clone.tile = tile;

		return clone;
	}

	@Override
	public void release() {

		owningPlayer = null;
		tile = null;

		CACHE.release(this);
	}

	public void relink(KemetGame game) {
		tile = game.getTileByCopy(tile);
		owningPlayer = game.getPlayerByCopy(owningPlayer);
	}

	public byte getMoveCount() {

		byte returnValue = owningPlayer.moveCapacity;
		if (beast != null) {
			returnValue += beast.moveBonus;
		}
		return returnValue;
	}

	public byte getScore(boolean isAttacking) {
		byte score = armySize;
		if (beast != null) {
			score += beast.fightBonus;
		}
		score += owningPlayer.fightBonus;

		if (isAttacking) {
			score += owningPlayer.attackBonus;
		} else {
			score += owningPlayer.defenseBonus;
		}

		return score;
	}

	public void bleedArmy(byte bleedCount, String reason) {

		if (bleedCount == 0) {
			return;
		}

		if (bleedCount > armySize) {
			bleedCount = armySize;
		}

		if (isPrintEnabled()) {
			printEvent("Army " + name + " bleed by " + bleedCount + " due to " + reason);
		}

		armySize -= bleedCount;
		owningPlayer.availableArmyTokens += bleedCount;
	}

	private void printEvent(String string) {
		owningPlayer.game.printEvent(string);
	}

	public void destroyArmy() {

		if (isPrintEnabled()) {
			printEvent("Army " + name + " removed from the game.");
		}

		moveToTile(null);

		owningPlayer.armyList.remove(this);
		owningPlayer.destroyedArmyList.add(this);
		returnBeastToPlayer();
		owningPlayer.availableArmyTokens += armySize;
		armySize = 0;
	}

	private boolean isPrintEnabled() {
		return owningPlayer.game.printActivations;
	}

	public void returnBeastToPlayer() {
		if (beast != null) {
			owningPlayer.availableBeasts.add(beast);
			beast = null;
		}
	}

	public void transferBeastToArmy(Army otherArmy) {
		if (beast != null) {
			if (otherArmy.beast != null) {
				LOGGER.warning("Trying to transfer beast to army that already has a beast.");
				return;
			}

			otherArmy.beast = beast;
			beast = null;
		}

	}

	public void transferSoldiersToArmy(Army otherArmy, byte count) {
		armySize -= count;
		otherArmy.armySize += count;
		validateArmySize();
		otherArmy.validateArmySize();
	}

	private void validateArmySize() {
		if (armySize > owningPlayer.maximumArmySize) {
			LOGGER.warning("validateArmySize lead to armySize > owningPlayer.maximumArmySize ." + name);
		}
		if (armySize < 0) {
			LOGGER.warning("validateArmySize lead to armySize = 0." + name);
		}
	}

	public void recruit(byte soldierCount) {
		armySize += soldierCount;
		owningPlayer.availableArmyTokens -= soldierCount;
		validateArmySize();
		validateAvailableArmyToken();
	}

	private void validateAvailableArmyToken() {
		if (owningPlayer.availableArmyTokens < 0) {
			LOGGER.warning("owningPlayer.availableArmyTokens < 0  after recruit.");
		}
	}

	public void addBeast(Beast beastAdd) {
		if (beastAdd != null) {
			beast = beastAdd;
			boolean success = owningPlayer.availableBeasts.remove(beastAdd);
			if (!success) {
				LOGGER.warning("Army.addBeast on beast that wasnt in player inventory " + beastAdd.name);
			}
		}
	}

	public void moveToTile(Tile newTile) {

		if (newTile != null && newTile.getArmy() == this) {
			return;
		}

		if (newTile != null && newTile.getArmy() != null) {
			LOGGER.warning("Army.moveToTile " + name + " already has army " + newTile.getArmy().name);
			return;
		}
		if (newTile != null) {
			newTile.setArmy(this);
		}
		if (tile != null) {
			// remove the old army association
			tile.setArmy(null);
		}
		tile = newTile;

	}

	public boolean isArmySizeFull() {
		return armySize >= owningPlayer.maximumArmySize;
	}

	public boolean isArmySizeAndBeastFull() {
		return isArmySizeFull() && beast != null;
	}

	@Override
	public String toString() {
		String string = name + " of " + armySize + " soldiers";
		if (beast != null) {
			string += " with beast " + beast.toString();
		}

		if (tile != null) {
			string += " on tile " + tile.name;
		}

		return string;
	}

	public void checkToDisbandArmy() {
		if (armySize == 0) {
			destroyArmy();
		}

	}

	public void recall() {
		// add prayer points back to player
		owningPlayer.modifyPrayerPoints(armySize, "army recall");

		destroyArmy();
	}

	public boolean playerCanRecruitOnArmy() {

		if (armySize < owningPlayer.maximumArmySize && owningPlayer.availableArmyTokens > 0) {
			return true;
		}

		if (beast == null && owningPlayer.availableBeasts.size() > 0) {
			return true;
		}

		return false;

	}

	public void describeArmy() {
		if (isPrintEnabled()) {
			String beastStr = "";
			if (beast != null) {
				beastStr = " with beast " + beast.name;
			}
			String tileName = null;
			if (tile != null) {
				tileName = tile.describe();
			}

			printEvent("\t Army : " + name + " of size " + armySize + beastStr + " on tile : " + tileName);
		}
	}

	public void validate(KemetGame currentGame) {
		validateArmySize();
		validateAvailableArmyToken();

		currentGame.validate(owningPlayer);
		currentGame.validate(tile);
	}

	public byte getAttackStrength() {
		return armySize;
	}

	public byte getAttackShield() {
		return 0;
	}

	public byte getAttackDamage() {
		return 0;
	}

	public byte getDefendingStrength() {
		return armySize;
	}

	public byte getDefendingShield() {
		return 0;
	}

	public byte getDefendingDamage() {
		return 0;
	}

}
