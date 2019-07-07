package kemet.model.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import kemet.data.TwoPlayerGame;
import kemet.model.BattleCard;
import kemet.model.BeastList;
import kemet.model.PowerList;
import kemet.model.Tile;
import kemet.model.action.choice.ChoiceInventory;

public class BeastTest extends TwoPlayerGameTest {

//	@Test
//	public void test_cleanGame() {
//		tpg = new TwoPlayerGame();
//		tpg.createAIPlayer("red");
//		tpg.createAIPlayer("blue");
//		tpg.createTiles();
//
//		game = tpg.game;
//		// game.setPrintActivations(false);
//
//		redPlayer = game.playerByInitiativeList.get(0);
//		bluePlayer = game.playerByInitiativeList.get(1);
//
//		// initialization
//		pickPyramidLevel(2);
//		pickPyramidColor(Color.RED);
//		pickPyramidColor(Color.BLACK);
//		pickPyramidLevel(2);
//		pickPyramidColor(Color.BLUE);
//		pickPyramidColor(Color.WHITE);
//		recruitArmySize(5);
//		recruitArmySize(5);
//		recruitArmySize(5);
//		recruitArmySize(5);
//
//		// turn one
//		upgradePyramid(4, redPlayer.cityTiles.get(0));
//		upgradePyramid(4, bluePlayer.cityTiles.get(0));
//		prayRowThree();
//		prayRowThree();
//		prayRowTwo();
//		prayRowTwo();
//
//		startRowOneMove();
//		moveFirstTile(redPlayer.cityTiles.get(0), game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE), 5);
//		endMove();
//
//		startRowOneMove();
//		moveFirstTile(bluePlayer.cityTiles.get(0), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 5);
//
//		moveRowTwoZeroArmy();
//		moveRowTwoZeroArmy();
//
//		// turn two
//		battlePick(BattleCard.FERVENT_PURGE_CARD, BattleCard.MIXED_TACTICS_CARD, BattleCard.FERVENT_PURGE_CARD,
//				BattleCard.MIXED_TACTICS_CARD);
//		pickPlayerOrder(1);
//		prayRowThree();
//		prayRowThree();
//		prayRowTwo();
//		prayRowTwo();
//		upgradePyramid(4, redPlayer.cityTiles.get(1));
//		upgradePyramid(4, bluePlayer.cityTiles.get(1));
//		moveRowOneZeroArmy();
//		moveRowOneZeroArmy();
//		moveRowTwoZeroArmy();
//		moveRowTwoZeroArmy();
//
//		// turn three
//		battlePick(BattleCard.CHARIOT_RAID_CARD, BattleCard.SHIELD_PUSH_CARD, BattleCard.CHARIOT_RAID_CARD,
//				BattleCard.SHIELD_PUSH_CARD);
//		pickPlayerOrder(1);
//		prayRowThree();
//		prayRowThree();
//		prayRowTwo();
//		prayRowTwo();
//		upgradePyramid(4, redPlayer.cityTiles.get(1));
//		upgradePyramid(4, bluePlayer.cityTiles.get(1));
//		moveRowOneZeroArmy();
//		moveRowOneZeroArmy();
//		moveRowTwoZeroArmy();
//		moveRowTwoZeroArmy();
//
//	}

	@Test
	public void test_beastRecruitAfterRecall() {

		Tile startTile = redPlayer.cityTiles.get(1);
		Tile nextTile = redPlayer.cityTiles.get(2);

		startRecruit();
		recruitArmy(nextTile, 2);
		// endRecruit();

		prayRowTwo();

		buyPowerTile(PowerList.RED_3_ROYAL_SCARAB);
		recruitBeastToTile(startTile);
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		moveRowTwoArmy(startTile, game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 4, true);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 4 army + 2 beast + 1 card = 7
		// defense strength : 3 army + 0 beast + 4 card = 7
		battlePick(BattleCard.DEFENSIVE_RETREAT_CARD, BattleCard.CAVALRY_BLITZ_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);

		recruitBeastToTile(nextTile);

		assertSame(nextTile.getArmy().beast, BeastList.RED_3_ROYAL_SCARAB);

	}

