package kemet.data;

import kemet.Options;

public class TrialVsTrial {

	public static void main(String[] args) {
    	TwoPlayerGame.PLAYER_ONE_NEURAL = false;
    	TwoPlayerGame.PLAYER_ONE_HUMAN = false;
    	TwoPlayerGame.PLAYER_ONE_TRIAL = true;
    	TwoPlayerGame.PLAYER_TWO_NEURAL = false;
    	TwoPlayerGame.PLAYER_TWO_TRIAL = true;
    	TwoPlayerGame.PLAYER_TWO_HUMAN = false;
    	Options.COACH_MCTS_SIMULATION_COUNT_PER_MOVE = 10000;
        TwoPlayerGame twoPlayerGame = new TwoPlayerGame();
        twoPlayerGame.initializeGame();
        twoPlayerGame.runGame();
	}
}
