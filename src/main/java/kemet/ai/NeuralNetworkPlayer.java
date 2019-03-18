package kemet.ai;

import kemet.Options;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.action.PlayerChoicePick;
import kemet.model.action.choice.Choice;
import kemet.util.PolicyVector;
import kemet.util.SearchPooler;
import kemet.util.StackingMCTS;

public class NeuralNetworkPlayer extends PlayerActor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2564094457500391132L;

	private StackingMCTS mcts;

	private SearchPooler search;

	private KemetNeuralNetwork network;

	private int searchCount = Options.COACH_MCTS_SIMULATION_COUNT_PER_MOVE;

	private float cpuct = 1.0f;

	/**
	 * Set to 0.0 to pick max probability action, 1 randomize based on calculated
	 * probabilities.
	 */
	private float temperature = 1f;

	public NeuralNetworkPlayer(Player player, KemetGame game, String folder, String filename) {
		super(player, game);
		network = new KemetNeuralNetwork();
		network.loadCheckpoint(folder, filename);

		search = new SearchPooler(network);
		mcts = new StackingMCTS(game, search, cpuct);

	}

	public void pickActionAndActivate(PlayerChoicePick pick) {
		PolicyVector actionProbability = mcts.getActionProbability(temperature, searchCount);

		mcts.printCurrentBoardProbability();

		int pickBestAction = actionProbability.pickBestAction();

		actionProbability.printActionProbabilities(game);
		game.activateAction(player.index, pickBestAction);
		
		mcts.cleanupOldCycles();
		mcts.incrementCycle();
		search.cleanup();
	}

	@Override
	public Choice pickAction(PlayerChoicePick pick) {

		throw new UnsupportedOperationException();

	}

}
