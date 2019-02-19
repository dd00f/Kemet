package kemet.ai;

import java.io.Serializable;
import java.util.List;

import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.action.PlayerChoicePick;
import kemet.model.action.choice.Choice;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class PlayerActor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4607835022667859217L;
	public Player player;
	public KemetGame game;
	public boolean print = true;

	public PlayerActor(Player player, KemetGame game) {
		this.player = player;
		this.game = game;

	}

	public void printChoiceList(List<kemet.model.action.choice.Choice> choiceList) {
		printPlayerStatus();

		PlayerChoicePick.print(choiceList);
	}

	public void printPlayerStatus() {
		if (print) {
			log.info("");
			log.info(player.name + " with " + player.actionTokenLeft + " actions left and "
					+ player.getPrayerPoints() + " prayer points. Pick Choice : ");
		}
	}

	public abstract Choice pickAction(PlayerChoicePick pick);

	public void pickActionAndActivate(PlayerChoicePick pick) {
		pickAction(pick).activate();
	}

}
