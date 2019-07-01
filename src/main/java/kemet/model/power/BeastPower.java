package kemet.model.power;

import kemet.model.Beast;
import kemet.model.Color;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Power;
import kemet.model.action.Action;
import kemet.model.action.BeastRecruitAction;

public class BeastPower extends Power {

	private static final long serialVersionUID = 5078625695268008189L;
	private final Beast beast;

	public BeastPower(int index, byte level, Color color, Beast beast) {
		super(index, beast.name, level, color, beast.description);
		this.beast = beast;
	}

	public void applyToPlayer(Player player) {
		player.availableBeasts.add(beast);
	}
	
	@Override
	public Action createNextAction(Player player, Action parent, KemetGame game) {
		
		BeastRecruitAction choice = BeastRecruitAction.create(game, player, parent, beast);
		
		return choice;
	}

}
