package kemet.model.action;

import java.util.Arrays;

import kemet.model.BoardInventory;
import kemet.model.DiCardList;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Validation;
import kemet.model.action.choice.ChoiceInventory;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

public class VetoAction extends EndableAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 16471122266807602L;

	private KemetGame game;
	private Player diCardPlayer;
	private Action parent;

	public int cardToVetoIndex;

	// by player index, check who has already passed on the veto
	public boolean[] playerVetoDone;
	public boolean isVetoed = false;
	private Player playerDoingVeto;

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {

		diCardPlayer.setCanonicalState(cannonicalForm, BoardInventory.STATE_VETO_PLAYER, playerIndex);

		cannonicalForm.set(DiCardList.CARDS[cardToVetoIndex].getVetoIndex(), diCardPlayer.getState(playerIndex));

		for (int i = 0; i < playerVetoDone.length; i++) {
			if (playerVetoDone[i]) {
				int vetoDonePlayerIndex = game.playerByInitiativeList.get(i).getCanonicalPlayerIndex(playerIndex);
				cannonicalForm.set(BoardInventory.STATE_PLAYER_VETO_DONE + vetoDonePlayerIndex, (byte) 1);
			}
		}

		if (playerDoingVeto != null) {
			playerDoingVeto.setCanonicalState(cannonicalForm, BoardInventory.STATE_PLAYER_DOING_VETO_ON_VETO,
					playerIndex);
		}

	}

	public static Cache<VetoAction> CACHE = new Cache<VetoAction>(() -> new VetoAction());

	@Override
	public void internalInitialize() {
		game = null;
		diCardPlayer = null;
		playerDoingVeto = null;
		parent = null;
		cardToVetoIndex = -1;
		playerVetoDone = null;
		isVetoed = false;
	}

	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {
		currentGame.validate(game);
		currentGame.validate(diCardPlayer);
		currentGame.validate(playerDoingVeto);
		if (expectedParent != parent) {
			Validation.validationFailed("Action parent isn't as expected.");
		}
	}

	private VetoAction() {

	}

	@Override
	public void relink(KemetGame clone) {
		this.game = clone;
		diCardPlayer = clone.getPlayerByCopy(diCardPlayer);
		playerDoingVeto = clone.getPlayerByCopy(playerDoingVeto);

		super.relink(clone);
	}

	@Override
	public VetoAction deepCacheClone() {

		VetoAction clone = CACHE.create();

		copy(clone);

		return clone;
	}

	private void copy(VetoAction clone) {
		clone.game = game;
		clone.diCardPlayer = diCardPlayer;
		clone.parent = parent;
		clone.playerDoingVeto = playerDoingVeto;

		clone.cardToVetoIndex = cardToVetoIndex;
		clone.playerVetoDone = Arrays.copyOf(playerVetoDone, playerVetoDone.length);
		clone.isVetoed = isVetoed;
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
		diCardPlayer = null;
		parent = null;
		playerDoingVeto = null;

		super.clear();
	}

	public static VetoAction create(KemetGame game, Player player, Action parent, int cardToVetoIndex) {
		VetoAction create = CACHE.create();
		create.initialize();
		create.game = game;
		create.diCardPlayer = player;
		create.parent = parent;
		create.cardToVetoIndex = cardToVetoIndex;

		return create;
	}

	@Override
	public void setParent(Action parent) {
		this.parent = parent;
	}

	public class ApplyVetoChoice extends PlayerChoice {

		public boolean applyVeto;

		public ApplyVetoChoice(KemetGame game, Player player, boolean applyVeto) {
			super(game, player);
			this.applyVeto = applyVeto;
		}

		@Override
		public String describe() {
			if (applyVeto) {

				return "Apply Veto to " + DiCardList.CARDS[cardToVetoIndex].name + " from player " + player.name;
			}

			return "Do not apply Veto to " + DiCardList.CARDS[cardToVetoIndex].name + " from player " + player.name;
		}

		@Override
		public void choiceActivate() {
			if (applyVeto) {
				useDiVetoCard(player);
				isVetoed = true;
				playerDoingVeto = player;
			}
			playerVetoDone[player.getIndex()] = true;
			
			// trigger next veto step
			parent.getNextPlayerChoicePick();
		}

		@Override
		public int getIndex() {
			if (applyVeto) {

				return ChoiceInventory.ACTIVATE_DI_CARD + DiCardList.VETO.index;
			}

			return ChoiceInventory.SKIP_DI_VETO;
		}

	}

	public class ApplyVetoToVetoChoice extends PlayerChoice {

		public boolean applyVeto;

		public ApplyVetoToVetoChoice(KemetGame game, Player player, boolean applyVeto) {
			super(game, player);
			this.applyVeto = applyVeto;
		}

		@Override
		public String describe() {
			if (applyVeto) {

				return "Apply veto to the veto of player " + playerDoingVeto.name + " on top of "
						+ DiCardList.CARDS[cardToVetoIndex].name + " from player " + player.name;
			}

			return "Do not apply Veto to the veto of player " + playerDoingVeto.name + " on top of "
					+ DiCardList.CARDS[cardToVetoIndex].name + " from player " + player.name;
		}

		@Override
		public void choiceActivate() {
			if (applyVeto) {
				useDiVetoCard(player);
				isVetoed = false;
			}
			
			// trigger DI card activation
			parent.getNextPlayerChoicePick();

			end();
		}

		@Override
		public int getIndex() {
			if (applyVeto) {
				return ChoiceInventory.ACTIVATE_DI_CARD + DiCardList.VETO.index;
			}

			return ChoiceInventory.SKIP_DI_VETO;
		}

	}

	public void useDiVetoCard(Player currentPlayer) {
		DiCardList.moveDiCard(currentPlayer.diCards, game.discardedDiCardList, DiCardList.VETO.index,
				currentPlayer.name, KemetGame.DISCARDED_DI_CARDS, "used DI card.", game);
	}

	public void applyDiCard(DiCardExecutor action) {
		getNextPlayerChoicePick();
		
		if (isEnded()) {
			if (!isVetoed) {
				action.applyDiCard(cardToVetoIndex);
			}
		}
	}
	

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {
		if (isEnded()) {
			return null;
		}

		if (isVetoed) {
			// veto the veto

			for (Player currentPlayer : game.playerByInitiativeList) {
				if (currentPlayer.getIndex() != playerDoingVeto.getIndex()) {

					if (currentPlayer.diCards[DiCardList.VETO.index] > 0) {

						// give player a choice to veto the veto

						// pick tile
						PlayerChoicePick pick = new PlayerChoicePick(game, currentPlayer, this);

						pick.choiceList.add(new ApplyVetoToVetoChoice(game, currentPlayer, true));
						pick.choiceList.add(new ApplyVetoToVetoChoice(game, currentPlayer, false));

						return pick;
					}
				}
			}
		} else {

			if (playerVetoDone == null) {
				playerVetoDone = new boolean[game.playerByInitiativeList.size()];
				
				for (Player currentPlayer : game.playerByInitiativeList) {
					if (currentPlayer.getIndex() == diCardPlayer.getIndex()) {
						playerVetoDone[currentPlayer.getIndex()] = true;
					}
					else if (currentPlayer.diCards[DiCardList.VETO.index] == 0 ) {
						playerVetoDone[currentPlayer.getIndex()] = true;
					}
				}
			}

			for (Player currentPlayer : game.playerByInitiativeList) {
				if (currentPlayer.getIndex() != diCardPlayer.getIndex()) {
					if (currentPlayer.diCards[DiCardList.VETO.index] > 0
							&& playerVetoDone[currentPlayer.getIndex()] == false) {

						// give player a choice to veto the veto

						// pick tile
						PlayerChoicePick pick = new PlayerChoicePick(game, currentPlayer, this);

						pick.choiceList.add(new ApplyVetoChoice(game, currentPlayer, true));
						pick.choiceList.add(new ApplyVetoChoice(game, currentPlayer, false));

						return pick;
					}
					playerVetoDone[currentPlayer.getIndex()] = true;
				}
			}
		}
		
		end();

		return null;
	}

	@Override
	public Action getParent() {
		return parent;
	}

	@Override
	public void enterSimulationMode(int playerIndex) {
		super.enterSimulationMode(playerIndex);
	}

	@Override
	public void stackPendingActionOnParent(Action pendingAction) {
		parent.stackPendingActionOnParent(pendingAction);
	}
}
