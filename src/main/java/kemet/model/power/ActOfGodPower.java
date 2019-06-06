package kemet.model.power;

import kemet.model.Color;
import kemet.model.Player;
import kemet.model.Power;

public class ActOfGodPower extends Power {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3787823629746734990L;

	public ActOfGodPower(int index, String name, byte level, Color color, String description) {
		super(index, name, level, color, description);
	}

	public void applyToPlayer(Player player) {
		player.silverTokenAvailable = true;
		player.silverTokenUsed = false;
	}

}
