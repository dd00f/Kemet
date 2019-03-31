package kemet;

public class Options {

	public static boolean COACH_PRINT_GAME_AFTER_SELF_TRAINING = false;

	// when set to false, epoch will be executed by the dl4j api without shuffling 
	// inputs first.
	public static boolean NEURAL_NET_SHUFFLE_BETWEEN_EPOCH = false;

	public static int COACH_MAX_TRAINING_LIST_LENGTH = 200000;

	public static boolean VALIDATE_POOLED_GAMES = false;

	public static boolean MCTS_PREDICT_VALUE_WITH_SIMULATION = false;

	public static boolean MCTS_VALIDATE_MOVE_FOR_BOARD = false;

	public static boolean VALIDATE_PLAYER_CHOICE_PICK_INDEX = false;
	
	public static boolean USE_RECURRENT_NEURAL_NET = true;


	static {
		// keep this as long as there are TODO notice.
		// int junk;
	}

	/**
	 * Number of steps at the beginning of a training game where the algorithm will
	 * try to explore more options. Used to end games quickly if there is a clear
	 * winner after X moves on average.
	 */
	public static int COACH_HIGH_EXPLORATION_MOVE_COUNT = 300;

	/**
	 * Number of iterations to coach. In each iteration, games are played and then
	 * used to train the neural network.
	 */
	public static int COACH_NUMBER_OF_NN_TRAINING_ITERATIONS = 1000;


	// Number of turns before the game ends.
	public static int GAME_TURN_LIMIT = 15;

	// Number of matches used to compare 2 generations of the neural network
	public static int COACH_ARENA_COMPARE_MATCH_COUNT = 50;

	// number of games to play when training the neural network
	public static int COACH_NEURAL_NETWORK_TRAIN_GAME_COUNT = 100;

	// number of move simulations to do in MCTS between moves while coaching a
	// neural network
	public static int COACH_MCTS_SIMULATION_COUNT_PER_MOVE = 1000;
	
	// number of times to run the test data to fit the neural network
	public static int NEURAL_NET_TRAIN_EPOCH = 30;

	// number of actions to remember in the game
	public static int GAME_TRACK_MAX_ACTION_COUNT = 1024;

	
	// option to print the probability of every option searched during MTCS
	// evaluation
	public static boolean PRINT_MCTS_SEARCH_PROBABILITIES = false;

	// option to print the every action activated during MTCS evaluation
	public static boolean PRINT_MCTS_SEARCH_ACTIONS = false;

	// option to print the probability of every option during move selection
	public static boolean PRINT_COACH_SEARCH_ACTIONS = false;

	// option to print the probability of every option during move selection
	public static boolean PRINT_COACH_SEARCH_PROBABILITIES = false;

	// option to validate move index before execution
	public static boolean ARENA_VALIDATE_MOVES = false;

	// option run the simulation in multiple threads
	public static boolean SIMULATION_MULTI_THREAD = true;

	// turn on object creation caching & reuse, only useful if
	// SIMULATION_USE_COPY_OVER_STREAMING is true
	@SuppressWarnings("unused")
	public static boolean USE_CACHE = false && !SIMULATION_MULTI_THREAD;

	// print object creation count from the creators
	public static boolean PRINT_CREATION_COUNT = false;

	// print object creation interval
	public static int CREATION_PRINT_COUNT = 100;

	// doesn't change anything in performance based on benchmark.
	public static boolean GAME_SKIP_RELEASE = true && !USE_CACHE;

	// maximum action depth that simulation can go for trial AI.
	public static int SIMULATION_MAX_SIMULATION_DEPTH = 30;

	// option to validate the simulation data after every clone
	public static boolean SIMULATION_VALIDATE_GAME_AFTER_CLONE = false;

	// simulate a full turn instead of a single action, leads to exponentially more
	// choices.
	public static boolean SIMULATE_FULL_TURN = false;

	// option to use copy instead of java streaming for replication. Copy is about
	// 25x faster
	public static boolean SIMULATION_USE_COPY_OVER_STREAMING = true;

	// interval at which to print the number of simulated choices executed
	public static long SIMULATION_CHOICE_COUNT_PRINT_INTERVAL = 1000000;

	// option to print every simulation step entry
	public static boolean SIMULATION_PRINT_STEP_ENTRY = false;

	// option to print every simulation step exit
	public static boolean SIMULATION_PRINT_STEP_EXIT = false;

	// end the simulation after a battle is over
	public static boolean SIMULATION_END_AFTER_BATTLE = true;

	// validate the game between player picks.
	public static boolean VALIDATE_GAME_BETWEEN_PICKS = false;

	// option to print statistics about the MCTS after a game is finished
	public static boolean PRINT_MCTS_STATS = true;

	public static float COACH_CPUCT = 1;

	public static boolean PRINT_MCTS_FULL_PROBABILITY_VECTOR = false;


	public static boolean PRINT_ARENA_GAME_EVENTS = false;

	public static boolean PRINT_ARENA_GAME_END = false;

	public static boolean COACH_VALIDATE_PLAYER_NAME = false;


	public static boolean COACH_USE_MANUAL_AI = false;

	
	public static boolean COACH_USE_STACKING_MCTS = true;

	
	public static boolean MCTS_USE_MANUAL_AI = false;
	
	public static boolean MCTS_PREPARE_PREDICTIONS = false;
	
	public static int MCTS_PREPARE_PREDICTION_DEPTH = 1;

	public static int MCTS_PREPARE_PREDICTION_MAX_DEPTH = 1;

}
