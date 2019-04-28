package kemet.model.power;

import kemet.model.Color;
import kemet.model.Player;
import kemet.model.Power;

public class GodSpeedPower extends Power {

	private static final long serialVersionUID = 3197633913713607773L;

	public GodSpeedPower(int index, String name, byte level, Color color, String description) {
		super(index, name, level, color, description);
	}

	public void applyToPlayer(Player player) {
		player.moveCapacity += 1;
	}

}
