package kemet.model.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import kemet.data.TwoPlayerGame;
import kemet.model.Army;
import kemet.model.BattleCard;
import kemet.model.Color;
import kemet.model.PowerList;
import kemet.model.Tile;
import kemet.model.action.choice.ChoiceInventory;

public class PowerTileTest extends TwoPlayerGameTest {

	@Test
	public void test_WHITE_1_PRIEST_1() {
		buyPowerTile(PowerList.RED_1_CHARGE_1);

		assertEquals(9, bluePlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_1_PRIEST_1);
		assertEquals(8, bluePlayer.getPrayerPoints());

		prayRowThree();

		prayRowThree();
		assertEquals(11, bluePlayer.getPrayerPoints());
	}

	@Test
	public void test_WHITE_1_PRIEST_2() {
		buyPowerTile(PowerList.RED_1_CHARGE_1);

		assertEquals(9, bluePlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_1_PRIEST_2);
		assertEquals(8, bluePlayer.getPrayerPoints());

		prayRowThree();

		prayRowThree();
		assertEquals(11, bluePlayer.getPrayerPoints());
	}

	@Test
	public void test_WHITE_1_PRIESTESS_1() {
		buyPowerTile(PowerList.RED_1_CHARGE_1);

		assertEquals(9, bluePlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_1_PRIESTESS_1);
		assertEquals(8, bluePlayer.getPrayerPoints());

		prayRowThree();

		// 1 cost instead of 2
		buyPowerTile(PowerList.BLUE_2_LEGION);
		assertEquals(7, bluePlayer.getPrayerPoints());
	}

	@Test
	public void test_WHITE_1_PRIESTESS_2() {
		buyPowerTile(PowerList.RED_1_CHARGE_1);

		assertEquals(9, bluePlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_1_PRIESTESS_2);
		assertEquals(8, bluePlayer.getPrayerPoints());

		prayRowThree();

		// 1 cost instead of 2
		buyPowerTile(PowerList.BLUE_2_LEGION);
		assertEquals(7, bluePlayer.getPrayerPoints());
	}

	@Test
	public void test_WHITE_2_SLAVE() {
		buyPowerTile(PowerList.RED_1_CHARGE_1);

		assertEquals(9, bluePlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_2_SLAVE);
		assertEquals(7, bluePlayer.getPrayerPoints());

		prayRowThree();

		upgradePyramid(3, Color.BLACK, bluePlayer.cityTiles.get(2));

		assertEquals(4, bluePlayer.getPrayerPoints());
	}

	@Test
	public void test_WHITE_2_GREAT_PRIEST() {
		buyPowerTile(PowerList.RED_1_CHARGE_1);
		assertEquals(9, bluePlayer.getPrayerPoints());
		assertEquals(2, bluePlayer.getNightPrayerPoints());
		buyPowerTile(PowerList.WHITE_2_GREAT_PRIEST);
		assertEquals(7, bluePlayer.getPrayerPoints());
		assertEquals(4, bluePlayer.getNightPrayerPoints());

		prayRowThree();
		upgradePyramid(3, Color.BLACK, bluePlayer.cityTiles.get(2));
		assertEquals(1, bluePlayer.getPrayerPoints());

		moveRowTwoZeroArmy();
		moveRowTwoZeroArmy();

		moveRowOneZeroArmy();
		moveRowOneZeroArmy();

		startRecruit();
		endRecruit();
		startRecruit();
		endRecruit();

		game.getNextPlayerChoicePick();

		// night + temple + remaining
		assertEquals(1 + 4 + 5, bluePlayer.getPrayerPoints());

	}

	@Test
	public void test_WHITE_2_CRUSADE() {
		buyPowerTile(PowerList.RED_1_CHARGE_1);
		assertEquals(9, bluePlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_2_CRUSADE);
		assertEquals(7, bluePlayer.getPrayerPoints());

		prayRowThree();
		moveRowTwoArmy(bluePlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE), 5);

