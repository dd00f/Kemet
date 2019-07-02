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
import kemet.model.Color;
import kemet.model.PowerList;
import kemet.model.Tile;
import kemet.model.action.choice.ChoiceInventory;

public class BeastTest extends TwoPlayerGameTest {

	
	@Test
	public void test_cleanGame() {
		tpg = new TwoPlayerGame();
		tpg.createAIPlayer("red");
		tpg.createAIPlayer("blue");
		tpg.createTiles();

		game = tpg.game;
		// game.setPrintActivations(false);

		redPlayer = game.playerByInitiativeList.get(0);
		bluePlayer = game.playerByInitiativeList.get(1);
	
		// initialization
		pickPyramidLevel(2);
		pickPyramidColor(Color.RED);
		pickPyramidColor(Color.BLACK);
		pickPyramidLevel(2);
		pickPyramidColor(Color.BLUE);
		pickPyramidColor(Color.WHITE);
		recruitArmySize(5);
		recruitArmySize(5);
		recruitArmySize(5);
		recruitArmySize(5);

		// turn one
		upgradePyramid(4, redPlayer.cityTiles.get(0));
		upgradePyramid(4, bluePlayer.cityTiles.get(0));
		prayRowThree();
		prayRowThree();
		prayRowTwo();
		prayRowTwo();
		
		startRowOneMove();
		moveFirstTile(redPlayer.cityTiles.get(0), game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE), 5);
		endMove();
		
		startRowOneMove();
		moveFirstTile(bluePlayer.cityTiles.get(0), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 5);

		
		moveRowTwoZeroArmy();
		moveRowTwoZeroArmy();


		// turn two
		battlePick(BattleCard.FERVENT_PURGE_CARD, BattleCard.MIXED_TACTICS_CARD, BattleCard.FERVENT_PURGE_CARD, BattleCard.MIXED_TACTICS_CARD);
		pickPlayerOrder(1);
		prayRowThree();
		prayRowThree();
		prayRowTwo();
		prayRowTwo();
		upgradePyramid(4, redPlayer.cityTiles.get(1));
		upgradePyramid(4, bluePlayer.cityTiles.get(1));
		moveRowOneZeroArmy();
		moveRowOneZeroArmy();
		moveRowTwoZeroArmy();
		moveRowTwoZeroArmy();

		// turn three
		battlePick(BattleCard.CHARIOT_RAID_CARD, BattleCard.SHIELD_PUSH_CARD, BattleCard.CHARIOT_RAID_CARD, BattleCard.SHIELD_PUSH_CARD);
		pickPlayerOrder(1);
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



	}
	
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

//	public static String RED_4_PHOENIX_POWER = "Ignore Walls";
//	public static Beast RED_4_PHOENIX = new Beast(RED_4_PHOENIX_NAME, 2, 0, 1, 0, RED_4_PHOENIX_POWER, BEAST_INDEXER++);

//	public static Beast BLUE_2_ANCESTRAL_ELEPHANT = new Beast(BLUE_2_ANCESTRAL_ELEPHANT_NAME, 1, 1, 1, 0, null, BEAST_INDEXER++);

//	public static String BLUE_2_DEEP_DESERT_SNAKE_POWER = "Ignore Enemy Beast";
//	public static Beast BLUE_2_DEEP_DESERT_SNAKE = new Beast(BLUE_2_DEEP_DESERT_SNAKE_NAME, 0, 0, 1, 0,
//			BLUE_2_DEEP_DESERT_SNAKE_POWER, BEAST_INDEXER++);
//
//	public static Beast BLUE_4_SPHINX = new Beast(BLUE_4_SPHINX_NAME, 2, 0, 0, 0,
//			BLUE_4_SPHINX_POWER, BEAST_INDEXER++);
//	
//	public static String BLACK_2_KHNUM_SPHINX_POWER = "Opponents pay 2 power to move on Khnum's Sphinx";
//	public static Beast BLACK_2_KHNUM_SPHINX = new Beast(BLACK_2_KHNUM_SPHINX_NAME, 1, 0, 1, 0,
//			BLACK_2_KHNUM_SPHINX_POWER, BEAST_INDEXER++);
//	
//	public static String BLACK_3_GRIFFIN_SPHINX_POWER = "Teleport from Obelisk";
//	public static Beast BLACK_3_GRIFFIN_SPHINX = new Beast(BLACK_3_GRIFFIN_SPHINX_NAME, 2, 0, 0, 0,
//			BLACK_3_GRIFFIN_SPHINX_POWER, BEAST_INDEXER++);
//	
//	public static String BLACK_4_DEVOURER_POWER = "+1 VP on battle victory & damage 2 enemy units";
//	public static Beast BLACK_4_DEVOURER = new Beast(BLACK_4_DEVOURER_NAME, 2, 0, 1, 0,
//			BLACK_4_DEVOURER_POWER, BEAST_INDEXER++);
//	
//	public static String WHITE_4_MUMMY_POWER = "+1 DI card at night";
//	public static Beast WHITE_4_MUMMY = new Beast(WHITE_4_MUMMY_NAME, 2, 0, 1, 0,
//			WHITE_4_MUMMY_POWER, BEAST_INDEXER++);

}
