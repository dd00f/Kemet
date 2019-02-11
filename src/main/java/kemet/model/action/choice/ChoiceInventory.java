package kemet.model.action.choice;

import kemet.model.BattleCard;
import kemet.model.BoardInventory;

public class ChoiceInventory {
	/*
	 Vector of all possible actions : 61 possible choices
	 	- end action
	 		1 choice
	 	- army size pick
	 		7 choices : 1 to 7
	 	- pick tile
	 		13 choices : 2 player game has 13 tiles
	 	- pick pyramid color
	 		4 choices : 4 color
	 	- pick pyramid level
	 		4 choices
	 	- pick battle card
	 		8 choices : 1 per card
	 	- recall army
	 	    1 choice
	 	- player token pick
	 		10 choices - for each token
	*/
	
	public static int INDEXER = 0;
	
	public static final int NON_AI_CHOICE = -1;
	
	// 1 pick for this
	public static final int PASS_CHOICE_INDEX = INDEXER++;
	
	// 7 picks for this
	public static final int ARMY_SIZE_CHOICE = INDEXER++;
	static {
		INDEXER += BoardInventory.LARGEST_ARMY;
	}
	
	// 13 picks for this
	public static final int PICK_TILE_CHOICE = INDEXER;
	static {
		INDEXER += BoardInventory.TILE_COUNT;
	}
	
	// 4 picks for this
	public static final int PICK_COLOR_CHOICE = INDEXER;
	static {
		INDEXER += BoardInventory.COLOR_COUNT;
	}
	
	// 4 picks for this
	public static final int PICK_PYRAMID_LEVEL_CHOICE = INDEXER;
	static {
		INDEXER += BoardInventory.MAX_PYRAMID_LEVEL;
	}
	
	// 8 picks for this
	public static final int PICK_BATTLE_CARD_CHOICE = INDEXER;
	static {
		INDEXER += BattleCard.INDEXER;
	}
	
	// 1 picks for this
	public static final int RECALL_CHOICE = INDEXER++;

	// 10 picks for this
	public static final int PICK_ROW_ONE_MOVE = INDEXER++;
	public static final int PICK_ROW_ONE_RECRUIT = INDEXER++;
	public static final int PICK_ROW_TWO_MOVE = INDEXER++;
	public static final int PICK_ROW_TWO_UPGRADE_PYRAMID = INDEXER++;
	public static final int PICK_ROW_TWO_PRAY = INDEXER++;
	public static final int PICK_ROW_THREE_PRAY = INDEXER++;
	public static final int PICK_ROW_THREE_BUILD_WHITE = INDEXER++;
	public static final int PICK_ROW_THREE_BUILD_RED = INDEXER++;
	public static final int PICK_ROW_THREE_BUILD_BLUE = INDEXER++;
	public static final int PICK_ROW_THREE_BUILD_BLACK = INDEXER++;



	// 13 picks for this, redundant with tiles, commenting out
	// public static final int PICK_ARMY_CHOICE = PICK_PYRAMID_LEVEL_CHOICE + 4;

	
	public static final int TOTAL_CHOICE = INDEXER;

	
}