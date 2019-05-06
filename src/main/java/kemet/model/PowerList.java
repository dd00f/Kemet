package kemet.model;

import kemet.model.power.BestialFuryPower;
import kemet.model.power.BladesOfNeithPower;
import kemet.model.power.CarnagePower;
import kemet.model.power.ChargePower;
import kemet.model.power.DefensePower;
import kemet.model.power.GodSpeedPower;
import kemet.model.power.LegionPower;
import kemet.model.power.ShieldOfNeithPower;
import kemet.model.power.VictoryPointPower;

public class PowerList {

	public static int POWER_INDEXER = 0;

	public static final String WHITE_1_PRIEST_NAME = "Priest";
	public static final String WHITE_1_PRIEST_DESCRIPTION = "+1 power point on pray action.";
	public static final Power WHITE_1_PRIEST_1 = new Power(POWER_INDEXER++, WHITE_1_PRIEST_NAME, (byte) 1, Color.WHITE,
			WHITE_1_PRIEST_DESCRIPTION);
	public static final Power WHITE_1_PRIEST_2 = new Power(POWER_INDEXER++, WHITE_1_PRIEST_NAME, (byte) 1, Color.WHITE,
			WHITE_1_PRIEST_DESCRIPTION);

	public static final String WHITE_1_PRIESTESS_NAME = "Priestess";
	public static final String WHITE_1_PRIESTESS_DESCRIPTION = "-1 cost to buy power tile.";
	public static final Power WHITE_1_PRIESTESS_1 = new Power(POWER_INDEXER++, WHITE_1_PRIESTESS_NAME, (byte) 1,
			Color.WHITE, WHITE_1_PRIESTESS_DESCRIPTION);
	public static final Power WHITE_1_PRIESTESS_2 = new Power(POWER_INDEXER++, WHITE_1_PRIESTESS_NAME, (byte) 1,
			Color.WHITE, WHITE_1_PRIESTESS_DESCRIPTION);

	public static final String WHITE_2_SLAVE_NAME = "Slave";
	public static final String WHITE_2_SLAVE_DESCRIPTION = "-1 cost per pyramid upgrade level.";
	public static final Power WHITE_2_SLAVE = new Power(POWER_INDEXER++, WHITE_2_SLAVE_NAME, (byte) 2, Color.WHITE,
			WHITE_2_SLAVE_DESCRIPTION);

	public static final String WHITE_2_GREAT_PRIEST_NAME = "Great Priest";
	public static final String WHITE_2_GREAT_PRIEST_DESCRIPTION = "+2 power at night.";
	public static final Power WHITE_2_GREAT_PRIEST = new Power(POWER_INDEXER++, WHITE_2_GREAT_PRIEST_NAME, (byte) 2,
			Color.WHITE, WHITE_2_GREAT_PRIEST_DESCRIPTION);

	public static final String WHITE_2_CRUSADE_NAME = "Crusade";
	public static final String WHITE_2_CRUSADE_DESCRIPTION = "+2 power for each unit you destroy in battle.";
	public static final Power WHITE_2_CRUSADE = new Power(POWER_INDEXER++, WHITE_2_CRUSADE_NAME, (byte) 2, Color.WHITE,
			WHITE_2_CRUSADE_DESCRIPTION);

	// TODO fill up
//	public static final String WHITE_2_CRUSADE_NAME = "Crusade";
//	public static final String WHITE_2_CRUSADE_DESCRIPTION = "+2 power for each unit you destroy in battle.";
//	public static final Power WHITE_2_MISSING = new Power(POWER_INDEXER++, WHITE_2_CRUSADE_NAME, (byte) 2, Color.WHITE,
//			WHITE_2_CRUSADE_DESCRIPTION);

