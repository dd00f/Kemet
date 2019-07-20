package kemet.model.action.choice;

import kemet.model.BattleCard;
import kemet.model.BeastList;
import kemet.model.BoardInventory;
import kemet.model.DiCardList;
import kemet.model.PowerList;

public class ChoiceInventory {


	public static int INDEXER = 0;

	public static final int NON_AI_CHOICE = -1;

	// 1 pick for this
	public static final int ZERO_ARMY_SIZE_CHOICE_INDEX = INDEXER++;

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

	// 1 picks for recall
	public static final int RECALL_CHOICE = INDEXER++;

	// 10 picks for token selection
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
	public static final int PASS_TOKEN_PICK = INDEXER++;
	public static final int PICK_GOLD_PRAY = INDEXER++;
	public static final int PICK_GOLD_MOVE = INDEXER++;
	public static final int PICK_GOLD_RECRUIT = INDEXER++;

	// dawn token selection range
	public static final int PICK_DAWN_TOKEN = INDEXER;
	static {
		INDEXER += BoardInventory.MAX_DAWN_TOKEN + 1;
	}

	public static final int PICK_PLAYER_ORDER = INDEXER;
	static {
		INDEXER += BoardInventory.PLAYER_COUNT;
	}

	// 1 pick for this
	public static final int PASS_RECRUIT_CHOICE_INDEX = INDEXER++;

	// 1 pick for this
	public static final int PASS_RECALL_CHOICE_INDEX = INDEXER++;

	public static final int BUY_POWER = INDEXER;
	static {
		INDEXER += PowerList.POWER_INDEXER;
	}

	public static final int PICK_BEAST = INDEXER;
	static {
		INDEXER += BeastList.BEAST_INDEXER;
	}

	// 1 pick
	public static final int ONLY_BEAST_MOVE = INDEXER++;

	// 7 picks for this
	public static final int ARMY_SIZE_WITH_BEAST_CHOICE = INDEXER;
	static {
		INDEXER += BoardInventory.LARGEST_ARMY;
	}

	// 1 pick
	public static final int BUY_NOTHING = INDEXER++;
	public static final int KEEP_BEAST = INDEXER++;
	public static final int UPGRADE_NOTHING = INDEXER++;
	public static final int END_RECRUIT = INDEXER++;

	public static final int ACTIVATE_DI_CARD = INDEXER;
	static {
		INDEXER += DiCardList.TOTAL_DI_CARD_TYPE_COUNT;
	}

	public static final int TACTICAL_CHOICE_SWAP = INDEXER++;
	public static final int TACTICAL_CHOICE_KEEP = INDEXER++;

	public static final int SKIP_DI_VETO = INDEXER++;
	public static final int SKIP_ESCAPE = INDEXER++;

	public static final int PICK_DI_CARD = INDEXER;
	static {
		INDEXER += DiCardList.TOTAL_DI_CARD_TYPE_COUNT;
	}
	
	
	// 13 picks for this
	public static final int ESCAPE_TILE_CHOICE = INDEXER;
	static {
		INDEXER += BoardInventory.TILE_COUNT;
	}

	public static final int DIVINE_WOUND_DI_CARD = INDEXER;
	static {
		INDEXER += DiCardList.TOTAL_DI_CARD_TYPE_COUNT;
	}
	public static final int END_DIVINE_WOUND = INDEXER++;

	public static final int TOTAL_CHOICE = INDEXER;

