package kemet.model.power;

import kemet.model.Color;
import kemet.model.Player;
import kemet.model.Power;

public class BestialFuryPower extends Power {

	private static final long serialVersionUID = 5078625695268008189L;

	public BestialFuryPower(int index, String name, byte level, Color color, String description) {
		super(index, name, level, color, description);
	}

	public void applyToPlayer(Player player) {
		player.strengthBonus += 1;
		player.damageBonus += 1;
		player.moveCapacity += 1;
	}

}
