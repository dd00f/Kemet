package kemet.model.power;

import kemet.model.Color;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Power;
import kemet.model.action.Action;
import kemet.model.action.RecruitAction;

public class MercenariesPower extends Power {


	private static final long serialVersionUID = -4294653863794215917L;

	public MercenariesPower(int index, String name, byte level, Color color, String description) {
		super(index, name, level, color, description);
	}

	public void applyToPlayer(Player player) {
		player.availableArmyTokens += 3;
	}

	public Action createNextAction(Player player, Action parent, KemetGame game) {
		
		RecruitAction action = RecruitAction.create(game, player, parent);
		action.allowPaidRecruit = false;
		action.freeRecruitLeft = 3;
		action.canRecruitOnAnyArmy = false;
		
		return action;
	}

}
