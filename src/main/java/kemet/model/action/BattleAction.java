package kemet.model.action;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import kemet.model.Army;
import kemet.model.BattleCard;
import kemet.model.BeastList;
import kemet.model.BoardInventory;
import kemet.model.DiCard;
import kemet.model.DiCardList;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.PowerList;
import kemet.model.Tile;
import kemet.model.Validation;
import kemet.model.action.choice.Choice;
import kemet.model.action.choice.ChoiceInventory;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;
import kemet.util.PolicyVector;
import kemet.util.StackingMCTS;
import kemet.util.StackingMCTS.MctsBoardInformation;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BattleAction implements Action {

	private static final String DEVOURER_VICTORY_POINT_REASON = "Devourer won victory point and damaged 2 or more troops.";

	/**
	 * 
	 */
	private static final long serialVersionUID = -4818172417656461599L;

	private KemetGame game;

	private Action parent;

	public Army attackingArmy;
	public BattleCard attackingDiscardBattleCard;
	public BattleCard attackingUsedBattleCard;
	public byte[] attackingUsedDiCard = new byte[DiCardList.TOTAL_BATTLE_DI_CARD_TYPE_COUNT];
	public boolean attackerRecall = false;

	public Army defendingArmy;
	public BattleCard defendingDiscardBattleCard;
	public BattleCard defendingUsedBattleCard;
	public byte[] defendingUsedDiCard = new byte[DiCardList.TOTAL_BATTLE_DI_CARD_TYPE_COUNT];
	public boolean defenderRecall = false;

	public Tile tile;
	public boolean attackerWins;
	public boolean attackerDestroyed;
	public boolean defenderDestroyed;
	public byte defenderScore;
	public byte attackerScore;
	public boolean diCardDiscarded;
	public boolean battleResolved;
	public boolean attackerRetreatPicked;
	public boolean attackerRetreatTilePicked;
	public boolean defenderRetreatPicked;
	public boolean defenderRetreatTilePicked;
	public boolean defenderTacticalChoicePicked;
	public boolean attackerTacticalChoicePicked;

	public boolean defenderDivineWoundPicked;
	public boolean attackerDivineWoundPicked;
	public byte defenderDivineWound;
	public byte attackerDivineWound;

	public ChainedAction pendingActions;

	private boolean incrementedCounter = false;

	@Override
	public void initialize() {

		game = null;

		parent = null;

		attackingArmy = null;
		attackingDiscardBattleCard = null;
		attackingUsedBattleCard = null;
		DiCardList.fillArray(attackingUsedDiCard, (byte) 0);
		DiCardList.fillArray(defendingUsedDiCard, (byte) 0);
		attackerRecall = false;

		defendingArmy = null;
		defendingDiscardBattleCard = null;
		defendingUsedBattleCard = null;
		defenderRecall = false;
		defenderTacticalChoicePicked = false;
		attackerTacticalChoicePicked = false;

		defenderDivineWoundPicked = false;
		attackerDivineWoundPicked = false;
		defenderDivineWound = 0;
		attackerDivineWound = 0;

		tile = null;
		attackerWins = false;
		attackerDestroyed = false;
		defenderDestroyed = false;
		defenderScore = 0;
		attackerScore = 0;
		battleResolved = false;
		diCardDiscarded = false;
		attackerRetreatPicked = false;
		attackerRetreatTilePicked = false;
		defenderRetreatPicked = false;
		defenderRetreatTilePicked = false;
		incrementedCounter = false;

		pendingActions = null;

	}

	public static Cache<BattleAction> CACHE = new Cache<BattleAction>(() -> new BattleAction());

	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {
		currentGame.validate(game);
		currentGame.validate(tile);
		currentGame.validate(attackingArmy);
		currentGame.validate(defendingArmy);

		if (expectedParent != parent) {
			Validation.validationFailed("Action parent isn't as expected.");
		}

		if (pendingActions != null) {
			pendingActions.validate(this, game);
		}
	}

	@Override
	public void relink(KemetGame clone) {
		this.game = clone;

		attackingArmy = clone.getArmyByCopy(attackingArmy);
		defendingArmy = clone.getArmyByCopy(defendingArmy);
		if (pendingActions != null) {
			pendingActions.relink(clone);
		}
		tile = clone.getTileByCopy(tile);
	}

	@Override
	public BattleAction deepCacheClone() {

		BattleAction clone = CACHE.create();

		clone.game = game;
		clone.parent = parent;
		clone.attackingArmy = attackingArmy;
		clone.attackingDiscardBattleCard = attackingDiscardBattleCard;
		clone.attackingUsedBattleCard = attackingUsedBattleCard;
		clone.defendingArmy = defendingArmy;
		clone.defendingDiscardBattleCard = defendingDiscardBattleCard;
		clone.defendingUsedBattleCard = defendingUsedBattleCard;
		clone.tile = tile;
		clone.attackerWins = attackerWins;
		clone.attackerDestroyed = attackerDestroyed;
		clone.defenderDestroyed = defenderDestroyed;
		clone.defenderScore = defenderScore;
		clone.attackerScore = attackerScore;
		clone.battleResolved = battleResolved;
		clone.diCardDiscarded = diCardDiscarded;
		clone.attackerRetreatPicked = attackerRetreatPicked;
		clone.attackerRetreatTilePicked = attackerRetreatTilePicked;
		clone.defenderRetreatPicked = defenderRetreatPicked;
		clone.defenderRetreatTilePicked = defenderRetreatTilePicked;
		clone.defenderRecall = defenderRecall;
		clone.attackerRecall = attackerRecall;
		clone.defenderTacticalChoicePicked = defenderTacticalChoicePicked;
		clone.attackerTacticalChoicePicked = attackerTacticalChoicePicked;

		clone.defenderDivineWoundPicked = defenderDivineWoundPicked;
		clone.attackerDivineWoundPicked = attackerDivineWoundPicked;
		clone.defenderDivineWound = defenderDivineWound;
		clone.attackerDivineWound = attackerDivineWound;

		DiCardList.copyArray(attackingUsedDiCard, clone.attackingUsedDiCard);
		DiCardList.copyArray(defendingUsedDiCard, clone.defendingUsedDiCard);

		clone.incrementedCounter = incrementedCounter;

		if (pendingActions != null) {
			clone.pendingActions = pendingActions.deepCacheClone();
			clone.pendingActions.setParent(clone);
		}

		return clone;
	}

	@Override
	public void release() {
		game = null;
		parent = null;
		attackingArmy = null;
		defendingArmy = null;
		tile = null;
		attackingDiscardBattleCard = null;
		attackingUsedBattleCard = null;
		defendingDiscardBattleCard = null;
		defendingUsedBattleCard = null;

		if (pendingActions != null) {
			pendingActions.release();
		}

		CACHE.release(this);

	}

	private BattleAction() {

	}

	public static BattleAction create(KemetGame game, Action parent) {
		BattleAction create = CACHE.create();
		create.initialize();

		create.game = game;
		create.parent = parent;

		return create;
	}

	public KemetGame getGame() {
		return game;
	}

	@Override
	public Action getParent() {
		return parent;
	}

	public byte calculateAttackerScore() {
		boolean isAttackerBeastIgnored = isAttackerBeastIgnored();

		byte score = attackingArmy.getScore(true, isAttackerBeastIgnored);
		score += attackingUsedBattleCard.attackBonus;

		score += attackingUsedDiCard[DiCardList.WAR_RAGE.index];
		score += attackingUsedDiCard[DiCardList.WAR_FURY.index] * 2;

		score += attackerDivineWound;

		return score;
	}

	public byte calculateDefenderScore() {
		boolean isDefenderBeastIgnored = isDefenderBeastIgnored();

		byte score = defendingArmy.getScore(false, isDefenderBeastIgnored);
		score += defendingUsedBattleCard.attackBonus;

		score += defendingUsedDiCard[DiCardList.WAR_RAGE.index];
		score += defendingUsedDiCard[DiCardList.WAR_FURY.index] * 2;

		score += defenderDivineWound;

		return score;
	}

	public byte calculateAttackerDamage() {
		byte score = attackingUsedBattleCard.bloodBonus;
		score += attackingArmy.owningPlayer.damageBonus;

		if (attackingArmy.beast != null) {
			score += attackingArmy.beast.damageBonus;
		}

		score += attackingUsedDiCard[DiCardList.BLOOD_BATTLE.index];
		score += attackingUsedDiCard[DiCardList.BLOOD_BATH.index] * 2;

		return score;
	}

	public byte calculateDefenderDamage() {
		byte score = defendingUsedBattleCard.bloodBonus;
		score += defendingArmy.owningPlayer.damageBonus;

		if (defendingArmy.beast != null) {
			score += defendingArmy.beast.damageBonus;
		}

		score += defendingUsedDiCard[DiCardList.BLOOD_BATTLE.index];
		score += defendingUsedDiCard[DiCardList.BLOOD_BATH.index] * 2;

		return score;
	}

	public byte calculateAttackerShield() {
		byte score = attackingUsedBattleCard.shieldBonus;
		score += attackingArmy.owningPlayer.shieldBonus;

		if (attackingArmy.beast != null) {
			score += attackingArmy.beast.shieldBonus;
		}

		score += attackingUsedDiCard[DiCardList.BRONZE_WALL.index];
		score += attackingUsedDiCard[DiCardList.IRON_WALL.index] * 2;

		return score;
	}

	public byte calculateDefenderShield() {
		byte score = defendingUsedBattleCard.shieldBonus;
		score += defendingArmy.owningPlayer.shieldBonus;

		if (defendingArmy.beast != null) {
			score += defendingArmy.beast.shieldBonus;
		}

		score += defendingUsedDiCard[DiCardList.BRONZE_WALL.index];
		score += defendingUsedDiCard[DiCardList.IRON_WALL.index] * 2;

		return score;
	}

	public byte calculateDamageOnAttacker() {
		if (attackingUsedDiCard[DiCardList.DIVINE_PROTECTION.index] > 0 && attackerWins) {
			return 0;
		}

		return (byte) Math.min(Math.max(calculateDefenderDamage() - calculateAttackerShield(), 0),
				attackingArmy.armySize);
	}

	public byte calculateDamageOnDefender() {
		if (defendingUsedDiCard[DiCardList.DIVINE_PROTECTION.index] > 0 && !attackerWins) {
			return 0;
		}

		return (byte) Math.min(Math.max(calculateAttackerDamage() - calculateDefenderShield(), 0),
				defendingArmy.armySize);
	}

	public Army getArmy(boolean isAttacker) {
		Army army = attackingArmy;
		if (!isAttacker) {
			army = defendingArmy;
		}
		return army;
	}

	public void checkIfAmyIsDestroyed() {
		attackerDestroyed = attackingArmy.armySize <= 0;

		if (attackerDestroyed) {

			addRecruitBeastFromRemovedArmy(attackingArmy);

			attackingArmy.destroyArmy();
			attackerRetreatPicked = true;
			attackerRetreatTilePicked = true;
		}

		defenderDestroyed = defendingArmy.armySize <= 0;
		if (defenderDestroyed) {

			addRecruitBeastFromRemovedArmy(defendingArmy);

			defendingArmy.destroyArmy();
			defenderRetreatPicked = true;
			defenderRetreatTilePicked = true;
		}
	}

	public void addRecruitBeastFromRemovedArmy(Army removedArmy) {
		if (removedArmy.beast != null) {
			createPendingActions();
			pendingActions
					.add(BeastRecruitAction.create(game, removedArmy.owningPlayer, pendingActions, removedArmy.beast));
		}
	}

	private void createPendingActions() {
		if (pendingActions == null) {
			pendingActions = ChainedAction.create(game, this);
		}
	}

	public void resolveBattleWinner() {
		// resolve battle
		attackerScore = calculateAttackerScore();
		defenderScore = calculateDefenderScore();

		attackerWins = attackerScore > defenderScore;
		if (attackerWins) {
			applyHolyWarBonus(attackingArmy.owningPlayer);

			byte gloryCount = attackingUsedDiCard[DiCardList.GLORY.index];
			for (int i = 0; i < gloryCount; ++i) {
				attackingArmy.owningPlayer.modifyPrayerPoints((byte) 4, DiCardList.GLORY.toString());
			}

		} else {
			applyHolyWarBonus(defendingArmy.owningPlayer);

			byte gloryCount = defendingUsedDiCard[DiCardList.GLORY.index];
			for (int i = 0; i < gloryCount; ++i) {
				defendingArmy.owningPlayer.modifyPrayerPoints((byte) 4, DiCardList.GLORY.toString());
			}
		}

		if (game.printActivations) {
			if (attackerWins) {
				game.printEvent("Attacking " + attackingArmy.name + " wins with battle score of " + attackerScore
						+ " over defending " + defendingArmy.name + " with a battle score of " + defenderScore);
			} else {
				game.printEvent("Defending " + defendingArmy.name + " wins with battle score of " + defenderScore
						+ " over attacking " + attackingArmy.name + " with a battle score of " + attackerScore);
			}
		}

		attackingArmy.owningPlayer.checkToRecuperateAllBattleCards();
		defendingArmy.owningPlayer.checkToRecuperateAllBattleCards();
	}

	private void applyHolyWarBonus(Player owningPlayer) {
		if (owningPlayer.hasPower(PowerList.WHITE_3_HOLY_WAR)) {
			owningPlayer.modifyPrayerPoints((byte) 4, PowerList.WHITE_3_HOLY_WAR.toString());
		}
	}

	public void giveBattleVictoryPoints(byte defenderBleed, byte attackerBleed, boolean attackerHasDeepDesertSnake,
			boolean defenderHasDeepDesertSnake) {
		// give winner victory point if attacker & still alive
		if (attackerWins) {
			if (!attackerDestroyed) {
				String reason = "Offensive Battle Victory";
				attackingArmy.owningPlayer.addBattleVictoryPoint(reason);

				if (attackingArmy.beast == BeastList.BLACK_4_DEVOURER && defenderBleed >= 2
						&& !defenderHasDeepDesertSnake) {
					attackingArmy.owningPlayer.addBattleVictoryPoint(DEVOURER_VICTORY_POINT_REASON);
				}
			}
		} else {
			if (!defenderDestroyed) {
				if (defendingArmy.owningPlayer.hasPower(PowerList.BLUE_3_DEFENSIVE_VICTORY)) {
					defendingArmy.owningPlayer.addBattleVictoryPoint("Defensive Battle Victory Power");

					if (defendingArmy.beast == BeastList.BLACK_4_DEVOURER && attackerBleed >= 2
							&& !attackerHasDeepDesertSnake) {
						defendingArmy.owningPlayer.addBattleVictoryPoint(DEVOURER_VICTORY_POINT_REASON);
					}
				}
			}
		}
	}

	public void giveDawnTokenToLoser() {
		// give loser 1 dawn token
		if (attackerWins) {
			defendingArmy.owningPlayer.addDawnToken();

		} else {
			attackingArmy.owningPlayer.addDawnToken();
		}
	}

	public byte applyAttackerBattleBleed() {
		byte attackerBleed = calculateDamageOnAttacker();

		// apply bleeds
		attackingArmy.bleedArmy(attackerBleed, "defender damage score");

		applyCrusadePowerBonusToDamage(attackerBleed, defendingArmy.owningPlayer);
		applyHonorInBattlePowerBonusToDamage(attackerBleed, attackingArmy.owningPlayer);

		return attackerBleed;
	}

	public byte applyDefenderBattleBleed() {
		byte defenderBleed = calculateDamageOnDefender();

		defendingArmy.bleedArmy(defenderBleed, "attacker damage score");

		applyCrusadePowerBonusToDamage(defenderBleed, attackingArmy.owningPlayer);
		applyHonorInBattlePowerBonusToDamage(defenderBleed, defendingArmy.owningPlayer);

		return defenderBleed;
	}

	private void applyCrusadePowerBonusToDamage(byte attackerBleed, Player owningPlayer) {
		if (owningPlayer.hasPower(PowerList.WHITE_2_CRUSADE)) {
			byte powerBonus = (byte) (attackerBleed * 2);
			owningPlayer.modifyPrayerPoints(powerBonus, PowerList.WHITE_2_CRUSADE.toString());
		}
	}

	private void applyHonorInBattlePowerBonusToDamage(byte attackerBleed, Player owningPlayer) {
		if (owningPlayer.hasPower(PowerList.BLACK_2_HONOR_IN_BATTLE)) {
			byte powerBonus = (byte) (attackerBleed * 1);
			owningPlayer.modifyPrayerPoints(powerBonus, PowerList.BLACK_2_HONOR_IN_BATTLE.toString());
		}
	}

	public void applyAttackCardBleed() {
		// apply battlecard mandatory bleed
		attackingArmy.bleedArmy(attackingUsedBattleCard.armyCost, "activated battle card");
		defendingArmy.bleedArmy(defendingUsedBattleCard.armyCost, "activated battle card");
	}

	public Player getBattlePlayer(boolean isAttacker) {
		if (isAttacker) {
			return attackingArmy.owningPlayer;
		}
		return defendingArmy.owningPlayer;
	}

	public class PickBattleDiCardChoice extends PlayerChoice {

		public int cardIndex;
		public boolean isAttacker;

		public PickBattleDiCardChoice(KemetGame game, Player player) {
			super(game, player);
		}

		@Override
		public String describe() {
			DiCard diCard = DiCardList.CARDS[cardIndex];

			return "Use divine intervention card : " + diCard;
		}

		@Override
		public void choiceActivate() {

			DiCard diCard = DiCardList.CARDS[cardIndex];

			payForDiCard(diCard, player);

			if (isAttacker) {
				attackingUsedDiCard[cardIndex] += 1;
			} else {
				defendingUsedDiCard[cardIndex] += 1;
			}

		}

		@Override
		public int getIndex() {
			return ChoiceInventory.ACTIVATE_DI_CARD + cardIndex;
		}

	}

	private boolean payForDiCard(DiCard diCard, Player player) {

		if (diCard.powerCost > 0) {
			byte cost = diCard.powerCost;
			cost = player.applyPriestOfRaBonus(cost);
			if (player.getPrayerPoints() >= cost) {
				player.modifyPrayerPoints((byte) -cost, "Activated DI Card");
			} else {
				return false;
			}
		}
		return true;
	}

	public class PickBattleCardChoice extends PlayerChoice {

		public BattleCard card;
		public boolean isAttacker;
		public boolean isDiscard;

		public PickBattleCardChoice(KemetGame game, Player player) {
			super(game, player);
		}

		@Override
		public String describe() {
			String action = "Use ";
			if (isDiscard) {
				action = "Discard ";
			}

			return action + "Battle card " + card;
		}

		@Override
		public void choiceActivate() {

			if (isAttacker) {
				if (isDiscard) {
					player.discardBattleCard(card);
					attackingDiscardBattleCard = card;
					pickDefenderCardBasedOnNeuralNetworkIfSimulation();
				} else {
					if (game.isSimulation()) {
						if (game.simulatedPlayerIndex != player.getIndex()) {
							// skip discard card selection for other players during simulations
							String message = "Reached a battle simulation state where the next action is for the opponent ATTACKER to pick a hidden ATTACK card which should be simulated by the MCTS.";
							log.error(message);
							attackingDiscardBattleCard = card;
						}
					}
					attackingUsedBattleCard = card;
					player.useBattleCard(card);

					// force discard
					List<BattleCard> availableBattleCards = player.availableBattleCards;
					if (availableBattleCards.size() == 1) {
						BattleCard forcedDiscard = availableBattleCards.get(0);
						player.discardBattleCard(forcedDiscard);
						attackingDiscardBattleCard = forcedDiscard;
						pickDefenderCardBasedOnNeuralNetworkIfSimulation();
					}
				}
			} else {
				if (isDiscard) {
					player.discardBattleCard(card);
					defendingDiscardBattleCard = card;
					pickAttackerCardsBasedOnNeuralNetworkIfSimulation();

				} else {

					if (game.isSimulation() && game.simulatedPlayerIndex != player.getIndex()) {
						// skip discard card selection for other players during simulations
						if (!attackingArmy.owningPlayer.hasPower(PowerList.BLUE_3_PRESCIENCE)) {
							String message = "Reached a battle simulation state where the next action is for the opponent DEFENDER to pick a hidden ATTACK card which should be simulated by the MCTS.";
							log.error(message);
						}
						defendingDiscardBattleCard = card;
					}

					defendingUsedBattleCard = card;
					player.useBattleCard(card);

					// force discard
					List<BattleCard> availableBattleCards = player.availableBattleCards;
					if (availableBattleCards.size() == 1) {
						BattleCard forcedDiscard = availableBattleCards.get(0);
						player.discardBattleCard(forcedDiscard);
						defendingDiscardBattleCard = forcedDiscard;
						pickAttackerCardsBasedOnNeuralNetworkIfSimulation();
					}
				}
			}
		}

		private void pickAttackerCardsBasedOnNeuralNetworkIfSimulation() {
			if (game.isSimulation()) {
				if (game.simulatedPlayerIndex == player.getIndex()) {

					if (player.hasPower(PowerList.BLUE_3_PRESCIENCE)) {
						// defender cards should be fully picked
						if (attackingUsedBattleCard != null && attackingDiscardBattleCard != null) {
							return;
						}
						String message = "Defender has Blue 3 Prescience, yet the attacker hasn't picked cards yet, {} pick, {} discard";
						log.error(message, attackingUsedBattleCard, attackingDiscardBattleCard);
					}

					Player attacker = attackingArmy.owningPlayer;

					if (!attacker.discardedBattleCards.isEmpty()) {
						String message = "Opponent attacker in a battle simulation should have all discarded cards available.";
						log.error(message);
					}

					if (attackingUsedBattleCard != null) {
						attacker.returnUsedBattleCard(attackingUsedBattleCard);
						log.debug("Returned attacker battle card {}", attackingUsedBattleCard.index);
					}
					if (attackingDiscardBattleCard != null) {
						if (!attacker.availableBattleCards.contains(attackingDiscardBattleCard)) {
							String message = "Opponent attacker in a battle simulation should have selected battle discard available.";
							log.error(message);
						}
					}

					attackingUsedBattleCard = null;
					attackingDiscardBattleCard = null;

					// force attack card & discard selection based on neural network policy
					BattleCard found = pickCardForPlayerBasedOnNeuralNetwork(attacker);

					attacker.useBattleCard(found);
					attackingUsedBattleCard = found;
					attackingDiscardBattleCard = found;
				} else {
					String message = "Reached a battle simulation state where the next action is for the opponent DEFENDER to pick a hidden DISCARD card which should be simulated by the MCTS.";
					log.error(message);
				}
			}
		}

		private void pickDefenderCardBasedOnNeuralNetworkIfSimulation() {
			if (game.isSimulation()) {
				if (game.simulatedPlayerIndex == player.getIndex()) {

					if (player.hasPower(PowerList.BLUE_3_PRESCIENCE)) {
						// defender cards should be fully picked
						if (defendingUsedBattleCard != null && defendingDiscardBattleCard != null) {
							return;
						}
						String message = "Attacker has Blue 3 Prescience, yet the defender hasn't picked cards yet, {} pick, {} discard";
						log.error(message, defendingUsedBattleCard, defendingDiscardBattleCard);
					}

					// force defense card & discard selection based on neural network policy
					Player defender = defendingArmy.owningPlayer;

					if (!defender.discardedBattleCards.isEmpty()) {
						String message = "Opponent defender in a battle simulation should have all discarded cards available.";
						log.error(message);
					}

					BattleCard found = pickCardForPlayerBasedOnNeuralNetwork(defender);

					defender.useBattleCard(found);
					defendingDiscardBattleCard = found;
					defendingUsedBattleCard = found;
				} else {
					String message = "Reached a battle simulation state where the next action is for the opponent ATTACKER to pick a hidden DISCARD card which should be simulated by the MCTS.";
					log.error(message);
				}
			}
		}

		private BattleCard pickCardForPlayerBasedOnNeuralNetwork(Player simulatedPlayer) {
			game.resetCachedChoices();

			ByteCanonicalForm canonicalForm = game.getCanonicalForm(simulatedPlayer.getIndex());

			List<BattleCard> availableBattleCards = simulatedPlayer.availableBattleCards;

			validateCanonicalFormForSimulatedMove(simulatedPlayer, canonicalForm);

			StackingMCTS simulationMcts = game.simulationMcts;
			MctsBoardInformation mctsBoardInformation = simulationMcts.getBoardInformation(canonicalForm);
			PolicyVector policy = mctsBoardInformation.choiceValuePredictionForBoardPs;
			if (policy == null) {
				policy = fetchPolicyFromNeuralNetwork(simulatedPlayer, canonicalForm, simulationMcts,
						mctsBoardInformation);
			}

			BattleCard found = null;
			int simulatedActionIndex = 0;
			boolean done = false;

			while (!done) {
				simulatedActionIndex = policy.pickRandomAction();

				if (isDiCardActionIndex(simulatedActionIndex)) {
					attemptToActivateDiCard(simulatedPlayer, simulatedActionIndex);

				} else {
					List<BattleCard> list = availableBattleCards;
					for (BattleCard battleCard : list) {
						if (battleCard.getPickChoiceIndex() == simulatedActionIndex) {
							found = battleCard;
							break;
						}
					}
					done = true;
				}
			}

			if (found == null) {
				// force a card at random.
				StringBuilder builder = new StringBuilder();
				simulatedPlayer.describePlayer(builder);
				String message = "MCTS battle card selection at index {} couldn't be found in the player's available cards. BattleCard Start index {}, Player : {}";
				log.error(message, simulatedActionIndex, ChoiceInventory.PICK_BATTLE_CARD_CHOICE, builder);

				found = availableBattleCards.get(0);
			}

			return found;
		}

		private void attemptToActivateDiCard(Player simulatedPlayer, int simulatedActionIndex) {

			int cardIndex = simulatedActionIndex - ChoiceInventory.ACTIVATE_DI_CARD;

			byte[] diCardList = defendingUsedDiCard;
			if (simulatedPlayer.getIndex() == attackingArmy.owningPlayer.getIndex()) {
				diCardList = attackingUsedDiCard;
			}

			if (simulatedPlayer.diCards[cardIndex] > diCardList[cardIndex]) {

				boolean successDiCard = payForDiCard(DiCardList.CARDS[cardIndex], simulatedPlayer);
				if( successDiCard ) {
					byte cost = DiCardList.CARDS[cardIndex].powerCost;
					cost = player.applyPriestOfRaBonus(cost);
					if (cost > 0 && player.getPrayerPoints() >= cost) {
						player.modifyPrayerPoints((byte) -cost, "Simulated DI card selection");
					}
	
					if (game.printActivations) {
						game.printEvent(
								"Simulated DI card for " + simulatedPlayer.name + " : " + DiCardList.CARDS[cardIndex].name);
					}
	
					diCardList[cardIndex] += 1;
				}
			}
		}

		private boolean isDiCardActionIndex(int simulatedActionIndex) {
			if (simulatedActionIndex >= ChoiceInventory.ACTIVATE_DI_CARD
					&& simulatedActionIndex <= (ChoiceInventory.ACTIVATE_DI_CARD
							+ DiCardList.TOTAL_DI_CARD_TYPE_COUNT)) {
				return true;
			}
			return false;
		}

		private void validateCanonicalFormForSimulatedMove(Player defender, ByteCanonicalForm canonicalForm) {
			int canonicalPlayerIndex = defender.getCanonicalPlayerIndex(defender.getIndex());

			String message = "available battle card {} not showing up in canonical form {}";
			if (defender.getIndex() == defendingArmy.owningPlayer.getIndex()) {
				// pick defender card
				if (canonicalForm.getCanonicalForm()[BoardInventory.STATE_PICK_DEFENSE_BATTLE_CARD] != defender
						.getState(defender.getIndex())) {
					log.error(message, card.index, canonicalForm);
				}
			} else {
				// pick attacker card
				if (canonicalForm.getCanonicalForm()[BoardInventory.STATE_PICK_ATTACK_BATTLE_CARD] != defender
						.getState(defender.getIndex())) {
					log.error(message, card.index, canonicalForm);
				}
			}

			for (BattleCard card : defender.availableBattleCards) {
				if (canonicalForm.getCanonicalForm()[Player.getCardStatusIndex(canonicalPlayerIndex, card)] != 1) {
					log.error(message, card.index, canonicalForm);
				}
			}

			for (BattleCard card : defender.usedBattleCards) {
				if (canonicalForm.getCanonicalForm()[Player.getCardStatusIndex(canonicalPlayerIndex, card)] != -1) {
					log.error(message, card.index, canonicalForm);
				}
			}
		}

		private PolicyVector fetchPolicyFromNeuralNetwork(Player simulatedPlayer, ByteCanonicalForm canonicalForm,
				StackingMCTS simulationMcts, MctsBoardInformation mctsBoardInformation) {
			PolicyVector policy;
			Pair<PolicyVector, Float> predict = simulationMcts.pooler.neuralNet.predict(canonicalForm);
			mctsBoardInformation.boardValue = predict.getValue();
			mctsBoardInformation.choiceValuePredictionForBoardPs = predict.getLeft();
			boolean[] validMoves = game.getValidMoves();

			for (BattleCard card : simulatedPlayer.availableBattleCards) {
				if (validMoves[card.getPickChoiceIndex()] != true) {
					log.error("available battle card {} not showing up in valid moves {}", card.index, validMoves);

					game.resetCachedChoices();
					validMoves = game.getValidMoves();
				}
			}

			for (BattleCard card : simulatedPlayer.usedBattleCards) {
				if (validMoves[card.getPickChoiceIndex()] != false) {
					log.error("used battle card {} IS showing up in valid moves {}", card.index, validMoves);

					game.resetCachedChoices();
					validMoves = game.getValidMoves();
				}
			}

			if (validMoves[ChoiceInventory.ZERO_ARMY_SIZE_CHOICE_INDEX] == true) {
				String message = "Pass choice is a valid move, even though we should be picking a battle card.";
				log.error(message, card.index, validMoves);

				game.resetCachedChoices();
				validMoves = game.getValidMoves();
			}

			mctsBoardInformation.validMoves = validMoves;
			policy = mctsBoardInformation.choiceValuePredictionForBoardPs;

			policy.maskInvalidMoves(validMoves);
			policy.patchMissingActivatedMoves(validMoves);
			policy.normalize();
			return policy;
		}

		@Override
		public int getIndex() {
			return card.getPickChoiceIndex();
		}

	}

	public PlayerChoicePick addPickBattleCardChoice(Player player, boolean isAttacker, boolean isDiscard) {

		PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

		for (BattleCard card : player.availableBattleCards) {

			PickBattleCardChoice pickBattleCardChoice = new PickBattleCardChoice(game, player);
			pickBattleCardChoice.card = card;
			pickBattleCardChoice.isAttacker = isAttacker;
			pickBattleCardChoice.isDiscard = isDiscard;
			pick.choiceList.add(pickBattleCardChoice);

		}

		if (!isDiscard) {
			addAllBattleDiCardChoice(player, isAttacker, pick);
		}

		return pick;

	}

	public void addAllBattleDiCardChoice(Player player, boolean isAttacker, PlayerChoicePick pick) {
		byte[] selectedDiCards = defendingUsedDiCard;
		if (isAttacker) {
			selectedDiCards = attackingUsedDiCard;
		}

		for (int i = 0; i < DiCardList.TOTAL_BATTLE_DI_CARD_TYPE_COUNT; ++i) {
			if (selectedDiCards[i] < player.diCards[i]) {

				if (playerCanAffordDiCard(i, player)) {
					PickBattleDiCardChoice pickBattleCardChoice = new PickBattleDiCardChoice(game, player);
					pickBattleCardChoice.cardIndex = i;
					pickBattleCardChoice.isAttacker = isAttacker;
					pick.choiceList.add(pickBattleCardChoice);
				}
			}
		}
	}

	private boolean playerCanAffordDiCard(int cardIndex, Player player) {
		byte cost = DiCardList.CARDS[cardIndex].powerCost;
		cost = player.applyPriestOfRaBonus(cost);

		if (cost > 0 && player.getPrayerPoints() < cost) {
			return false;
		}

		return true;
	}

	public class ArmyRetreatTileMoveChoice extends PlayerChoice {

		public ArmyRetreatTileMoveChoice(KemetGame game, Player player) {
			super(game, player);
		}

		public Tile destinationTile;
		public Army army;
		public boolean attacker;

		@Override
		public void choiceActivate() {
			army.moveToTile(destinationTile);
			if (attacker) {
				attackerRetreatTilePicked = true;
			} else {
				defenderRetreatTilePicked = true;
			}
		}

		@Override
		public String describe() {

			StringBuilder builder = new StringBuilder();
			builder.append("Retreat army \"").append(army).append("\"");
			if (army.tile != null) {
				builder.append(" from tile ").append(army.tile.name);
			}

			builder.append(" to tile ").append(destinationTile);
			builder.append(".");

			return builder.toString();

		}

		@Override
		public int getIndex() {
			return destinationTile.getPickChoiceIndex(player.getIndex());
		}

	}

	public class RecallArmyChoice extends PlayerChoice {

		public boolean isAttacker;
		public boolean recall;

		public RecallArmyChoice(KemetGame game, Player player) {
			super(game, player);
		}

		@Override
		public String describe() {
			Army army = getArmy(isAttacker);

			String action = "Recall ";
			if (!recall) {
				action = "Don't recall ";
			}
			return action + army;
		}

		@Override
		public void choiceActivate() {
			Army army = getArmy(isAttacker);
			if (isAttacker) {
				attackerRecall = recall;
				attackerRetreatPicked = true;
				if (recall) {
					addRecruitBeastFromRemovedArmy(army);
					army.recall();
					attackerRetreatTilePicked = true;
				} else {
					if (attackerWins) {
						// if victory, take the tile
						attackerRetreatTilePicked = true;
					}
				}

			} else {
				defenderRecall = recall;
				defenderRetreatPicked = true;
				if (recall) {
					addRecruitBeastFromRemovedArmy(army);
					army.recall();
					defenderRetreatTilePicked = true;
				} else {
					if (!attackerWins || attackerDestroyed) {
						// if victory, keep the tile
						defenderRetreatTilePicked = true;
					}
				}
			}
		}

		@Override
		public int getIndex() {
			if (recall) {
				return ChoiceInventory.RECALL_CHOICE;
			}
			return ChoiceInventory.PASS_RECALL_CHOICE_INDEX;
		}

	}

	public class TacticalChoice extends PlayerChoice {

		public boolean isAttacker;
		public boolean swap;

		public TacticalChoice(KemetGame game, Player player) {
			super(game, player);
		}

		@Override
		public String describe() {
			String action = "Tactical Choice : ";
			if (swap) {
				action += "Tactical Choice : Swap Cards";
			} else {
				action = "Tactical Choice : Don't Swap Cards";
			}
			return action;
		}

		@Override
		public void choiceActivate() {
			if (isAttacker) {
				attackerTacticalChoicePicked = true;
				if (swap) {
					BattleCard swap = attackingUsedBattleCard;
					attackingUsedBattleCard = attackingDiscardBattleCard;
					attackingDiscardBattleCard = swap;
				}

			} else {
				defenderTacticalChoicePicked = true;

				if (swap) {
					BattleCard swap = defendingUsedBattleCard;
					defendingUsedBattleCard = defendingDiscardBattleCard;
					defendingDiscardBattleCard = swap;
				}
			}
		}

		@Override
		public int getIndex() {
			if (swap) {
				return ChoiceInventory.TACTICAL_CHOICE_SWAP;
			}
			return ChoiceInventory.TACTICAL_CHOICE_KEEP;
		}

	}

	public class DivineWoundChoice extends PlayerChoice {

		private boolean isAttacker;
		private int cardIndex;

		public DivineWoundChoice(KemetGame game, Player player, boolean isAttacker, int cardIndex) {
			super(game, player);
			this.isAttacker = isAttacker;
			this.cardIndex = cardIndex;
		}

		@Override
		public String describe() {
			if (cardIndex < 0) {
				return "Stop spending DI cards on Divine Wounds";
			}
			StringBuilder builder = new StringBuilder();
			builder.append("Inflict Divine Wound. Attacker Score : ");
			builder.append(calculateAttackerScore());
			builder.append(", Defender Score : ");
			builder.append(calculateDefenderScore());
			builder.append(", Using DI card : ");
			builder.append(DiCardList.CARDS[cardIndex].name);

			return builder.toString();
		}

		@Override
		public void choiceActivate() {
			if (cardIndex < 0) {
				if (isAttacker) {
					attackerDivineWoundPicked = true;
				} else {
					defenderDivineWoundPicked = true;
				}
			} else {
				DiCardList.moveDiCard(player.diCards, game.discardedDiCardList, cardIndex, player.name,
						KemetGame.DISCARDED_DI_CARDS, "divine wounds", game);
				if (isAttacker) {
					attackerDivineWound += 1;
				} else {
					defenderDivineWound += 1;
				}
			}
		}

		@Override
		public int getIndex() {
			if (cardIndex < 0) {
				return ChoiceInventory.END_DIVINE_WOUND;
			}
			return ChoiceInventory.DIVINE_WOUND_DI_CARD + cardIndex;
		}

	}

	public PlayerChoicePick pickRecallOption(boolean attacker) {

		Army army = getArmy(attacker);
		PlayerChoicePick pick = new PlayerChoicePick(game, army.owningPlayer, this);

		{
			RecallArmyChoice recallChoice = new RecallArmyChoice(game, army.owningPlayer);
			recallChoice.isAttacker = attacker;
			recallChoice.recall = true;

			pick.choiceList.add(recallChoice);
		}
		{
			RecallArmyChoice recallChoice = new RecallArmyChoice(game, army.owningPlayer);
			recallChoice.isAttacker = attacker;
			recallChoice.recall = false;

			pick.choiceList.add(recallChoice);
		}

		return pick;
	}

	public PlayerChoicePick addArmyTileRetreatMoveChoice(boolean attacker, Tile sourceTile) {

		Army army = getArmy(attacker);
		Player actionPlayer = null;

		if (attacker) {
			actionPlayer = defendingArmy.owningPlayer;

		} else {
			actionPlayer = attackingArmy.owningPlayer;

		}

		PlayerChoicePick pick = new PlayerChoicePick(game, actionPlayer, this);

		for (Tile tile : sourceTile.connectedTiles) {

			if (tile.getArmy() != null) {
				// can't retreat on occupied tile, even friendly
				continue;
			}

			ArmyRetreatTileMoveChoice subChoice = new ArmyRetreatTileMoveChoice(game, pick.player);
			subChoice.army = army;
			subChoice.destinationTile = tile;
			subChoice.attacker = attacker;

			if (tile.isWalledByEnemy(pick.player)) {
				// moving into a city tile with walls
				// LOGGER.info("Army " + army + " can't retreat to tile " + tile.name + "
				// because it has walls.");
				continue;
			}

			pick.choiceList.add(subChoice);
		}

		return pick;
	}

	private void checkToResolveBattle() {
		if (attackerDivineWoundPicked && defenderDivineWoundPicked && !battleResolved) {
			resolveBattle();
		}
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {

		// force defender to go first if this game is a simulation from the defender
		// perspective
		Player defendingPlayer = defendingArmy.owningPlayer;
		if (isDefenderFirstToPick()) {
			if (defendingUsedBattleCard == null) {
				return addPickBattleCardChoice(defendingPlayer, false, false).validate();
			} else if (defendingDiscardBattleCard == null) {
				return addPickBattleCardChoice(defendingPlayer, false, true).validate();
			}
		}

		Player attackingPlayer = attackingArmy.owningPlayer;
		if (attackingUsedBattleCard == null) {
			return addPickBattleCardChoice(attackingPlayer, true, false).validate();
		}
		if (attackingDiscardBattleCard == null) {
			return addPickBattleCardChoice(attackingPlayer, true, true).validate();
		}
		if (defendingUsedBattleCard == null) {
			return addPickBattleCardChoice(defendingPlayer, false, false).validate();
		}
		if (defendingDiscardBattleCard == null) {
			return addPickBattleCardChoice(defendingPlayer, false, true).validate();
		}

		if (!attackerTacticalChoicePicked) {
			if (attackingUsedDiCard[DiCardList.TACTICAL_CHOICE.index] > 0) {
				return addPickTacticalChoice(attackingPlayer, true).validate();
			}
			attackerTacticalChoicePicked = true;

		}

		if (!defenderTacticalChoicePicked) {
			if (defendingUsedDiCard[DiCardList.TACTICAL_CHOICE.index] > 0) {
				return addPickTacticalChoice(defendingPlayer, false).validate();
			}
			defenderTacticalChoicePicked = true;

		}
		
		checkToDiscardDiCards();

		if (attackerDivineWoundPicked == false) {
			if (attackingPlayer.hasPower(PowerList.RED_3_DIVINE_WOUND)) {
				// ensure attacker didn't win yet
				if (calculateAttackerScore() <= calculateDefenderScore()) {
					if (DiCardList.sumArray(attackingPlayer.diCards) > 0) {
						return addPickDivineWoundChoice(attackingPlayer, true);
					}
				}
			}
			attackerDivineWoundPicked = true;
		}

		if (defenderDivineWoundPicked == false) {
			if (defendingPlayer.hasPower(PowerList.RED_3_DIVINE_WOUND)) {
				// ensure defender didn't win yet
				if (calculateAttackerScore() > calculateDefenderScore()) {
					if (DiCardList.sumArray(defendingPlayer.diCards) > 0) {
						return addPickDivineWoundChoice(defendingPlayer, false);
					}
				}
			}
			defenderDivineWoundPicked = true;
		}

		checkToResolveBattle();

		if (pendingActions != null) {
			PlayerChoicePick nextPlayerChoicePick = pendingActions.getNextPlayerChoicePick();
			if (nextPlayerChoicePick != null) {
				return nextPlayerChoicePick;
			}
		}

		checkForForcedRecall();

		if (!attackerRetreatPicked) {
			return pickRecallOption(true).validate();
		}

		if (!attackerRetreatTilePicked) {
			PlayerChoicePick pick = addArmyTileRetreatMoveChoice(true, tile);
			if (pick.choiceList.size() == 1) {
				// force the retreat tile if there is only one available
				pick.choiceList.get(0).activate();
			} else {
				return pick.validate();
			}
		}

		if (!defenderRetreatPicked) {
			return pickRecallOption(false).validate();
		}

		if (!defenderRetreatTilePicked) {
			PlayerChoicePick pick = addArmyTileRetreatMoveChoice(false, tile);
			if (pick.choiceList.size() == 1) {
				// force the retreat tile if there is only one available
				pick.choiceList.get(0).activate();
			} else {
				return pick.validate();
			}
		}

		if (!incrementedCounter) {
			incrementedCounter = true;
			game.battleCount++;
		}
		// battle is over
		return null;
	}

	private void checkToDiscardDiCards() {
		if( ! diCardDiscarded ) {
			
			discardUsedDiCards();
			
			diCardDiscarded = true;
		}
		
	}

	private PlayerChoicePick addPickDivineWoundChoice(Player owningPlayer, boolean isAttacker) {
		PlayerChoicePick pick = new PlayerChoicePick(game, owningPlayer, this);
		List<Choice> choiceList = pick.choiceList;

		byte[] diCards = owningPlayer.diCards;
		for (int i = 0; i < diCards.length; i++) {
			byte b = diCards[i];
			if (b > 0) {

				DivineWoundChoice choice = new DivineWoundChoice(game, owningPlayer, isAttacker, i);
				choiceList.add(choice);
			}
		}

		// add the end choice
		DivineWoundChoice choice = new DivineWoundChoice(game, owningPlayer, isAttacker, -1);
		choiceList.add(choice);

		return pick;
	}

	private PlayerChoicePick addPickTacticalChoice(Player player, boolean isAttacker) {
		PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

		TacticalChoice keep = new TacticalChoice(game, player);
		keep.swap = false;
		keep.isAttacker = isAttacker;
		pick.choiceList.add(keep);

		TacticalChoice swap = new TacticalChoice(game, player);
		swap.swap = true;
		swap.isAttacker = isAttacker;
		pick.choiceList.add(swap);

		return pick;
	}

	private boolean isDefenderFirstToPick() {
		if (attackingArmy.owningPlayer.hasPower(PowerList.BLUE_3_PRESCIENCE)) {
			return true;
		}

		if (defendingArmy.owningPlayer.hasPower(PowerList.BLUE_3_PRESCIENCE)) {
			return false;
		}

		return attackingUsedBattleCard == null && game.isSimulation()
				&& game.simulatedPlayerIndex == defendingArmy.owningPlayer.getIndex();
	}

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {

		cannonicalForm.set(BoardInventory.STATE_BATTLE, attackingArmy.owningPlayer.getState(playerIndex));
		tile.setSelected(cannonicalForm, playerIndex, attackingArmy.owningPlayer.getState(playerIndex));

		boolean isAttackerBeastIgnored = isAttackerBeastIgnored();
		boolean isDefenderBeastIgnored = isDefenderBeastIgnored();

		cannonicalForm.set(BoardInventory.BATTLE_ATTACKER_STRENGTH,
				attackingArmy.getAttackStrength(isAttackerBeastIgnored));
		cannonicalForm.set(BoardInventory.BATTLE_ATTACKER_SHIELD,
				attackingArmy.getAttackShield(isAttackerBeastIgnored));
		cannonicalForm.set(BoardInventory.BATTLE_ATTACKER_DAMAGE,
				attackingArmy.getAttackDamage(isAttackerBeastIgnored));
		cannonicalForm.set(BoardInventory.BATTLE_DEFENDER_STRENGTH,
				defendingArmy.getDefendingStrength(isDefenderBeastIgnored));
		cannonicalForm.set(BoardInventory.BATTLE_DEFENDER_SHIELD,
				defendingArmy.getDefendingShield(isDefenderBeastIgnored));
		cannonicalForm.set(BoardInventory.BATTLE_DEFENDER_DAMAGE,
				defendingArmy.getDefendingDamage(isDefenderBeastIgnored));

		if (playerIndex == attackingArmy.owningPlayer.getIndex()) {
			DiCardList.fillBattleCanonicalForm(attackingUsedDiCard, cannonicalForm,
					BoardInventory.CURRENT_PLAYER_ACTIVATED_DI);
		} else if (playerIndex == defendingArmy.owningPlayer.getIndex()) {
			DiCardList.fillBattleCanonicalForm(defendingUsedDiCard, cannonicalForm,
					BoardInventory.CURRENT_PLAYER_ACTIVATED_DI);
		}

		if (battleResolved) {
			cannonicalForm.set(BoardInventory.BATTLE_ATTACKER_WON, (byte) (attackerWins ? 1 : -1));
		}

		boolean stateSimulationOverride = false;
		if (isDefenderFirstToPick()) {
			if (defendingUsedBattleCard == null) {
				cannonicalForm.set(BoardInventory.STATE_PICK_DEFENSE_BATTLE_CARD,
						defendingArmy.owningPlayer.getState(playerIndex));
				stateSimulationOverride = true;
			} else if (defendingDiscardBattleCard == null) {
				cannonicalForm.set(BoardInventory.STATE_PICK_DEFENSE_DISCARD,
						defendingArmy.owningPlayer.getState(playerIndex));
				stateSimulationOverride = true;
			}
		}

		if (!stateSimulationOverride) {
			if (attackingUsedBattleCard == null) {
				cannonicalForm.set(BoardInventory.STATE_PICK_ATTACK_BATTLE_CARD,
						attackingArmy.owningPlayer.getState(playerIndex));
			} else if (attackingDiscardBattleCard == null) {
				cannonicalForm.set(BoardInventory.STATE_PICK_ATTACK_DISCARD,
						attackingArmy.owningPlayer.getState(playerIndex));
			} else if (defendingUsedBattleCard == null) {
				cannonicalForm.set(BoardInventory.STATE_PICK_DEFENSE_BATTLE_CARD,
						defendingArmy.owningPlayer.getState(playerIndex));
			} else if (defendingDiscardBattleCard == null) {
				cannonicalForm.set(BoardInventory.STATE_PICK_DEFENSE_DISCARD,
						defendingArmy.owningPlayer.getState(playerIndex));
			} else if (attackerTacticalChoicePicked == false) {
				cannonicalForm.set(BoardInventory.STATE_PICK_ATTACKER_TACTICAL_CHOICE,
						attackingArmy.owningPlayer.getState(playerIndex));
			} else if (defenderTacticalChoicePicked == false) {
				cannonicalForm.set(BoardInventory.STATE_PICK_DEFENDER_TACTICAL_CHOICE,
						defendingArmy.owningPlayer.getState(playerIndex));
			} else if (attackerDivineWoundPicked == false) {
				cannonicalForm.set(BoardInventory.STATE_PICK_ATTACKER_DIVINE_WOUND,
						attackingArmy.owningPlayer.getState(playerIndex));
			} else if (defenderDivineWoundPicked == false) {
				cannonicalForm.set(BoardInventory.STATE_PICK_DEFENDER_DIVINE_WOUND,
						defendingArmy.owningPlayer.getState(playerIndex));
			} else {

				if (pendingActions != null && pendingActions.size() > 0) {
					pendingActions.fillCanonicalForm(cannonicalForm, playerIndex);
				}

				else if (!attackerRetreatPicked) {
					cannonicalForm.set(BoardInventory.STATE_PICK_ATTACKER_RECALL,
							attackingArmy.owningPlayer.getState(playerIndex));
				}

				else if (!attackerRetreatTilePicked) {
					cannonicalForm.set(BoardInventory.STATE_PICK_ATTACKER_RETREAT,
							defendingArmy.owningPlayer.getState(playerIndex));
				}

				else if (!defenderRetreatPicked) {
					cannonicalForm.set(BoardInventory.STATE_PICK_DEFENDER_RECALL,
							defendingArmy.owningPlayer.getState(playerIndex));
				}

				else if (!defenderRetreatTilePicked) {
					cannonicalForm.set(BoardInventory.STATE_PICK_DEFENDER_RETREAT,
							attackingArmy.owningPlayer.getState(playerIndex));
				}

			}
		}
	}

	private boolean isDefenderBeastIgnored() {
		return attackingArmy.beast == BeastList.BLUE_2_DEEP_DESERT_SNAKE;
	}

	private boolean isAttackerBeastIgnored() {
		return defendingArmy.beast == BeastList.BLUE_2_DEEP_DESERT_SNAKE;
	}

	public void resolveBattle() {
		if (!battleResolved) {
			// resolve battle

			resolveBattleWinner();

			byte attackerBleed = applyAttackerBattleBleed();

			byte defenderBleed = applyDefenderBattleBleed();

			boolean attackerHasDeepDesertSnake = attackingArmy.beast == BeastList.BLUE_2_DEEP_DESERT_SNAKE;
			boolean defenderHasDeepDesertSnake = defendingArmy.beast == BeastList.BLUE_2_DEEP_DESERT_SNAKE;

			applyAttackCardBleed();

			checkIfAmyIsDestroyed();

			giveDawnTokenToLoser();

			giveBattleVictoryPoints(defenderBleed, attackerBleed, attackerHasDeepDesertSnake,
					defenderHasDeepDesertSnake);

			moveWinnerToTile();

			// discardUsedDiCards();

			addReinforcementsDiCardActions();

			battleResolved = true;
		}
	}

	private void addReinforcementsDiCardActions() {
		byte attackReinforcementsCount = attackingUsedDiCard[DiCardList.REINFORCEMENTS.index];

		if (attackerWins && attackReinforcementsCount > 0) {
			createPendingActions();
			RecruitAction action = RecruitAction.create(game, attackingArmy.owningPlayer, pendingActions);
			action.allowPaidRecruit = false;
			action.freeRecruitLeft = (byte) (3 * attackReinforcementsCount);
			action.canRecruitOnAnyArmy = true;
			pendingActions.add(action);
		}

		byte defendingReinforcementsCount = defendingUsedDiCard[DiCardList.REINFORCEMENTS.index];

		if (!attackerWins && defendingReinforcementsCount > 0) {
			createPendingActions();
			RecruitAction action = RecruitAction.create(game, defendingArmy.owningPlayer, pendingActions);
			action.allowPaidRecruit = false;
			action.freeRecruitLeft = (byte) (3 * defendingReinforcementsCount);
			action.canRecruitOnAnyArmy = true;
			pendingActions.add(action);
		}
	}

	private void discardUsedDiCards() {

		Player attacker = attackingArmy.owningPlayer;
		byte[] attackingDiCards = attacker.diCards;
		Player defender = defendingArmy.owningPlayer;
		byte[] defendingDiCards = defender.diCards;
		byte[] discardedDiCardList = game.discardedDiCardList;

		for (int i = 0; i < DiCardList.TOTAL_BATTLE_DI_CARD_TYPE_COUNT; ++i) {

			byte usedAttackCardCount = attackingUsedDiCard[i];
			for (int j = 0; j < usedAttackCardCount; ++j) {
				DiCardList.moveDiCard(attackingDiCards, discardedDiCardList, i, attacker.name,
						KemetGame.DISCARDED_DI_CARDS, "used in battle", game);
			}

			byte usedDefenseCardCount = defendingUsedDiCard[i];
			for (int j = 0; j < usedDefenseCardCount; ++j) {
				DiCardList.moveDiCard(defendingDiCards, discardedDiCardList, i, defender.name,
						KemetGame.DISCARDED_DI_CARDS, "used in battle", game);
			}
		}
	}

	public void moveWinnerToTile() {
		if (attackerWins) {
			if (attackerDestroyed) {
				defenderRetreatTilePicked = true;
			} else {
				if (!defenderDestroyed) {
					defendingArmy.moveToTile(null);
				}
				attackingArmy.moveToTile(tile);
			}
		}
	}

	private void checkForForcedRecall() {
		if (!attackerRetreatPicked) {

			PlayerChoicePick retreatTilePick = addArmyTileRetreatMoveChoice(true, tile);
			if (!attackerWins && retreatTilePick.choiceList.size() == 0) {
				// force a recall
				addRecruitBeastFromRemovedArmy(attackingArmy);
				attackingArmy.recall();
				attackerRecall = true;
				attackerRetreatPicked = true;
				attackerRetreatTilePicked = true;
			}
		}

		if (!defenderRetreatPicked) {
			PlayerChoicePick retreatTilePick = addArmyTileRetreatMoveChoice(false, tile);

			if (attackerWins && !attackerDestroyed && retreatTilePick.choiceList.size() == 0) {
				// force a recall
				addRecruitBeastFromRemovedArmy(defendingArmy);
				defendingArmy.recall();
				defenderRecall = true;
				defenderRetreatPicked = true;
				defenderRetreatTilePicked = true;
			}
		}
	}

	@Override
	public void setParent(Action parent) {
		this.parent = parent;
	}

	@Override
	public void enterSimulationMode(int playerIndex) {

		if (pendingActions != null) {
			pendingActions.enterSimulationMode(playerIndex);
		}

		if (!battleResolved) {
			if (playerIndex == attackingArmy.owningPlayer.getIndex()) {
				// reset the defender DI cards
				byte[] source = defendingUsedDiCard;
				Player owningPlayer = defendingArmy.owningPlayer;
				String reason = "Enter Simulation from Attacker perspective";

				recoverAndRefundAllDiCards(source, owningPlayer, reason);

			} else if (playerIndex == defendingArmy.owningPlayer.getIndex()) {
				// reset the attacker DI cards

				Player owningPlayer = attackingArmy.owningPlayer;

				recoverAndRefundAllDiCards(attackingUsedDiCard, owningPlayer, 
						"Enter Simulation from Defender perspective");

			}
		}
	}

	private void recoverAndRefundAllDiCards(byte[] source, Player owningPlayer, String reason) {
		for (int i = 0; i < source.length; i++) {
			if (source[i] > 0) {
				byte cost = owningPlayer.applyPriestOfRaBonus(DiCardList.CARDS[i].powerCost);
				if (cost > 0) {
					owningPlayer.modifyPrayerPoints((byte) (cost * source[i]), "Recuperate DI card cost");
				}

				if (game.printActivations) {
					String cardName = DiCardList.CARDS[i].name;
					game.printEvent(
							"Removing " + source[i] + " DI Card \"" + cardName + "\" from battle due to " + reason);
				}
			}
		}

		DiCardList.fillArray(source, (byte) 0);
	}

	@Override
	public void stackPendingActionOnParent(Action pendingAction) {
		parent.stackPendingActionOnParent(pendingAction);
	}

}
