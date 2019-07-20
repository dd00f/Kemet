package kemet.model;

public class BoardInventory {

	private static final int MAX_ACTION_PER_TURN = 3;

	public static final int PLAYER_COUNT = 2;

	public static final int TILE_COUNT = 13;

	public static final int LARGEST_ARMY = 7;

	public static final int COLOR_COUNT = 4;

	public static final int MAX_PYRAMID_LEVEL = 4;

	public static final int MAX_DAWN_TOKEN = 10;

	public static final float MAX_DAWN_BATTLE_STRENGTH_TOKEN = MAX_DAWN_TOKEN + 5;

	public static int INDEXER = 0;

	// -----------------------
	// GAME DATA
	// -----------------------
	public static final int ROUND_NUMBER = INDEXER++;

	// -----------------------
	// GAME STATE DATA
	// -----------------------
	public static final int STATE_RECRUIT = INDEXER++;
	public static final int STATE_FREE_RECRUIT = INDEXER++;
	public static final int STATE_MOVE = INDEXER++;
	public static final int STATE_BATTLE = INDEXER++;
	public static final int STATE_UPGRADE_PYRAMID = INDEXER++;
	public static final int STATE_INITIAL_PYRAMID = INDEXER++;
	public static final int STATE_INITIAL_ARMY = INDEXER++;
	public static final int STATE_PICK_ACTION_TOKEN = INDEXER++;
	public static final int STATE_PICK_ATTACK_BATTLE_CARD = INDEXER++;
	public static final int STATE_PICK_ATTACK_DISCARD = INDEXER++;
	public static final int STATE_PICK_DEFENSE_BATTLE_CARD = INDEXER++;
	public static final int STATE_PICK_DEFENSE_DISCARD = INDEXER++;
	public static final int STATE_PICK_ARMY_SIZE = INDEXER++;
	public static final int STATE_PICK_TILE = INDEXER++;
	public static final int STATE_PICK_BEAST = INDEXER++;
	public static final int STATE_PICK_SOURCE_TILE = INDEXER++;
	public static final int STATE_PICK_PYRAMID_COLOR = INDEXER++;
	public static final int STATE_PICK_PYRAMID_LEVEL = INDEXER++;
	public static final int STATE_PICK_ATTACKER_RECALL = INDEXER++;
	public static final int STATE_PICK_DEFENDER_RECALL = INDEXER++;
	public static final int STATE_PICK_ATTACKER_RETREAT = INDEXER++;
	public static final int STATE_PICK_DEFENDER_RETREAT = INDEXER++;
	public static final int STATE_PICK_INITIATIVE_BATTLE_CARD = INDEXER++;
	public static final int STATE_PICK_INITIATIVE_DISCARD = INDEXER++;
	public static final int STATE_PICK_INITIATIVE_DAWN_TOKEN = INDEXER++;
	public static final int STATE_PICK_INITIATIVE_ORDER = INDEXER++;
	public static final int STATE_PICK_BATTLECARD_TO_REMOVE = INDEXER++;
	public static final int STATE_BUY_POWER_COLOR = INDEXER;
	static {
		INDEXER += COLOR_COUNT;
	}

	public static final int PICKED_ACTION_IN_ORDER = INDEXER;
	static {
		INDEXER += ActionList.TOTAL_ACTION_COUNT * MAX_ACTION_PER_TURN;
	}

	// -----------------------
	// GAME SELECTION DATA
	// -----------------------
	public static final int MAIN_TOKEN_PICKED = INDEXER++;
	public static final int PICKED_SIZE = INDEXER++;
	public static final int PICKED_LEVEL = INDEXER++;
	public static final int MOVES_LEFT = INDEXER++;
	public static final int IS_FIRST_MOVE = INDEXER++; // 1 or 0
	public static final int BATTLE_ATTACKER_STRENGTH = INDEXER++;
	public static final int BATTLE_ATTACKER_SHIELD = INDEXER++;
	public static final int BATTLE_ATTACKER_DAMAGE = INDEXER++;
	public static final int BATTLE_DEFENDER_STRENGTH = INDEXER++;
	public static final int BATTLE_DEFENDER_SHIELD = INDEXER++;
	public static final int BATTLE_DEFENDER_DAMAGE = INDEXER++;
	public static final int BATTLE_ATTACKER_WON = INDEXER++; // 1 yes, -1 no
	public static final int FREE_RECRUIT_LEFT = INDEXER++;

	// Battle stats to help nudge things ? 2x2x3 = 12 datapoints
	// - min/max attack/defense strength/shield/damage

	// -----------------------
	// POWER TILES DATA
	// -----------------------
	// for each power : 0 x 1 = 0 datapoint
	// owning player index

	// -----------------------
	// TILE DATA
	// -----------------------
	public static final int TILE_PLAYER_ARMY_SIZE = INDEXER;
	static {
		INDEXER += PLAYER_COUNT * TILE_COUNT;
	}

	// tile selected for upgrade pyramid, recruit, or movement target
	public static final int TILE_SELECTED = INDEXER;
	static {
		INDEXER += TILE_COUNT;
	}

	// tile selected as source for a movement
	public static final int TILE_SOURCE_SELECTED = INDEXER;
	static {
		INDEXER += TILE_COUNT;
	}

