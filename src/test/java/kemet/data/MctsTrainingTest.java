package kemet.data;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.junit.jupiter.api.Test;

import kemet.Options;
import kemet.ai.KemetNeuralNetwork;
import kemet.ai.KemetRecurrentNeuralNetBuilder;
import kemet.ai.TrialPlayerAI;
import kemet.model.KemetGame;
import kemet.util.GameFactory;
import kemet.util.SearchPooler;
import kemet.util.StackingMCTS;

class MctsTrainingTest {

	private ComputationGraph build;

	public boolean useRecurrentNetwork = true;

	@Test
	void test() throws InterruptedException {
		
		Options.PRINT_MCTS_FULL_PROBABILITY_VECTOR = false;
		Options.PRINT_MCTS_SEARCH_ACTIONS = true;
		Options.PRINT_MCTS_SEARCH_PROBABILITIES = true;
		Options.MCTS_PREDICT_VALUE_WITH_SIMULATION = true;
		Options.MCTS_USE_MANUAL_AI = true;

		// for the size of each layer in increments of 10
		// for the number of residual layer
		// for the activation of residual layer or not
		// run 500 training session
		// calculate average error for 500 to 1000 in increments of 100
		// pick the best net

		nn = new KemetNeuralNetwork();

		if (useRecurrentNetwork) {
			build = KemetRecurrentNeuralNetBuilder.build();
			nn.model = build;
			nn.addListeners();
		}

		build = nn.model;
		
		GameFactory factory = new TwoPlayerGame();
		KemetGame createGame = (KemetGame) factory.createGame();
		createGame.printActivations = false;
		
		// play a bunch of moves
		for( int i=0;i<50;++i ) {
			
			TrialPlayerAI ai = new TrialPlayerAI(createGame.getPlayerByIndex(createGame.getNextPlayer()), createGame);
			ai.pickActionAndActivate(createGame.action.getNextPlayerChoicePick());
			
		}
		
		createGame.printActivations = true;
		createGame.printDescribeGame();
		

		SearchPooler pooler = new SearchPooler(nn);
		StackingMCTS mcts = new StackingMCTS(createGame, pooler, 0.5f);
		
		mcts.setCpuct(0.5f);
		
		//PolicyVector actionProbability = mcts.getActionProbability(0.5f, 500);
		
		



	}

	private KemetNeuralNetwork nn;

}