	public static final String WHITE_3_HOLY_WAR_NAME = "Holy War";
	public static final String WHITE_3_HOLY_WAR_DESCRIPTION = "+4 power per battle won.";
	public static final Power WHITE_3_HOLY_WAR = new Power(POWER_INDEXER++, WHITE_3_HOLY_WAR_NAME, (byte) 3,
			Color.WHITE, WHITE_3_HOLY_WAR_DESCRIPTION);

	public static final String VICTORY_POINT_NAME = "Victory Point";
	public static final String VICTORY_POINT_DESCRIPTION = "+1 Permanent Victory Point.";
	public static final Power WHITE_3_VICTORY_POINT = new VictoryPointPower(POWER_INDEXER++, VICTORY_POINT_NAME,
			(byte) 3, Color.WHITE, VICTORY_POINT_DESCRIPTION);

	public static final String WHITE_4_PRIEST_OF_RA_NAME = "PRIEST_OF_RA";
	public static final String WHITE_4_PRIEST_OF_RA_DESCRIPTION = "-1 cost on everything.";
	public static final Power WHITE_4_PRIEST_OF_RA = new Power(POWER_INDEXER++, WHITE_4_PRIEST_OF_RA_NAME, (byte) 4,
			Color.WHITE, WHITE_4_PRIEST_OF_RA_DESCRIPTION);

	public static final String WHITE_4_PRIEST_OF_AMON_NAME = "PRIEST_OF_AMON";
	public static final String WHITE_4_PRIEST_OF_AMON_DESCRIPTION = "+5 power at night.";
	public static final Power WHITE_4_PRIEST_OF_AMON = new Power(POWER_INDEXER++, WHITE_4_PRIEST_OF_AMON_NAME, (byte) 4,
			Color.WHITE, WHITE_4_PRIEST_OF_AMON_DESCRIPTION);

	public static final String RED_1_CHARGE_NAME = "Charge";
	public static final String RED_1_CHARGE_DESCRIPTION = "+1 strength when attacking.";
	public static final Power RED_1_CHARGE_1 = new ChargePower(POWER_INDEXER++, RED_1_CHARGE_NAME, (byte) 1, Color.RED,
			RED_1_CHARGE_DESCRIPTION);
	public static final Power RED_1_CHARGE_2 = new ChargePower(POWER_INDEXER++, RED_1_CHARGE_NAME, (byte) 1, Color.RED,
			RED_1_CHARGE_DESCRIPTION);

	public static final String RED_1_STARGATE_NAME = "Stargate";
	public static final String RED_1_STARGATE_DESCRIPTION = "-1 cost to teleport.";
	public static final Power RED_1_STARGATE = new Power(POWER_INDEXER++, RED_1_STARGATE_NAME, (byte) 1, Color.RED,
			RED_1_STARGATE_DESCRIPTION);

	public static final String RED_1_GOD_SPEED_NAME = "God Speed";
	public static final String RED_1_GOD_SPEED_DESCRIPTION = "+1 movement to all armies.";
	public static final Power RED_1_GOD_SPEED = new GodSpeedPower(POWER_INDEXER++, RED_1_GOD_SPEED_NAME, (byte) 1,
			Color.RED, RED_1_GOD_SPEED_DESCRIPTION);

	public static final String RED_2_CARNAGE_NAME = "Carnage";
	public static final String RED_2_CARNAGE_DESCRIPTION = "+1 damage to all armies.";
	public static final Power RED_2_CARNAGE = new CarnagePower(POWER_INDEXER++, RED_2_CARNAGE_NAME, (byte) 2, Color.RED,
			RED_2_CARNAGE_DESCRIPTION);

	public static final String RED_2_OPEN_GATE_NAME = "Open Gate";
	public static final String RED_2_OPEN_GATE_DESCRIPTION = "Ignore effect of walls.";
	public static final Power RED_2_OPEN_GATE = new Power(POWER_INDEXER++, RED_2_OPEN_GATE_NAME, (byte) 2, Color.RED,
			RED_2_OPEN_GATE_DESCRIPTION);

