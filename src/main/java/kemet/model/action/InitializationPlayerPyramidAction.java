package kemet.model.action;

import java.util.List;

import kemet.model.BoardInventory;
import kemet.model.Color;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Tile;
import kemet.model.Validation;
import kemet.model.action.choice.Choice;
import kemet.model.action.choice.ChoiceInventory;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

public class InitializationPlayerPyramidAction implements Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5798076013017724149L;
	private Player player;
	private byte levelLeft = 3;
	private byte currentDistrictIndex = 0;
	private byte targetLevel =-1;
	private KemetGame game;
	private Action parent;

	public static Cache<InitializationPlayerPyramidAction> CACHE = new Cache<InitializationPlayerPyramidAction>(
			() -> new InitializationPlayerPyramidAction());

	@Override
	public void initialize() {
		player = null;
		levelLeft = 3;
		currentDistrictIndex = 0;
		game = null;
		parent = null;
		targetLevel = -1;
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
	public InitializationPlayerPyramidAction deepCacheClone() {
		// create the object
		InitializationPlayerPyramidAction clone = CACHE.create();

		// copy all objects
		clone.player = player;
		clone.levelLeft = levelLeft;
		clone.currentDistrictIndex = currentDistrictIndex;
		clone.game = game;
		clone.parent = parent;
		clone.targetLevel = targetLevel;

		return clone;
	}

	@Override
	public void release() {
		// null all references
		player = null;
		game = null;
		parent = null;

		CACHE.release(this);
	}

	@Override
	public void relink(KemetGame clone) {
		// relink game
		this.game = clone;

		// relink pointers
		player = clone.getPlayerByCopy(player);
	}

	private InitializationPlayerPyramidAction() {

	}

	public static InitializationPlayerPyramidAction create(KemetGame game, Player player, Action parent) {
		InitializationPlayerPyramidAction create = CACHE.create();
		create.initialize();
		create.game = game;
		create.player = player;
		create.parent = parent;
		return create;
	}

	@Override
	public Action getParent() {
		return parent;
	}

	@Override
	public void setParent(Action parent) {
		this.parent = parent;
	}
	

	public class InitialPyramidLevelChoice extends PlayerChoice {
		
		public InitialPyramidLevelChoice(KemetGame game, Player player) {
			super(game, player);
		}

		public byte endLevel;
		public Tile tile;

		@Override
		public String describe() {
			return "initial pyramid level " + endLevel + " on tile \"" + tile.name + "\"";
		}

		@Override
		public int getIndex() {
			return ChoiceInventory.PICK_PYRAMID_LEVEL_CHOICE + endLevel -1;
		}

		@Override
		public void choiceActivate() {
		
			targetLevel = endLevel;
		}

	}	

	public class InitialPyramidColorChoice extends PlayerChoice {
		public InitialPyramidColorChoice(KemetGame game, Player player) {
			super(game, player);
		}

		public Color color;
		public byte endLevel;
		public Tile tile;

		@Override
		public String describe() {
			return "initial " + color + " pyramid level " + endLevel + " on tile \"" + tile.name + "\"";
		}

		@Override
		public void choiceActivate() {

			tile.pyramidColor = color;
			tile.setPyramidLevel(endLevel);
			levelLeft -= endLevel;
			targetLevel = -1;
			currentDistrictIndex++;
		}

		@Override
		public int getIndex() {
			return ChoiceInventory.PICK_COLOR_CHOICE + color.ordinal();
		}


	}

	private void createInitializePyramidChoice(PlayerChoicePick player) {
		List<Choice> choiceList = player.choiceList;
		
		Tile district1 = player.player.cityTiles.get(currentDistrictIndex);

		if( targetLevel == -1 ) {
			if (levelLeft > 1 ) {
				createInitialPyramidLevel(district1, (byte) 1, choiceList);
				createInitialPyramidLevel(district1, (byte) 2, choiceList);
				return;
			}
			targetLevel = 1;
		}

		createInitialPyramid(district1, Color.RED, targetLevel, choiceList);
		createInitialPyramid(district1, Color.BLACK, targetLevel, choiceList);
		createInitialPyramid(district1, Color.BLUE, targetLevel, choiceList);
		createInitialPyramid(district1, Color.WHITE, targetLevel, choiceList);
	}

	
	private void createInitialPyramidLevel(Tile tile,  byte level, List<Choice> choiceList) {
		InitialPyramidLevelChoice choice = new InitialPyramidLevelChoice(game, player);
		choice.endLevel = level;
		choice.tile = tile;
		choiceList.add(choice);
	}
	
	private void createInitialPyramid(Tile tile, Color color, byte level, List<Choice> choiceList) {
		InitialPyramidColorChoice choice = new InitialPyramidColorChoice(game, player);
		choice.color = color;
		choice.endLevel = level;
		choice.tile = tile;

		if (!player.hasPyramid(color)) {
			choiceList.add(choice);
		}

	}

	@Override
	public PlayerChoicePick getNextPlayerChoicePick() {
		if (levelLeft == 0) {
			return null;
		}

		PlayerChoicePick pick = createPlayerChoicePick();

		createInitializePyramidChoice(pick);

		return pick.validate();
	}

	public PlayerChoicePick createPlayerChoicePick() {
		PlayerChoicePick pick = new PlayerChoicePick(game, player, this);
		return pick;
	}

	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {
		
		// cannonicalForm.set(BoardInventory.STATE_INITIAL_PYRAMID, player.getState(playerIndex));
		player.setCanonicalState(cannonicalForm, BoardInventory.STATE_INITIAL_PYRAMID, playerIndex);

		
		Tile district1 = player.cityTiles.get(currentDistrictIndex);
		// district1.setSelected(cannonicalForm, playerIndex, player.getState(playerIndex));
		district1.setCanonicalSelected(cannonicalForm, player, playerIndex);

		if( targetLevel == -1 ) {
//			cannonicalForm.set(BoardInventory.STATE_PICK_PYRAMID_LEVEL, player.getState(playerIndex));
			player.setCanonicalState(cannonicalForm, BoardInventory.STATE_PICK_PYRAMID_LEVEL, playerIndex);

		}
		else {
//			cannonicalForm.set(BoardInventory.STATE_PICK_PYRAMID_COLOR, player.getState(playerIndex));
			player.setCanonicalState(cannonicalForm, BoardInventory.STATE_PICK_PYRAMID_COLOR, playerIndex);

			
			cannonicalForm.set(BoardInventory.PICKED_LEVEL, targetLevel);
			
		}
		
	}

	@Override
	public void enterSimulationMode(int playerIndex) {

	}
	
	@Override
	public void stackPendingActionOnParent(Action pendingAction) {
		parent.stackPendingActionOnParent(pendingAction);
	}
}