	@Test
	public void test_cantMoveBeastToArmyThatHasBeast() {

		Tile startTile = redPlayer.cityTiles.get(1);
		Tile nextTile = redPlayer.cityTiles.get(2);

		startRecruit();
		recruitArmy(nextTile, 2);
		// endRecruit();

		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		buyPowerTile(PowerList.RED_3_ROYAL_SCARAB);
		recruitBeastToTile(startTile);

		prayRowThree();

		buyPowerTile(PowerList.BLACK_2_KHNUM_SPHINX);
		recruitBeastToTile(nextTile);

		prayRowTwo();

		startRowTwoMove();
		try {
			moveFirstTile(startTile, nextTile, 1, true);
			fail("Should not be able to move the beast to a tile with another beast");
		} catch (Exception ex) {

		}
	}

	@Test
	public void test_moveBeastToOtherArmy() {

		Tile startTile = redPlayer.cityTiles.get(1);
		Tile nextTile = redPlayer.cityTiles.get(2);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_3_ROYAL_SCARAB);
		assertEquals(4, redPlayer.getPrayerPoints());
		recruitBeastToTile(startTile);
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		startRecruit();
		recruitArmy(nextTile, 2);

		// endRecruit();

		prayRowThree();

		startRowTwoMove();

		assertNull(nextTile.getArmy().beast);

		moveFirstTile(startTile, nextTile, 0, true);

		assertSame(nextTile.getArmy().beast, BeastList.RED_3_ROYAL_SCARAB);

		moveNextTile(redPlayer.cityFront, 2, true);
		moveNextTile(nextTile, 2, true);

