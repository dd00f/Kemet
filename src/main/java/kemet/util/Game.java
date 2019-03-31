package kemet.util;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public interface Game {


	public void initialize();

	/**
	 * Returns: startBoard: a representation of the board (ideally this is the form
	 * that will be the input to your neural network)
	 */
	public void getInitialBoard();

	/**
	 * 
	 * @return the board dimensions ???
	 */
	public Pair<Integer, Integer> getBoardSize();

	/**
	 * 
	 * @return the number of all possible actions
	 */
	public int getActionSize();

	/**
	 * get the next game state
	 * 
	 * @param player      the player index
	 * @param actionIndex the action index
	 * @return the new game state, the current game remains untouched.
	 */
	public Game getNextState(int player, int actionIndex);
	
	/**
	 * Activate a specific action in the current game.
	 * @param player the player index
	 * @param actionIndex the action index.
	 */
	public void activateAction(int player, int actionIndex);

	/**
	 * 
	 * @return the player that's about to play next
	 */
	public int getNextPlayer();

	/**
	 * 
	 * @return a vector of all the valid moves the current player can take set to
	 *         true.
	 */
	public boolean[] getValidMoves();

	/**
	 * 
	 * @param playerIndex the player index
	 * @return 1 if current player has won the game, small non-zero value for a draw
	 */

	public int getGameEnded(int playerIndex);

	/**
	 * 
	 * @param playerIndex the player index
	 * @return canonicalBoard: returns canonical form of board. The canonical form
	 *         should be independent of player. For e.g. in chess, the canonical
	 *         form can be chosen to be from the pov of white. When the player is
	 *         white, we can return board as is. When the player is black, we can
	 *         invert the colors and return the board.
	 */
	public ByteCanonicalForm getCanonicalForm(int playerIndex);

	/**
	 * 
	 * @param playerIndex
	 * @return symmForms: a list of [(board,pi)] where each tuple is a symmetrical
	 *         form of the board and the corresponding pi vector. This is used when
	 *         training the neural network from examples.
	 */
	public List<Game> getSymmetries(int playerIndex);


	public void playbackGame(int[] actions);

	public void describeGame(StringBuilder builder);
	
	public void setPrintActivations(boolean printActivations);

	public Game clone();

	public String describeAction(int i);

	public void printDescribeGame();

	public void printChoiceList();

	
	public String getPlayerName(int playerIndex);
	
	public void setPlayerName(int playerIndex, String name);
	
	public int[] getActivatedActions();
	
	public void replayMultipleActions(int[] actions);
	
	/**
	 * Used to get a simplified view of the value of the board from a player perspective.
	 * Useful for games where the winning conditions is based on a clear score counter.
	 * @param playerIndex the player index from which to get the game value.
	 * @param predictedValue the value that was predicted by the neural network.
	 * @return the simplified value.
	 */
	public float getSimpleValue(int playerIndex, float predictedValue );
	
	public boolean isGameEnded();
	
	public void enterSimulationMode(int playerIndex, StackingMCTS mcts);


}
