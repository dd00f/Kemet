package kemet.dummy;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import kemet.Options;
import kemet.ai.KemetNeuralNetwork;
import kemet.ai.KemetRecurrentNeuralNetBuilder;
import kemet.model.BoardInventory;
import kemet.model.DiCardList;
import kemet.model.Model;
import kemet.model.action.choice.ChoiceInventory;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;
import kemet.util.Coach;
import kemet.util.Game;
import kemet.util.GameFactory;
import kemet.util.NeuralNet;
import kemet.util.StackingMCTS;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DummyGame implements Game, Model {

	public static class DummyGameFactory implements GameFactory {

		@Override
		public Game createGame() {
			return GAME_CACHE.create();
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3336477477042535907L;

	private static Cache<DummyGame> GAME_CACHE = new Cache<DummyGame>(() -> new DummyGame());

	public static int OPTION_COUNT = 20;

	public static int POINT_COUNT = 400 + OPTION_COUNT;

	public static int ACTION_COUNT = 400;

	public byte[] owner = new byte[POINT_COUNT * 2];

	public int actionLeft = ACTION_COUNT;

	@Override
	public void initialize() {
	}

	@Override
	public void getInitialBoard() {

	}

	@Override
	public Pair<Integer, Integer> getBoardSize() {
		return null;
	}

	@Override
	public int getActionSize() {
		return ChoiceInventory.TOTAL_CHOICE;
	}

	@Override
	public Game getNextState(int player, int actionIndex) {
		return null;
	}

	@Override
	public void activateAction(int player, int actionIndex) {

		int otherPlayerIndex = getOtherPlayerIndex(player);
		int removeIndex = POINT_COUNT * otherPlayerIndex + actionIndex;
		owner[removeIndex] = 0;

		int index = POINT_COUNT * player + actionIndex;
		owner[index] = 1;

		actionLeft--;
	}

	private int getOtherPlayerIndex(int player) {
		if (player == 0) {
			return 1;
		}
		return 0;
	}

	@Override
	public int getNextPlayer() {
		return actionLeft % 2;
	}

	@Override
	public boolean[] getValidMoves() {

		// 20 options, over 200 moves

		// 420 points to cover

		int currentActionLeft = (actionLeft + 1) / 2;

		boolean[] moves = new boolean[ChoiceInventory.TOTAL_CHOICE];

		int fractionOfGameDone = (ACTION_COUNT / 2) - currentActionLeft;

		int incrementByAction = (POINT_COUNT - OPTION_COUNT) / ACTION_COUNT;

		int startOffset = fractionOfGameDone * incrementByAction;
		int stopOffset = startOffset + OPTION_COUNT;

		for (int i = startOffset; i < stopOffset; ++i) {
			moves[i] = true;
		}

		return moves;
	}

	@Override
	public int getGameEnded(int playerIndex) {
		if (actionLeft == 0) {
			return isPlayerWinner(playerIndex);
		}
		return 0;
	}

	private int isPlayerWinner(int playerIndex) {
		int playerScore = getPlayerScore(playerIndex);
		int otherPlayerScore = getPlayerScore(getOtherPlayerIndex(playerIndex));
		if (playerScore > otherPlayerScore) {
			return 1;
		}
//		if( playerScore == otherPlayerScore ) {
//			// draw
//			return -0.01;
//		}
		return -1;
	}

	private int getPlayerScore(int playerIndex) {
		int start = playerIndex * ACTION_COUNT;
		int end = start + ACTION_COUNT;
		int score = 0;
		for (int i = start; i < end; ++i) {
			if (owner[i] == 1) {
				int points = i % ACTION_COUNT;
				score += points;
			}
		}
		return score;
	}

	public static int CANONICAL_INDEXER = 0;

	public static int CANONICAL_POINTS = CANONICAL_INDEXER;
	static {
		CANONICAL_INDEXER += POINT_COUNT * 2;
	}
	public static int CANONICAL_SCORE_PER_PLAYER = CANONICAL_INDEXER;
	static {
		CANONICAL_INDEXER += 2;
	}
	public static int CANONICAL_SCORE_DIFF = CANONICAL_INDEXER;
	static {
		CANONICAL_INDEXER += 1;
	}
	public static int CANONICAL_MOVE_LEFT = CANONICAL_INDEXER;
	static {
		CANONICAL_INDEXER += 2;
	}
	public static int CANONICAL_TOTAL = CANONICAL_INDEXER;

	@Override
	public ByteCanonicalForm getCanonicalForm(int playerIndex) {
		ByteCanonicalForm bcf = new ByteCanonicalForm(BoardInventory.TOTAL_STATE_COUNT);

		int playerZeroOffset = 0;
		if (playerIndex == 1) {
			playerZeroOffset = POINT_COUNT;
		}

		for (int i = 0; i < POINT_COUNT; i++) {
			bcf.set(playerZeroOffset + i, owner[i]);
		}

		int playerOneOffset = 0;
		if (playerIndex == 0) {
			playerOneOffset = POINT_COUNT;
		}

		int endIndex = POINT_COUNT * 2;
		for (int i = POINT_COUNT; i < endIndex; i++) {
			bcf.set(playerOneOffset + (i % POINT_COUNT), owner[i]);
		}

		byte playerZeroScore = (byte) (getPlayerScore(0) / (POINT_COUNT * ACTION_COUNT / 4));
		byte playerOneScore = (byte) (getPlayerScore(1) / (POINT_COUNT * ACTION_COUNT / 4));
		byte scoreDiff = 0;

		if (playerIndex == 0) {
			bcf.set(CANONICAL_SCORE_PER_PLAYER + 0, playerZeroScore);
			bcf.set(CANONICAL_SCORE_PER_PLAYER + 1, playerOneScore);
			scoreDiff = (byte) (playerZeroScore - playerOneScore);
		} else {
			bcf.set(CANONICAL_SCORE_PER_PLAYER + 0, playerOneScore);
			bcf.set(CANONICAL_SCORE_PER_PLAYER + 1, playerZeroScore);
			scoreDiff = (byte) (playerOneScore - playerZeroScore);
		}

		bcf.set(CANONICAL_SCORE_DIFF, scoreDiff);
		bcf.set(CANONICAL_MOVE_LEFT, (byte) (actionLeft / 50));
		bcf.set(CANONICAL_MOVE_LEFT+1, (byte) (actionLeft % 50));

		return bcf;
	}

	@Override
	public List<Game> getSymmetries(int playerIndex) {
		return null;
	}

	@Override
	public void playbackGame(int[] actions) {

	}

	@Override
	public void describeGame(StringBuilder builder) {

		builder.append("Player 0 has : ");

		for (int i = 0; i < owner.length / 2; i++) {
			if (owner[i] == 1) {
				builder.append(i);
				builder.append(", ");
			}
		}
		builder.append("Player 0 score : ");
		builder.append(getPlayerScore(0));

		builder.append("Player 1 has : ");
		for (int i = owner.length / 2; i < owner.length; i++) {
			if (owner[i] == 1) {
				builder.append(i - POINT_COUNT);
				builder.append(", ");
			}
		}
		builder.append("Player 1 score : ");
		builder.append(getPlayerScore(1));

	}

	@Override
	public void setPrintActivations(boolean printActivations) {

	}

	@Override
	public Game clone() {
		return deepCacheClone();
	}

	@Override
	public String describeAction(int i) {
		return "Grab spot : " + i;
	}

	@Override
	public void printDescribeGame() {

		printEvent(toString());

	}

	public void printEvent(String event) {
		log.info(event);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		describeGame(builder);
		return builder.toString();
	}
	
	@Override
	public void printChoiceList() {

	}

	@Override
	public String getPlayerName(int playerIndex) {
		return Integer.toString(playerIndex);
	}

	@Override
	public void setPlayerName(int playerIndex, String name) {

	}

	@Override
	public int[] getActivatedActions() {
		return null;
	}

	@Override
	public void replayMultipleActions(int[] actions) {

	}

	@Override
	public float getSimpleValue(int playerIndex, float predictedValue) {
		return 0;
	}

	@Override
	public boolean isGameEnded() {
		return getGameEnded(0) != 0;
	}

	@Override
	public void enterSimulationMode(int playerIndex, StackingMCTS mcts) {

	}

	@Override
	public DummyGame deepCacheClone() {

		DummyGame clone = GAME_CACHE.create();

		clone.actionLeft = actionLeft;
		DiCardList.copyArray(owner, clone.owner);

		return clone;
	}

	@Override
	public void release() {

		GAME_CACHE.release(this);
	}

	public static void main(String[] args) {

		Options.COACH_MCTS_SIMULATION_COUNT_PER_MOVE = 10;
		Options.COACH_NUMBER_OF_NN_TRAINING_ITERATIONS = 10;
		Options.USE_RECURRENT_NEURAL_NET = false;

		runTraining();
		
//		KemetRecurrentNeuralNetBuilder.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT = 0;
//		KemetRecurrentNeuralNetBuilder.LAYER_SIZE = 2048;
//
//		runTraining();
//		
//		KemetRecurrentNeuralNetBuilder.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT = 1;
//		KemetRecurrentNeuralNetBuilder.LAYER_SIZE = 512;
//
//		runTraining();
//		
//		KemetRecurrentNeuralNetBuilder.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT = 1;
//		KemetRecurrentNeuralNetBuilder.LAYER_SIZE = 1024;
//
//		runTraining();
//
//		KemetRecurrentNeuralNetBuilder.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT = 1;
//		KemetRecurrentNeuralNetBuilder.LAYER_SIZE = 2048;
//
//		runTraining();
//		
//		KemetRecurrentNeuralNetBuilder.LAYER_SIZE = 4096;
//
//		runTraining();

	}

	public static void runTraining() {
		GameFactory gameFactory = new DummyGameFactory();

		NeuralNet neuralNet = null;
		neuralNet = new KemetNeuralNetwork();

//		boolean loadModel = true;
//		boolean loadTrainingExamples = true;
//		if (loadModel) {
//			neuralNet.loadCheckpoint("./tempDummy", "best.pth.tar");
//		}
//
//		else {
//
//			neuralNet.saveCheckpoint("./tempDummy", "initial.nn");
//			neuralNet.loadCheckpoint("./tempDummy", "initial.nn");
//
//		}

		Coach coach = new Coach(gameFactory, neuralNet);
		coach.checkpoint = "./tempDummy/";

//	    int TODO_REVERT_THIS;

		boolean pitVsBasicNet = false;
		if (pitVsBasicNet) {
			coach.pnet = new KemetNeuralNetwork();
			coach.pitBestVsBasic();
		} else {
//			if (loadTrainingExamples) {
//				coach.loadTrainExamples();
//			}

			coach.learn();
		}
	}

}
