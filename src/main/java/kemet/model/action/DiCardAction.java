package kemet.model.action;

import java.util.List;

import kemet.model.DiCardList;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.PowerList;
import kemet.model.Validation;
import kemet.model.action.choice.Choice;
import kemet.model.action.choice.ChoiceInventory;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class DiCardAction extends EndableAction implements DiCardExecutor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8242900401097675710L;

	public KemetGame game;

	@Getter
	public Player player;

	private VetoAction veto;
	private Action diAction;

	public Action parent;

	protected DiCardAction() {

	}

	@Override
	public void internalInitialize() {
		game = null;
		player = null;

		veto = null;
		diAction = null;

		parent = null;
	}

	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {
		currentGame.validate(game);
		currentGame.validate(player);

		if (veto != null) {
			veto.validate(this, currentGame);
		}
		if (diAction != null) {
			diAction.validate(this, currentGame);
		}
		if (expectedParent != parent) {
			Validation.validationFailed("Action parent isn't as expected.");
		}
	}

	@Override
	public void relink(KemetGame clone) {
		this.game = clone;
		player = clone.getPlayerByCopy(player);

		if (veto != null) {
			veto.relink(clone);
		}
		if (diAction != null) {
			diAction.relink(clone);
		}
		super.relink(clone);

	}

	public void copy(DiCardAction clone) {
		clone.game = game;
		clone.player = player;

		clone.veto = veto;
		if (veto != null) {
			clone.veto = veto.deepCacheClone();
			clone.veto.setParent(clone);
		}

		clone.diAction = diAction;
		if (diAction != null) {
			clone.diAction = diAction.deepCacheClone();
			clone.diAction.setParent(clone);
		}

		clone.parent = parent;

		super.copy(clone);
	}

	@Override
	public void release() {
		if (veto != null) {
			veto.release();
		}

		if (diAction != null) {
			diAction.release();
		}
		clear();
	}

	@Override
	public void clear() {
		game = null;
		player = null;
		veto = null;
		diAction = null;
		parent = null;

		super.clear();
	}

	@Override
	public Action getParent() {
		return parent;
	}

	public class ActivateDiChoice extends PlayerChoice {

		private int cardIndex;

		public ActivateDiChoice(KemetGame game, Player player, int cardIndex) {
			super(game, player);
			this.cardIndex = cardIndex;
		}

		@Override
		public void choiceActivate() {

			byte cost = DiCardList.CARDS[cardIndex].powerCost;
			cost = player.applyPriestOfRaBonus(cost);

			if (cost > 0) {
				player.modifyPrayerPoints((byte) -cost, "Used a DI card.");
			}

			createVetoAction(cardIndex);

		}

		@Override
		public String describe() {

			return "Use DI Card : " + DiCardList.CARDS[cardIndex].toString();

		}

		@Override
		public int getIndex() {
			return ChoiceInventory.ACTIVATE_DI_CARD + cardIndex;
		}

	}

	public void createVetoAction(int diCardIndex) {
		Player diCardPlayer = player;
		createVetoAction(diCardIndex, diCardPlayer);
	}

	public void createVetoAction(int diCardIndex, Player diCardPlayer) {
		DiCardList.moveDiCard(diCardPlayer.diCards, game.discardedDiCardList, diCardIndex, diCardPlayer.name,
				KemetGame.DISCARDED_DI_CARDS, "activated", game);

		veto = VetoAction.create(game, diCardPlayer, this, diCardIndex);

		tryToApplyVetoAction();
	}

	protected void tryToApplyVetoAction() {
		if (veto != null) {
			veto.applyDiCard(this);

			if (veto.isEnded()) {
				veto = null;
			}
		}
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {

		tryToApplyVetoAction();

		if (veto != null) {
			return veto.getNextPlayerChoicePick();
		}

		if (diAction != null) {
			PlayerChoicePick tempNextChoice = diAction.getNextPlayerChoicePick();
			if (tempNextChoice == null) {
				diAction = null;
			} else {
				return tempNextChoice;
			}
		}

		if (isEnded()) {
			return null;
		}

		return null;

	}

	public void addDiCardChoice(List<Choice> choiceList, int cardIndex) {
		byte cardCount = player.diCards[cardIndex];
		if (cardCount > 0) {

			// ensure the player can afford it
			if (player.getPrayerPoints() >= DiCardList.CARDS[cardIndex].powerCost) {
				ActivateDiChoice choice = new ActivateDiChoice(game, player, cardIndex);
				choiceList.add(choice);
			}
		}
	}

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {

		if (isEnded()) {
			return;
		}

		if (veto != null) {
			veto.fillCanonicalForm(cannonicalForm, playerIndex);
		}

		if (diAction != null) {
			diAction.fillCanonicalForm(cannonicalForm, playerIndex);
		}

	}

	@Override
	public void setParent(Action parent) {
		this.parent = parent;
	}

	@Override
	public void applyDiCard(int index) {

		if (index == DiCardList.PRAYER.index) {
			applyPrayerDiCard();
		} else if (index == DiCardList.MANA_THEFT.index) {
			applyManaTheftDiCard();
		} else if (index == DiCardList.DIVINE_MEMORY.index) {
			PickDiCardAction pickAction = PickDiCardAction.create(game, player, this);
			pickAction.moveToDiscard = false;
			DiCardList.copyArray(game.discardedDiCardList, pickAction.availableDiCards);
			diAction = pickAction;
		} else if (index == DiCardList.RAINING_FIRE.index) {
			
			RainingFireAction rfa = RainingFireAction.create(game, player, this);
			diAction = rfa;
			
		} else if (index == DiCardList.ENLISTMENT.index) {
			RecruitAction recruit = RecruitAction.create(game, player, this);
			recruit.allowPaidRecruit = false;
			recruit.freeRecruitLeft = 2;
			if( player.hasPower(PowerList.BLUE_1_RECRUITING_SCRIBE_1)) {
				recruit.freeRecruitLeft += 2;
			}
			diAction = recruit;
			
		} else {
			log.error("unknown di card index {}", index);
		}

	}

	private void applyPrayerDiCard() {
		player.modifyPrayerPoints(player.getPrayActionPowerIncrease(), "Di Card");
	}

	private void applyManaTheftDiCard() {
		List<Player> playerByInitiativeList = game.playerByInitiativeList;
		for (Player currentPlayer : playerByInitiativeList) {
			if (currentPlayer.getIndex() == player.getIndex()) {
				// boost by 1 + bonuses
				byte boost = 1;
				if (player.hasPower(PowerList.BLACK_4_DIVINE_STRENGTH)) {
					boost += 1;
				}

				currentPlayer.modifyPrayerPoints(boost, "Used Mana Theft DI card.");
			} else {
				// lose 1 mana
				if (currentPlayer.getPrayerPoints() > 0) {
					currentPlayer.modifyPrayerPoints((byte) -1, "Victim of Mana Theft DI card.");
				}
			}
		}
	}

	public void addGenericDiCardChoice(List<Choice> choiceList) {
		addDiCardChoice(choiceList, DiCardList.PRAYER.index);
		addDiCardChoice(choiceList, DiCardList.MANA_THEFT.index);
		addDiCardChoice(choiceList, DiCardList.DIVINE_MEMORY.index);
		addDiCardChoice(choiceList, DiCardList.RAINING_FIRE.index);
	}

}
