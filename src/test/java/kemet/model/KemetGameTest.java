package kemet.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import kemet.data.TwoPlayerGame;
import kemet.util.ByteCanonicalForm;
import lombok.extern.log4j.Log4j2;

@Log4j2
class KemetGameTest {

	@Test
	void getPlayerCanonicalIndex() {

		Player player = Player.create();

		player.setIndex(0);
		assertEquals(0, player.getCanonicalPlayerIndex(0));
		assertEquals(1, player.getCanonicalPlayerIndex(1));
		assertEquals(1, player.getCanonicalPlayerIndex(2));

		player.setIndex(1);
		assertEquals(1, player.getCanonicalPlayerIndex(0));
		assertEquals(0, player.getCanonicalPlayerIndex(1));
		assertEquals(2, player.getCanonicalPlayerIndex(2));

		player.setIndex(2);
		assertEquals(2, player.getCanonicalPlayerIndex(0));
		assertEquals(2, player.getCanonicalPlayerIndex(1));
		assertEquals(0, player.getCanonicalPlayerIndex(2));

	}

	@Test
	void testSimulationSkipDiscardChoice() {
		TwoPlayerGame factory = new TwoPlayerGame();
		KemetGame createGame = factory.createGame();

		int[] actions = new int[] {

				27, 23, 22, 27, 23, 22, 5, 5, 5, 5, 39, 14, 11, 5, 0, 39, 14, 11, 5, 30, 31, 32, 30
		};
		createGame.replayMultipleActions(actions);
		
		log.info("--------------");
	
		assertEquals(0, createGame.getPlayerByIndex(0).victoryPoints );
		assertEquals(2, createGame.getPlayerByIndex(1).victoryPoints );
		
		
		createGame = factory.createGame();
		createGame.enterSimulationMode(0, null);

		actions = new int[] {

				27, 23, 22, 27, 23, 22, 5, 5, 5, 5, 39, 14, 11, 5, 0, 39, 14, 11, 5, 30, 32, 30
		};
		createGame.replayMultipleActions(actions);
		log.info("--------------");
		
		assertEquals(0, createGame.getPlayerByIndex(0).victoryPoints );
		assertEquals(2, createGame.getPlayerByIndex(1).victoryPoints );
		
		
		createGame = factory.createGame();
		createGame.enterSimulationMode(1, null);

		actions = new int[] {

				27, 23, 22, 27, 23, 22, 5, 5, 5, 5, 39, 14, 11, 5, 0, 39, 14, 11, 5, 30, 31, 32
		};
		createGame.replayMultipleActions(actions);
		createGame.getNextPlayerChoicePick();
		log.info("--------------");

		assertEquals(0, createGame.getPlayerByIndex(0).victoryPoints );
		assertEquals(2, createGame.getPlayerByIndex(1).victoryPoints );
		
	}
	
		
	@Test
	void testCanonicalForm() {
		TwoPlayerGame factory = new TwoPlayerGame();
		KemetGame createGame = factory.createGame();

		createGame.getPlayerByIndex(0);

		ByteCanonicalForm initForm0 = createGame.getCanonicalForm(0);
		createGame.canonicalForm = null;
		ByteCanonicalForm initForm1 = createGame.getCanonicalForm(1);
		createGame.canonicalForm = null;

		createGame.getPlayerByIndex(0).useBattleCard(BattleCard.CAVALRY_BLITZ_CARD);

		ByteCanonicalForm initForm01 = createGame.getCanonicalForm(0);
		createGame.canonicalForm = null;
		ByteCanonicalForm initForm11 = createGame.getCanonicalForm(1);
		createGame.canonicalForm = null;

		createGame.getPlayerByIndex(0).discardBattleCard(BattleCard.CHARIOT_RAID_CARD);

		ByteCanonicalForm initForm02 = createGame.getCanonicalForm(0);
		createGame.canonicalForm = null;
		ByteCanonicalForm initForm12 = createGame.getCanonicalForm(1);
		createGame.canonicalForm = null;

		assertNotEquals(initForm0, initForm01);
		assertNotEquals(initForm02, initForm01);
		assertNotEquals(initForm02, initForm0);

		int usedCardIndexFromP1 = Player.getCardStatusIndex(1, BattleCard.CAVALRY_BLITZ_CARD);
		int discardCardIndexFromP1 = Player.getCardStatusIndex(1, BattleCard.CHARIOT_RAID_CARD);

		assertEquals(1, initForm1.getCanonicalForm()[usedCardIndexFromP1]);
		assertEquals(1, initForm1.getCanonicalForm()[discardCardIndexFromP1]);

		assertEquals(-1, initForm11.getCanonicalForm()[usedCardIndexFromP1]);
		assertEquals(1, initForm11.getCanonicalForm()[discardCardIndexFromP1]);

		assertEquals(-1, initForm12.getCanonicalForm()[usedCardIndexFromP1]);
		assertEquals(1, initForm12.getCanonicalForm()[discardCardIndexFromP1]);

		assertNotEquals(initForm1, initForm11);
		assertNotEquals(initForm12, initForm1);
		assertEquals(initForm12, initForm11);

	}
	

}
