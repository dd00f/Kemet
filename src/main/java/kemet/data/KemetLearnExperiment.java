package kemet.data;

import kemet.Options;
import kemet.ai.KemetNeuralNetBuilder;
import kemet.ai.KemetNeuralNetwork;
import kemet.ai.KemetRecurrentNeuralNetBuilder;
import kemet.util.Coach;
import kemet.util.GameFactory;
import kemet.util.NeuralNet;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class KemetLearnExperiment {

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
		Options.COACH_ARENA_COMPARE_MATCH_COUNT = 100;
		// Options.COACH_HIGH_EXPLORATION_MOVE_COUNT = 600;
		Options.COACH_MCTS_SIMULATION_COUNT_PER_MOVE = 30;
		Options.COACH_PRINT_GAME_AFTER_SELF_TRAINING = false;
		Options.COACH_MAX_TRAINING_LIST_LENGTH = 500000;
		
		
		
		Options.USE_RECURRENT_NEURAL_NET = false;
		
		
		Coach.PLAY_PRINT_INTERVAL = 2000;

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

		boolean loadModel = false;
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

			learn(coach);
		}
	}

	/**
	 * Performs numIters iterations with numEps episodes of this.play in each
	 * iteration. After every iteration, it retrains neural network with examples in
	 * trainExamples (which has a maximum length of maxlenofQueue). It then pits
	 * the new neural network against the old one and accepts it only if it wins >=
	 * updateThreshold fraction of games.
	 * 
	 * @return
	 */
	public static void learn(Coach coach) {

		long totalTime = 0;
		long end = System.currentTimeMillis();
		long start = end;

		int numIters = 10000;

		for (int j = 1; j < numIters + 1; j++) {

			if (coach.trainExamplesHistory.size() > Options.COACH_MAX_TRAINING_LIST_LENGTH) {
				break;
			}

			coach.runSelfTrainingStacking();

			// bookkeeping + plot progress
			long now = System.currentTimeMillis();
			// eps_time.update(now - end);

			end = now;
			long duration = end - start;
			totalTime += duration;
			long average = totalTime / j;
			long timeLeft = average * (numIters - j);

			String durationStr = Coach.formatDuration(duration);
			String totalTimeStr = Coach.formatDuration(totalTime);
			String timeLeftStr = Coach.formatDuration(timeLeft);

			log.info(j + "/" + numIters + " | coach learn time " + durationStr + " | total " + totalTimeStr + " | ETA "
					+ timeLeftStr + " train list size " + coach.trainExamplesHistory.size());

			start = now;
		}

		coach.saveTrainExamples(coach.getBestExampleFileName());
		coach.nnet.saveCheckpoint(coach.checkpoint, Coach.TEMPORARY_NNET_SAVE_POINT_NAME);

		KemetRecurrentNeuralNetBuilder.NESTEROV_MOMENTUM = 0.9;
		runTrainingExerciseNestrovMomentum(coach);

//		KemetRecurrentNeuralNetBuilder.NESTEROV_MOMENTUM = 0.4;
//		runTrainingExerciseNestrovMomentum(coach);
//
//		KemetRecurrentNeuralNetBuilder.NESTEROV_MOMENTUM = 0.15;
//		runTrainingExerciseNestrovMomentum(coach);

	}

	public static void runTrainingExerciseNestrovMomentum(Coach coach) {
		KemetRecurrentNeuralNetBuilder.NESTEROV_LEARN_RATE = 0.006;

		runTrainingExerciseNestrovLearnRate(coach);
//
//		KemetRecurrentNeuralNetBuilder.NESTEROV_LEARN_RATE = 0.003;
//
//		runTrainingExerciseNestrovLearnRate(coach);

//		KemetRecurrentNeuralNetBuilder.NESTEROV_LEARN_RATE = 0.001;
//
//		runTrainingExerciseNestrovLearnRate(coach);
	}

	public static void runTrainingExerciseNestrovLearnRate(Coach coach) {

//		Options.NEURAL_NET_TRAIN_EPOCH = 5;
//		runTrainingExerciseNeuralLayerSize(coach);

		Options.NEURAL_NET_TRAIN_EPOCH = 10;
		runTrainingExerciseNeuralLayerSize(coach);
//
//		Options.NEURAL_NET_TRAIN_EPOCH = 20;
//		runTrainingExerciseNeuralLayerSize(coach);

	}

	
	public static void runTrainingExerciseNeuralLayerSize(Coach coach) {

//		KemetRecurrentNeuralNetBuilder.LAYER_SIZE = KemetRecurrentNeuralNetBuilder.INPUT_SIZE / 2;
//		runTrainingExerciseLayerCount(coach);
		
//		KemetRecurrentNeuralNetBuilder.LAYER_SIZE = KemetRecurrentNeuralNetBuilder.INPUT_SIZE * 1;
//		runTrainingExerciseLayerCount(coach);

//		KemetRecurrentNeuralNetBuilder.LAYER_SIZE = KemetRecurrentNeuralNetBuilder.INPUT_SIZE * 2;
//		runTrainingExerciseLayerCount(coach);

//		KemetRecurrentNeuralNetBuilder.LAYER_SIZE = KemetRecurrentNeuralNetBuilder.INPUT_SIZE * 4;
//		runTrainingExerciseLayerCount(coach);

		KemetNeuralNetBuilder.LAYER_SIZE = KemetRecurrentNeuralNetBuilder.INPUT_SIZE * 2;
		KemetRecurrentNeuralNetBuilder.LAYER_SIZE = KemetRecurrentNeuralNetBuilder.INPUT_SIZE * 8;
		runTrainingExerciseLayerCount(coach);
	}
	
	public static void runTrainingExerciseLayerCount(Coach coach) {

//		KemetRecurrentNeuralNetBuilder.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT = 0;
//		runTrainingExercise(coach);
		
//		KemetRecurrentNeuralNetBuilder.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT = 1;
//		runTrainingExercise(coach);

//		KemetRecurrentNeuralNetBuilder.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT = 2;
//		runTrainingExercise(coach);
		
		KemetNeuralNetBuilder.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT = 3;
		KemetRecurrentNeuralNetBuilder.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT = 3;
		runTrainingExercise(coach);


	}
	
	public static void runTrainingExercise(Coach coach) {
		coach.nnet = new KemetNeuralNetwork();
		coach.nnet.train(coach.trainExamplesHistory);

		coach.initiateArena(Options.COACH_ARENA_COMPARE_MATCH_COUNT);
		coach.arena.runGames();
		coach.arena.pickArenaWinner(1);
		log.info("Training result with NESTEROV_MOMENTUM={} NESTEROV_LEARN_RATE={} NEURAL_NET_TRAIN_EPOCH={}",
				KemetRecurrentNeuralNetBuilder.NESTEROV_MOMENTUM, KemetRecurrentNeuralNetBuilder.NESTEROV_LEARN_RATE,
				Options.NEURAL_NET_TRAIN_EPOCH);
	}

}
