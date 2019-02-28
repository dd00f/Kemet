package kemet.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import kemet.data.TwoPlayerGame;
import kemet.util.ByteCanonicalForm;
import lombok.EqualsAndHashCode;

class KemetGameTest {

	@Test
	void getPlayerCanonicalIndex() {

		Player player = Player.create();

		player.index = 0;
		assertEquals(0, player.getCanonicalPlayerIndex(0));
		assertEquals(1, player.getCanonicalPlayerIndex(1));
		assertEquals(1, player.getCanonicalPlayerIndex(2));

		player.index = 1;
		assertEquals(1, player.getCanonicalPlayerIndex(0));
		assertEquals(0, player.getCanonicalPlayerIndex(1));
		assertEquals(2, player.getCanonicalPlayerIndex(2));

		player.index = 2;
		assertEquals(2, player.getCanonicalPlayerIndex(0));
		assertEquals(2, player.getCanonicalPlayerIndex(1));
		assertEquals(0, player.getCanonicalPlayerIndex(2));

	}

	@Test
	void testCanonicalForm() {
		TwoPlayerGame factory = new TwoPlayerGame();
		KemetGame createGame = factory.createGame();

		createGame.getPlayerByIndex(0);

		ByteCanonicalForm initForm0 = createGame.getCanonicalForm(0);
		ByteCanonicalForm initForm1 = createGame.getCanonicalForm(1);

		createGame.getPlayerByIndex(0).useBattleCard(BattleCard.CAVALRY_BLITZ_CARD);

		ByteCanonicalForm initForm01 = createGame.getCanonicalForm(0);
		ByteCanonicalForm initForm11 = createGame.getCanonicalForm(1);

		createGame.getPlayerByIndex(0).discardBattleCard(BattleCard.CHARIOT_RAID_CARD);

		ByteCanonicalForm initForm02 = createGame.getCanonicalForm(0);
		ByteCanonicalForm initForm12 = createGame.getCanonicalForm(1);

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
