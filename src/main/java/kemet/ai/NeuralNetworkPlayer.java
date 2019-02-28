package kemet.ai;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.action.PlayerChoicePick;
import kemet.model.action.choice.Choice;
import kemet.util.MCTS;
import kemet.util.PolicyVector;

public class NeuralNetworkPlayer extends PlayerActor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2564094457500391132L;

	// Logger initialization example
	private static final Logger LOGGER = LogManager.getLogger(NeuralNetworkPlayer.class);


	private boolean printChoices = false;

	private MCTS mcts;

	private KemetNeuralNetwork network;
	
	

	public NeuralNetworkPlayer(Player player, KemetGame game, String folder, String filename) {
		super(player, game);
		network = new KemetNeuralNetwork();
		network.loadCheckpoint(folder, filename);
		mcts = new MCTS(game, network, 1, 2000);
		
	}

	
	public void pickActionAndActivate(PlayerChoicePick pick) {
		PolicyVector actionProbability = mcts.getActionProbability(1);
		int pickBestAction = actionProbability.pickBestAction();
		
		actionProbability.printActionProbabilities(game);
		game.activateAction(player.index, pickBestAction);
	}
	
	@Override
	public Choice pickAction(PlayerChoicePick pick) {

		throw new UnsupportedOperationException();

	}


}