		// moved by teleport
		assertEquals(5, bluePlayer.getPrayerPoints());

		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);
		// one damage applied

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);

		assertEquals(7, bluePlayer.getPrayerPoints());

	}

	@Test
	public void test_WHITE_3_HOLY_WAR_lost() {
		buyPowerTile(PowerList.RED_1_CHARGE_1);
		assertEquals(9, bluePlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_3_HOLY_WAR);
		assertEquals(6, bluePlayer.getPrayerPoints());

		prayRowThree();
		moveRowTwoArmy(bluePlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE), 5);

		// moved by teleport
		assertEquals(4, bluePlayer.getPrayerPoints());
		assertEquals(3, bluePlayer.victoryPoints);
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);
		// one damage applied

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);

		assertEquals(4, bluePlayer.getPrayerPoints());
		assertEquals(3, bluePlayer.victoryPoints);
	}

	@Test
	public void test_WHITE_3_HOLY_WAR_won() {
		buyPowerTile(PowerList.RED_1_CHARGE_1);
		assertEquals(9, bluePlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_3_HOLY_WAR);
		assertEquals(6, bluePlayer.getPrayerPoints());

		prayRowThree();
		moveRowTwoArmy(bluePlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE), 5);

		// moved by teleport
		assertEquals(4, bluePlayer.getPrayerPoints());
		assertEquals(3, bluePlayer.victoryPoints);
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.PHALANX_DEFENSE_CARD,
				BattleCard.CAVALRY_BLITZ_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);

		assertEquals(8, bluePlayer.getPrayerPoints());
		assertEquals(5, bluePlayer.victoryPoints);
	}

	@Test
	public void test_WHITE_3_VICTORY_POINT() {
		buyPowerTile(PowerList.RED_1_CHARGE_1);
		assertEquals(9, bluePlayer.getPrayerPoints());
		assertEquals(3, bluePlayer.victoryPoints);
		buyPowerTile(PowerList.WHITE_3_VICTORY_POINT);
		assertEquals(6, bluePlayer.getPrayerPoints());
		assertEquals(4, bluePlayer.victoryPoints);
	}

	
	@Test
	public void test_WHITE_3_HAND_OF_GOD() {
		buyPowerTile(PowerList.RED_1_CHARGE_1);
		assertEquals(9, bluePlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_3_HAND_OF_GOD);
		assertEquals(6, bluePlayer.getPrayerPoints());
		
		// burn 4 actions
		prayRowThree();
		prayRowThree();
		
		prayRowTwo();
		prayRowTwo();
		
		startRecruit();
		endRecruit();
		startRecruit();
		endRecruit();
		
		moveRowOneZeroArmy();
		moveRowOneZeroArmy();
		
		// night actions : upgrade pyramid from level 0 to 1 for free
		
		game.getNextPlayerChoicePick();
		assertEquals(11, bluePlayer.getPrayerPoints());
		Tile blueDistrict3 = bluePlayer.cityTiles.get(2);
		assertEquals(0, blueDistrict3.getPyramidLevel());
		game.activateAction(game.getNextPlayer(), blueDistrict3.getPickChoiceIndex(game.getNextPlayer()));
		assertEquals(11, bluePlayer.getPrayerPoints());
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PICK_COLOR_CHOICE + Color.BLACK.ordinal());
		assertEquals(11, bluePlayer.getPrayerPoints());
		
		// battle for initiative
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);
		
		assertEquals(11, bluePlayer.getPrayerPoints());
		assertEquals(1, blueDistrict3.getPyramidLevel());
		assertEquals(Color.BLACK, blueDistrict3.pyramidColor);
		
	}
	
	@Test
	public void test_WHITE_4_PRIEST_OF_RA() {
		buyPowerTile(PowerList.RED_1_CHARGE_1);
		assertEquals(9, bluePlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_RA);
		assertEquals(5, bluePlayer.getPrayerPoints());

		moveRowOneZeroArmy();
		buyPowerTile(PowerList.BLUE_1_DEFENSE_1);
		// tile was free
		assertEquals(5, bluePlayer.getPrayerPoints());

		moveRowTwoZeroArmy();
		startRecruit();
		recruitArmy(bluePlayer.cityTiles.get(0), 2);
		endRecruit();

		assertEquals(4, bluePlayer.getPrayerPoints());

		prayRowThree();
		moveRowTwoArmy(bluePlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE), 5);

		// moved by teleport, only 1 cost
		assertEquals(3, bluePlayer.getPrayerPoints());
		assertEquals(3, bluePlayer.victoryPoints);
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.PHALANX_DEFENSE_CARD,
				BattleCard.CAVALRY_BLITZ_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);

		assertEquals(5, bluePlayer.victoryPoints);

	}

	@Test
	public void test_WHITE_4_PRIEST_OF_AMON() {
		buyPowerTile(PowerList.RED_1_CHARGE_1);
		assertEquals(9, bluePlayer.getPrayerPoints());
		assertEquals(2, bluePlayer.getNightPrayerPoints());
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);
		assertEquals(5, bluePlayer.getPrayerPoints());
		assertEquals(7, bluePlayer.getNightPrayerPoints());

		prayRowThree();
		prayRowThree();
		assertEquals(7, bluePlayer.getPrayerPoints());

		moveRowTwoZeroArmy();
		upgradePyramid(3, Color.BLACK, bluePlayer.cityTiles.get(2));
		assertEquals(1, bluePlayer.getPrayerPoints());

		moveRowOneZeroArmy();
		buyPowerTile(PowerList.BLACK_1_ENFORCED_RECRUITMENT);
		assertEquals(0, bluePlayer.getPrayerPoints());

		startRecruit();
		endRecruit();

		startRecruit();
		endRecruit();

		game.getNextPlayerChoicePick();

		// night + temple + remaining
		assertEquals(0 + 7 + 4, bluePlayer.getPrayerPoints());
	}

	@Test
	public void test_RED_1_CHARGE_1_lost() {

		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 3);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		assertEquals(3, redPlayer.victoryPoints);

	}

	@Test
	public void test_RED_1_CHARGE_1_won() {
		assertEquals(7, redPlayer.getPrayerPoints());
		assertEquals(0, redPlayer.attackBonus);
		buyPowerTile(PowerList.RED_1_CHARGE_1);
		assertEquals(1, redPlayer.attackBonus);
		assertEquals(6, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 3);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		assertEquals(5, redPlayer.victoryPoints);

	}

	@Test
	public void test_RED_1_CHARGE_2_won() {
		assertEquals(7, redPlayer.getPrayerPoints());
		assertEquals(0, redPlayer.attackBonus);
		buyPowerTile(PowerList.RED_1_CHARGE_2);
		assertEquals(1, redPlayer.attackBonus);
		assertEquals(6, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 3);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		assertEquals(5, redPlayer.victoryPoints);
	}

	@Test
	public void test_RED_1_STARGATE() {
		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_1_STARGATE);
		assertEquals(6, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 5);
		assertEquals(5, redPlayer.getPrayerPoints());

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		assertEquals(5, redPlayer.victoryPoints);
	}

	@Test
	public void test_RED_1_GOD_SPEED() {
		assertEquals(1, redPlayer.moveCapacity);
		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_1_GOD_SPEED);
		assertEquals(6, redPlayer.getPrayerPoints());
		assertEquals(2, redPlayer.moveCapacity);
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		moveRowTwoArmy(redPlayer.cityTiles.get(1), redPlayer.cityFront, 5);
		moveNextTile(game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE_ENTRANCE), 5);
	}

	@Test
	public void test_RED_2_CARNAGE() {
		assertEquals(7, redPlayer.getPrayerPoints());
		assertEquals(0, redPlayer.damageBonus);
		buyPowerTile(PowerList.RED_2_CARNAGE);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(1, redPlayer.damageBonus);
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		Tile tileByName = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);

		assertEquals(3, tileByName.getArmy().armySize);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), tileByName, 2);

		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);

		// did 2 damage instead of 1
		assertEquals(1, tileByName.getArmy().armySize);
	}

	@Test
	public void test_RED_2_OPEN_GATE_missing() {
		assertEquals(1, redPlayer.moveCapacity);
		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_1_GOD_SPEED);
		assertEquals(6, redPlayer.getPrayerPoints());
		assertEquals(2, redPlayer.moveCapacity);
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.SMALL_TEMPLE), 5);
		moveNextTile(bluePlayer.cityFront, 5);
		try {
			moveNextTile(bluePlayer.cityTiles.get(2), 5);
			fail("Should not be able to go through walls without open gate in 1 move");
		} catch (Exception ex) {
			// expected
		}

	}

	@Test
	public void test_RED_2_OPEN_GATE() {
		// give RED_1_GOD_SPEED for free
		game.movePowerToPlayer(redPlayer, PowerList.RED_1_GOD_SPEED);

		assertEquals(2, redPlayer.moveCapacity);
		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_2_OPEN_GATE);
		assertEquals(5, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.SMALL_TEMPLE), 5);
		moveNextTile(bluePlayer.cityFront, 5);
		moveNextTile(bluePlayer.cityTiles.get(2), 5);
	}

	@Test
	public void test_RED_2_TELEPORT() {

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_2_TELEPORT);
		assertEquals(5, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.SMALL_TEMPLE), 5);
		assertEquals(3, redPlayer.getPrayerPoints());
		moveNextTile(game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 5);
		assertEquals(1, redPlayer.getPrayerPoints());
	}
	
	@Test
	public void test_RED_2_OFFENSIVE_STRATEGY() {

		assertEquals(7, redPlayer.getPrayerPoints());
		
		assertEquals(4, redPlayer.availableBattleCards.size());
		assertFalse( redPlayer.availableBattleCards.contains(BattleCard.OFFENSIVE_STRATEGY_CARD));
		assertTrue( redPlayer.usedBattleCards.contains(BattleCard.FERVENT_PURGE_CARD));

		
		buyPowerTile(PowerList.RED_2_OFFENSIVE_STRATEGY);
		game.activateAction(game.getNextPlayer(), BattleCard.FERVENT_PURGE_CARD.getPickChoiceIndex());

		assertTrue( redPlayer.availableBattleCards.contains(BattleCard.OFFENSIVE_STRATEGY_CARD));
		assertFalse( redPlayer.availableBattleCards.contains(BattleCard.FERVENT_PURGE_CARD));
		assertFalse( redPlayer.usedBattleCards.contains(BattleCard.FERVENT_PURGE_CARD));
		assertEquals(8, redPlayer.availableBattleCards.size());

		
		assertEquals(5, redPlayer.getPrayerPoints());

		
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

	}

	@Test
	public void test_RED_2_TELEPORT_missing() {

		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.SMALL_TEMPLE), 5);
		assertEquals(5, redPlayer.getPrayerPoints());
		try {
			moveNextTile(game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 5);
			fail("Should not be able to move to island temple from small temple.");
		} catch (Exception ex) {

		}
	}

	@Test
	public void test_RED_3_BLADES_OF_NEITH_attack() {
		assertEquals(7, redPlayer.getPrayerPoints());
		assertEquals(0, redPlayer.strengthBonus);
		buyPowerTile(PowerList.RED_3_BLADES_OF_NEITH);
		assertEquals(1, redPlayer.strengthBonus);
		assertEquals(4, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 3);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		assertEquals(5, redPlayer.victoryPoints);
	}

	@Test
	public void test_RED_3_BLADES_OF_NEITH_defense() {
		assertEquals(7, redPlayer.getPrayerPoints());
		assertEquals(0, redPlayer.strengthBonus);
		buyPowerTile(PowerList.RED_3_BLADES_OF_NEITH);
		assertEquals(1, redPlayer.strengthBonus);
		assertEquals(4, redPlayer.getPrayerPoints());

		moveRowTwoArmy(bluePlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE), 5);

		assertEquals(3, bluePlayer.victoryPoints);
		assertEquals(3, redPlayer.victoryPoints);
		battlePick(BattleCard.DEFENSIVE_RETREAT_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.PHALANX_DEFENSE_CARD,
				BattleCard.DEFENSIVE_RETREAT_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		assertEquals(3, bluePlayer.victoryPoints);
		assertEquals(3, redPlayer.victoryPoints);
	}

	@Test
	public void test_RED_3_VICTORY_POINT() {
		assertEquals(7, redPlayer.getPrayerPoints());
		assertEquals(3, redPlayer.victoryPoints);
		buyPowerTile(PowerList.RED_3_VICTORY_POINT);
		assertEquals(4, redPlayer.getPrayerPoints());
		assertEquals(4, redPlayer.victoryPoints);
	}

	@Test
	public void test_RED_4_INITIATIVE_attack() {
		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_4_INITIATIVE);
		assertEquals(3, redPlayer.getPrayerPoints());

		assertEquals(9, bluePlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);
		assertEquals(5, bluePlayer.getPrayerPoints());

		// enemy army size 3, reduced to 1 after initiative. Send 2 army to win with
		// equal cards.
		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 2);

		assertEquals(3, redPlayer.victoryPoints);
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);

		prayRowThree();
		assertEquals(5, redPlayer.victoryPoints);
		assertEquals(7, bluePlayer.getPrayerPoints());

	}

	@Test
	public void test_BLUE_1_RECRUITING_SCRIBE() {
		buyPowerTile(PowerList.RED_1_CHARGE_1);

		assertEquals(9, bluePlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLUE_1_RECRUITING_SCRIBE);
		assertEquals(8, bluePlayer.getPrayerPoints());

		prayRowThree();

		// recruit only costs 2
		startRecruit();
		recruitArmy(bluePlayer.cityTiles.get(0), 4);
		assertEquals(6, bluePlayer.getPrayerPoints());
	}

	@Test
	public void test_BLUE_1_DEFENSE_1_won() {
		assertEquals(7, redPlayer.getPrayerPoints());
		assertEquals(0, redPlayer.attackBonus);
		buyPowerTile(PowerList.RED_1_CHARGE_1);
		assertEquals(1, redPlayer.attackBonus);
		assertEquals(6, redPlayer.getPrayerPoints());

		assertEquals(9, bluePlayer.getPrayerPoints());
		assertEquals(0, bluePlayer.defenseBonus);
		buyPowerTile(PowerList.BLUE_1_DEFENSE_1);
		assertEquals(8, bluePlayer.getPrayerPoints());
		assertEquals(1, bluePlayer.defenseBonus);

		// should be equal army of 3v3, charge vs defense
		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 3);

		assertEquals(3, redPlayer.victoryPoints);
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		assertEquals(3, redPlayer.victoryPoints);

	}

	@Test
	public void test_BLUE_1_DEFENSE_2_won() {
		assertEquals(7, redPlayer.getPrayerPoints());
		assertEquals(0, redPlayer.attackBonus);
		buyPowerTile(PowerList.RED_1_CHARGE_1);
		assertEquals(1, redPlayer.attackBonus);
		assertEquals(6, redPlayer.getPrayerPoints());

		assertEquals(9, bluePlayer.getPrayerPoints());
		assertEquals(0, bluePlayer.defenseBonus);
		buyPowerTile(PowerList.BLUE_1_DEFENSE_2);
		assertEquals(8, bluePlayer.getPrayerPoints());
		assertEquals(1, bluePlayer.defenseBonus);

		// should be equal army of 3v3, charge vs defense
		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 3);

		assertEquals(3, redPlayer.victoryPoints);
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		assertEquals(3, redPlayer.victoryPoints);

	}

	@Test
	public void test_BLUE_2_LEGION() {
		assertEquals(7, redPlayer.getPrayerPoints());
		assertEquals(0, redPlayer.attackBonus);
		buyPowerTile(PowerList.RED_1_CHARGE_1);
		assertEquals(1, redPlayer.attackBonus);
		assertEquals(6, redPlayer.getPrayerPoints());

		assertEquals(9, bluePlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLUE_2_LEGION);
		assertEquals(7, bluePlayer.getPrayerPoints());

		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 5);

		assertEquals(3, redPlayer.victoryPoints);
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		assertEquals(5, redPlayer.victoryPoints);
		assertEquals(9, bluePlayer.getPrayerPoints());

		// recruit up to 7
		startRecruit();
		recruitArmy(bluePlayer.cityTiles.get(0), 7);
		assertEquals(2, bluePlayer.getPrayerPoints());

	}
	
	@Test
	public void test_BLUE_2_DEFENSIVE_STRATEGY() {

		buyPowerTile(PowerList.RED_1_CHARGE_1);
		
		assertEquals(9, bluePlayer.getPrayerPoints());
		
		assertEquals(4, bluePlayer.availableBattleCards.size());
		assertFalse( bluePlayer.availableBattleCards.contains(BattleCard.DEFENSIVE_STRATEGY_CARD));
		assertTrue( bluePlayer.usedBattleCards.contains(BattleCard.FERVENT_PURGE_CARD));

		buyPowerTile(PowerList.BLUE_2_DEFENSIVE_STRATEGY);
		game.activateAction(game.getNextPlayer(), BattleCard.FERVENT_PURGE_CARD.getPickChoiceIndex());

		assertTrue( bluePlayer.availableBattleCards.contains(BattleCard.DEFENSIVE_STRATEGY_CARD));
		assertFalse( bluePlayer.availableBattleCards.contains(BattleCard.FERVENT_PURGE_CARD));
		assertFalse( bluePlayer.usedBattleCards.contains(BattleCard.FERVENT_PURGE_CARD));
		assertEquals(8, bluePlayer.availableBattleCards.size());

		
		assertEquals(7, bluePlayer.getPrayerPoints());

	}

	@Test
	public void test_BLUE_3_SHIELD_OF_NEITH() {
		assertEquals(7, redPlayer.getPrayerPoints());
		assertEquals(0, redPlayer.attackBonus);
		buyPowerTile(PowerList.RED_1_CHARGE_1);
		assertEquals(1, redPlayer.attackBonus);
		assertEquals(6, redPlayer.getPrayerPoints());

		assertEquals(9, bluePlayer.getPrayerPoints());
		assertEquals(0, bluePlayer.shieldBonus);
		buyPowerTile(PowerList.BLUE_3_SHIELD_OF_NEITH);
		assertEquals(6, bluePlayer.getPrayerPoints());
		assertEquals(1, bluePlayer.shieldBonus);

		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 5);

		assertEquals(3, redPlayer.victoryPoints);
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		assertEquals(5, redPlayer.victoryPoints);
		// gain 1 extra prayer point due to shield
		assertEquals(9, bluePlayer.getPrayerPoints());
	}

	@Test
	public void test_BLUE_3_DEFENSIVE_VICTORY() {
		assertEquals(7, redPlayer.getPrayerPoints());
		assertEquals(0, redPlayer.attackBonus);
		buyPowerTile(PowerList.RED_1_CHARGE_1);
		assertEquals(1, redPlayer.attackBonus);
		assertEquals(6, redPlayer.getPrayerPoints());

		assertEquals(9, bluePlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLUE_3_DEFENSIVE_VICTORY);
		assertEquals(6, bluePlayer.getPrayerPoints());

		// 2+1 vs 3
		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 2);

		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(3, bluePlayer.victoryPoints);
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		assertEquals(3, redPlayer.victoryPoints);
		// gain 1 VP due to defensive victory
		assertEquals(4, bluePlayer.victoryPoints);
	}

	@Test
	public void test_BLUE_4_REINFORCEMENTS() {
		buyPowerTile(PowerList.RED_1_CHARGE_1);

		assertEquals(9, bluePlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLUE_4_REINFORCEMENTS);
		assertEquals(5, bluePlayer.getPrayerPoints());

		prayRowThree();
		prayRowThree();
		assertEquals(7, bluePlayer.getPrayerPoints());

		startRecruit();
		endRecruit();
		startRecruit();
		endRecruit();

		moveRowOneZeroArmy();
		moveRowOneZeroArmy();

		prayRowTwo();
		prayRowTwo();
		assertEquals(9, bluePlayer.getPrayerPoints());

		game.getNextPlayerChoicePick();

		// night prayer bonus
		assertEquals(11, bluePlayer.getPrayerPoints());

		// no cost recruit of 4 at night time
		recruitArmy(bluePlayer.cityTiles.get(0), 4);
		assertEquals(11, bluePlayer.getPrayerPoints());

		// battle for initiative
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);
	}
	
	@Test
	public void test_BLUE_4_REINFORCEMENTS_dual_tile() {
		buyPowerTile(PowerList.RED_1_CHARGE_1);

		assertEquals(9, bluePlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLUE_4_REINFORCEMENTS);
		assertEquals(5, bluePlayer.getPrayerPoints());

		prayRowThree();
		prayRowThree();
		assertEquals(7, bluePlayer.getPrayerPoints());

		startRecruit();
		endRecruit();
		startRecruit();
		endRecruit();

		moveRowOneZeroArmy();
		moveRowOneZeroArmy();

		prayRowTwo();
		prayRowTwo();
		assertEquals(9, bluePlayer.getPrayerPoints());

		game.getNextPlayerChoicePick();

		// night prayer bonus
		assertEquals(11, bluePlayer.getPrayerPoints());

		// no cost recruit of 4 at night time
		recruitArmy(bluePlayer.cityTiles.get(0), 2);
		recruitArmy(bluePlayer.cityTiles.get(2), 2);
		assertEquals(11, bluePlayer.getPrayerPoints());

		// battle for initiative
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);
	}

	@Test
	public void test_BLUE_3_VICTORY_POINT() {
		buyPowerTile(PowerList.RED_1_CHARGE_1);
		assertEquals(9, bluePlayer.getPrayerPoints());
		assertEquals(3, bluePlayer.victoryPoints);
		buyPowerTile(PowerList.BLUE_3_VICTORY_POINT);
		assertEquals(6, bluePlayer.getPrayerPoints());
		assertEquals(4, bluePlayer.victoryPoints);
	}

	@Test
	public void test_BLACK_1_MERCENARIES_1() {
		
		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_1_MERCENARIES_1);
		assertEquals(6, redPlayer.getPrayerPoints());
		
		recruitArmy(redPlayer.cityTiles.get(0), 3);
		prayRowThree();
		prayRowThree();
	}
	
	@Test
	public void test_BLACK_1_MERCENARIES_2() {
		
		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_1_MERCENARIES_2);
		assertEquals(6, redPlayer.getPrayerPoints());
		
		recruitArmy(redPlayer.cityTiles.get(0), 3);
		prayRowThree();
		prayRowThree();
	}
	
	@Test
	public void test_BLACK_1_ENFORCED_RECRUITMENT() {
		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_1_ENFORCED_RECRUITMENT);
		assertEquals(6, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLUE_3_VICTORY_POINT);

		moveRowTwoArmy(redPlayer.cityTiles.get(1), redPlayer.cityFront, 2);
		prayRowThree();

		// recruit where it doesn't normally work.
		startRecruit();
		assertEquals(2, redPlayer.cityFront.getArmy().armySize);
		recruitArmy(redPlayer.cityFront, 2);
		buyPowerTile(PowerList.WHITE_1_PRIEST_1);
		assertEquals(4, redPlayer.cityFront.getArmy().armySize);

	}

	@Test
	public void test_BLACK_2_HONOR_IN_BATTLE() {
		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_2_HONOR_IN_BATTLE);
		assertEquals(5, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 3);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(3, redPlayer.getPrayerPoints());
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		assertEquals(3, redPlayer.victoryPoints);
		// 1 extra prayer point from destroyed troop.
		assertEquals(6, redPlayer.getPrayerPoints());
	}

	@Test
	public void test_BLACK_2_DEDICATION_TO_BATTLE() {
		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_2_DEDICATION_TO_BATTLE);
		assertEquals(5, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 4);

		assertEquals(3, redPlayer.victoryPoints);
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		assertEquals(5, redPlayer.getPrayerPoints());

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		assertEquals(5, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
	}

	@Test
	public void test_BLACK_3_VICTORY_POINT() {
		assertEquals(7, redPlayer.getPrayerPoints());
		assertEquals(3, redPlayer.victoryPoints);
		buyPowerTile(PowerList.BLACK_3_VICTORY_POINT);
		assertEquals(4, redPlayer.getPrayerPoints());
		assertEquals(4, redPlayer.victoryPoints);
	}

	@Test
	public void test_BLACK_3_DEADLY_TRAP() {
		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_3_DEADLY_TRAP);
		assertEquals(4, redPlayer.getPrayerPoints());

		assertEquals(9, bluePlayer.getPrayerPoints());
		Tile blueCity1 = bluePlayer.cityTiles.get(1);
		Army army = blueCity1.getArmy();
		moveRowTwoArmy(blueCity1, game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE), 5);
		assertEquals(4, army.armySize);

		assertEquals(7, bluePlayer.getPrayerPoints());

		assertEquals(3, bluePlayer.victoryPoints);
		assertEquals(3, redPlayer.victoryPoints);
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.RECALL_CHOICE);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);

		startRecruit();
		endRecruit();

		assertEquals(10, bluePlayer.getPrayerPoints());
		assertEquals(3, bluePlayer.victoryPoints);
		assertEquals(3, redPlayer.victoryPoints);
	}

	@Test
	public void test_BLACK_4_BESTIAL_FURY() {
		assertEquals(7, redPlayer.getPrayerPoints());

		assertEquals(0, redPlayer.damageBonus);
		assertEquals(0, redPlayer.strengthBonus);
		assertEquals(1, redPlayer.moveCapacity);

		buyPowerTile(PowerList.BLACK_4_BESTIAL_FURY);
		assertEquals(3, redPlayer.getPrayerPoints());

		assertEquals(1, redPlayer.damageBonus);
		assertEquals(1, redPlayer.strengthBonus);
		assertEquals(2, redPlayer.moveCapacity);
	}

	@Test
	public void test_BLACK_4_DIVINE_STRENGTH_pray() {
		assertEquals(7, redPlayer.getPrayerPoints());

		buyPowerTile(PowerList.BLACK_4_DIVINE_STRENGTH);
		assertEquals(3, redPlayer.getPrayerPoints());

		prayRowThree();
		prayRowThree();
		assertEquals(6, redPlayer.getPrayerPoints());
	}

	@Test
	public void test_BLACK_4_DIVINE_STRENGTH_and_BLACK_2_DEDICATION_TO_BATTLE() {
		game.movePowerToPlayer(redPlayer, PowerList.BLACK_4_DIVINE_STRENGTH);
		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_2_DEDICATION_TO_BATTLE);
		assertEquals(5, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 4);

		assertEquals(3, redPlayer.victoryPoints);
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		assertEquals(6, redPlayer.getPrayerPoints());

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		assertEquals(5, redPlayer.victoryPoints);
		assertEquals(6, redPlayer.getPrayerPoints());
	}

//	game.availablePowerList.add(BLACK_4_DIVINE_STRENGTH);

}
