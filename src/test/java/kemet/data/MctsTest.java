package kemet.data;

import java.util.List;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.dataset.api.MultiDataSet;

import kemet.Options;
import kemet.ai.KemetNeuralNetwork;
import kemet.ai.KemetRecurrentNeuralNetBuilder;
import kemet.ai.TrialPlayerAI;
import kemet.model.KemetGame;
import kemet.util.Coach;
import kemet.util.Game;
import kemet.util.GameFactory;
import kemet.util.MCTS;
import kemet.util.PolicyVector;
import kemet.util.TrainExample;

class MctsTest {

	private ComputationGraph build;

	public boolean useRecurrentNetwork = true;

	@Test
	void test() throws InterruptedException {
		
		System.out.println("Board Hit count s tweak");
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.7f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 5, 0.5f, 0.7f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 4, 0.5f, 0.7f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 3, 0.5f, 0.7f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 2, 0.5f, 0.7f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 1, 0.5f, 0.7f, 1));

		System.out.println("cpuct");

		System.out.println(MCTS.getAdjustedActionValueForSearch(2.0f, 6, 0.5f, 0.7f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.5f, 6, 0.5f, 0.7f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.7f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(0.5f, 6, 0.5f, 0.7f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(0.1f, 6, 0.5f, 0.7f, 1));
		
		
		System.out.println("v from policy");

		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.7f, 0.7f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.6f, 0.7f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.7f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.4f, 0.7f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.3f, 0.7f, 1));
		
		System.out.println("v previous hit");

		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.7f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.6f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.5f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.4f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.3f, 1));


		System.out.println("s,a hit count");

		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.7f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.7f, 2));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.7f, 3));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.7f, 4));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.7f, 5));

		
		System.out.println("s,a hit count, cpuct 2");

		System.out.println(MCTS.getAdjustedActionValueForSearch(2.0f, 6, 0.5f, 0.7f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(2.0f, 6, 0.5f, 0.7f, 2));
		System.out.println(MCTS.getAdjustedActionValueForSearch(2.0f, 6, 0.5f, 0.7f, 3));
		System.out.println(MCTS.getAdjustedActionValueForSearch(2.0f, 6, 0.5f, 0.7f, 4));
		System.out.println(MCTS.getAdjustedActionValueForSearch(2.0f, 6, 0.5f, 0.7f, 5));

		
		System.out.println("additional hit with much better value");

		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.5f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(1.0f, 7, 0.5f, 0.7f, 2));

		System.out.println("additional hit with much better value, cpuct 2");

		System.out.println(MCTS.getAdjustedActionValueForSearch(2.0f, 6, 0.5f, 0.5f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(2.0f, 7, 0.5f, 0.7f, 2));

		System.out.println("additional hit with much better value, cpuct 0.5");

		System.out.println(MCTS.getAdjustedActionValueForSearch(0.0f, 6, 0.5f, 0.5f, 1));
		System.out.println(MCTS.getAdjustedActionValueForSearch(0.5f, 7, 0.5f, 0.7f, 2));

	}

	private KemetNeuralNetwork nn;

	private List<TrainExample> executeEpisode;

}
