package kemet.util;

import static org.junit.jupiter.api.Assertions.*;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.junit.jupiter.api.Test;

import kemet.Options;
import kemet.ai.KemetNeuralNetwork;
import kemet.ai.KemetNeuralNetBuilder;
import kemet.ai.TrialPlayerAI;
import kemet.data.TwoPlayerGame;
import kemet.model.KemetGame;
import kemet.model.action.choice.Choice;

class ArenaTest {

	@Test
	void test() {

		Options.PRINT_ARENA_GAME_END = true;
		
		TrialPlayerAI trial = new TrialPlayerAI(null, null);
		trial.print = false;
		
		KemetNeuralNetwork knn = new KemetNeuralNetwork(KemetNeuralNetBuilder.build());
		

		MCTS pmcts = new MCTS(null, knn, 1, 10);


		System.out.println("PITTING AGAINST PREVIOUS VERSION");

		Player previousPlayer = new Player() {


			@Override
			public int getActionProbability(Game game) {

				pmcts.setGame(game);
				return pmcts.getActionProbability(0).getMaximumMove();
			}

			@Override
			public void printStats() {
				pmcts.printStats();

			}
		};

		Player newPlayer = new Player() {


			@Override
			public int getActionProbability(Game game) {
				KemetGame kg = (KemetGame) game;
				
				trial.plannedChoices.clear();
				trial.game = kg;
				trial.player = kg.getPlayerByIndex(game.getNextPlayer());
				
				Choice pickAction = trial.pickAction(kg.action.getNextPlayerChoicePick());
				

				return pickAction.getIndex();
			}

			@Override
			public void printStats() {

			}

		};

		Arena arena = new Arena(previousPlayer, newPlayer, new TwoPlayerGame());

		arena.playGames(4);

		int newWinCount = arena.getNewWinCount();
		int previousWinCount = arena.getPreviousWinCount();
		int drawCount = arena.getDrawCount();
		System.out.println(String.format("NEW/PREV WINS : %d / %d ; DRAWS : %d", newWinCount, previousWinCount, drawCount));
		if (newWinCount + previousWinCount > 0
				&& newWinCount / (newWinCount + previousWinCount) < 0.55) {
			System.out.println("REJECTING NEW MODEL");
		} else {
			System.out.println("Trial AI is stronger");
		}
	
	}

}
