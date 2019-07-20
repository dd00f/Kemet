package kemet.model.action;

import java.util.List;

import kemet.model.BoardInventory;
import kemet.model.DiCardList;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Validation;
import kemet.model.action.choice.Choice;
import kemet.model.action.choice.ChoiceInventory;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

public class PickDiCardAction extends EndableAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1148319752018577895L;

	public Player player;

	private KemetGame game;
	private Action parent;
	public byte[] availableDiCards = new byte[DiCardList.TOTAL_DI_CARD_TYPE_COUNT];
	public boolean moveToDiscard;

	public static Cache<PickDiCardAction> CACHE = new Cache<PickDiCardAction>(() -> new PickDiCardAction());

	private PickDiCardAction() {

	}

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {

		cannonicalForm.set(BoardInventory.PICK_DI_STATE, player.getState(playerIndex));
		if (moveToDiscard) {

			cannonicalForm.set(BoardInventory.PICK_DI_MOVE_REST_TO_DISCARD, (byte) 1);
		}

		DiCardList.fillCanonicalForm(availableDiCards, cannonicalForm, BoardInventory.AVAILABLE_DI);

	}

	@Override
	public void internalInitialize() {
		game = null;
		player = null;
		parent = null;
		DiCardList.fillArray(availableDiCards, (byte) 0);
		moveToDiscard = false;

	}

	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {
		currentGame.validate(game);
		currentGame.validate(player);

		if (expectedParent != parent) {
			Validation.validationFailed("Action parent isn't as expected.");
		}
	}

	@Override
	public void relink(KemetGame clone) {
		this.game = clone;
		player = clone.getPlayerByCopy(player);

		super.relink(clone);
	}

	@Override
	public PickDiCardAction deepCacheClone() {

		PickDiCardAction clone = CACHE.create();

		copy(clone);

		return clone;
	}

	private void copy(PickDiCardAction clone) {
		clone.game = game;
		clone.player = player;
		clone.moveToDiscard = moveToDiscard;

		DiCardList.copyArray(availableDiCards, clone.availableDiCards);

		clone.parent = parent;

		super.copy(clone);
	}

	@Override
	public void release() {

		clear();
		CACHE.release(this);
	}

	@Override
	public void clear() {

		game = null;
		player = null;
		parent = null;

		super.clear();
	}

	public static PickDiCardAction create(KemetGame game, Player player, Action parent) {
		PickDiCardAction create = CACHE.create();
		create.initialize();
		create.game = game;
		create.player = player;
		create.parent = parent;

		return create;
	}

	@Override
	public void setParent(Action parent) {
		this.parent = parent;
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {

		if (isEnded()) {
			return null;
		}

		// pick tile
		PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

		List<Choice> choiceList = pick.choiceList;
		addDiPickChoices(choiceList);

		if (choiceList.size() == 0) {

			// no valid choices left
			end();
			return null;
		} else if (choiceList.size() == 1) {
			choiceList.get(0).activate();
			end();
			return null;
		}

		return pick.validate();
	}

	public void addDiPickChoices(List<Choice> choiceList) {

		for (int i = 0; i < availableDiCards.length; i++) {
			byte b = availableDiCards[i];
			if (b > 0) {
				PickDiChoice choice = new PickDiChoice(game, player);
				choice.index = i;
				choiceList.add(choice);
			}
		}
	}

	public class PickDiChoice extends PlayerChoice {

		public PickDiChoice(KemetGame game, Player player) {
			super(game, player);
		}

		public int index;

		@Override
		public void choiceActivate() {
			if (moveToDiscard) {
				// cards have already been moved from selection
				DiCardList.moveDiCard(availableDiCards, player.diCards, index, KemetGame.AVAILABLE_DI_CARDS,
						player.name, "Picked card from selection", game);

				DiCardList.moveAllDiCard(availableDiCards, game.discardedDiCardList, KemetGame.AVAILABLE_DI_CARDS,
						KemetGame.DISCARDED_DI_CARDS, "DI card not picked", game);

			} else {
				DiCardList.moveDiCard(game.discardedDiCardList, player.diCards, index, KemetGame.DISCARDED_DI_CARDS,
						player.name, "Picked card from discard.", game);
			}

			end();
		}

		@Override
		public String describe() {

			return "Pick DI Card " + DiCardList.CARDS[index].toString();

		}

		@Override
		public int getIndex() {
			return ChoiceInventory.PICK_DI_CARD + index;
		}
	}

	@Override
	public Action getParent() {
		return parent;
	}

}
