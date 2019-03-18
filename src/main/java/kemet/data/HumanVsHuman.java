package kemet.data;

public class HumanVsHuman {

	public static void main(String[] args) {
    	TwoPlayerGame.PLAYER_ONE_NEURAL = false;
    	TwoPlayerGame.PLAYER_ONE_HUMAN = true;
    	TwoPlayerGame.PLAYER_TWO_NEURAL = false;
    	TwoPlayerGame.PLAYER_TWO_HUMAN = true;
        TwoPlayerGame twoPlayerGame = new TwoPlayerGame();
        twoPlayerGame.initializeGame();
        twoPlayerGame.runGame();
	}
}
