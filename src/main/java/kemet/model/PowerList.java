package kemet.model;

import kemet.model.power.ActOfGodPower;
import kemet.model.power.BattleCardPower;
import kemet.model.power.BeastPower;
import kemet.model.power.BestialFuryPower;
import kemet.model.power.BladesOfNeithPower;
import kemet.model.power.CarnagePower;
import kemet.model.power.ChargePower;
import kemet.model.power.DefensePower;
import kemet.model.power.GodSpeedPower;
import kemet.model.power.GoldTokenPower;
import kemet.model.power.LegionPower;
import kemet.model.power.MercenariesPower;
import kemet.model.power.ShieldOfNeithPower;
import kemet.model.power.VictoryPointBeastPower;
import kemet.model.power.VictoryPointPower;

public class PowerList {

	public static int POWER_INDEXER = 0;

	public static final String ACT_OF_GOD_NAME = "Act of God";
	public static final String ACT_OF_GOD_DESCRIPTION = "Add a silver token to play at the same time as a normal token.";

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

	public static final String WHITE_2_DIVINE_BOON_NAME = "Divine Boon";
	public static final String WHITE_2_DIVINE_BOON_DESCRIPTION = "+1 DI card at night.";
	public static final Power WHITE_2_DIVINE_BOON = new Power(POWER_INDEXER++, WHITE_2_DIVINE_BOON_NAME, (byte) 2, Color.WHITE,
			WHITE_2_DIVINE_BOON_DESCRIPTION);
	

	public static final String WHITE_3_HOLY_WAR_NAME = "Holy War";
	public static final String WHITE_3_HOLY_WAR_DESCRIPTION = "+4 power per battle won.";
	public static final Power WHITE_3_HOLY_WAR = new Power(POWER_INDEXER++, WHITE_3_HOLY_WAR_NAME, (byte) 3,
			Color.WHITE, WHITE_3_HOLY_WAR_DESCRIPTION);

	public static final String VICTORY_POINT_NAME = "Victory Point";
	public static final String VICTORY_POINT_DESCRIPTION = "+1 Permanent Victory Point.";
	public static final Power WHITE_3_VICTORY_POINT = new VictoryPointPower(POWER_INDEXER++, VICTORY_POINT_NAME,
			(byte) 3, Color.WHITE, VICTORY_POINT_DESCRIPTION);

	public static final String WHITE_3_HAND_OF_GOD_NAME = "Hand of God";
	public static final String WHITE_3_HAND_OF_GOD_DESCRIPTION = "+1 pyramid upgrade per night.";
	public static final Power WHITE_3_HAND_OF_GOD = new Power(POWER_INDEXER++, WHITE_3_HAND_OF_GOD_NAME, (byte) 3,
			Color.WHITE, WHITE_3_HAND_OF_GOD_DESCRIPTION);
	
	public static final String WHITE_3_VISION_NAME = "Vision";
	public static final String WHITE_3_VISION_DESCRIPTION = "Draw 5 DI card at night, keep one.";
	public static final Power WHITE_3_VISION = new Power(POWER_INDEXER++, WHITE_3_VISION_NAME, (byte) 3, Color.WHITE,
			WHITE_3_VISION_DESCRIPTION);

	public static final String WHITE_4_PRIEST_OF_RA_NAME = "PRIEST_OF_RA";
	public static final String WHITE_4_PRIEST_OF_RA_DESCRIPTION = "-1 cost on everything.";
	public static final Power WHITE_4_PRIEST_OF_RA = new Power(POWER_INDEXER++, WHITE_4_PRIEST_OF_RA_NAME, (byte) 4,
			Color.WHITE, WHITE_4_PRIEST_OF_RA_DESCRIPTION);

	public static final String WHITE_4_PRIEST_OF_AMON_NAME = "PRIEST_OF_AMON";
	public static final String WHITE_4_PRIEST_OF_AMON_DESCRIPTION = "+5 power at night.";
	public static final Power WHITE_4_PRIEST_OF_AMON = new Power(POWER_INDEXER++, WHITE_4_PRIEST_OF_AMON_NAME, (byte) 4,
			Color.WHITE, WHITE_4_PRIEST_OF_AMON_DESCRIPTION);

	public static final Power WHITE_4_ACT_OF_GOD = new ActOfGodPower(POWER_INDEXER++, ACT_OF_GOD_NAME, (byte) 4,
			Color.WHITE, ACT_OF_GOD_DESCRIPTION);

	public static final Power WHITE_4_MUMMY = new BeastPower(POWER_INDEXER++, (byte) 4, Color.WHITE,
			BeastList.WHITE_4_MUMMY);

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

