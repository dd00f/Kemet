package kemet.model;

import kemet.model.action.choice.ChoiceInventory;

public class BattleCard implements Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3973427547601563125L;

	public static final String SACRIFICIAL_CHARGE = "Sacrificial charge";
	public static final String PHALANX_DEFENSE = "Phalanx defense";
	public static final String CHARIOT_RAID = "Chariot Raid";
	public static final String CAVALRY_BLITZ = "Cavalry Blitz";
	public static final String MIXED_TACTICS = "Mixed Tactics";
	public static final String SHIELD_PUSH = "Shield Push";
	public static final String DEFENSIVE_RETREAT = "Defensive Retreat";
	public static final String FERVENT_PURGE = "Fervent Purge";
	
	public static final int CARD_COUNT = 8;

	public static int INDEXER = 0;

	public static final BattleCard SACRIFICIAL_CHARGE_CARD = new BattleCard(SACRIFICIAL_CHARGE, (byte) 5, (byte) 0,
			(byte) 0, (byte) 2, INDEXER++);
	public static final BattleCard DEFENSIVE_RETREAT_CARD = new BattleCard(DEFENSIVE_RETREAT, (byte) 1, (byte) 3,
			(byte) 1, (byte) 0, INDEXER++);
	public static final BattleCard PHALANX_DEFENSE_CARD = new BattleCard(PHALANX_DEFENSE, (byte) 2, (byte) 2, (byte) 0,
			(byte) 0, INDEXER++);
	public static final BattleCard CAVALRY_BLITZ_CARD = new BattleCard(CAVALRY_BLITZ, (byte) 4, (byte) 0, (byte) 1,
			(byte) 0, INDEXER++);

	public static final BattleCard CHARIOT_RAID_CARD = new BattleCard(CHARIOT_RAID, (byte) 1, (byte) 0, (byte) 3,
			(byte) 0, INDEXER++);
	public static final BattleCard SHIELD_PUSH_CARD = new BattleCard(SHIELD_PUSH, (byte) 3, (byte) 1, (byte) 0,
			(byte) 0, INDEXER++);
	public static final BattleCard MIXED_TACTICS_CARD = new BattleCard(MIXED_TACTICS, (byte) 2, (byte) 1, (byte) 2,
			(byte) 0, INDEXER++);
	public static final BattleCard FERVENT_PURGE_CARD = new BattleCard(FERVENT_PURGE, (byte) 3, (byte) 0, (byte) 2,
			(byte) 0, INDEXER++);

	public byte attackBonus = 0;
	public byte shieldBonus = 0;
	public byte bloodBonus = 0;
	public byte armyCost = 0;

	public String name;

	/**
	 * zero based card index in the game
	 */
	public int index;

	@Override
	public void initialize() {
		attackBonus = 0;
		shieldBonus = 0;
		bloodBonus = 0;
		armyCost = 0;
		index = 0;
	}

	private BattleCard(String name, byte attackBonus, byte shieldBonus, byte bloodBonus, byte armyCost, int index) {
		this.name = name;
		this.attackBonus = attackBonus;
		this.shieldBonus = shieldBonus;
		this.bloodBonus = bloodBonus;
		this.armyCost = armyCost;
		this.index = index;

	}

	@Override
	public String toString() {
		return "\"" + name + "\" Attack " + attackBonus + " Shield " + shieldBonus + " Blood " + bloodBonus
				+ " Army Cost " + armyCost;
	}

	@Override
	public Model deepCacheClone() {
		return this;
	}

	@Override
	public void release() {

	}

	public int getPickChoiceIndex() {
		return ChoiceInventory.PICK_BATTLE_CARD_CHOICE + index;
	}
}
