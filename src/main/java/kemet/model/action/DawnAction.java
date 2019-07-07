package kemet.model.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kemet.model.BattleCard;
import kemet.model.BoardInventory;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Validation;
import kemet.model.action.choice.ChoiceInventory;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

public class DawnAction implements Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5798076013017724149L;

	private KemetGame game;

	private Action parent;

	private BattleCard[] selectedCardByCurrentPlayerIndex;
	private BattleCard[] discardedCardByCurrentPlayerIndex;
	private byte[] dawnTokenByCurrentPlayerIndex;
	private byte[] selectedPlayerOrderByCurrentPlayerIndex;
	private byte[] initiativeSelectionOrderPlayerIndex;

	public static Cache<DawnAction> CACHE = new Cache<DawnAction>(() -> new DawnAction());

	private DawnAction() {

	}

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {
		initializeData();
		// in reverse player order, pick attack card & dawn token
		boolean stepSet = false;

		int playerCount = game.playerByInitiativeList.size();
		for (int playerIndexByInitiative = playerCount - 1; playerIndexByInitiative >= 0; --playerIndexByInitiative) {

			Player player = game.getPlayerByInitiativeIndex(playerIndexByInitiative);

			// pick battle card
			if (selectedCardByCurrentPlayerIndex[playerIndexByInitiative] == null) {

				cannonicalForm.set(BoardInventory.STATE_PICK_INITIATIVE_BATTLE_CARD, player.getState(playerIndex));
				stepSet = true;
				break;
			}

			// pick discard card
			if (discardedCardByCurrentPlayerIndex[playerIndexByInitiative] == null) {
				cannonicalForm.set(BoardInventory.STATE_PICK_INITIATIVE_DISCARD, player.getState(playerIndex));
				stepSet = true;
				break;
			}

			// pick dawn token
			if (dawnTokenByCurrentPlayerIndex[playerIndexByInitiative] == -1) {
				cannonicalForm.set(BoardInventory.STATE_PICK_INITIATIVE_DAWN_TOKEN, player.getState(playerIndex));
				stepSet = true;
				break;
			}
		}

		// once all picked, ask all players to pick position in initiative selection
		// order, skip last player
		if (!stepSet && initiativeSelectionOrderPlayerIndex != null) {
			for (int i = 0; i < playerCount; ++i) {

				int currentSelectingPlayerIndex = initiativeSelectionOrderPlayerIndex[i];
				Player player = game.getPlayerByIndex(currentSelectingPlayerIndex);
				if (selectedPlayerOrderByCurrentPlayerIndex[currentSelectingPlayerIndex] == -1) {
					cannonicalForm.set(BoardInventory.STATE_PICK_INITIATIVE_ORDER, player.getState(playerIndex));
					stepSet = true;
					break;
				}
			}
		}

		for (int playerIndexByInitiative = playerCount - 1; playerIndexByInitiative >= 0; --playerIndexByInitiative) {
			byte dawnStrength = getPlayerDawnBattleStrengthByInitiativeIndex(playerIndexByInitiative);
			Player player = game.getPlayerByInitiativeIndex(playerIndexByInitiative);
			int canonicalPlayerIndex = player.getCanonicalPlayerIndex(playerIndex);
			cannonicalForm.set(BoardInventory.PLAYER_DAWN_STRENGTH + canonicalPlayerIndex, dawnStrength);
		}

		if (initiativeSelectionOrderPlayerIndex != null) {

			for (int currentPlayerIndex = playerCount -1; currentPlayerIndex >= 0; --currentPlayerIndex) {
				byte order = selectedPlayerOrderByCurrentPlayerIndex[currentPlayerIndex];
				if (order != -1) {
					Player player = game.getPlayerByIndex(currentPlayerIndex);
					int canonicalPlayerIndex = player.getCanonicalPlayerIndex(playerIndex);
					cannonicalForm.set(BoardInventory.PLAYER_SELECTED_ORDER
							+ canonicalPlayerIndex * BoardInventory.PLAYER_COUNT + order - 1, (byte) 1);
				}
			}
		}
	}

	@Override
	public void initialize() {
		game = null;
		parent = null;
		selectedCardByCurrentPlayerIndex = null;
		discardedCardByCurrentPlayerIndex = null;
		dawnTokenByCurrentPlayerIndex = null;
		selectedPlayerOrderByCurrentPlayerIndex = null;
		initiativeSelectionOrderPlayerIndex = null;

	}

	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {
		currentGame.validate(game);

		if (expectedParent != parent) {
			Validation.validationFailed("Action parent isn't as expected.");
		}
	}

	@Override
	public DawnAction deepCacheClone() {
		// create the object
		DawnAction clone = CACHE.create();

		// copy all objects
		clone.game = game;
		clone.parent = parent;

		if (selectedCardByCurrentPlayerIndex != null) {

			clone.selectedCardByCurrentPlayerIndex = Arrays.copyOf(selectedCardByCurrentPlayerIndex,
					selectedCardByCurrentPlayerIndex.length);
			clone.discardedCardByCurrentPlayerIndex = Arrays.copyOf(discardedCardByCurrentPlayerIndex,
					discardedCardByCurrentPlayerIndex.length);
			clone.dawnTokenByCurrentPlayerIndex = Arrays.copyOf(dawnTokenByCurrentPlayerIndex,
					dawnTokenByCurrentPlayerIndex.length);
			clone.selectedPlayerOrderByCurrentPlayerIndex = Arrays.copyOf(selectedPlayerOrderByCurrentPlayerIndex,
					selectedPlayerOrderByCurrentPlayerIndex.length);
		}

		if (initiativeSelectionOrderPlayerIndex != null) {
			clone.initiativeSelectionOrderPlayerIndex = Arrays.copyOf(initiativeSelectionOrderPlayerIndex,
					initiativeSelectionOrderPlayerIndex.length);
		}

		return clone;
	}

	@Override
	public void release() {
		// null all references
		game = null;
		parent = null;
		selectedCardByCurrentPlayerIndex = null;
		discardedCardByCurrentPlayerIndex = null;
		dawnTokenByCurrentPlayerIndex = null;
		selectedPlayerOrderByCurrentPlayerIndex = null;
		initiativeSelectionOrderPlayerIndex = null;

		CACHE.release(this);
	}

	@Override
	public void relink(KemetGame clone) {
		// relink game
		this.game = clone;
	}

	public static DawnAction create(KemetGame game, Action parent) {
		DawnAction create = CACHE.create();
		create.initialize();
		create.game = game;
		create.parent = parent;
		return create;
	}

	public KemetGame getGame() {
		return game;
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {
		initializeData();
		// in reverse player order, pick attack card & dawn token

		int playerCount = game.playerByInitiativeList.size();
		for (int playerIndexByInitiative = playerCount - 1; playerIndexByInitiative >= 0; --playerIndexByInitiative) {

			Player player = game.getPlayerByInitiativeIndex(playerIndexByInitiative);

			// pick battle card
			if (selectedCardByCurrentPlayerIndex[playerIndexByInitiative] == null) {
				return createBattleCardSelectionAction(player, playerIndexByInitiative, false);
			}

			// pick discard card
			if (discardedCardByCurrentPlayerIndex[playerIndexByInitiative] == null) {
				return createBattleCardSelectionAction(player, playerIndexByInitiative, true);
			}

			// pick dawn token
			if (dawnTokenByCurrentPlayerIndex[playerIndexByInitiative] == -1) {

				if (player.initiativeTokens == 0) {
					dawnTokenByCurrentPlayerIndex[playerIndexByInitiative] = 0;
				} else {
					return createDawnTokenSelectionAction(player, playerIndexByInitiative);
				}
			}
		}

		resolveInitiativeSelectionOrder();

		// once all picked, ask all players to pick position in initiative selection
		// order, skip last player

		for (int i = 0; i < playerCount; ++i) {
			int currentSelectingPlayerIndex = initiativeSelectionOrderPlayerIndex[i];
			if (selectedPlayerOrderByCurrentPlayerIndex[currentSelectingPlayerIndex] == -1) {

				PlayerChoicePick createInitiativeSelectionAction = createInitiativeSelectionAction(
						currentSelectingPlayerIndex);

				return createInitiativeSelectionAction;
			}
		}

		return null;
	}

	private void checkToForceLastOrderSelection() {
		int playerCount = game.playerByInitiativeList.size();
		int choiceLeft = 0;
		int remainingPlayerIndex = 0;
		for (int i = 0; i < playerCount; ++i) {
			int currentSelectingPlayerIndex = initiativeSelectionOrderPlayerIndex[i];
			if (selectedPlayerOrderByCurrentPlayerIndex[currentSelectingPlayerIndex] == -1) {
				choiceLeft++;
				remainingPlayerIndex = currentSelectingPlayerIndex;
			}
		}

		if (choiceLeft == 1) {
			for (byte initiativeOrder = 1; initiativeOrder <= playerCount; ++initiativeOrder) {
				if (!isPositionAlreadySelected(initiativeOrder)) {
					selectedPlayerOrderByCurrentPlayerIndex[remainingPlayerIndex] = initiativeOrder;
					adjustPlayerOrder();
				}
			}
		}
	}

	private void adjustPlayerOrder() {
		int playerCount = game.playerByInitiativeList.size();

		List<Player> newInitiativeList = new ArrayList<>(game.playerByInitiativeList);

		for (int playerIndex = 0; playerIndex < playerCount; ++playerIndex) {
			int selectedPlayerOrder = selectedPlayerOrderByCurrentPlayerIndex[playerIndex];

			newInitiativeList.set(selectedPlayerOrder - 1, game.getPlayerByIndex(playerIndex));
		}

		game.playerByInitiativeList = newInitiativeList;

	}

	private PlayerChoicePick createInitiativeSelectionAction(int currentSelectingPlayerIndex) {
		Player playerByIndex = game.getPlayerByIndex(currentSelectingPlayerIndex);
		PlayerChoicePick pick = new PlayerChoicePick(game, playerByIndex, this);
		int playerCount = game.playerByInitiativeList.size();

		for (byte initiativeOrder = 1; initiativeOrder <= playerCount; ++initiativeOrder) {
			if (!isPositionAlreadySelected(initiativeOrder)) {
				PickPlayerOrder pickBattleCardChoice = new PickPlayerOrder(game, playerByIndex, initiativeOrder);
				pick.choiceList.add(pickBattleCardChoice);
			}
		}

		return pick;
	}

	public class PickPlayerOrder extends PlayerChoice {

		public int playerIndexByInitiative;
		public byte playerOrder;

		public PickPlayerOrder(KemetGame game, Player player, byte playerOrder) {
			super(game, player);
			this.playerOrder = playerOrder;
		}

		@Override
		public String describe() {
			String action = "Battle for initiative : Select player order ";

			return action + playerOrder;
		}

		@Override
		public void choiceActivate() {
			selectedPlayerOrderByCurrentPlayerIndex[player.getIndex()] = playerOrder;

			checkToForceLastOrderSelection();
		}

		@Override
		public int getIndex() {
			return ChoiceInventory.PICK_PLAYER_ORDER + playerOrder - 1;
		}

	}

	private boolean isPositionAlreadySelected(int initiativeOrder) {
		for (byte selectedOrder : selectedPlayerOrderByCurrentPlayerIndex) {
			if (selectedOrder == initiativeOrder) {
				return true;
			}
		}
		return false;
	}

	private void resolveInitiativeSelectionOrder() {
		if (initiativeSelectionOrderPlayerIndex == null) {
			int playerCount = game.playerByInitiativeList.size();
			
			for (int i = 0; i < playerCount; ++i) {
				Player player = game.playerByInitiativeList.get(i);
				player.checkToRecuperateAllBattleCards();
			}
			
			initiativeSelectionOrderPlayerIndex = new byte[playerCount];

			byte[] scoreByPlayerInitiativeIndex = new byte[playerCount];
			for (int i = 0; i < playerCount; ++i) {
				scoreByPlayerInitiativeIndex[i] = getPlayerDawnBattleStrengthByInitiativeIndex(i);
			}

			for (int i = 0; i < playerCount; ++i) {
				int indexOfLargest = getIndexOfLargest(scoreByPlayerInitiativeIndex);
				initiativeSelectionOrderPlayerIndex[i] = (byte) game.getPlayerByInitiativeIndex(indexOfLargest)
						.getIndex();
				scoreByPlayerInitiativeIndex[indexOfLargest] = -1;
			}
		}
	}

	private byte getPlayerDawnBattleStrengthByInitiativeIndex(int initiativeIndex) {
		BattleCard battleCard = selectedCardByCurrentPlayerIndex[initiativeIndex];
		if( battleCard == null ) {
			return (byte)0;
		}
		byte attackBonus = battleCard.attackBonus;
		byte dawnTokenCount = dawnTokenByCurrentPlayerIndex[initiativeIndex];
		return (byte) (attackBonus + dawnTokenCount);
	}

	public int getIndexOfLargest(byte[] array) {
		if (array == null || array.length == 0)
			return -1; // null or empty

		int largest = 0;
		for (int i = 1; i < array.length; i++) {
			if (array[i] > array[largest])
				largest = i;
		}
		return largest;
	}

	private PlayerChoicePick createDawnTokenSelectionAction(Player player, int playerIndexByInitiative) {
		PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

		int maxDawnToken = Math.min(BoardInventory.MAX_DAWN_TOKEN, player.initiativeTokens);

		for (byte dawnTokenCount = 0; dawnTokenCount <= maxDawnToken; ++dawnTokenCount) {
			PickDawnTokenChoice pickBattleCardChoice = new PickDawnTokenChoice(game, player, playerIndexByInitiative,
					dawnTokenCount);
			pick.choiceList.add(pickBattleCardChoice);
		}

		return pick;

	}

	public PlayerChoicePick createBattleCardSelectionAction(Player player, int playerIndexByInitiative,
			boolean isDiscard) {

		PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

		for (BattleCard card : player.availableBattleCards) {

			PickBattleCardChoice pickBattleCardChoice = new PickBattleCardChoice(game, player);
			pickBattleCardChoice.card = card;
			pickBattleCardChoice.isDiscard = isDiscard;
			pickBattleCardChoice.playerIndexByInitiative = playerIndexByInitiative;
			pick.choiceList.add(pickBattleCardChoice);

		}

		return pick;

	}

	private void initializeData() {
		if (selectedCardByCurrentPlayerIndex == null) {

			int playerCount = game.playerByInitiativeList.size();

			selectedCardByCurrentPlayerIndex = new BattleCard[playerCount];

			discardedCardByCurrentPlayerIndex = new BattleCard[playerCount];

			dawnTokenByCurrentPlayerIndex = new byte[playerCount];
			Arrays.fill(dawnTokenByCurrentPlayerIndex, (byte) -1);

			selectedPlayerOrderByCurrentPlayerIndex = new byte[playerCount];
			Arrays.fill(selectedPlayerOrderByCurrentPlayerIndex, (byte) -1);
		}
	}

	@Override
	public Action getParent() {
		return parent;
	}

	@Override
	public void setParent(Action parent) {
		this.parent = parent;
	}

	public class PickDawnTokenChoice extends PlayerChoice {

		public int playerIndexByInitiative;
		public byte tokenCount;

		public PickDawnTokenChoice(KemetGame game, Player player, int playerIndexByInitiative, byte tokenCount) {
			super(game, player);
			this.playerIndexByInitiative = playerIndexByInitiative;
			this.tokenCount = tokenCount;
		}

		@Override
		public String describe() {
			String action = "Battle for initiative : Use ";

			return action + tokenCount + " dawn tokens.";
		}

		@Override
		public void choiceActivate() {
			dawnTokenByCurrentPlayerIndex[playerIndexByInitiative] = tokenCount;
			player.removeInitiativeToken(tokenCount);
		}

		@Override
		public int getIndex() {
			return ChoiceInventory.PICK_DAWN_TOKEN + tokenCount;
		}

	}

	public class PickBattleCardChoice extends PlayerChoice {

		public BattleCard card;
		public boolean isDiscard;
		public int playerIndexByInitiative;

		public PickBattleCardChoice(KemetGame game, Player player) {
			super(game, player);
		}

		@Override
		public String describe() {
			String action = "Battle for initiative : Use ";
			if (isDiscard) {
				action = "Battle for initiative : Discard ";
			}

			return action + "Battle card " + card;
		}

		@Override
		public void choiceActivate() {

			if (isDiscard) {
				player.discardBattleCard(card);
				discardedCardByCurrentPlayerIndex[playerIndexByInitiative] = card;
			} else {
				if (game.isSimulation()) {
					if (game.simulatedPlayerIndex != player.getIndex()) {
						// skip discard card selection for other players during simulations
						discardedCardByCurrentPlayerIndex[playerIndexByInitiative] = card;
					}
				}
				selectedCardByCurrentPlayerIndex[playerIndexByInitiative] = card;
				player.useBattleCard(card);

				// force discard
				List<BattleCard> availableBattleCards = player.availableBattleCards;
				if (availableBattleCards.size() == 1) {
					BattleCard forcedDiscard = availableBattleCards.get(0);
					player.discardBattleCard(forcedDiscard);
					discardedCardByCurrentPlayerIndex[playerIndexByInitiative] = forcedDiscard;
				}
			}

		}

		@Override
		public int getIndex() {
			return card.getPickChoiceIndex();
		}

	}

}
