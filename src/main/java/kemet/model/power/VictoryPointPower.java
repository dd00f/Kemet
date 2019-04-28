package kemet.model.power;

import kemet.model.Color;
import kemet.model.Player;
import kemet.model.Power;

public class VictoryPointPower extends Power {

	private static final long serialVersionUID = -7088019599682157617L;

	public VictoryPointPower(int index, String name, byte level, Color color, String description) {
		super(index, name, level, color, description);
	}

	public void applyToPlayer(Player player) {
		player.victoryPoints += 1;
	}

}