	public static final String RED_2_OFFENSIVE_STRATEGY_NAME = "Offensive Strategy";
	public static final String RED_2_OFFENSIVE_STRATEGY_DESCRIPTION = "Recuperate battle cards, replace one with 3 attack 3 damage.";
	public static final Power RED_2_OFFENSIVE_STRATEGY = new BattleCardPower(POWER_INDEXER++,
			RED_2_OFFENSIVE_STRATEGY_NAME, (byte) 2, Color.RED, RED_2_OFFENSIVE_STRATEGY_DESCRIPTION,
			BattleCard.OFFENSIVE_STRATEGY_CARD);

	public static final String RED_3_BLADES_OF_NEITH_NAME = "Blades of Neith";
	public static final String RED_3_BLADES_OF_NEITH_DESCRIPTION = "+1 strength to all armies.";
	public static final Power RED_3_BLADES_OF_NEITH = new BladesOfNeithPower(POWER_INDEXER++,
			RED_3_BLADES_OF_NEITH_NAME, (byte) 3, Color.RED, RED_3_BLADES_OF_NEITH_DESCRIPTION);

	public static final Power RED_3_VICTORY_POINT = new VictoryPointPower(POWER_INDEXER++, VICTORY_POINT_NAME, (byte) 3,
			Color.RED, VICTORY_POINT_DESCRIPTION);

	public static final Power RED_3_ROYAL_SCARAB = new BeastPower(POWER_INDEXER++, (byte) 3, Color.RED,
			BeastList.RED_3_ROYAL_SCARAB);

	public static final String RED_3_DIVINE_WOUND_NAME = "Divine Wound";
	public static final String RED_3_DIVINE_WOUND_DESCRIPTION = "+1 strength per DI card spent after battle.";
	public static final Power RED_3_DIVINE_WOUND = new Power(POWER_INDEXER++, RED_3_DIVINE_WOUND_NAME, (byte) 3, Color.RED,
			RED_3_DIVINE_WOUND_DESCRIPTION);

	
	public static final Power RED_4_GIANT_SCORPION = new BeastPower(POWER_INDEXER++, (byte) 4, Color.RED,
			BeastList.RED_4_GIANT_SCORPION);

	public static final Power RED_4_PHOENIX = new BeastPower(POWER_INDEXER++, (byte) 4, Color.RED,
			BeastList.RED_4_PHOENIX);

	public static final String RED_4_INITIATIVE_NAME = "INITIATIVE";
	public static final String RED_4_INITIATIVE_DESCRIPTION = "Destroy 2 enemy units before battle.";
	public static final Power RED_4_INITIATIVE = new Power(POWER_INDEXER++, RED_4_INITIATIVE_NAME, (byte) 4, Color.RED,
			RED_4_INITIATIVE_DESCRIPTION);

	public static final Power RED_4_ACT_OF_GOD = new ActOfGodPower(POWER_INDEXER++, ACT_OF_GOD_NAME, (byte) 4,
			Color.RED, ACT_OF_GOD_DESCRIPTION);

	public static final String BLUE_1_RECRUITING_SCRIBE_NAME = "Recruiting Scribe";
	public static final String BLUE_1_RECRUITING_SCRIBE_DESCRIPTION = "2 free troops on recruit action.";

