package kemet.model.power;

import kemet.model.Beast;
import kemet.model.Color;
import kemet.model.Player;

public class VictoryPointBeastPower extends BeastPower {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2362781280102688654L;

	public VictoryPointBeastPower(int index, byte level, Color color, Beast beast) {
		super(index, level, color, beast);
	}

	public void applyToPlayer(Player player) {
		player.victoryPoints += 1;
		super.applyToPlayer(player);
	}

}
