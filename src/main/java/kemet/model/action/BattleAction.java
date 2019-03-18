package kemet.model.action;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import kemet.model.Army;
import kemet.model.BattleCard;
import kemet.model.BoardInventory;
import kemet.model.DiCard;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Tile;
import kemet.model.Validation;
import kemet.model.action.choice.ChoiceInventory;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

public class BattleAction implements Action {
	public static final Logger LOGGER = Logger.getLogger(BattleAction.class.getName());

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
	}

	@Override
	public void relink(KemetGame clone) {
		this.game = clone;

		attackingArmy = clone.getArmyByCopy(attackingArmy);
		defendingArmy = clone.getArmyByCopy(defendingArmy);

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

	public byte calculateAttackerBleed() {
		byte score = attackingUsedBattleCard.bloodBonus;
		score += attackingArmy.owningPlayer.bloodBonus;

		if (attackingArmy.beast != null) {
			score += attackingArmy.beast.bloodBonus;
		}

		return score;
	}

	public byte calculateDefenderBleed() {
		byte score = defendingUsedBattleCard.bloodBonus;
		score += defendingArmy.owningPlayer.bloodBonus;

		if (defendingArmy.beast != null) {
			score += defendingArmy.beast.bloodBonus;
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
		return (byte) Math.min(Math.max(calculateDefenderBleed() - calculateAttackerShield(), 0),
				attackingArmy.armySize);
	}

	public byte calculateDamageOnDefender() {
		return (byte) Math.min(Math.max(calculateAttackerBleed() - calculateDefenderShield(), 0),
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
			attackingArmy.destroyArmy();
			attackerRetreatPicked = true;
			attackerRetreatTilePicked = true;
		}

		defenderDestroyed = defendingArmy.armySize <= 0;
		if (defenderDestroyed) {
			defendingArmy.destroyArmy();
			defenderRetreatPicked = true;
			defenderRetreatTilePicked = true;
		}
	}

	public void resolveBattleWinner() {
		// resolve battle
		attackerScore = calculateAttackerScore();
		defenderScore = calculateDefenderScore();

		attackerWins = attackerScore > defenderScore;

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

	public void giveBattleVictoryPoints() {
		// give winner victory point if attacker & still alive
		if (attackerWins) {
			if (!attackerDestroyed) {
				attackingArmy.owningPlayer.addBattleVictoryPoint();
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
		attackingArmy.bleedArmy(attackerBleed, "defender bleed score");
		defendingArmy.bleedArmy(defenderBleed, "attacker bleed score");
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

			if (isDiscard) {
				player.discardBattleCard(card);
			} else {
				player.useBattleCard(card);

				// force discard
				List<BattleCard> availableBattleCards = player.availableBattleCards;
				if (availableBattleCards.size() == 1) {
					BattleCard forcedDiscard = availableBattleCards.get(0);
					player.discardBattleCard(forcedDiscard);
					if (isAttacker) {
						attackingDiscardBattleCard = forcedDiscard;
					} else {
						defendingDiscardBattleCard = forcedDiscard;
					}
				}
			}

			if (isAttacker) {
				if (isDiscard) {
					attackingDiscardBattleCard = card;
				} else {
					attackingUsedBattleCard = card;
					if( game.simulatedPlayerIndex >= 0 && game.simulatedPlayerIndex != player.index ) {
						// skip discard card selection for other players during simulations
						attackingDiscardBattleCard = card;
					}
					
				}
			} else {
				if (isDiscard) {
					defendingDiscardBattleCard = card;
				} else {
					defendingUsedBattleCard = card;

					if( game.simulatedPlayerIndex >= 0 && game.simulatedPlayerIndex != player.index ) {
						// skip discard card selection for other players during simulations
						defendingDiscardBattleCard = card;
					}

				}
			}

			checkToResolveBattle();
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
			return destinationTile.getPickChoiceIndex(player.index);
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
			return ChoiceInventory.PASS_CHOICE_INDEX;
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
		if (defendingDiscardBattleCard != null) {
			resolveBattle();
		}
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {
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

		if (!incrementedCounter) {
			incrementedCounter = true;
			game.battleCount++;
		}
		// battle is over
		return null;
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
		}

	}

	public void resolveBattle() {
		if (!battleResolved) {
			// resolve battle

			resolveBattleWinner();

			applyAttackCardBleed();

			applyBattleBleed();

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
