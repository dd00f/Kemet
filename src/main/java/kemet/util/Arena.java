package kemet.util;

import kemet.Options;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Arena {
	private Player player2;
	private Player player1;
	private GameFactory gameFactory;

	/**
	 * An Arena class where any 2 agents can be pit against each other.
	 * 
	 * Input: player 1,2: two functions that takes board as input, return action
	 * game: Game object display: a function that takes board as input and prints it
	 * (e.g. display in othello/OthelloGame). Is necessary for verbose mode.
	 * 
	 * see othello/OthelloPlayers.py for an example. See pit.py for pitting human
	 * players/other baselines with each other.
	 *
	 *
	 */

	public Arena(Player player1, Player player2, GameFactory gameFactory) {
		this.player1 = player1;
		this.player2 = player2;
		this.gameFactory = gameFactory;

	}

	/**
	 * Executes one episode of a game.
	 * 
	 * Returns: either winner: player who won the game (1 if player1, -1 if player2)
	 * or draw result returned from the game that is neither 1, -1, nor 0.
	 * 
	 * @param verbose
	 */
	public int playGame(boolean verbose) {

		Game game = gameFactory.createGame();
		game.setPrintActivations(Options.PRINT_ARENA_GAME_EVENTS);
		if(swapped) {
			game.setPlayerName(0, "new");
			game.setPlayerName(1, "old");
		}
		else {
			game.setPlayerName(0, "old");
			game.setPlayerName(1, "new");
		}
		
		int currentPlayerIndex = game.getNextPlayer();
		int it = 0;
		while (game.getGameEnded(currentPlayerIndex) == 0) {
			it++;
			if (verbose) {
				log.debug("Turn {} Player {}", it, currentPlayerIndex);
				game.printDescribeGame();
			}

			Player currentPlayer = player2;
			if( swapped )
			{
				if (currentPlayerIndex == 1) {
					currentPlayer = player1;
				}
			}
			else {
				if (currentPlayerIndex == 0) {
					currentPlayer = player1;
				}
			}
			

			
			int actionIndex = currentPlayer.getActionProbability(game);

			if (Options.ARENA_VALIDATE_MOVES) {
				boolean[] validMoves = game.getValidMoves();
				if (validMoves[actionIndex] == false) {
					log.error("Invalid move selected {}", actionIndex);
					game.printChoiceList();
					actionIndex = Utilities.getFirstValidMoveIndex(game.getValidMoves());
				}

			}

			game.activateAction(currentPlayerIndex, actionIndex);
			currentPlayerIndex = game.getNextPlayer();

		}

		if (Options.PRINT_ARENA_GAME_END) {
			log.info( "Game over : Turn " + it + " result " + game.getGameEnded(1) + " swapped " + swapped );
			game.setPrintActivations(true);
			game.printDescribeGame();
			
			player1.printStats();
			
			player2.printStats();
		}

		return game.getGameEnded(0);
	}


	public int getNewWinCount() {
		return twoWon;
	}

	public int getPreviousWinCount() {
		return oneWon;
	}

	public int getDrawCount() {
		return draw;
	}

	public int oneWon = 0;
	public int twoWon = 0;
	public int draw = 0;
	private boolean swapped;

	/**
	 * Plays num games in which player1 starts num/2 games and player2 starts num/2
	 * games.
	 * 
	 * Returns: oneWon: games won by player1 twoWon: games won by player2 draws:
	 * games won by nobody
	 * 
	 * @param arenaCompare
	 */
	public void playGames(int arenaCompare) {
		long epsTime = 0;
		int eps = 0;
		long currentStart = System.currentTimeMillis();
		long endTime = 0;
		int maxeps = arenaCompare;
		swapped = false;

		oneWon = 0;
		twoWon = 0;
		draw = 0;

		for (int i = 0; i < maxeps; i++) {
			int playGame = playGame(false);
			if (playGame == 1) {
				if (swapped) {
					twoWon++;
				} else {
					oneWon++;
				}
			} else if (playGame == -1) {
				if (swapped) {
					oneWon++;
				} else {
					twoWon++;
				}
			} else {
				draw++;
			}

			eps++;
			endTime = System.currentTimeMillis();
			long epsDuration = endTime - currentStart;
			epsTime += epsDuration;
			long averageTime = epsTime / eps;
			long timeLeft = averageTime * (maxeps - eps);

			log.info(eps + "/" + maxeps + " | eps time " + epsDuration + "ms | total " + epsTime + "ms | ETA "
					+ timeLeft + "ms");

			currentStart = endTime;

			swapped = !swapped;

		}

	}
}
