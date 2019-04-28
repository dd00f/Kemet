package kemet.model.power;

import kemet.model.Color;
import kemet.model.Player;
import kemet.model.Power;

public class BladesOfNeithPower extends Power {

	private static final long serialVersionUID = 5195490397257752923L;

	public BladesOfNeithPower(int index, String name, byte level, Color color, String description) {
		super(index, name, level, color, description);
	}

	public void applyToPlayer(Player player) {
		player.strengthBonus += 1;
	}

}