	public static final String RED_2_TELEPORT_NAME = "Teleport";
	public static final String RED_2_TELEPORT_DESCRIPTION = "Can teleport from obelisk.";
	public static final Power RED_2_TELEPORT = new Power(POWER_INDEXER++, RED_2_TELEPORT_NAME, (byte) 2, Color.RED,
			RED_2_TELEPORT_DESCRIPTION);

	public static final String RED_3_BLADES_OF_NEITH_NAME = "Blades of Neith";
	public static final String RED_3_BLADES_OF_NEITH_DESCRIPTION = "+1 strength to all armies.";
	public static final Power RED_3_BLADES_OF_NEITH = new BladesOfNeithPower(POWER_INDEXER++,
			RED_3_BLADES_OF_NEITH_NAME, (byte) 3, Color.RED, RED_3_BLADES_OF_NEITH_DESCRIPTION);

	public static final Power RED_3_VICTORY_POINT = new VictoryPointPower(POWER_INDEXER++, VICTORY_POINT_NAME, (byte) 3,
			Color.RED, VICTORY_POINT_DESCRIPTION);

	public static final String RED_4_INITIATIVE_NAME = "INITIATIVE";
	public static final String RED_4_INITIATIVE_DESCRIPTION = "Destroy 2 enemy units before battle.";
	public static final Power RED_4_INITIATIVE = new Power(POWER_INDEXER++, RED_4_INITIATIVE_NAME, (byte) 4, Color.RED,
			RED_4_INITIATIVE_DESCRIPTION);

	public static final String BLUE_1_RECRUITING_SCRIBE_NAME = "Recruiting Scribe";
	public static final String BLUE_1_RECRUITING_SCRIBE_DESCRIPTION = "2 free troops on recruit action.";
	public static final Power BLUE_1_RECRUITING_SCRIBE = new Power(POWER_INDEXER++, BLUE_1_RECRUITING_SCRIBE_NAME,
			(byte) 1, Color.BLUE, BLUE_1_RECRUITING_SCRIBE_DESCRIPTION);

	public static final String BLUE_1_DEFENSE_NAME = "Defense";
	public static final String BLUE_1_DEFENSE_DESCRIPTION = "+1 strength when defending.";
	public static final Power BLUE_1_DEFENSE_1 = new DefensePower(POWER_INDEXER++, BLUE_1_DEFENSE_NAME, (byte) 1,
			Color.BLUE, BLUE_1_DEFENSE_DESCRIPTION);
	public static final Power BLUE_1_DEFENSE_2 = new DefensePower(POWER_INDEXER++, BLUE_1_DEFENSE_NAME, (byte) 1,
			Color.BLUE, BLUE_1_DEFENSE_DESCRIPTION);

	public static final String BLUE_2_LEGION_NAME = "Legion";
	public static final String BLUE_2_LEGION_DESCRIPTION = "Maximum army size 7.";
	public static final Power BLUE_2_LEGION = new LegionPower(POWER_INDEXER++, BLUE_2_LEGION_NAME, (byte) 2, Color.BLUE,
			BLUE_2_LEGION_DESCRIPTION);

	public static final String BLUE_3_SHIELD_OF_NEITH_NAME = "Shield of Neith";
	public static final String BLUE_3_SHIELD_OF_NEITH_DESCRIPTION = "+1 protection in battle.";
	public static final Power BLUE_3_SHIELD_OF_NEITH = new ShieldOfNeithPower(POWER_INDEXER++,
			BLUE_3_SHIELD_OF_NEITH_NAME, (byte) 3, Color.BLUE, BLUE_3_SHIELD_OF_NEITH_DESCRIPTION);

