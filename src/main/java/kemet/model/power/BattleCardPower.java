package kemet.model.power;

import kemet.model.BattleCard;
import kemet.model.Color;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Power;
import kemet.model.action.Action;
import kemet.model.action.RemoveBattleCardAction;

public class BattleCardPower extends Power {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3787823629746734990L;
	
	private BattleCard gainedCard;

	public BattleCardPower(int index, String name, byte level, Color color, String description, BattleCard gainedCard) {
		super(index, name, level, color, description);
		this.gainedCard = gainedCard;
	}

	public void applyToPlayer(Player player) {
		player.recoverAllDiscardedBattleCards();
		player.recoverAllUsedBattleCards();
	}

	public Action createNextAction(Player player, Action parent, KemetGame game) {
		
		RemoveBattleCardAction removeAction = RemoveBattleCardAction.create(game, parent, player);
		removeAction.newBattleCard = gainedCard;
		
		return removeAction;
	}

}
