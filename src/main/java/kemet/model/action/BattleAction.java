package kemet.model.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import kemet.model.Army;
import kemet.model.BattleCard;
import kemet.model.BoardInventory;
import kemet.model.DiCard;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.PowerList;
import kemet.model.Tile;
import kemet.model.Validation;
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

	/**
	 * 
	 */
	private static final long serialVersionUID = -4818172417656461599L;

	private KemetGame game;

	private Action parent;

	public Army attackingArmy;
	public BattleCard attackingDiscardBattleCard;
	public BattleCard attackingUsedBattleCard;
	public List<DiCard> attackingUsedDiCard = new ArrayList<>();
	public boolean attackerRecall = false;

	public Army defendingArmy;
	public BattleCard defendingDiscardBattleCard;
	public BattleCard defendingUsedBattleCard;
	public List<DiCard> defendingUsedDiCard = new ArrayList<>();
	public boolean defenderRecall = false;

	public Tile tile;
	public boolean attackerWins;
	public boolean attackerDestroyed;
	public boolean defenderDestroyed;
	public byte defenderScore;
	public byte attackerScore;
	public boolean battleResolved;
	public boolean attackerRetreatPicked;
	public boolean attackerRetreatTilePicked;
	public boolean defenderRetreatPicked;
	public boolean defenderRetreatTilePicked;

	public ChainedAction pendingActions;

	private boolean incrementedCounter = false;

	@Override
	public void initialize() {

		game = null;

		parent = null;

		attackingArmy = null;
		attackingDiscardBattleCard = null;
		attackingUsedBattleCard = null;
		attackingUsedDiCard.clear();
		attackerRecall = false;

		defendingArmy = null;
		defendingDiscardBattleCard = null;
		defendingUsedBattleCard = null;
		defendingUsedDiCard.clear();
		defenderRecall = false;

		tile = null;
		attackerWins = false;
		attackerDestroyed = false;
		defenderDestroyed = false;
		defenderScore = 0;
		attackerScore = 0;
		battleResolved = false;
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
		clone.attackerRetreatPicked = attackerRetreatPicked;
		clone.attackerRetreatTilePicked = attackerRetreatTilePicked;
		clone.defenderRetreatPicked = defenderRetreatPicked;
		clone.defenderRetreatTilePicked = defenderRetreatTilePicked;
		clone.attackingUsedDiCard.clear();
		clone.attackingUsedDiCard.addAll(attackingUsedDiCard);
		clone.defendingUsedDiCard.clear();
		clone.defendingUsedDiCard.addAll(defendingUsedDiCard);
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
		attackingUsedDiCard.clear();
		defendingUsedDiCard.clear();
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
		byte score = attackingArmy.getScore(true);
		score += attackingUsedBattleCard.attackBonus;
		return score;
	}

	public byte calculateDefenderScore() {
		byte score = defendingArmy.getScore(false);
		score += defendingUsedBattleCard.attackBonus;
		return score;
	}

	public byte calculateAttackerDamage() {
		byte score = attackingUsedBattleCard.bloodBonus;
		score += attackingArmy.owningPlayer.damageBonus;

		if (attackingArmy.beast != null) {
			score += attackingArmy.beast.damageBonus;
		}

		return score;
	}

	public byte calculateDefenderDamage() {
		byte score = defendingUsedBattleCard.bloodBonus;
		score += defendingArmy.owningPlayer.damageBonus;

		if (defendingArmy.beast != null) {
			score += defendingArmy.beast.damageBonus;
		}
		return score;
	}

	public byte calculateAttackerShield() {
		byte score = attackingUsedBattleCard.shieldBonus;
		score += attackingArmy.owningPlayer.shieldBonus;

		if (attackingArmy.beast != null) {
			score += attackingArmy.beast.shieldBonus;
		}

		return score;
	}

	public byte calculateDefenderShield() {
		byte score = defendingUsedBattleCard.shieldBonus;
		score += defendingArmy.owningPlayer.shieldBonus;

		if (defendingArmy.beast != null) {
			score += defendingArmy.beast.shieldBonus;
		}

		return score;
	}

	public byte calculateDamageOnAttacker() {
		return (byte) Math.min(Math.max(calculateDefenderDamage() - calculateAttackerShield(), 0),
				attackingArmy.armySize);
	}

	public byte calculateDamageOnDefender() {
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
			pendingActions.add(BeastRecruitAction.create(game, removedArmy.owningPlayer, pendingActions,
					removedArmy.beast));
		}
	}

	private void createPendingActions() {
		if( pendingActions == null ) {
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
		} else {
			applyHolyWarBonus(defendingArmy.owningPlayer);
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

	public void giveBattleVictoryPoints() {
		// give winner victory point if attacker & still alive
		if (attackerWins) {
			if (!attackerDestroyed) {
				attackingArmy.owningPlayer.addBattleVictoryPoint();
			}
		} else {
			if (!defenderDestroyed) {
				if (defendingArmy.owningPlayer.hasPower(PowerList.BLUE_3_DEFENSIVE_VICTORY)) {
					defendingArmy.owningPlayer.addBattleVictoryPoint();
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

	public void applyBattleBleed() {
		byte attackerBleed = calculateDamageOnAttacker();
		byte defenderBleed = calculateDamageOnDefender();

		// apply bleeds
		attackingArmy.bleedArmy(attackerBleed, "defender damage score");

		applyCrusadePowerBonusToDamage(attackerBleed, defendingArmy.owningPlayer);
		applyHonorInBattlePowerBonusToDamage(attackerBleed, attackingArmy.owningPlayer);

		defendingArmy.bleedArmy(defenderBleed, "attacker damage score");

		applyCrusadePowerBonusToDamage(defenderBleed, attackingArmy.owningPlayer);
		applyHonorInBattlePowerBonusToDamage(defenderBleed, defendingArmy.owningPlayer);
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
						String message = "Reached a battle simulation state where the next action is for the opponent DEFENDER to pick a hidden ATTACK card which should be simulated by the MCTS.";
						log.error(message);
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

			checkToResolveBattle();
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

		private BattleCard pickCardForPlayerBasedOnNeuralNetwork(Player defender) {
			game.resetCachedChoices();

			ByteCanonicalForm canonicalForm = game.getCanonicalForm(defender.getIndex());

			List<BattleCard> availableBattleCards = defender.availableBattleCards;

			validateCanonicalFormForSimulatedMove(defender, canonicalForm);

			StackingMCTS simulationMcts = game.simulationMcts;
			MctsBoardInformation mctsBoardInformation = simulationMcts.getBoardInformation(canonicalForm);
			PolicyVector policy = mctsBoardInformation.choiceValuePredictionForBoardPs;
			if (policy == null) {
				policy = fetchPolicyFromNeuralNetwork(defender, canonicalForm, simulationMcts, mctsBoardInformation);
			}

			int simulatedActionIndex = policy.pickRandomAction();

			BattleCard found = null;
			List<BattleCard> list = availableBattleCards;
			for (BattleCard battleCard : list) {
				if (battleCard.getPickChoiceIndex() == simulatedActionIndex) {
					found = battleCard;
					break;
				}
			}

			if (found == null) {
				// force a card at random.
				StringBuilder builder = new StringBuilder();
				defender.describePlayer(builder);
				String message = "MCTS battle card selection at index {} couldn't be found in the player's available cards. BattleCard Start index {}, Player : {}";
				log.error(message, simulatedActionIndex, ChoiceInventory.PICK_BATTLE_CARD_CHOICE, builder);

				found = list.get(0);
			}

			return found;
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

		private PolicyVector fetchPolicyFromNeuralNetwork(Player defender, ByteCanonicalForm canonicalForm,
				StackingMCTS simulationMcts, MctsBoardInformation mctsBoardInformation) {
			PolicyVector policy;
			Pair<PolicyVector, Float> predict = simulationMcts.pooler.neuralNet.predict(canonicalForm);
			mctsBoardInformation.boardValue = predict.getValue();
			mctsBoardInformation.choiceValuePredictionForBoardPs = predict.getLeft();
			boolean[] validMoves = game.getValidMoves();

			for (BattleCard card : defender.availableBattleCards) {
				if (validMoves[card.getPickChoiceIndex()] != true) {
					log.error("available battle card {} not showing up in valid moves {}", card.index, validMoves);

					game.resetCachedChoices();
					validMoves = game.getValidMoves();
				}
			}

			for (BattleCard card : defender.usedBattleCards) {
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

		return pick;

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
		if (defendingDiscardBattleCard != null && attackingDiscardBattleCard != null) {
			resolveBattle();
		}
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {

		// force defender to go first if this game is a simulation from the defender
		// perspective
		if (isDefenderFirstToPick()) {
			if (defendingUsedBattleCard == null) {
				return addPickBattleCardChoice(defendingArmy.owningPlayer, false, false).validate();
			} else if (defendingDiscardBattleCard == null) {
				return addPickBattleCardChoice(defendingArmy.owningPlayer, false, true).validate();
			}
		}

		if (attackingUsedBattleCard == null) {
			return addPickBattleCardChoice(attackingArmy.owningPlayer, true, false).validate();
		}
		if (attackingDiscardBattleCard == null) {
			return addPickBattleCardChoice(attackingArmy.owningPlayer, true, true).validate();
		}
		if (defendingUsedBattleCard == null) {
			return addPickBattleCardChoice(defendingArmy.owningPlayer, false, false).validate();
		}
		if (defendingDiscardBattleCard == null) {
			return addPickBattleCardChoice(defendingArmy.owningPlayer, false, true).validate();
		}

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

		if (pendingActions != null) {
			PlayerChoicePick nextPlayerChoicePick = pendingActions.getNextPlayerChoicePick();
			if (nextPlayerChoicePick != null) {
				return nextPlayerChoicePick;
			}
		}

		if (!incrementedCounter) {
			incrementedCounter = true;
			game.battleCount++;
		}
		// battle is over
		return null;
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

		cannonicalForm.set(BoardInventory.BATTLE_ATTACKER_STRENGTH, attackingArmy.getAttackStrength());
		cannonicalForm.set(BoardInventory.BATTLE_ATTACKER_SHIELD, attackingArmy.getAttackShield());
		cannonicalForm.set(BoardInventory.BATTLE_ATTACKER_DAMAGE, attackingArmy.getAttackDamage());
		cannonicalForm.set(BoardInventory.BATTLE_DEFENDER_STRENGTH, defendingArmy.getDefendingStrength());
		cannonicalForm.set(BoardInventory.BATTLE_DEFENDER_SHIELD, defendingArmy.getDefendingShield());
		cannonicalForm.set(BoardInventory.BATTLE_DEFENDER_DAMAGE, defendingArmy.getDefendingDamage());

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
			} else {
				if (pendingActions != null) {
					pendingActions.fillCanonicalForm(cannonicalForm, playerIndex);
				}
			}
		}
	}

	public void resolveBattle() {
		if (!battleResolved) {
			// resolve battle

			resolveBattleWinner();

			applyBattleBleed();

			applyAttackCardBleed();

			checkIfAmyIsDestroyed();

			giveDawnTokenToLoser();

			giveBattleVictoryPoints();

			checkForForcedRecall();

			moveWinnerToTile();

			battleResolved = true;
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

	public byte getHighestAttackerScore() {
		if (attackingUsedBattleCard == null) {
			byte score = attackingArmy.getScore(true);
			score += attackingUsedBattleCard.attackBonus;
			return score;
		} else {
			return calculateAttackerScore();
		}
	}

	@Override
	public void setParent(Action parent) {
		this.parent = parent;
	}

}
