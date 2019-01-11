package kemet.data.state;

import kemet.model.Player;

public class PlayerWonState implements State {

	public PlayerWonState(Player player) {
		System.out.println("Player " + player.name + " won the game with " + player.victoryPoints + " victory points.");
	}

	@Override
	public State triggerNextAction() {
		return null;
	}

}
