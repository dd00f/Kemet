package kemet.util;

import kemet.Options;


public class Arena {
	private Player player2;
	private Player player1;
	private Game game;

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

	public Arena(Player player1, Player player2, Game game) {
		this.player1 = player1;
		this.player2 = player2;
		this.game = game;

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

		int currentPlayerIndex = game.getNextPlayer();
		int it = 0;
		while (game.getGameEnded(currentPlayerIndex) == 0) {
			it++;
			if (verbose) {
				print("Turn " + it + "Player " + currentPlayerIndex);
				game.describeGame();
			}

			Player currentPlayer = player2;
			if (currentPlayerIndex == 1) {
				currentPlayer = player1;
			}
			int actionIndex = currentPlayer.getActionProbability(game.getCanonicalForm(currentPlayerIndex));

			if (Options.ARENA_VALIDATE_MOVES) {
				boolean[] validMoves = game.getValidMoves();
				if (validMoves[actionIndex] == false) {
					print("Invalid move selected " + actionIndex);
					System.exit(-1);
				}

			}

			game.getNextState(currentPlayerIndex, actionIndex);
			currentPlayerIndex = game.getNextPlayer();

		}

		if (verbose) {
			String print = "Game over : Turn " + it + " result " + game.getGameEnded(1);
			print(print);
			game.describeGame();
		}

		return game.getGameEnded(1);
	}

	private void print(String print) {
		System.out.println(print);
	}

	public int getNewWinCount() {
		throw new UnsupportedOperationException();
	}

	public int getPreviousWinCount() {
		throw new UnsupportedOperationException();
	}

	public int getDrawCount() {
		throw new UnsupportedOperationException();
	}

	public int oneWon = 0;
	public int twoWon = 0;
	public int draw = 0;

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
		boolean swapped = false;

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

			print(eps + "/" + maxeps + " | eps time " + epsDuration + "ms | total " + epsTime + "ms | ETA "
					+ timeLeft + "ms");

			currentStart = endTime;
			Player swap = player1;
			player1 = player2;
			player2 = swap;
			swapped = !swapped;

		}

	}
}