	public static final String BLUE_3_DEFENSIVE_VICTORY_NAME = "Defensive Victory";
	public static final String BLUE_3_DEFENSIVE_VICTORY_DESCRIPTION = "+1 VP on defensive victory.";
	public static final Power BLUE_3_DEFENSIVE_VICTORY = new Power(POWER_INDEXER++, BLUE_3_DEFENSIVE_VICTORY_NAME,
			(byte) 3, Color.BLUE, BLUE_3_DEFENSIVE_VICTORY_DESCRIPTION);

	public static final Power BLUE_3_VICTORY_POINT = new VictoryPointPower(POWER_INDEXER++, VICTORY_POINT_NAME,
			(byte) 3, Color.BLUE, VICTORY_POINT_DESCRIPTION);
	
	

//	- Black : 2 : Dedication to battle : +2 pray when moving to a tile with an enemy army

	public static final String BLACK_1_ENFORCED_RECRUITMENT_NAME = "Enforced Recruitment";
	public static final String BLACK_1_ENFORCED_RECRUITMENT_DESCRIPTION = "Recruit on any existing army. ";
	public static final Power BLACK_1_ENFORCED_RECRUITMENT = new Power(POWER_INDEXER++, BLACK_1_ENFORCED_RECRUITMENT_NAME, (byte) 1, Color.BLACK,
			BLACK_1_ENFORCED_RECRUITMENT_DESCRIPTION);
	
	public static final String BLACK_2_HONOR_IN_BATTLE_NAME = "Honor in battle";
	public static final String BLACK_2_HONOR_IN_BATTLE_DESCRIPTION = "+1 power per reach troop destroyed by your opponent in battle.";
	public static final Power BLACK_2_HONOR_IN_BATTLE = new Power(POWER_INDEXER++, BLACK_2_HONOR_IN_BATTLE_NAME, (byte) 2, Color.BLACK,
			BLACK_2_HONOR_IN_BATTLE_DESCRIPTION);
	
	public static final String BLACK_2_DEDICATION_TO_BATTLE_NAME = "Dedication to battle";
	public static final String BLACK_2_DEDICATION_TO_BATTLE_DESCRIPTION = "+2 power when moving to a tile with an enemy army.";
	public static final Power BLACK_2_DEDICATION_TO_BATTLE = new Power(POWER_INDEXER++, BLACK_2_DEDICATION_TO_BATTLE_NAME, (byte) 2, Color.BLACK,
			BLACK_2_DEDICATION_TO_BATTLE_DESCRIPTION);

	public static final Power BLACK_3_VICTORY_POINT = new VictoryPointPower(POWER_INDEXER++, VICTORY_POINT_NAME,
			(byte) 3, Color.BLACK, VICTORY_POINT_DESCRIPTION);

	public static final String BLACK_3_DEADLY_TRAP_NAME = "Deadly Trap";
	public static final String BLACK_3_DEADLY_TRAP_DESCRIPTION = "Destroy 1 enemy troop when they move to a tile you occupy.";
	public static final Power BLACK_3_DEADLY_TRAP = new Power(POWER_INDEXER++, BLACK_3_DEADLY_TRAP_NAME, (byte) 3, Color.BLACK,
			BLACK_3_DEADLY_TRAP_DESCRIPTION);

	
	public static final String BLACK_4_BESTIAL_FURY_NAME = "Bestial Fury";
	public static final String BLACK_4_BESTIAL_FURY_DESCRIPTION = "+1 move, +1damage, +1strength.";
	public static final Power BLACK_4_BESTIAL_FURY = new BestialFuryPower(POWER_INDEXER++, BLACK_4_BESTIAL_FURY_NAME, (byte) 4, Color.BLACK,
			BLACK_4_BESTIAL_FURY_DESCRIPTION);

