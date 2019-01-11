package kemet.data.state;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import kemet.model.Game;
import kemet.model.Player;

public class GameState implements State {

	public Game game;

	@Override
	public State triggerNextAction() {

		InitializationPyramidState initialState = new InitializationPyramidState();
		initialState.game = game;
		initialState.triggerNextAction();

		InitializationRecruitState initializationRecruitState = new InitializationRecruitState();
		initializationRecruitState.game = game;
		initializationRecruitState.triggerNextAction();

		int i = 0;
		// for (int i = 1; i <= 200; ++i) {
		while( true ) {

			i++;
			if (getWinningPlayer() != null) {
				break;
			}

			System.out.println("Turn " + i + " Starting");

			NightState night = new NightState();
			night.game = game;
			night.triggerNextAction();

			if (i == 1) {
				// trigger dawn phase
				System.out.println("Skipping turn 1 dawn phase");
			} else {
				DawnState dawn = new DawnState();
				dawn.game = game;
				dawn.triggerNextAction();
			}
			
			DayState day = new DayState();
			day.game = game;
			day.triggerNextAction();

			System.out.println("Turn " + i + " Ending");
			
			game.describeGame();
		}
		
		// force a winner
		findWinner();

		return null;
	}

	private Player getWinningPlayer() {
		if (game.winner == null) {
			if (game.victoryConditionTriggered == true) {
				findWinner();
			}
		}
		return game.winner;
	}

	private void findWinner() {
		List<Player> playerList = new ArrayList<>(game.playerByInitiativeList);
		byte vp = playerList.stream().max(Comparator.comparing(ft -> ft.victoryPoints)).get().victoryPoints;
		List<Player> filter = playerList.stream().filter(player -> player.victoryPoints >= vp).collect(Collectors.toList());

		long count = filter.size();
		if (count > 1) {
			System.out.println("Tie for VP " + vp);

			byte battlePoint = filter.stream().max(Comparator.comparing(ft -> ft.battlePoints)).get().battlePoints;
			filter = filter.stream().filter(player -> player.battlePoints >= battlePoint).collect(Collectors.toList());

			count = filter.size();
			if (count > 1) {
				System.out.println("Tie for Battle VP " + battlePoint);

				for (Player orderedPlayer : game.playerByInitiativeList) {
					if (filter.contains(orderedPlayer)) {
						game.winner = orderedPlayer;
						System.out.println(game.winner.name + " won at the end of day. Earliest to act");
						break;
					}
				}
			} else {
				game.winner = filter.iterator().next();
				System.out.println(game.winner.name + " won at the end of day. Most Battle VP " + vp);
			}
		} else {
			game.winner = filter.iterator().next();
			System.out.println(game.winner.name + " won at the end of day. Most VP " + vp);

		}
	}

}
