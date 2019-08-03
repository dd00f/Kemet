package kemet.model.action;

import java.util.List;

import kemet.model.Army;
import kemet.model.BeastList;
import kemet.model.BoardInventory;
import kemet.model.DiCardList;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Tile;
import kemet.model.Validation;
import kemet.model.action.choice.Choice;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

public class RainingFireAction extends EndableAction {




	/**
	 * 
	 */
	private static final long serialVersionUID = -6366339572500494712L;

	public Player player;

	private KemetGame game;
	private Action parent;

	public static Cache<RainingFireAction> CACHE = new Cache<RainingFireAction>(() -> new RainingFireAction());

	private RainingFireAction() {

	}

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {

		cannonicalForm.set(BoardInventory.RAINING_FIRE_STATE, player.getState(playerIndex));

	}

	@Override
	public void internalInitialize() {
		game = null;
		player = null;
		parent = null;
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
	public RainingFireAction deepCacheClone() {

		RainingFireAction clone = CACHE.create();

		copy(clone);

		return clone;
	}

	private void copy(RainingFireAction clone) {
		clone.game = game;
		clone.player = player;

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

	public static RainingFireAction create(KemetGame game, Player player, Action parent) {
		RainingFireAction create = CACHE.create();
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

	public boolean canRecruitBeast() {

		if (player.availableBeasts.size() == 0) {
			// no beast to recruit
			return false;
		}

		return true;
	}

	public boolean playerCanRecruitBeast() {
		if (player.availableBeasts.size() <= 0) {
			return false;
		}
		return true;
	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {

		if (isEnded()) {
			return null;
		}

		// pick tile
		PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

		addRainingFirePickTileChoice(pick.choiceList);

		if (pick.choiceList.size() == 0) {

			// no valid choices left
			end();
			return null;
		}
		
		if (pick.choiceList.size() == 1) {

			// no valid choices left
			pick.choiceList.get(0).activate();
			end();
			return null;
		}

		return pick.validate();
	}

	public void addRainingFirePickTileChoice(List<Choice> choiceList) {

		for (Tile tile : game.tileList) {

			checkToAddRainingFireChoice(choiceList, tile);
		}
	}

	private void checkToAddRainingFireChoice(List<Choice> choiceList, Tile tile) {

		if (tile == null) {
			return;
		}

		Army army = tile.getArmy();
		if (army == null) {
			return;
		}
		
		if (army.owningPlayer == player) {
			return;
		}
		
		if (army.beast == BeastList.BLACK_4_DEVOURER ) {
			return;
		}

		RainingFirePickTileChoice subChoice = new RainingFirePickTileChoice(game, player);
		subChoice.pickTile = tile;
		choiceList.add(subChoice);
	}

	public class RainingFirePickTileChoice extends PlayerChoice {

		public RainingFirePickTileChoice(KemetGame game, Player player) {
			super(game, player);
		}

		public Tile pickTile;

		@Override
		public void choiceActivate() {
			
			Army army = pickTile.getArmy();
			army.bleedArmy((byte) 1, DiCardList.RAINING_FIRE.name);
			
			if( army.armySize == 0 ) {
				army.destroyArmy();
			}
			
			end();
		}

		@Override
		public String describe() {

			return "Raining fire on " + pickTile.getArmy().describeArmy();

		}

		@Override
		public int getIndex() {
			return pickTile.getPickChoiceIndex(player.getIndex());
		}
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
