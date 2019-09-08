package kemet.data;

import kemet.Options;
import kemet.ai.KemetNeuralNetwork;
import kemet.ai.KemetRecurrentNeuralNetBuilder;
import kemet.util.Coach;
import kemet.util.GameFactory;
import kemet.util.NeuralNet;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class KemetLearn {

	public static void main(String[] args) {
		log.debug("Starting Kemet Learn");

// original values
//		KemetRecurrentNeuralNetBuilder.NESTEROV_MOMENTUM = 0.9;
//		KemetRecurrentNeuralNetBuilder.NESTEROV_LEARN_RATE = 0.006;
//		
		KemetRecurrentNeuralNetBuilder.NESTEROV_MOMENTUM = 0.4;
		KemetRecurrentNeuralNetBuilder.NESTEROV_LEARN_RATE = 0.003;

//		KemetRecurrentNeuralNetBuilder.NESTEROV_MOMENTUM = 0.15;
//		KemetRecurrentNeuralNetBuilder.NESTEROV_LEARN_RATE = 0.001;

//		int TODO_DISABLE;

		boolean pitVsBasicNet = false;
		// Options.PRINT_ARENA_GAME_EVENTS = false;
		// Options.PRINT_ARENA_GAME_END = false;
		Options.COACH_ARENA_COMPARE_MATCH_COUNT = 10;
		// Options.COACH_HIGH_EXPLORATION_MOVE_COUNT = 600;
		Options.COACH_MCTS_SIMULATION_COUNT_PER_MOVE = 10;
		Options.COACH_PRINT_GAME_AFTER_SELF_TRAINING = true;

		// Options.COACH_BLIND_LEARN = false;
		// Options.VALIDATE_GAME_BETWEEN_PICKS = false;
//		Options.VALIDATE_PLAYER_CHOICE_PICK_INDEX = true;
//		Options.VALIDATE_POOLED_GAMES = true;
//		Options.ARENA_VALIDATE_MOVES = true;
//		Options.COACH_VALIDATE_PLAYER_NAME = true;
//		Options.MCTS_VALIDATE_MOVE_FOR_BOARD = true;
//		Options.SIMULATION_VALIDATE_GAME_AFTER_CLONE = true;

		GameFactory gameFactory = new TwoPlayerGame();

		NeuralNet neuralNet = null;
		neuralNet = new KemetNeuralNetwork();

		boolean loadModel = true;
		boolean loadTrainingExamples = true;
		if (loadModel) {
//			log.info("loading previous best neural network");
			neuralNet.loadCheckpoint("./temp", "best.pth.tar");
		}

		else {

			neuralNet.saveCheckpoint("./temp", "initial.nn");
			neuralNet.loadCheckpoint("./temp", "initial.nn");

		}

		Coach coach = new Coach(gameFactory, neuralNet);

//	    int TODO_REVERT_THIS;

		if (pitVsBasicNet) {
			coach.pnet = new KemetNeuralNetwork();
			coach.pitBestVsBasic();
		} else {
			if (loadTrainingExamples) {
				coach.loadTrainExamples();
			}

			coach.learn();
		}
	}

}
