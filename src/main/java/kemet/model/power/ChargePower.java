package kemet.model.power;

import kemet.model.Color;
import kemet.model.Player;
import kemet.model.Power;

public class ChargePower extends Power {

	private static final long serialVersionUID = 1707677783011265057L;

	public ChargePower(int index, String name, byte level, Color color, String description) {
		super(index, name, level, color, description);
	}

	public void applyToPlayer(Player player) {
		player.attackBonus += 1;
	}

}
