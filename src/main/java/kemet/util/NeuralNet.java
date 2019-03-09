package kemet.util;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * This class specifies the base NeuralNet class. To define your own neural
 * network, subclass this class and implement the functions below. The neural
 * network does not consider the current player, and instead only deals with the
 * canonical form of the board.
 * 
 *
 */
public interface NeuralNet {

	/**
	 * 
	 * @param examples a list of training examples, where each example is of form
	 *                 (board, pi, v). pi is the MCTS informed policy vector for the
	 *                 given board, and v is its value. The examples has board in
	 *                 its canonical form.
	 */
	public void train(List<TrainExample> examples);

	/**
	 * 
	 * @param gameCanonicalForm current board in its canonical form.
	 * @return a policy vector for the current board- a array of length
	 *         game.getActionSize v: a float in [-1,1] that gives the value of the
	 *         current board
	 */
	public Pair<PolicyVector, Float> predict(ByteCanonicalForm gameCanonicalForm);

	/**
	 * Saves the current neural network (with its parameters) in folder/filename
	 * 
	 * @param folder
	 * @param filename
	 */
	public void saveCheckpoint(String folder, String filename);

	/**
	 * Loads parameters of the neural network from folder/filename
	 * 
	 * @param folder
	 * @param filename
	 */
	public void loadCheckpoint(String folder, String filename);
	
	public NeuralNet clone();

	/**
	 * Predict the outcome of multiple game states
	 * @param gameCanonicalForm array of canonical forms
	 * @return resulting association of policy vector and value
	 */
	public Pair<PolicyVector, Float>[] predict(List<ByteCanonicalForm> gameCanonicalForm);

}
