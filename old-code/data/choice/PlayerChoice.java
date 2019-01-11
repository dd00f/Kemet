package kemet.data.choice;

import kemet.data.state.State;
import kemet.model.Game;
import kemet.model.Player;

public abstract class PlayerChoice implements Choice {

	public Game game;
	public Player player;

	public PlayerChoice(Game game, Player player) {
		this.game = game;
		this.player = player;
	}

	@Override
	public final State activate() {

		System.out.println("Activated : " + toString());
		
		// TODO
		int TODO_return_null_on_choiceActivate;

		return choiceActivate();
	}

	public abstract State choiceActivate();

	@Override
	public String toString() {
		return "Player \"" + player.name + "\" : " +  describe();
	}
	
	public Player getPlayer()
	{
	    return player;
	}
}
