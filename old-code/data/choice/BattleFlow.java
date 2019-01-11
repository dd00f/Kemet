package kemet.data.choice;

import java.util.ArrayList;
import java.util.List;

import kemet.model.Game;
import kemet.model.action.BattleAction;

public class BattleFlow {

	private Game game;
	private BattleAction battle;

	public BattleFlow(Game game, BattleAction battle) {
		this.game = game;
		this.battle = battle;

	}

	public void triggerBattle() {

		PickBattleCardChoice.pickAllBattleCards(game, battle);

		resolveBattleWinner();

		applyAttackCardBleed();

		applyBattleBleed();

		checkIfAmyIsDestroyed();

		giveDawnTokenToLoser();

		giveBattleVictoryPoints();

		handleAttackerArmy();

		handleDefenderArmy();
	}

	private void checkIfAmyIsDestroyed() {
		battle.attackerDestroyed = battle.attackingArmy.armySize <= 0;
		battle.defenderDestroyed = battle.defendingArmy.armySize <= 0;
	}

	private void resolveBattleWinner() {
		// resolve battle
		battle.attackerScore = battle.calculateAttackerScore();
		battle.defenderScore = battle.calculateDefenderScore();

		battle.attackerWins = battle.attackerScore > battle.defenderScore;

		if (battle.attackerWins) {
			System.out.println("Attacking " + battle.attackingArmy.name + " wins with battle score of "
					+ battle.attackerScore + " over defending " + battle.defendingArmy.name + " with a battle score of "
					+ battle.defenderScore);
		} else {
			System.out.println("Defending " + battle.defendingArmy.name + " wins with battle score of "
					+ battle.defenderScore + " over attacking " + battle.attackingArmy.name + " with a battle score of "
					+ battle.attackerScore);
		}

	}

	private void handleDefenderArmy() {
		// Handle the defender army
		if (battle.defenderDestroyed) {
			battle.defendingArmy.destroyArmy();
		} else {
			List<Choice> retreatChoiceList = new ArrayList<>();
			ArmyRetreatTileMoveChoice.addArmyTileRetreatMoveChoice(game, battle.defendingArmy.owningPlayer,
					battle.defendingArmy, battle.tile, retreatChoiceList);

			if (battle.attackerWins && retreatChoiceList.size() == 0) {
				// force a recall
				battle.defendingArmy.recall();
			} else {
				RecallArmyChoice.pickRecallOption(game, battle, false);

				// if not recall
				if (battle.defenderRecall) {
					battle.defendingArmy.recall();
				} else {
					if (!battle.attackerWins || battle.attackerDestroyed) {
						// if victory or attacker destroyed, stay where you are
					} else {
						// defeat : ask attacker to move army
						battle.attackingArmy.owningPlayer.actor.pickActionAndActivateOld(retreatChoiceList);
					}
				}
			}
		}
	}

	private void handleAttackerArmy() {
		// Handle the attacker army
		if (battle.attackerDestroyed) {
			battle.attackingArmy.destroyArmy();
		} else {
			List<Choice> retreatChoiceList = new ArrayList<>();
			ArmyRetreatTileMoveChoice.addArmyTileRetreatMoveChoice(game, battle.attackingArmy.owningPlayer,
					battle.attackingArmy, battle.tile, retreatChoiceList);

			if (!battle.attackerWins && retreatChoiceList.size() == 0) {
				// force a recall
				battle.attackingArmy.recall();
				battle.attackerRecall = true;
			} else {
				RecallArmyChoice.pickRecallOption(game, battle, true);

				// if not recall
				if (battle.attackerRecall) {
					battle.attackingArmy.recall();
				} else {
					if (battle.attackerWins) {
						// if victory, take the tile
						battle.defendingArmy.moveToTile(null);
						battle.attackingArmy.moveToTile(battle.tile);
					} else {
						// defeat : ask defender to move army
						battle.defendingArmy.owningPlayer.actor.pickActionAndActivateOld(retreatChoiceList);
					}
				}
			}
		}
	}

	private void giveBattleVictoryPoints() {
		// give winner victory point if attacker & still alive
		if (battle.attackerWins) {
			if (!battle.attackerDestroyed) {
				battle.attackingArmy.owningPlayer.addBattleVictoryPoint();
			}
		}
	}

	private void giveDawnTokenToLoser() {
		// give loser 1 dawn token
		if (battle.attackerWins) {
			battle.defendingArmy.owningPlayer.addDawnToken();

		} else {
			battle.attackingArmy.owningPlayer.addDawnToken();
		}
	}

	private void applyBattleBleed() {
		byte attackerBleed = battle.calculateDamageOnAttacker();
		byte defenderBleed = battle.calculateDamageOnDefender();

		// apply bleeds
		battle.attackingArmy.bleedArmy(attackerBleed, "defender bleed score");
		battle.defendingArmy.bleedArmy(defenderBleed, "attacker bleed score");
	}

	private void applyAttackCardBleed() {
		// apply battlecard mandatory bleed
		battle.attackingArmy.bleedArmy(battle.attackingUsedBattleCard.armyCost, "activated battle card");
		battle.defendingArmy.bleedArmy(battle.defendingUsedBattleCard.armyCost, "activated battle card");
	}

}