	public static final String BLACK_4_DIVINE_STRENGTH_NAME = "Divine Strength";
	public static final String BLACK_4_DIVINE_STRENGTH_DESCRIPTION = "+1 power every time you gain power points during day.";
	public static final Power BLACK_4_DIVINE_STRENGTH = new Power(POWER_INDEXER++, BLACK_4_DIVINE_STRENGTH_NAME, (byte) 4, Color.BLACK,
			BLACK_4_DIVINE_STRENGTH_DESCRIPTION);

	
//	- Black : 4 : Divine Strength : +1 prayer every time you gain prayer points during day

	
	public static void initializeGame(KemetGame game) {

		game.availablePowerList.add(WHITE_1_PRIEST_1);
		game.availablePowerList.add(WHITE_1_PRIEST_2);
		game.availablePowerList.add(WHITE_1_PRIESTESS_1);
		game.availablePowerList.add(WHITE_1_PRIESTESS_2);

		game.availablePowerList.add(WHITE_2_SLAVE);
		game.availablePowerList.add(WHITE_2_GREAT_PRIEST);
		game.availablePowerList.add(WHITE_2_CRUSADE);
		// TODO missing one

		game.availablePowerList.add(WHITE_3_HOLY_WAR);
		game.availablePowerList.add(WHITE_3_VICTORY_POINT);
		// TODO missing one
		// TODO missing one

		game.availablePowerList.add(WHITE_4_PRIEST_OF_RA);
		game.availablePowerList.add(WHITE_4_PRIEST_OF_AMON);
		// TODO missing one
		// TODO missing one

		game.availablePowerList.add(RED_1_CHARGE_1);
		game.availablePowerList.add(RED_1_CHARGE_2);
		game.availablePowerList.add(RED_1_STARGATE);
		game.availablePowerList.add(RED_1_GOD_SPEED);

		game.availablePowerList.add(RED_2_CARNAGE);
		game.availablePowerList.add(RED_2_OPEN_GATE);
		game.availablePowerList.add(RED_2_TELEPORT);
		// TODO missing one

		game.availablePowerList.add(RED_3_BLADES_OF_NEITH);
		game.availablePowerList.add(RED_3_VICTORY_POINT);
		// TODO missing one
		// TODO missing one

		game.availablePowerList.add(RED_4_INITIATIVE);
		// TODO missing one
		// TODO missing one
		// TODO missing one

		game.availablePowerList.add(BLUE_1_RECRUITING_SCRIBE);
		game.availablePowerList.add(BLUE_1_DEFENSE_1);
		game.availablePowerList.add(BLUE_1_DEFENSE_2);
		// TODO missing one

		game.availablePowerList.add(BLUE_2_LEGION);
		// TODO missing one
		// TODO missing one
		// TODO missing one

		game.availablePowerList.add(BLUE_3_SHIELD_OF_NEITH);
		game.availablePowerList.add(BLUE_3_DEFENSIVE_VICTORY);
		game.availablePowerList.add(BLUE_3_VICTORY_POINT);
		// TODO missing one

		// TODO missing one
		// TODO missing one
		// TODO missing one
		// TODO missing one

		
		game.availablePowerList.add(BLACK_1_ENFORCED_RECRUITMENT);
		// TODO missing one
		// TODO missing one
		// TODO missing one
		
		game.availablePowerList.add(BLACK_2_HONOR_IN_BATTLE);
		game.availablePowerList.add(BLACK_2_DEDICATION_TO_BATTLE);
		// TODO missing one
		// TODO missing one
		
		game.availablePowerList.add(BLACK_3_VICTORY_POINT);
		game.availablePowerList.add(BLACK_3_DEADLY_TRAP);
		// TODO missing one
		// TODO missing one
		
		
		game.availablePowerList.add(BLACK_4_BESTIAL_FURY);
		game.availablePowerList.add(BLACK_4_DIVINE_STRENGTH);
		// TODO missing one
		// TODO missing one
		
		
		for( int i = 0; i< game.availablePowerList.size();++i) {
			Power power = game.availablePowerList.get(i);
			if (power.index != i ) {
				throw new IllegalStateException(power + " is not at index " + i);
			}
		}

	}

}
