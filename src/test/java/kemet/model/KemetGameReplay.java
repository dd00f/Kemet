package kemet.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import kemet.data.TwoPlayerGame;
import kemet.model.action.PlayerChoicePick;
import kemet.model.action.TwoPlayerGameTest;

class KemetGameReplay extends TwoPlayerGameTest{


	
	@Test
	public void replayGame() {
		TwoPlayerGame factory = new TwoPlayerGame();
		game = factory.createGame();
		game.setPrintActivations(true);
		
		game.setInitialSeed(246612676278441l);
		game.replayMultipleActions(new int[] { 26, 24, 26, 23, 25, 26, 23, 26, 22, 24, 4, 3, 2, 3, 41, 15, 17, 3, 45, 44, 16, 154, 46, 45, 48, 89, 49, 105, 50, 119, 15, 2, 155, 46, 41, 14, 0, 32, 35, 37, 31, 67, 45, 47, 72, 46, 42, 155, 49, 152, 41, 15, 17, 2, 10, 1, 44, 154, 43, 17, 171, 10, 1, 21, 1, 163, 34, 30, 157, 32, 34, 40, 40, 41, 0, 44, 16, 29, 33, 30, 37, 36, 56, 67, 43, 15, 10, 1, 17, 1, 9, 1, 45, 46, 41, 0, 47, 78, 44, 14, 27, 42, 14, 155, 43, 0, 41, 10, 21, 1, 0, 46, 31, 36, 67, 44, 154, 48, 88, 46, 44, 154, 47, 74, 43, 9, 21, 1, 12, 2, 42, 155, 50, 152, 43, 16, 0, 42, 15, 155, 30, 35, 35, 37, 66, 50, 118, 45, 42, 14, 68, 155, 49, 102, 44, 15, 28, 44, 154, 43, 15, 11, 2, 172, 178, 0, 46, 47, 70, 41, 0, 30, 32, 34, 33, 67, 45, 42, 14, 155, 49, 45, 43, 0, 41, 12, 21, 2, 9, 2, 46, 47, 76, 41, 0, 50, 126, 16, 36, 34, 37, 36, 67, 45, 46, 168, 174, 174, 11, 46, 44, 169, 14, 29, 42, 14, 155, 47, 152, 43, 16, 0, 48, 97, 49, 41, 9, 12, 1, 17, 1, 31, 33, 67, 44, 15, 29, 47, 71, 50, 152, 49, 42, 11, 1, 9, 68, 17, 155, 43, 170, 14, 68, 155, 0, 43, 9, 0, 46, 48, 152, 42, 155, 35, 33, 34, 37, 67, 45, 44, 154, 46, 47, 84, 49, 45, 50, 130, 41, 0, 43, 17, 12, 1, 17, 1, 9, 1, 47, 77, 41, 16, 10, 149, 11, 2, 0, 34, 37, 33, 32, 67, 45, 45, 43, 9, 21, 1, 10, 1, 11, 144, 9, 4, 167, 181, 17, 4, 168, 21, 46, 42, 15, 3, 153, 155, 44, 15, 27, 47, 85, 9, 47, 73, 44, 154, 42, 15, 5, 46, 35, 36, 32, 36, 67, 45, 41, 17, 15, 1, 9, 4, 12, 5, 153, 9, 0, 45, 46, 47, 152, 44, 154, 42, 50, 131, 46, 43, 17, 15, 1, 10, 1, 21, 0, 48, 92, 30, 30, 66, 42, 51, 155, 45, 47, 43, 152, 170, 15, 2, 143, 10, 17, 4, 15, 3, 0, 47, 75, 50, 133, 10, 42, 48, 93, 30, 49, 41, 10, 9});

		
		game.printDescribeGame();
		System.out.println(  game.getNextPlayerChoicePick() );
		assertEquals(0, getCanonicalValue(BoardInventory.RAINING_FIRE_STATE));

		PlayerChoicePick nextPlayerChoicePick = game.getNextPlayerChoicePick();
		System.out.println(nextPlayerChoicePick);
		
		System.out.println(game.getCanonicalForm(game.getNextPlayer()));
		
		
		game.printDescribeGame();
		game.getCanonicalForm(0);
		game.getCanonicalForm(1);
		game.getValidMoves();
		game.validate();
		
		assertTrue(true);

	}


}
