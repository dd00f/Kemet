package kemet.model.power;

import kemet.model.Color;
import kemet.model.Player;
import kemet.model.Power;

public class LegionPower extends Power {


	private static final long serialVersionUID = -4294653863794215917L;

	public LegionPower(int index, String name, byte level, Color color, String description) {
		super(index, name, level, color, description);
	}

	public void applyToPlayer(Player player) {
		player.maximumArmySize = 7;
	}

}
