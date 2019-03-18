package kemet.data;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import kemet.ai.KemetNeuralNetwork;
import kemet.model.KemetGame;
import kemet.util.ByteCanonicalForm;
import kemet.util.Coach;
import kemet.util.GameFactory;
import kemet.util.NeuralNet;
import kemet.util.PolicyVector;
import kemet.util.SearchPooler;
import kemet.util.StackingMCTS;
import kemet.util.TrainExample;

class KemetLearnTest {

	@Test
	void test() {

		GameFactory gameFactory = new TwoPlayerGame();
		NeuralNet neuralNet = new KemetNeuralNetwork();
		Coach coach = new Coach(gameFactory, neuralNet);

		neuralNet.saveCheckpoint("./temp", "test.nn");
		neuralNet.loadCheckpoint("./temp", "test.nn");

		int simulationPerMove = 20;
		float cpuct = 1f;


		KemetGame game = (KemetGame) gameFactory.createGame();
		game.setPrintActivations(true);

		int nextPlayer = game.getNextPlayer();

		// At temperature 1, do more exploration
		int temperature = 1;

		game.printChoiceList();

		ByteCanonicalForm canonicalBoard = game.getCanonicalForm(nextPlayer);
		// String stringRepresentation = game.stringRepresentation();

//		PolicyVector actionProbabilityPi = mcts.getActionProbability(temperature);




		PolicyVector newActionProbabilityPi = null;
		PolicyVector NNETPredict2 = null;
		for (int i = 0; i < 1; ++i) 
		{

			List<TrainExample> trainExamples = new ArrayList<>();

			SearchPooler pooler = new SearchPooler(neuralNet);
			StackingMCTS newmcts = new StackingMCTS(game, pooler, cpuct);
			newmcts.setGame(game);
			
			PolicyVector NNETPredict = neuralNet.predict(canonicalBoard).getLeft();
			System.out.println("NNET prediction policy 26 = " + NNETPredict.vector[26] + " at 27 = " + NNETPredict.vector[27]);
			NNETPredict.printProbabilityVector();

			newActionProbabilityPi = newmcts.getActionProbability(temperature, simulationPerMove);
			// newmcts.choiceValuePredictionForBoardPs.get(stringRepresentation).printProbabilityVector();

			System.out.println("Training policy");
			newActionProbabilityPi.printProbabilityVector();
			
//			NNETPredict.vector[26] = newActionProbabilityPi.vector[26];
//			NNETPredict.vector[27] = newActionProbabilityPi.vector[27];

			TrainExample trainExample = new TrainExample(canonicalBoard, nextPlayer, newActionProbabilityPi, 0,
					game.getValidMoves());
			trainExamples.add(trainExample);

			if (newActionProbabilityPi.vector[27] > newActionProbabilityPi.vector[26]) {
				trainExample.valueV = 1;
			} else {
				trainExample.valueV = -1;
			}

			for (int j = 0;j < 100; ++j) 
			{
				neuralNet.train(trainExamples);
				NNETPredict2 = neuralNet.predict(canonicalBoard).getLeft();
				System.out.println(j + " NNET prediction policy 26 = " + NNETPredict2.vector[26] + " at 27 = " + NNETPredict2.vector[27]);
				
			}
			
			NNETPredict2.printProbabilityVector();
			
			
		}

	}

}
