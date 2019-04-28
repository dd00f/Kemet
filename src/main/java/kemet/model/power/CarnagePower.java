package kemet.model.power;

import kemet.model.Color;
import kemet.model.Player;
import kemet.model.Power;

public class CarnagePower extends Power {

	private static final long serialVersionUID = -3622299886204246257L;

	public CarnagePower(int index, String name, byte level, Color color, String description) {
		super(index, name, level, color, description);
	}

	public void applyToPlayer(Player player) {
		player.damageBonus += 1;
	}

}