	public static final Power BLUE_1_RECRUITING_SCRIBE_1 = new Power(POWER_INDEXER++, BLUE_1_RECRUITING_SCRIBE_NAME,
			(byte) 1, Color.BLUE, BLUE_1_RECRUITING_SCRIBE_DESCRIPTION);
	public static final Power BLUE_1_RECRUITING_SCRIBE_2 = new Power(POWER_INDEXER++, BLUE_1_RECRUITING_SCRIBE_NAME,
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

	public static final String BLUE_2_DEFENSIVE_STRATEGY_NAME = "Defensive Strategy";
	public static final String BLUE_2_DEFENSIVE_STRATEGY_DESCRIPTION = "Recuperate battle cards, replace one with 3 attack 3 shield.";
	public static final Power BLUE_2_DEFENSIVE_STRATEGY = new BattleCardPower(POWER_INDEXER++,
			BLUE_2_DEFENSIVE_STRATEGY_NAME, (byte) 2, Color.BLUE, BLUE_2_DEFENSIVE_STRATEGY_DESCRIPTION,
			BattleCard.DEFENSIVE_STRATEGY_CARD);

	public static final Power BLUE_2_ANCESTRAL_ELEPHANT = new BeastPower(POWER_INDEXER++, (byte) 2, Color.BLUE,
			BeastList.BLUE_2_ANCESTRAL_ELEPHANT);

	public static final Power BLUE_2_DEEP_DESERT_SNAKE = new BeastPower(POWER_INDEXER++, (byte) 2, Color.BLUE,
			BeastList.BLUE_2_DEEP_DESERT_SNAKE);

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

	public static final String BLUE_3_PRESCIENCE_NAME = "Prescience";
	public static final String BLUE_3_PRESCIENCE_DESCRIPTION = "Opponent plays battle card before you.";
	public static final Power BLUE_3_PRESCIENCE = new Power(POWER_INDEXER++, BLUE_3_PRESCIENCE_NAME, (byte) 3,
			Color.BLUE, BLUE_3_PRESCIENCE_DESCRIPTION);

	public static final String BLUE_4_REINFORCEMENTS_NAME = "Reinforcements";
	public static final String BLUE_4_REINFORCEMENTS_DESCRIPTION = "4 free troop recruitment at night.";
	public static final Power BLUE_4_REINFORCEMENTS = new Power(POWER_INDEXER++, BLUE_4_REINFORCEMENTS_NAME, (byte) 4,
			Color.BLUE, BLUE_4_REINFORCEMENTS_DESCRIPTION);

	public static final Power BLUE_4_ACT_OF_GOD = new ActOfGodPower(POWER_INDEXER++, ACT_OF_GOD_NAME, (byte) 4,
			Color.BLUE, ACT_OF_GOD_DESCRIPTION);

	public static final String BLUE_4_DIVINE_WILL_NAME = "Divine Will";
	public static final String BLUE_4_DIVINE_WILL_DESCRIPTION = "Gold Token : Recruit, Move.";
	public static final Power BLUE_4_DIVINE_WILL = new GoldTokenPower(POWER_INDEXER++, BLUE_4_DIVINE_WILL_NAME,
			(byte) 4, Color.BLUE, BLUE_4_DIVINE_WILL_DESCRIPTION);

	public static final Power BLUE_4_SPHINX = new VictoryPointBeastPower(POWER_INDEXER++, (byte) 4, Color.BLUE,
			BeastList.BLUE_4_SPHINX);

	public static final String BLACK_1_ENFORCED_RECRUITMENT_NAME = "Enforced Recruitment";
	public static final String BLACK_1_ENFORCED_RECRUITMENT_DESCRIPTION = "Recruit on any existing army. ";
	public static final Power BLACK_1_ENFORCED_RECRUITMENT = new Power(POWER_INDEXER++,
			BLACK_1_ENFORCED_RECRUITMENT_NAME, (byte) 1, Color.BLACK, BLACK_1_ENFORCED_RECRUITMENT_DESCRIPTION);

	public static final String BLACK_1_MERCENARIES_NAME = "Mercenaries";
	public static final String BLACK_1_MERCENARIES_DESCRIPTION = "Add 3 troops to your army. May recruit them immediately. ";
	public static final Power BLACK_1_MERCENARIES_1 = new MercenariesPower(POWER_INDEXER++, BLACK_1_MERCENARIES_NAME,
			(byte) 1, Color.BLACK, BLACK_1_MERCENARIES_DESCRIPTION);
	public static final Power BLACK_1_MERCENARIES_2 = new MercenariesPower(POWER_INDEXER++, BLACK_1_MERCENARIES_NAME,
			(byte) 1, Color.BLACK, BLACK_1_MERCENARIES_DESCRIPTION);

	public static final String BLACK_1_DARK_RITUAL_NAME = "Dark Ritual";
	public static final String BLACK_1_DARK_RITUAL_DESCRIPTION = "Gold token : Pray.";
	public static final Power BLACK_1_DARK_RITUAL = new GoldTokenPower(POWER_INDEXER++, BLACK_1_DARK_RITUAL_NAME,
			(byte) 1, Color.BLACK, BLACK_1_DARK_RITUAL_DESCRIPTION);

	public static final String BLACK_2_HONOR_IN_BATTLE_NAME = "Honor in battle";
	public static final String BLACK_2_HONOR_IN_BATTLE_DESCRIPTION = "+1 power per reach troop destroyed by your opponent in battle.";
	public static final Power BLACK_2_HONOR_IN_BATTLE = new Power(POWER_INDEXER++, BLACK_2_HONOR_IN_BATTLE_NAME,
			(byte) 2, Color.BLACK, BLACK_2_HONOR_IN_BATTLE_DESCRIPTION);

	public static final String BLACK_2_DEDICATION_TO_BATTLE_NAME = "Dedication to battle";
	public static final String BLACK_2_DEDICATION_TO_BATTLE_DESCRIPTION = "+2 power when moving to a tile with an enemy army.";
	public static final Power BLACK_2_DEDICATION_TO_BATTLE = new Power(POWER_INDEXER++,
			BLACK_2_DEDICATION_TO_BATTLE_NAME, (byte) 2, Color.BLACK, BLACK_2_DEDICATION_TO_BATTLE_DESCRIPTION);

	public static final String BLACK_2_TWIN_CEREMONY_NAME = "Twin Ceremony";
	public static final String BLACK_2_TWIN_CEREMONY_DESCRIPTION = "Gold Token : Reuse Buy action +2 cost on tile.";
	public static final Power BLACK_2_TWIN_CEREMONY = new GoldTokenPower(POWER_INDEXER++, BLACK_2_TWIN_CEREMONY_NAME,
			(byte) 2, Color.BLACK, BLACK_2_TWIN_CEREMONY_DESCRIPTION);

	public static final Power BLACK_2_KHNUM_SPHINX = new BeastPower(POWER_INDEXER++, (byte) 2, Color.BLACK,
			BeastList.BLACK_2_KHNUM_SPHINX);
	public static final Power BLACK_3_GRIFFIN_SPHINX = new BeastPower(POWER_INDEXER++, (byte) 3, Color.BLACK,
			BeastList.BLACK_3_GRIFFIN_SPHINX);

	public static final Power BLACK_3_VICTORY_POINT = new VictoryPointPower(POWER_INDEXER++, VICTORY_POINT_NAME,
			(byte) 3, Color.BLACK, VICTORY_POINT_DESCRIPTION);

	public static final String BLACK_3_DEADLY_TRAP_NAME = "Deadly Trap";
	public static final String BLACK_3_DEADLY_TRAP_DESCRIPTION = "Destroy 1 enemy troop when they move to a tile you occupy.";
	public static final Power BLACK_3_DEADLY_TRAP = new Power(POWER_INDEXER++, BLACK_3_DEADLY_TRAP_NAME, (byte) 3,
			Color.BLACK, BLACK_3_DEADLY_TRAP_DESCRIPTION);

	public static final String BLACK_3_FORCED_MARCH_NAME = "Forced March";
	public static final String BLACK_3_FORCED_MARCH_DESCRIPTION = " Gold Token : Move.";
	public static final Power BLACK_3_FORCED_MARCH = new GoldTokenPower(POWER_INDEXER++, BLACK_3_FORCED_MARCH_NAME,
			(byte) 3, Color.BLACK, BLACK_3_FORCED_MARCH_DESCRIPTION);

	public static final String BLACK_4_BESTIAL_FURY_NAME = "Bestial Fury";
	public static final String BLACK_4_BESTIAL_FURY_DESCRIPTION = "+1 move, +1damage, +1strength.";
	public static final Power BLACK_4_BESTIAL_FURY = new BestialFuryPower(POWER_INDEXER++, BLACK_4_BESTIAL_FURY_NAME,
			(byte) 4, Color.BLACK, BLACK_4_BESTIAL_FURY_DESCRIPTION);

	public static final String BLACK_4_DIVINE_STRENGTH_NAME = "Divine Strength";
	public static final String BLACK_4_DIVINE_STRENGTH_DESCRIPTION = "+1 power every time you gain power points during day.";
	public static final Power BLACK_4_DIVINE_STRENGTH = new Power(POWER_INDEXER++, BLACK_4_DIVINE_STRENGTH_NAME,
			(byte) 4, Color.BLACK, BLACK_4_DIVINE_STRENGTH_DESCRIPTION);

	public static final Power BLACK_4_ACT_OF_GOD = new ActOfGodPower(POWER_INDEXER++, ACT_OF_GOD_NAME, (byte) 4,
			Color.BLACK, ACT_OF_GOD_DESCRIPTION);

	public static final Power BLACK_4_DEVOURER = new BeastPower(POWER_INDEXER++, (byte) 2, Color.BLACK,
			BeastList.BLACK_4_DEVOURER);

	public static void initializeGame(KemetGame game) {

		game.availablePowerList.add(WHITE_1_PRIEST_1);
		game.availablePowerList.add(WHITE_1_PRIEST_2);
		game.availablePowerList.add(WHITE_1_PRIESTESS_1);
		game.availablePowerList.add(WHITE_1_PRIESTESS_2);

		game.availablePowerList.add(WHITE_2_SLAVE);
		game.availablePowerList.add(WHITE_2_GREAT_PRIEST);
		game.availablePowerList.add(WHITE_2_CRUSADE);
		game.availablePowerList.add(WHITE_2_DIVINE_BOON);

		game.availablePowerList.add(WHITE_3_HOLY_WAR);
		game.availablePowerList.add(WHITE_3_VICTORY_POINT);
		game.availablePowerList.add(WHITE_3_HAND_OF_GOD);
		game.availablePowerList.add(WHITE_3_VISION);

		game.availablePowerList.add(WHITE_4_PRIEST_OF_RA);
		game.availablePowerList.add(WHITE_4_PRIEST_OF_AMON);
		game.availablePowerList.add(WHITE_4_ACT_OF_GOD);
		game.availablePowerList.add(WHITE_4_MUMMY);

		game.availablePowerList.add(RED_1_CHARGE_1);
		game.availablePowerList.add(RED_1_CHARGE_2);
		game.availablePowerList.add(RED_1_STARGATE);
		game.availablePowerList.add(RED_1_GOD_SPEED);

		game.availablePowerList.add(RED_2_CARNAGE);
		game.availablePowerList.add(RED_2_OPEN_GATE);
		game.availablePowerList.add(RED_2_TELEPORT);
		game.availablePowerList.add(RED_2_OFFENSIVE_STRATEGY);

		game.availablePowerList.add(RED_3_BLADES_OF_NEITH);
		game.availablePowerList.add(RED_3_VICTORY_POINT);
		game.availablePowerList.add(RED_3_ROYAL_SCARAB);
		game.availablePowerList.add(RED_3_DIVINE_WOUND);

		game.availablePowerList.add(RED_4_GIANT_SCORPION);
		game.availablePowerList.add(RED_4_PHOENIX);
		game.availablePowerList.add(RED_4_INITIATIVE);
		game.availablePowerList.add(RED_4_ACT_OF_GOD);

		game.availablePowerList.add(BLUE_1_RECRUITING_SCRIBE_1);
		game.availablePowerList.add(BLUE_1_RECRUITING_SCRIBE_2);
		game.availablePowerList.add(BLUE_1_DEFENSE_1);
		game.availablePowerList.add(BLUE_1_DEFENSE_2);

		game.availablePowerList.add(BLUE_2_LEGION);
		game.availablePowerList.add(BLUE_2_DEFENSIVE_STRATEGY);
		game.availablePowerList.add(BLUE_2_ANCESTRAL_ELEPHANT);
		game.availablePowerList.add(BLUE_2_DEEP_DESERT_SNAKE);

		game.availablePowerList.add(BLUE_3_SHIELD_OF_NEITH);
		game.availablePowerList.add(BLUE_3_DEFENSIVE_VICTORY);
		game.availablePowerList.add(BLUE_3_VICTORY_POINT);
		game.availablePowerList.add(BLUE_3_PRESCIENCE);

		game.availablePowerList.add(BLUE_4_REINFORCEMENTS);
		game.availablePowerList.add(BLUE_4_ACT_OF_GOD);
		game.availablePowerList.add(BLUE_4_DIVINE_WILL);
		game.availablePowerList.add(BLUE_4_SPHINX);

		game.availablePowerList.add(BLACK_1_ENFORCED_RECRUITMENT);
		game.availablePowerList.add(BLACK_1_MERCENARIES_1);
		game.availablePowerList.add(BLACK_1_MERCENARIES_2);
		game.availablePowerList.add(BLACK_1_DARK_RITUAL);

		game.availablePowerList.add(BLACK_2_HONOR_IN_BATTLE);
		game.availablePowerList.add(BLACK_2_DEDICATION_TO_BATTLE);
		game.availablePowerList.add(BLACK_2_TWIN_CEREMONY);
		game.availablePowerList.add(BLACK_2_KHNUM_SPHINX);

		game.availablePowerList.add(BLACK_3_GRIFFIN_SPHINX);
		game.availablePowerList.add(BLACK_3_VICTORY_POINT);
		game.availablePowerList.add(BLACK_3_DEADLY_TRAP);
		game.availablePowerList.add(BLACK_3_FORCED_MARCH);

		game.availablePowerList.add(BLACK_4_BESTIAL_FURY);
		game.availablePowerList.add(BLACK_4_DIVINE_STRENGTH);
		game.availablePowerList.add(BLACK_4_ACT_OF_GOD);
		game.availablePowerList.add(BLACK_4_DEVOURER);

		for (int i = 0; i < game.availablePowerList.size(); ++i) {
			Power power = game.availablePowerList.get(i);
			if (power.index != i) {
				throw new IllegalStateException(power + " is not at index " + i);
			}
		}

	}

}
