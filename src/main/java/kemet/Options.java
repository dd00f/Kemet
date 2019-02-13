package kemet;

public class Options {
	
	// force the game to end after a number of turns
	public static int GAME_TURN_LIMIT = 20;
	
	// option to print the probability of every option searched during MTCS evaluation
	public static final boolean PRINT_MCTS_SEARCH_PROBABILITIES = false;
	
	// option to print the every action activated during MTCS evaluation
	public static final boolean PRINT_MCTS_SEARCH_ACTIONS = false;
	
	
	// option to print the probability of every option during move selection
	public static final boolean PRINT_COACH_SEARCH_ACTIONS = false;
	
	// option to print the probability of every option during move selection
	public static final boolean PRINT_COACH_SEARCH_PROBABILITIES = false;
	
	
	// option to validate move index before execution
	public static final boolean ARENA_VALIDATE_MOVES = true;

	// option run the simulation in multiple threads
	public static final boolean SIMULATION_MULTI_THREAD = true;

	// turn on object creation caching & reuse, only useful if SIMULATION_USE_COPY_OVER_STREAMING is true
	public static final boolean USE_CACHE = false && ! SIMULATION_MULTI_THREAD;
	
	// print object creation count from the creators
	public static boolean PRINT_CREATION_COUNT = false;
	
	// print object creation interval
	public static int CREATION_PRINT_COUNT = 100;
	
	// doesn't change anything in performance based on benchmark.
	public static final boolean GAME_SKIP_RELEASE = true && ! USE_CACHE;

	// maximum action depth that simulation can go for trial AI.
	public static final int SIMULATION_MAX_SIMULATION_DEPTH = 30;

	// option to validate the simulation data after every clone 
	public static final boolean SIMULATION_VALIDATE_GAME_AFTER_CLONE = false;

	// simulate a full turn instead of a single action, leads to exponentially more choices.
	public static final boolean SIMULATE_FULL_TURN = false;

	// option to use copy instead of java streaming for replication. Copy is about 25x faster
	public static final boolean SIMULATION_USE_COPY_OVER_STREAMING  = true;

	// interval at which to print the number of simulated choices executed
	public static final long SIMULATION_CHOICE_COUNT_PRINT_INTERVAL = 1000000;

	// option to print every simulation step entry
	public static final boolean SIMULATION_PRINT_STEP_ENTRY = false;

	// option to print every simulation step exit
	public static final boolean SIMULATION_PRINT_STEP_EXIT = false;

	// end the simulation after a battle is over
	public static final boolean SIMULATION_END_AFTER_BATTLE = true;

	// validate the game between player picks.
	public static final boolean VALIDATE_GAME_BETWEEN_PICKS = false;

	// option to print statistics about the MCTS after a game is finished
	public static final boolean PRINT_MCTS_STATS = true;



}
