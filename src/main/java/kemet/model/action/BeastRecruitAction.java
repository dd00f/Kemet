package kemet.model.action;

import java.util.List;

import kemet.model.Army;
import kemet.model.Beast;
import kemet.model.BoardInventory;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.PowerList;
import kemet.model.Tile;
import kemet.model.Validation;
import kemet.model.action.choice.Choice;
import kemet.model.action.choice.ChoiceInventory;
import kemet.model.action.choice.EndTurnChoice;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

public class BeastRecruitAction extends EndableAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1772950270292734562L;

	public Beast beast;

	public Player player;

	private KemetGame game;
	private Action parent;

	public static Cache<BeastRecruitAction> CACHE = new Cache<BeastRecruitAction>(() -> new BeastRecruitAction());

	private BeastRecruitAction() {

	}

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {

		cannonicalForm.set(BoardInventory.RECRUIT_BEAST + beast.index, player.getState(playerIndex));

	}

	@Override
	public void internalInitialize() {
		game = null;
		player = null;
		parent = null;
		beast = null;
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
	public BeastRecruitAction deepCacheClone() {

		BeastRecruitAction clone = CACHE.create();

		copy(clone);

		return clone;
	}

	private void copy(BeastRecruitAction clone) {
		clone.game = game;
		clone.player = player;
		clone.beast = beast;

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
		beast = null;
		parent = null;

		super.clear();
	}

	public static BeastRecruitAction create(KemetGame game, Player player, Action parent, Beast beast) {
		BeastRecruitAction create = CACHE.create();
		create.initialize();
		create.game = game;
		create.player = player;
		create.parent = parent;
		create.beast = beast;

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

		addRecruitPickTileChoice(pick.choiceList);

		if (pick.choiceList.size() == 0) {

			// no valid choices left
			end();
			return null;
		}

		EndTurnChoice.addEndTurnChoice(game, player, pick.choiceList, this, ChoiceInventory.KEEP_BEAST);
		return pick.validate();
	}

	public void addRecruitPickTileChoice(List<Choice> choiceList) {

		if (player.availableArmyTokens <= 0 && player.availableBeasts.size() == 0) {
			// no option to add
			return;
		}

		for (Tile tile : player.cityTiles) {

			checkToAddRecruitChoice(choiceList, tile);
		}

		if (player.hasPower(PowerList.BLACK_1_ENFORCED_RECRUITMENT)) {

			for (Army army : player.armyList) {
				if (!player.cityTiles.contains(army.tile)) {
					checkToAddRecruitChoice(choiceList, army.tile);
				}
			}
		}
	}

	private void checkToAddRecruitChoice(List<Choice> choiceList, Tile tile) {

		Army army = tile.getArmy();
		if (army == null) {
			return;
		}
		if (army.owningPlayer != player) {
			return;
		}
		if (army.beast != null) {
			return;
		}

		RecruitPickTileChoice subChoice = new RecruitPickTileChoice(game, player);
		subChoice.pickTile = tile;
		choiceList.add(subChoice);
	}

	public class RecruitPickTileChoice extends PlayerChoice {

		public RecruitPickTileChoice(KemetGame game, Player player) {
			super(game, player);
		}

		public Tile pickTile;

		@Override
		public void choiceActivate() {
			pickTile.getArmy().addBeast(beast);
			end();
		}

		@Override
		public String describe() {

			return "Recruit "+beast.name+" on tile \"" + pickTile.name;

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

}
