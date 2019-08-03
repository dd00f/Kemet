package kemet.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import kemet.data.TwoPlayerGame;
import kemet.model.action.TwoPlayerGameTest;

class KemetGameReplay extends TwoPlayerGameTest{


	
	@Test
	public void replayGame() {
		TwoPlayerGame factory = new TwoPlayerGame();
		game = factory.createGame();
		game.setPrintActivations(true);
		
		game.setInitialSeed(104473288237735l);
		replayMultipleActions(new int[] { 26, 22, 26, 25, 23, 27, 24, 22, 0, 2, 3, 170, 14, 1, 46, 46, 42, 14, 68, 50, 120, 15, 155, 45, 47, 76, 41, 14, 13, 0, 41, 15, 17, 0, 48, 89, 45, 34, 30, 37, 30, 67, 48, 152, 41, 14, 11, 2, 12, 2, 44, 154, 50, 118, 41, 16, 14, 4, 17, 5, 42, 15, 155, 46, 46, 42, 14, 1, 45, 33, 32, 37, 33, 66, 41, 15, 11, 1, 0, 46, 44, 16, 28, 154, 42, 42, 14, 4, 12, 68, 11, 1, 48, 88, 43, 12, 9, 0, 45, 47, 77, 168, 20, 44, 154, 36, 31, 31, 34, 67, 42, 46, 50, 119, 155, 41, 14, 9, 3, 12, 0, 48, 87, 167, 192, 47, 168, 19, 74, 44, 15, 28, 42, 16, 68, 15, 68, 9, 68, 11, 68, 12, 1, 171, 43, 17, 9, 2, 161, 36});
		
		game.printDescribeGame();
		game.getCanonicalForm(0);
		game.getCanonicalForm(1);
		game.getValidMoves();
		game.validate();
		
		assertTrue(true);

	}


}
