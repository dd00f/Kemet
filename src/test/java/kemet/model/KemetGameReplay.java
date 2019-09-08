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
		KemetGame game = factory.createGame();
		game.setPrintActivations(true);
		
		game.setInitialSeed(228084711607877l);
		game.replayMultipleActions(new int[] { 26, 23, 27, 24, 27, 25, 23, 1, 5, 0, 47, 77, 46, 46, 42, 155, 45, 45, 48, 86, 48, 88, 41, 0, 41, 0, 36, 33, 36, 32, 67, 48, 89, 170, 14, 1, 16, 1, 45, 47, 73, 46, 44, 15, 28, 42, 14, 155, 42, 48, 152, 41, 0, 43, 0, 37, 31, 32, 30, 67, 48, 167, 87, 236, 48, 46, 236, 42, 45, 236, 46, 170, 155, 42, 155, 47, 74, 43, 0, 44, 14, 27, 30, 35, 37, 31, 67, 48, 43, 0, 45, 42, 46, 44, 15, 29, 42, 155, 48, 91, 43, 0, 45, 33, 34, 67, 45, 47, 79, 169, 170, 46, 48, 93, 37, 48, 45, 42, 155, 42, 43, 0, 44, 14, 28, 32, 35, 31, 30, 67, 43, 167, 193, 169, 0, 47, 81, 171, 48, 48, 152, 46, 46, 45, 44, 14, 29, 42, 155, 42, 184, 31, 33, 32, 36, 66, 168, 18, 48, 47, 85, 15, 45, 46, 46, 48, 152, 42, 155, 42, 14, 1, 43, 0, 44, 16, 26, 22});

		
		game.printDescribeGame();
		System.out.println(  game.getNextPlayerChoicePick() );
//		assertEquals(0, getCanonicalValue(BoardInventory.RAINING_FIRE_STATE));

//		PlayerChoicePick nextPlayerChoicePick = game.getNextPlayerChoicePick();
//		System.out.println(nextPlayerChoicePick);
		
//		System.out.println(game.getCanonicalForm(game.getNextPlayer()));
		
		
//		game.printDescribeGame();
//		game.getCanonicalForm(0);
//		game.getCanonicalForm(1);
//		game.getValidMoves();
		game.validate();
		
		assertTrue(true);

	}


}