	public static void main(String[] args) {

		System.out.println("Choice Index List");
		System.out.println("PASS_CHOICE_INDEX " + ZERO_ARMY_SIZE_CHOICE_INDEX);
		System.out.println("ARMY_SIZE_CHOICE " + ARMY_SIZE_CHOICE);
		System.out.println("PICK_TILE_CHOICE " + PICK_TILE_CHOICE);
		System.out.println("PICK_COLOR_CHOICE " + PICK_COLOR_CHOICE);
		System.out.println("PICK_PYRAMID_LEVEL_CHOICE " + PICK_PYRAMID_LEVEL_CHOICE);
		System.out.println("PICK_BATTLE_CARD_CHOICE " + PICK_BATTLE_CARD_CHOICE);
		System.out.println("RECALL_CHOICE " + RECALL_CHOICE);
		System.out.println("PICK_ROW_ONE_MOVE " + PICK_ROW_ONE_MOVE);
		System.out.println("PICK_ROW_ONE_RECRUIT " + PICK_ROW_ONE_RECRUIT);
		System.out.println("PICK_ROW_TWO_MOVE " + PICK_ROW_TWO_MOVE);
		System.out.println("PICK_ROW_TWO_UPGRADE_PYRAMID " + PICK_ROW_TWO_UPGRADE_PYRAMID);
		System.out.println("PICK_ROW_TWO_PRAY " + PICK_ROW_TWO_PRAY);
		System.out.println("PICK_ROW_THREE_PRAY " + PICK_ROW_THREE_PRAY);
		System.out.println("PICK_ROW_THREE_BUILD_WHITE " + PICK_ROW_THREE_BUILD_WHITE);
		System.out.println("PICK_ROW_THREE_BUILD_RED " + PICK_ROW_THREE_BUILD_RED);
		System.out.println("PICK_ROW_THREE_BUILD_BLUE " + PICK_ROW_THREE_BUILD_BLUE);
		System.out.println("PICK_ROW_THREE_BUILD_BLACK " + PICK_ROW_THREE_BUILD_BLACK);
		System.out.println("PASS_TOKEN_PICK " + PASS_TOKEN_PICK);
		System.out.println("PICK_GOLD_PRAY " + PICK_GOLD_PRAY);
		System.out.println("PICK_GOLD_MOVE " + PICK_GOLD_MOVE);
		System.out.println("PICK_GOLD_RECRUIT " + PICK_GOLD_RECRUIT);

		System.out.println("PICK_DAWN_TOKEN " + PICK_DAWN_TOKEN);
		System.out.println("PICK_PLAYER_ORDER " + PICK_PLAYER_ORDER);
		System.out.println("PASS_RECRUIT_CHOICE_INDEX " + PASS_RECRUIT_CHOICE_INDEX);
		System.out.println("PASS_RECALL_CHOICE_INDEX " + PASS_RECALL_CHOICE_INDEX);
		System.out.println("BUY_POWER " + BUY_POWER);
		System.out.println("PICK_BEAST " + PICK_BEAST);
		System.out.println("ARMY_SIZE_WITH_BEAST_CHOICE " + ARMY_SIZE_WITH_BEAST_CHOICE);
		System.out.println("BUY_NOTHING " + BUY_NOTHING);
		System.out.println("KEEP_BEAST " + KEEP_BEAST);
		System.out.println("UPGRADE_NOTHING " + UPGRADE_NOTHING);
		System.out.println("END_RECRUIT " + END_RECRUIT);
		System.out.println("ACTIVATE_DI_CARD " + ACTIVATE_DI_CARD);
		System.out.println("TACTICAL_CHOICE_SWAP " + TACTICAL_CHOICE_SWAP);
		System.out.println("TACTICAL_CHOICE_KEEP " + TACTICAL_CHOICE_KEEP);
		System.out.println("SKIP_DI_VETO " + SKIP_DI_VETO);
		System.out.println("SKIP_ESCAPE " + SKIP_ESCAPE);
		System.out.println("PICK_DI_CARD " + PICK_DI_CARD);
		System.out.println("ESCAPE_TILE_CHOICE " + ESCAPE_TILE_CHOICE);
		System.out.println("DIVINE_WOUND_DI_CARD " + DIVINE_WOUND_DI_CARD);
		System.out.println("END_DIVINE_WOUND " + END_DIVINE_WOUND);

		System.out.println("LAST_INDEX " + (INDEXER - 1));

//		Choice Index List
//		PASS_CHOICE_INDEX 0
//		ARMY_SIZE_CHOICE 1
//		PICK_TILE_CHOICE 9
//		PICK_COLOR_CHOICE 22
//		PICK_PYRAMID_LEVEL_CHOICE 26
//		PICK_BATTLE_CARD_CHOICE 30
//		RECALL_CHOICE 40
//		PICK_ROW_ONE_MOVE 41
//		PICK_ROW_ONE_RECRUIT 42
//		PICK_ROW_TWO_MOVE 43
//		PICK_ROW_TWO_UPGRADE_PYRAMID 44
//		PICK_ROW_TWO_PRAY 45
//		PICK_ROW_THREE_PRAY 46
//		PICK_ROW_THREE_BUILD_WHITE 47
//		PICK_ROW_THREE_BUILD_RED 48
//		PICK_ROW_THREE_BUILD_BLUE 49
//		PICK_ROW_THREE_BUILD_BLACK 50
//		PASS_TOKEN_PICK 51
//		PICK_GOLD_PRAY 52
//		PICK_GOLD_MOVE 53
//		PICK_GOLD_RECRUIT 54
//		PICK_DAWN_TOKEN 55
//		PICK_PLAYER_ORDER 66
//		PASS_RECRUIT_CHOICE_INDEX 68
//		PASS_RECALL_CHOICE_INDEX 69
//		BUY_POWER 70
//		PICK_BEAST 134
//		ARMY_SIZE_WITH_BEAST_CHOICE 145
//		BUY_NOTHING 152
//		KEEP_BEAST 153
//		UPGRADE_NOTHING 154
//		END_RECRUIT 155
//		ACTIVATE_DI_CARD 156
//		TACTICAL_CHOICE_SWAP 176
//		TACTICAL_CHOICE_KEEP 177
//		SKIP_DI_VETO 178
//		SKIP_ESCAPE 179
//		PICK_DI_CARD 180
//		ESCAPE_TILE_CHOICE 200
//		LAST_INDEX 212

	}

}
