package kemet.data.choice;

import kemet.data.state.State;
import kemet.model.Game;
import kemet.model.Player;

public class PrayerChoice extends PlayerChoice {

	public PrayerChoice(Game game, Player player, byte row) {
		super(game, player);
		this.row = row;
	}

	public byte increasedPower = 2;
	public byte row;

	@Override
	public State choiceActivate() {
		player.modifyPrayerPoints(increasedPower, "prayer action");
		if (row == 2) {
			player.rowTwoPrayUsed = true;
		} else {
			player.rowThreePrayUsed = true;
		}
		player.actionTokenLeft--;
		return null;
	}

	public String describe() {

		return "Prayer action, row " + row + ", with " + player.getPrayerPoints() + " prayer points, pray for "
				+ increasedPower + " prayer points";

	}

}
