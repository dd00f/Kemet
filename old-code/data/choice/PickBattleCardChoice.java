package kemet.data.choice;

import java.util.ArrayList;
import java.util.List;

import kemet.data.state.State;
import kemet.model.BattleCard;
import kemet.model.Game;
import kemet.model.Player;
import kemet.model.action.BattleAction;

public class PickBattleCardChoice extends PlayerChoice {

	public BattleCard card;
	public BattleAction battle;
	public boolean isAttacker;
	public boolean isDiscard;
	

	public PickBattleCardChoice(Game game, Player player) {
		super(game, player);
	}

	@Override
	public String describe() {
		String action = "Use ";
		if( isDiscard ) {
			action = "Discard ";
		}
		
		return action + "Battle card " + card;
	}

	@Override
	public State choiceActivate() {
		
		player.removeBattleCard(card);
		if( isAttacker ) {
			if( isDiscard ) {
				battle.attackingDiscardBattleCard = card;
			}
			else {
				battle.attackingUsedBattleCard = card;
			}
		}
		else {
			if( isDiscard ) {
				battle.defendingDiscardBattleCard = card;
			}
			else {
				battle.defendingUsedBattleCard = card;
			}
		}
		return null;
	}

	public static void pickAllBattleCards(Game game, BattleAction battle) {
		
		
		List<Choice> choiceList = new ArrayList<>();
				
		choiceList.clear();
		addPickBattleCardChoice(game, battle.attackingArmy.owningPlayer, choiceList, battle, true, false );
		battle.attackingArmy.owningPlayer.actor.pickActionAndActivateOld(choiceList);
		
		
		choiceList.clear();
		addPickBattleCardChoice(game, battle.attackingArmy.owningPlayer, choiceList, battle, true, true );
		battle.attackingArmy.owningPlayer.actor.pickActionAndActivateOld(choiceList);
		

		choiceList.clear();
		addPickBattleCardChoice(game, battle.defendingArmy.owningPlayer, choiceList, battle, false, false );
		battle.defendingArmy.owningPlayer.actor.pickActionAndActivateOld(choiceList);		
		

		choiceList.clear();
		addPickBattleCardChoice(game, battle.defendingArmy.owningPlayer, choiceList, battle, false, true );
		battle.defendingArmy.owningPlayer.actor.pickActionAndActivateOld(choiceList);

		
	}

		
	public static void addPickBattleCardChoice(Game game, Player player, List<Choice> choiceList, BattleAction battle, boolean isAttacker, boolean isDiscard) {

		for (BattleCard card : player.availableBattleCards) {

			PickBattleCardChoice pickBattleCardChoice = new PickBattleCardChoice(game, player);
			pickBattleCardChoice.card = card;
			pickBattleCardChoice.battle = battle;
			pickBattleCardChoice.isAttacker = isAttacker;
			pickBattleCardChoice.isDiscard = isDiscard;
			choiceList.add(pickBattleCardChoice);

		}

	}

}
