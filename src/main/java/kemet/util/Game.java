package kemet.util;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public interface Game {
	/*
	 * This class specifies the base Game class. To define your own game, subclass
	 * this class and implement the functions below. This works when the game is
	 * two-player, adversarial and turn-based.
	 * 
	 * Use 1 for player1 and -1 for player2.
	 * 
	 * See othello/OthelloGame.py for an example implementation.
	 */

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
	 * @return the new game state
	 */
	public Game getNextState(int player, int actionIndex);

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

	/**
	 * 
	 * @return boardString: a quick conversion of board to a string format. Required
	 *         by MCTS for hashing.
	 */
	public String stringRepresentation();

	public void describeGame();

}