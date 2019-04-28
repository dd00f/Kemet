package kemet.data;

import kemet.Options;

public class NeuralNetworkVsHuman {

	public static void main(String[] args) {
    	TwoPlayerGame.PLAYER_ONE_NEURAL = false;
    	TwoPlayerGame.PLAYER_ONE_HUMAN = true;
    	TwoPlayerGame.PLAYER_TWO_NEURAL = true;
    	TwoPlayerGame.PLAYER_TWO_HUMAN = false;
    	Options.COACH_MCTS_SIMULATION_COUNT_PER_MOVE = 10000;
        TwoPlayerGame twoPlayerGame = new TwoPlayerGame();
        twoPlayerGame.initializeGame();
        twoPlayerGame.runGame();
	}
}
