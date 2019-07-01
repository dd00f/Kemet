package kemet.model.power;

import kemet.model.Color;
import kemet.model.Player;
import kemet.model.Power;

public class GoldTokenPower extends Power {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5522555805411677438L;

	public GoldTokenPower(int index, String name, byte level, Color color, String description) {
		super(index, name, level, color, description);
	}

	public void applyToPlayer(Player player) {
		if (player.goldTokenAvailable == false) {
			player.goldTokenAvailable = true;
			player.goldTokenUsed = false;
		}
	}

}