		prayRowTwo();

	}

	@Test
	public void test_recruitBeastDuringRecruitPhase() {

		Tile nextTile = redPlayer.cityTiles.get(2);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_3_ROYAL_SCARAB);
		assertEquals(4, redPlayer.getPrayerPoints());
		skipRecruitBeast();
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		startRecruit();
		recruitArmy(nextTile, 2);
		recruitBeast(BeastList.RED_3_ROYAL_SCARAB);
		// endRecruit();

		assertSame(nextTile.getArmy().beast, BeastList.RED_3_ROYAL_SCARAB);
		assertEquals(2, nextTile.getArmy().armySize);

		prayRowTwo();

	}

	@Test
	public void test_beastLeaveBehindComesBack() {

		Tile startTile = redPlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_3_ROYAL_SCARAB);
		assertEquals(4, redPlayer.getPrayerPoints());
		recruitBeastToTile(startTile);
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		Tile nextTile = redPlayer.cityFront;
		moveRowTwoArmy(startTile, nextTile, 5, false);

		assertTrue(redPlayer.availableBeasts.contains(BeastList.RED_3_ROYAL_SCARAB));
		assertNull(nextTile.getArmy().beast);
		assertNull(startTile.getArmy());

	}

	@Test
	public void test_armyDestroyedByDeadlyTrapPromptsBeastRecruit() {

		Tile startTile = redPlayer.cityTiles.get(1);

		Tile blue1Tile = bluePlayer.cityTiles.get(1);
		Tile blue0Tile = bluePlayer.cityTiles.get(0);
		Tile middleObelisk = game.getTileByName(TwoPlayerGame.MIDDLE_OBELISK);

		// red
		buyPowerTile(PowerList.BLACK_3_DEADLY_TRAP);

		// blue
		startRecruit();
		recruitArmy(blue0Tile, 1);
		endRecruit();

		// red
		startRowOneMove();
		moveFirstTile(startTile, middleObelisk, 5, false);
		endMove();

		// blue
		buyPowerTile(PowerList.BLUE_2_DEEP_DESERT_SNAKE);
		recruitBeastToTile(blue0Tile);

		// red
		prayRowTwo();

		// blue
		startRowOneMove();
		// move destroys army due to deadly trap
		moveFirstTile(blue0Tile, middleObelisk, 1, true);
		recruitBeastToTile(blue1Tile);

		// red
		prayRowThree();

		assertSame(blue1Tile.getArmy().beast, BeastList.BLUE_2_DEEP_DESERT_SNAKE);

	}

	@Test
	public void test_armyDestroyedByInitiativePromptsBeastRecruit() {

		Tile startTile = redPlayer.cityTiles.get(1);
		Tile cityFront = redPlayer.cityFront;

		Tile blue1Tile = bluePlayer.cityTiles.get(1);
		Tile blue0Tile = bluePlayer.cityTiles.get(0);
		Tile middleObelisk = game.getTileByName(TwoPlayerGame.MIDDLE_OBELISK);

		// red
		buyPowerTile(PowerList.RED_4_INITIATIVE);

		// blue
		startRecruit();
		recruitArmy(blue0Tile, 2);
		endRecruit();

		// red
		prayRowThree();

		// blue
		buyPowerTile(PowerList.BLUE_2_DEEP_DESERT_SNAKE);
		recruitBeastToTile(blue0Tile);

		// red
		prayRowTwo();

		// blue
		startRowOneMove();
		moveFirstTile(blue0Tile, middleObelisk, 2, true);
		endMove();

		// red
		startRowOneMove();
		// kills army with initiative
		moveFirstTile(startTile, middleObelisk, 5, false);
		// prompt blue to recruit beast again
		recruitBeastToTile(blue1Tile);
		// move next tile
		moveNextTile(cityFront, 5, false);

		prayRowTwo();

		assertSame(blue1Tile.getArmy().beast, BeastList.BLUE_2_DEEP_DESERT_SNAKE);

	}

	@Test
	public void test_beastLeaveBehindPromptsRecruit() {

		Tile startTile = redPlayer.cityTiles.get(1);
		Tile nextTile = redPlayer.cityTiles.get(2);
		Tile cityFront = redPlayer.cityFront;

		startRecruit();
		recruitArmy(nextTile, 2);

		prayRowThree();

		buyPowerTile(PowerList.RED_3_ROYAL_SCARAB);
		recruitBeastToTile(startTile);

		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		moveRowTwoArmy(startTile, cityFront, 5, false);
		recruitBeastToTile(nextTile);
		moveNextTile(startTile, 5, false);
		moveNextTile(cityFront, 5, false);

		prayRowTwo();

		assertSame(nextTile.getArmy().beast, BeastList.RED_3_ROYAL_SCARAB);
		assertNull(cityFront.getArmy().beast);
		assertNull(startTile.getArmy());

	}

	@Test
	public void test_RED_3_ROYAL_SCARAB_win() {

		Tile startTile = redPlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_3_ROYAL_SCARAB);
		assertEquals(4, redPlayer.getPrayerPoints());
		recruitBeastToTile(startTile);
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		moveRowTwoArmy(startTile, game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 5, true);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 5 army + 2 beast + 1 card = 8
		// defense strength : 3 army + 0 beast + 4 card = 7
		battlePick(BattleCard.DEFENSIVE_RETREAT_CARD, BattleCard.CAVALRY_BLITZ_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		assertEquals(5, redPlayer.victoryPoints);
		assertEquals(7, bluePlayer.getPrayerPoints());

	}

	@Test
	public void test_RED_3_ROYAL_SCARAB_move() {
		Tile startTile = redPlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_3_ROYAL_SCARAB);
		assertEquals(4, redPlayer.getPrayerPoints());
		recruitBeastToTile(startTile);
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		// +2 move
		Tile nextTile = redPlayer.cityFront;
		moveRowTwoArmy(startTile, nextTile, 5, true);
		moveNextTile(startTile, 5, true);
		moveNextTile(nextTile, 5, true);

		prayRowThree();
	}

	@Test
	public void test_RED_4_GIANT_SCORPION_win() {
		Tile startTile = redPlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_4_GIANT_SCORPION);
		recruitBeastToTile(startTile);
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);
		assertEquals(5, bluePlayer.getPrayerPoints());

		Tile tileByName = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);

		assertEquals(3, tileByName.getArmy().armySize);

		moveRowTwoArmy(startTile, tileByName, 5, true);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 5 army + 2 beast + 1 card = 8
		// defense strength : 3 army + 0 beast + 4 card = 7
		battlePick(BattleCard.DEFENSIVE_RETREAT_CARD, BattleCard.CAVALRY_BLITZ_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		moveRowTwoZeroArmy();
		// 1+2 damage
		assertEquals(5, bluePlayer.getPrayerPoints());

		assertEquals(5, redPlayer.victoryPoints);

	}

	@Test
	public void test_RED_4_GIANT_SCORPION_move() {
		Tile startTile = redPlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_4_GIANT_SCORPION);
		assertEquals(3, redPlayer.getPrayerPoints());
		recruitBeastToTile(startTile);
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		// +1 move
		Tile nextTile = redPlayer.cityFront;
		moveRowTwoArmy(startTile, nextTile, 5, true);
		moveNextTile(startTile, 5, true);

		prayRowThree();
	}

	@Test
	public void test_RED_4_GIANT_SCORPION_moveNotBypassWalls() {
		Tile startTile = redPlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_4_GIANT_SCORPION);
		assertEquals(3, redPlayer.getPrayerPoints());
		recruitBeastToTile(startTile);
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		// +1 move
		moveRowTwoArmy(startTile, game.getTileByName(TwoPlayerGame.MIDDLE_OBELISK), 5, true);
		moveNextTile(bluePlayer.cityFront, 5, true);
		// try to bypass wall and fail
		try {
			moveNextTile(bluePlayer.cityTiles.get(2), 5, true);
			fail("Managed to bypass wall with scorpion");
		} catch (Exception ex) {
			// expected
		}
		endMove();

		prayRowThree();
	}

	@Test
	public void test_RED_4_PHOENIX_moveBypassWalls() {
		Tile startTile = redPlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_4_PHOENIX);
		assertEquals(3, redPlayer.getPrayerPoints());
		recruitBeastToTile(startTile);
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);

		// +1 move
		moveRowTwoArmy(startTile, game.getTileByName(TwoPlayerGame.MIDDLE_OBELISK), 5, true);
		moveNextTile(bluePlayer.cityFront, 5, true);
		// bypass wall
		moveNextTile(bluePlayer.cityTiles.get(2), 5, true);

		prayRowThree();
	}

	@Test
	public void test_RED_4_PHOENIX_win() {
		Tile startTile = redPlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_4_PHOENIX);
		recruitBeastToTile(startTile);
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);
		assertEquals(5, bluePlayer.getPrayerPoints());

		Tile tileByName = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);

		assertEquals(3, tileByName.getArmy().armySize);

		moveRowTwoArmy(startTile, tileByName, 5, true);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 5 army + 2 beast + 1 card = 8
		// defense strength : 3 army + 0 beast + 4 card = 7
		battlePick(BattleCard.DEFENSIVE_RETREAT_CARD, BattleCard.CAVALRY_BLITZ_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		moveRowTwoZeroArmy();
		// 1 damage
		assertEquals(7, bluePlayer.getPrayerPoints());

		assertEquals(5, redPlayer.victoryPoints);

	}

	@Test
	public void test_BLACK_4_DEVOURER_win_sub2damage() {
		Tile startTile = redPlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_4_DEVOURER);
		recruitBeastToTile(startTile);
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);
		assertEquals(5, bluePlayer.getPrayerPoints());

		Tile tileByName = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);

		assertEquals(3, tileByName.getArmy().armySize);

		moveRowTwoArmy(startTile, tileByName, 5, true);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 5 army + 2 beast + 1 card = 8
		// defense strength : 3 army + 0 beast + 4 card = 7
		battlePick(BattleCard.DEFENSIVE_RETREAT_CARD, BattleCard.CAVALRY_BLITZ_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		moveRowTwoZeroArmy();
		// 1 damage
		assertEquals(7, bluePlayer.getPrayerPoints());

		assertEquals(5, redPlayer.victoryPoints);
	}
	
	@Test
	public void test_BLACK_4_DEVOURER_immuneToDeadlyTrap() {
		
		redPlayer.recuperateAllBattleCards();
		bluePlayer.recuperateAllBattleCards();

		game.movePowerToPlayer(bluePlayer, PowerList.BLACK_3_DEADLY_TRAP);
		
		Tile startTile = redPlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_4_DEVOURER);
		recruitBeastToTile(startTile);
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);
		assertEquals(5, bluePlayer.getPrayerPoints());

		Tile tileByName = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);

		assertEquals(3, tileByName.getArmy().armySize);

		moveRowTwoArmy(startTile, tileByName, 5, true);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 5 army + 2 beast + 1 card = 8
		// defense strength : 3 army + 0 beast + 4 card = 7
		battlePick(BattleCard.DEFENSIVE_RETREAT_CARD, BattleCard.CAVALRY_BLITZ_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		moveRowTwoZeroArmy();
		// 1 damage
		assertEquals(7, bluePlayer.getPrayerPoints());

		assertEquals(5, redPlayer.victoryPoints);
	}

	@Test
	public void test_BLACK_4_DEVOURER_win_with2damage() {

		redPlayer.recuperateAllBattleCards();
		bluePlayer.recuperateAllBattleCards();

		Tile startTile = redPlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_4_DEVOURER);
		recruitBeastToTile(startTile);
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);
		assertEquals(5, bluePlayer.getPrayerPoints());

		Tile tileByName = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);

		assertEquals(3, tileByName.getArmy().armySize);

		moveRowTwoArmy(startTile, tileByName, 4, true);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 4 army + 2 beast + 2 card = 8
		// defense strength : 3 army + 0 beast + 4 card = 7
		battlePick(BattleCard.MIXED_TACTICS_CARD, BattleCard.CAVALRY_BLITZ_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		moveRowTwoZeroArmy();
		// 2 damage
		assertEquals(6, bluePlayer.getPrayerPoints());

		assertEquals(6, redPlayer.victoryPoints);
	}

	@Test
	public void test_BLACK_4_DEVOURER_lose_with2damage() {

		redPlayer.recuperateAllBattleCards();
		bluePlayer.recuperateAllBattleCards();

		Tile startTile = redPlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_4_DEVOURER);
		recruitBeastToTile(startTile);
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);
		assertEquals(5, bluePlayer.getPrayerPoints());

		Tile tileByName = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);

		assertEquals(3, tileByName.getArmy().armySize);

		moveRowTwoArmy(startTile, tileByName, 3, true);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 3 army + 2 beast + 2 card = 7
		// defense strength : 3 army + 0 beast + 4 card = 7
		battlePick(BattleCard.MIXED_TACTICS_CARD, BattleCard.CAVALRY_BLITZ_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		recruitBeastToTile(redPlayer.cityTiles.get(1));
		moveRowTwoZeroArmy();
		// 1 damage
		// assertEquals(7, bluePlayer.getPrayerPoints());

		assertEquals(3, redPlayer.victoryPoints);
	}

	@Test
	public void test_BLACK_4_DEVOURER_win_sub2damage_defensive() {
		game.movePowerToPlayer(redPlayer, PowerList.BLUE_3_DEFENSIVE_VICTORY);
		game.movePowerToPlayer(redPlayer, PowerList.BLACK_1_ENFORCED_RECRUITMENT);

		Tile startTile = bluePlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_4_DEVOURER);
		Tile tileByName = game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE);
		recruitBeastToTile(tileByName);
		
		moveRowTwoArmy(startTile, tileByName, 5, false);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 5 army + 0 beast + 4 card = 9
		// defense strength : 5 army + 2 beast + 4 card = 11
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.RECALL_CHOICE);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		
		prayRowThree();
		
		assertEquals(4, redPlayer.victoryPoints);
	}

	@Test
	public void test_BLACK_4_DEVOURER_win_with2damage_defensive() {
		redPlayer.recuperateAllBattleCards();
		bluePlayer.recuperateAllBattleCards();
		
		game.movePowerToPlayer(redPlayer, PowerList.BLUE_3_DEFENSIVE_VICTORY);
		game.movePowerToPlayer(redPlayer, PowerList.BLACK_1_ENFORCED_RECRUITMENT);

		Tile startTile = bluePlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_4_DEVOURER);
		Tile tileByName = game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE);
		recruitBeastToTile(tileByName);
		
		moveRowTwoArmy(startTile, tileByName, 5, false);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 5 army + 0 beast + 4 card = 9
		// defense strength : 5 army + 2 beast + 2 card = 9
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.MIXED_TACTICS_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.RECALL_CHOICE);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		
		prayRowThree();
		
		assertEquals(5, redPlayer.victoryPoints);
	}

	@Test
	public void test_BLACK_4_DEVOURER_lose_with2damage_defensive() {
		redPlayer.recuperateAllBattleCards();
		bluePlayer.recuperateAllBattleCards();
		
		game.movePowerToPlayer(redPlayer, PowerList.BLUE_3_DEFENSIVE_VICTORY);
		game.movePowerToPlayer(redPlayer, PowerList.BLACK_1_ENFORCED_RECRUITMENT);

		Tile startTile = bluePlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_4_DEVOURER);
		Tile tileByName = game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE);
		recruitBeastToTile(tileByName);
		
		moveRowTwoArmy(startTile, tileByName, 5, false);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 5 army + 0 beast + 5 card = 10
		// defense strength : 5 army + 2 beast + 2 card = 9
		battlePick(BattleCard.SACRIFICIAL_CHARGE_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.MIXED_TACTICS_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.RECALL_CHOICE);
		skipRecruitBeast();
		
		prayRowThree();
		
		// 5 army size - 2 damage from mixed tactic - 2 damage from sacrificial charge
		assertEquals(1, tileByName.getArmy().armySize);
		
		// -1 points : lost 1 temple
		assertEquals(2, redPlayer.victoryPoints);
	}

	
	@Test
	public void test_BLACK_4_DEVOURER_immuneToInitiative() {
		redPlayer.recuperateAllBattleCards();
		bluePlayer.recuperateAllBattleCards();
		
		game.movePowerToPlayer(bluePlayer, PowerList.RED_4_INITIATIVE);
		game.movePowerToPlayer(redPlayer, PowerList.BLACK_1_ENFORCED_RECRUITMENT);

		Tile startTile = bluePlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_4_DEVOURER);
		Tile tileByName = game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE);
		recruitBeastToTile(tileByName);
		
		moveRowTwoArmy(startTile, tileByName, 5, false);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 5 army + 0 beast + 4 card = 9
		// defense strength : 5 army + 2 beast + 2 card = 9
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.MIXED_TACTICS_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.RECALL_CHOICE);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		
		prayRowThree();
		
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, tileByName.getArmy().armySize);
	}
	
	

	@Test
	public void test_BLACK_2_KHNUM_SPHINX_movementCost() {
		redPlayer.recuperateAllBattleCards();
		bluePlayer.recuperateAllBattleCards();
		
		game.movePowerToPlayer(redPlayer, PowerList.BLACK_1_ENFORCED_RECRUITMENT);

		Tile startTile = bluePlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_2_KHNUM_SPHINX);
		Tile tileByName = game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE);
		recruitBeastToTile(tileByName);
		assertEquals(5, redPlayer.getPrayerPoints());
		
		assertEquals(9, bluePlayer.getPrayerPoints());
		
		moveRowTwoArmy(startTile, tileByName, 5, false);

		// 2 for teleport + 2 for sphinx
		assertEquals(5, bluePlayer.getPrayerPoints());
		
		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 5 army + 0 beast + 4 card = 9
		// defense strength : 5 army + 1 beast + 3 card = 9
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.FERVENT_PURGE_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.RECALL_CHOICE);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		
		prayRowThree();
		
		assertEquals(3, redPlayer.victoryPoints);
	}

	@Test
	public void test_BLACK_3_GRIFFIN_SPHINX_multiTeleport() {
		Tile startTile = redPlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_3_GRIFFIN_SPHINX);
		recruitBeastToTile(startTile);
		buyPowerTile(PowerList.WHITE_4_PRIEST_OF_AMON);
		assertEquals(5, bluePlayer.getPrayerPoints());

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		Tile middleObeliskTile = game.getTileByName(TwoPlayerGame.MIDDLE_OBELISK);

		assertEquals(3, islandTile.getArmy().armySize);

		moveRowTwoArmy(startTile, middleObeliskTile, 5, true);
		moveNextTile(islandTile, 5, true);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 5 army + 2 beast + 1 card = 8
		// defense strength : 3 army + 0 beast + 4 card = 7
		battlePick(BattleCard.DEFENSIVE_RETREAT_CARD, BattleCard.CAVALRY_BLITZ_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		moveRowTwoZeroArmy();
		// 1 damage
		assertEquals(7, bluePlayer.getPrayerPoints());

		assertEquals(5, redPlayer.victoryPoints);
	}
	

	@Test
	public void test_BLUE_2_DEEP_DESERT_SNAKE_noExtraVPforDevourer() {
		redPlayer.recuperateAllBattleCards();
		bluePlayer.recuperateAllBattleCards();
		game.movePowerToPlayer(bluePlayer, PowerList.BLACK_1_ENFORCED_RECRUITMENT);

		Tile startTile = redPlayer.cityTiles.get(1);
		Tile tileByName = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_4_DEVOURER);
		recruitBeastToTile(startTile);
		buyPowerTile(PowerList.BLUE_2_DEEP_DESERT_SNAKE);
		recruitBeastToTile(tileByName);


		assertEquals(3, tileByName.getArmy().armySize);
		assertEquals(7, bluePlayer.getPrayerPoints());

		moveRowTwoArmy(startTile, tileByName, 5, true);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 5 army + 0 beast + 3 card = 8
		// defense strength : 3 army + 0 beast + 4 card = 7
		battlePick(BattleCard.FERVENT_PURGE_CARD, BattleCard.CAVALRY_BLITZ_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		skipRecruitBeast();
		moveRowTwoZeroArmy();
		// 2 damage
		assertEquals(8, bluePlayer.getPrayerPoints());

		// missing 1 VP from devourer due to deep desert snake
		assertEquals(5, redPlayer.victoryPoints);
	}
	
	@Test
	public void test_BLUE_2_DEEP_DESERT_SNAKE_devourerGetsDamageFromInitiative() {
		redPlayer.recuperateAllBattleCards();
		bluePlayer.recuperateAllBattleCards();
		
		game.movePowerToPlayer(bluePlayer, PowerList.RED_4_INITIATIVE);
		game.movePowerToPlayer(redPlayer, PowerList.BLACK_1_ENFORCED_RECRUITMENT);

		Tile startTile = bluePlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_4_DEVOURER);
		Tile tileByName = game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE);
		recruitBeastToTile(tileByName);
		
		buyPowerTile(PowerList.BLUE_2_DEEP_DESERT_SNAKE);
		recruitBeastToTile(startTile);
		
		prayRowTwo();
		
		moveRowTwoArmy(startTile, tileByName, 5, true);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 5 army + 0 beast + 1 card = 6
		// defense strength : 3 army + 0 beast + 2 card = 5
		battlePick(BattleCard.DEFENSIVE_RETREAT_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.MIXED_TACTICS_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.RECALL_CHOICE);
		skipRecruitBeast();
		
		prayRowThree();
		
		assertEquals(2, redPlayer.victoryPoints);
	}
	
	@Test
	public void test_BLUE_2_DEEP_DESERT_SNAKE_devourerGetsDamageFromDeadlyTrap() {

		redPlayer.recuperateAllBattleCards();
		bluePlayer.recuperateAllBattleCards();

		game.movePowerToPlayer(bluePlayer, PowerList.BLACK_3_DEADLY_TRAP);
		game.movePowerToPlayer(bluePlayer, PowerList.BLACK_1_ENFORCED_RECRUITMENT);
		
		Tile startTile = redPlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_4_DEVOURER);
		recruitBeastToTile(startTile);
		buyPowerTile(PowerList.BLUE_2_DEEP_DESERT_SNAKE);

		Tile tileByName = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		recruitBeastToTile(tileByName);

		assertEquals(3, tileByName.getArmy().armySize);

		moveRowTwoArmy(startTile, tileByName, 5, true);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 4 army + 0 beast + 2 card = 6
		// defense strength : 3 army + 0 beast + 4 card = 7
		battlePick(BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		moveRowTwoZeroArmy();

		assertEquals(3, redPlayer.victoryPoints);
	}
	
	@Test
	public void test_BLUE_2_DEEP_DESERT_SNAKE_ignoreKhnumSphinxMoveCost() {
		redPlayer.recuperateAllBattleCards();
		bluePlayer.recuperateAllBattleCards();
		
		game.movePowerToPlayer(redPlayer, PowerList.BLACK_1_ENFORCED_RECRUITMENT);

		Tile startTile = bluePlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLACK_2_KHNUM_SPHINX);
		Tile tileByName = game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE);
		recruitBeastToTile(tileByName);
		assertEquals(5, redPlayer.getPrayerPoints());
		
		assertEquals(9, bluePlayer.getPrayerPoints());
		buyPowerTile(PowerList.BLUE_2_DEEP_DESERT_SNAKE);
		recruitBeastToTile(startTile);
		assertEquals(7, bluePlayer.getPrayerPoints());
		
		prayRowThree();
		
		moveRowTwoArmy(startTile, tileByName, 5, true);

		// 2 for teleport, no + 2 for sphinx
		assertEquals(5, bluePlayer.getPrayerPoints());
		
		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 5 army + 0 beast + 4 card = 9
		// defense strength : 5 army + 0 beast + 3 card = 8
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.FERVENT_PURGE_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.RECALL_CHOICE);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		
		prayRowTwo();
		
		assertEquals(2, redPlayer.victoryPoints);
	}
	

	@Test
	public void test_BLUE_2_ANCESTRAL_ELEPHANT() {
		redPlayer.recuperateAllBattleCards();
		bluePlayer.recuperateAllBattleCards();
		game.movePowerToPlayer(bluePlayer, PowerList.BLACK_1_ENFORCED_RECRUITMENT);
		Tile tileByName = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);

		Tile startTile = redPlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_4_GIANT_SCORPION);
		recruitBeastToTile(startTile);
		buyPowerTile(PowerList.BLUE_2_ANCESTRAL_ELEPHANT);
		recruitBeastToTile(tileByName);
		assertEquals(7, bluePlayer.getPrayerPoints());

		assertEquals(3, tileByName.getArmy().armySize);

		moveRowTwoArmy(startTile, tileByName, 5, true);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 5 army + 2 beast + 1 card = 8
		// defense strength : 3 army + 1 beast + 4 card = 8
		battlePick(BattleCard.DEFENSIVE_RETREAT_CARD, BattleCard.CAVALRY_BLITZ_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.RECALL_CHOICE);
		skipRecruitBeast();
		
		moveRowTwoZeroArmy();
		// 1+1 damage
		assertEquals(8, bluePlayer.getPrayerPoints());

		assertEquals(3, redPlayer.victoryPoints);
	}
	
	@Test
	public void test_BLUE_4_SPHINX() {
		redPlayer.recuperateAllBattleCards();
		bluePlayer.recuperateAllBattleCards();
		game.movePowerToPlayer(bluePlayer, PowerList.BLACK_1_ENFORCED_RECRUITMENT);
		Tile tileByName = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);

		Tile startTile = redPlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_4_GIANT_SCORPION);
		recruitBeastToTile(startTile);
		
		assertEquals(3, bluePlayer.victoryPoints);
		
		buyPowerTile(PowerList.BLUE_4_SPHINX);
		recruitBeastToTile(tileByName);
		
		assertEquals(4, bluePlayer.victoryPoints);
		
		assertEquals(5, bluePlayer.getPrayerPoints());

		assertEquals(3, tileByName.getArmy().armySize);

		moveRowTwoArmy(startTile, tileByName, 5, true);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 5 army + 2 beast + 1 card = 8
		// defense strength : 3 army + 2 beast + 3 card = 8
		battlePick(BattleCard.DEFENSIVE_RETREAT_CARD, BattleCard.CAVALRY_BLITZ_CARD, BattleCard.FERVENT_PURGE_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		skipRecruitBeast();
		
		moveRowTwoZeroArmy();
		// 1+2 damage
		assertEquals(5, bluePlayer.getPrayerPoints());

		assertEquals(3, redPlayer.victoryPoints);
	}
	
	@Test
	public void test_WHITE_4_MUMMY() {
		redPlayer.recuperateAllBattleCards();
		bluePlayer.recuperateAllBattleCards();
		game.movePowerToPlayer(bluePlayer, PowerList.BLACK_1_ENFORCED_RECRUITMENT);
		Tile tileByName = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);

		Tile startTile = redPlayer.cityTiles.get(1);

		assertEquals(7, redPlayer.getPrayerPoints());
		buyPowerTile(PowerList.RED_4_GIANT_SCORPION);
		recruitBeastToTile(startTile);
		
		assertEquals(3, bluePlayer.victoryPoints);
		
		buyPowerTile(PowerList.WHITE_4_MUMMY);
		recruitBeastToTile(tileByName);
		
		assertEquals(3, bluePlayer.victoryPoints);
		
		assertEquals(5, bluePlayer.getPrayerPoints());

		assertEquals(3, tileByName.getArmy().armySize);

		moveRowTwoArmy(startTile, tileByName, 5, true);

		assertEquals(3, redPlayer.victoryPoints);
		// attack strength : 5 army + 2 beast + 1 card = 8
		// defense strength : 3 army + 2 beast + 3 card = 8
		battlePick(BattleCard.DEFENSIVE_RETREAT_CARD, BattleCard.CAVALRY_BLITZ_CARD, BattleCard.FERVENT_PURGE_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		skipRecruitBeast();
		
		moveRowTwoZeroArmy();
		// 1+2 damage
		assertEquals(5, bluePlayer.getPrayerPoints());

		assertEquals(3, redPlayer.victoryPoints);
	}

}