	public static final int TILE_BLACK_PYRAMID_LEVEL = INDEXER;
	static {
		INDEXER += TILE_COUNT;
	}

	public static final int TILE_RED_PYRAMID_LEVEL = INDEXER;
	static {
		INDEXER += TILE_COUNT;
	}

	public static final int TILE_BLUE_PYRAMID_LEVEL = INDEXER;
	static {
		INDEXER += TILE_COUNT;
	}

	public static final int TILE_WHITE_PYRAMID_LEVEL = INDEXER;
	static {
		INDEXER += TILE_COUNT;
	}

	// -----------------------
	// PLAYER DATA
	// -----------------------

	public static final int PLAYER_VICTORY_POINTS = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int PLAYER_BATTLE_POINTS = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int PLAYER_PRAYER_POINTS = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int PLAYER_AVAILABLE_ARMY_TOKENS = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int PLAYER_ROW_ONE_MOVE_USED = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int PLAYER_ROW_ONE_RECRUIT_USED = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int PLAYER_ROW_TWO_MOVE_USED = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int PLAYER_ROW_TWO_UPGRADE_PYRAMID_USED = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int PLAYER_ROW_TWO_PRAY_USED = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int PLAYER_ROW_THREE_PRAY_USED = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int PLAYER_ROW_THREE_BUILD_WHITE_USED = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int PLAYER_ROW_THREE_BUILD_RED_USED = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int PLAYER_ROW_THREE_BUILD_BLUE_USED = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int PLAYER_ROW_THREE_BUILD_BLACK_USED = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int PLAYER_ACTION_TOKEN_LEFT = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int PLAYER_TEMPLE_COUNT = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	// 1 available, -1 in discard, 0 not available
	public static final int PLAYER_BATTLE_CARD_AVALIABLE = INDEXER;
	static {
		INDEXER += PLAYER_COUNT * BattleCard.INDEXER;
	}

	public static final int PLAYER_DAWN_TOKEN = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int PLAYER_SILVER_TOKEN_USED = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int PLAYER_GOLD_TOKEN_USED = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int PLAYER_DAWN_STRENGTH = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int PLAYER_SELECTED_ORDER = INDEXER;
	static {
		INDEXER += PLAYER_COUNT * PLAYER_COUNT;
	}

	public static final int PLAYER_ORDER = INDEXER;
	static {
		INDEXER += PLAYER_COUNT * PLAYER_COUNT;
	}

	public static final int PLAYER_POWERS = INDEXER;
	static {
		INDEXER += PLAYER_COUNT * PowerList.POWER_INDEXER;
	}

	public static final int RECRUIT_BEAST = INDEXER;
	static {
		INDEXER += BeastList.BEAST_INDEXER;
	}

	public static final int BEAST_POSITION = INDEXER;
	static {
		INDEXER += PLAYER_COUNT * BeastList.BEAST_INDEXER * TILE_COUNT;
	}

	public static final int BEAST_AVAILABLE = INDEXER;
	static {
		INDEXER += PLAYER_COUNT * BeastList.BEAST_INDEXER;
	}

	public static final int DI_DISCARD = INDEXER;
	static {
		INDEXER += DiCardList.TOTAL_DI_CARD_TYPE_COUNT;
	}

	public static final int CURRENT_PLAYER_DI = INDEXER;
	static {
		INDEXER += DiCardList.TOTAL_DI_CARD_TYPE_COUNT;
	}

	public static final int CURRENT_PLAYER_ACTIVATED_DI = INDEXER;
	static {
		INDEXER += DiCardList.TOTAL_BATTLE_DI_CARD_TYPE_COUNT;
	}

	public static final int DI_CARD_PER_PLAYER = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int STATE_VETO_PLAYER = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int STATE_VETO_DI_CARD = INDEXER;
	static {
		INDEXER += DiCardList.TOTAL_NON_BATTLE_DI_CARD_TYPE_COUNT;
	}

	public static final int STATE_PLAYER_VETO_DONE = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int STATE_PLAYER_DOING_VETO_ON_VETO = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int MOVE_FREE_BREACH_WALL = INDEXER++;
	public static final int MOVE_FREE_TELEPORT = INDEXER++;

	public static final int AVAILABLE_DI = INDEXER;
	static {
		INDEXER += DiCardList.TOTAL_DI_CARD_TYPE_COUNT;
	}

	public static final int PICK_DI_STATE = INDEXER++;
	public static final int PICK_DI_MOVE_REST_TO_DISCARD = INDEXER++;
	public static final int RAINING_FIRE_STATE = INDEXER++;
	public static final int ESCAPE_PICKED = INDEXER++;
	public static final int ESCAPE_TILE_PICKED = INDEXER++;
	public static final int STATE_PICK_ATTACKER_DIVINE_WOUND = INDEXER++;
	public static final int STATE_PICK_DEFENDER_DIVINE_WOUND = INDEXER++;
	public static final int STATE_PICK_ATTACKER_TACTICAL_CHOICE = INDEXER++;
	public static final int STATE_PICK_DEFENDER_TACTICAL_CHOICE = INDEXER++;

	public static final int TOTAL_STATE_COUNT = INDEXER;

}
