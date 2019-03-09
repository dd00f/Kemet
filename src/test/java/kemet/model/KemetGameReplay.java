package kemet.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import kemet.data.TwoPlayerGame;

class KemetGameReplay {

	@Test
	public void replayGame() {
		TwoPlayerGame factory = new TwoPlayerGame();
		KemetGame createGame = factory.createGame();
		createGame.setPrintActivations(true);

		int[] actions = new int[] {  };
		
		
		actions = new int[] {

				27, 24, 22, 27, 24, 23, 0, 5, 0, 5, 41, 0, 41, 0, 40, 0, 39, 0, 39, 0, 43, 44, 44, 42, 0, 40, 0, 43, 42, 0, 39, 0, 40, 0, 40, 0, 41, 0, 41, 0, 44, 44, 43, 42, 0, 41, 0, 41, 0, 40, 0, 39, 0, 43, 44, 44, 43, 42, 0, 41, 0, 39, 0, 39, 15, 0, 44, 43, 41, 0, 44, 43, 42, 0, 40, 0, 39, 0, 44, 40, 0, 40, 0, 43, 43, 44, 42, 0, 41, 0, 39, 0, 41, 0, 43, 40, 0, 44, 39, 0, 41, 0, 44, 39, 0, 43, 40, 0, 43, 43, 40, 14, 2, 41, 0, 41, 0, 40, 0, 44, 39, 0, 42, 0, 44, 40, 43, 41, 14, 0, 44, 44, 40, 0, 39, 0, 41, 0, 43, 39, 0, 44, 41, 0, 43, 40, 0, 40, 44, 41, 0, 42, 0, 42, 0, 43, 39, 0, 39, 0, 43, 41, 0, 41, 0, 43, 40, 44, 44, 40, 0, 43, 41, 0, 39, 0, 40, 0, 41, 15, 10, 5, 0, 42, 0, 42, 14, 0, 44, 44, 39, 0, 42, 0, 39, 0, 41, 0, 41, 0, 40, 44, 44, 43, 39, 0, 40, 0, 42, 15, 0, 39, 0, 40, 44, 39, 16, 0, 40, 0, 44, 41, 0, 43, 42, 0, 41, 0, 41, 0, 40, 40, 14, 2, 42, 15, 0, 39, 0, 44, 44, 43, 42, 0, 41, 16, 17, 0, 40, 40, 43, 39, 0, 42, 0, 42, 0, 44, 44, 39, 0
		};
		
		createGame.replayMultipleActions(actions);
		createGame.printDescribeGame();
		
		assertTrue(true);

	}

}
