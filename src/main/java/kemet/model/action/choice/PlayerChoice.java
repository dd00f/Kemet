package kemet.model.action.choice;

import kemet.model.KemetGame;
import kemet.model.Player;

public abstract class PlayerChoice implements Choice {

	public KemetGame game;
	public Player player;

	public PlayerChoice(KemetGame game, Player player) {
		this.game = game;
		this.player = player;
	}

	@Override
	public final void activate() {

		if (game.printActivations) {
			game.printEvent("Activated : " + toString());
		}

		choiceActivate();
	}

	public abstract void choiceActivate();

	@Override
	public String toString() {
		return "Player \"" + player.name + "\" : " + describe();
	}

	public Player getPlayer() {
		return player;
	}
}
