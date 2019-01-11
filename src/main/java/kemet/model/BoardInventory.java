package kemet.model;

public class BoardInventory {

	public static final int PLAYER_COUNT = 2;

	public static final int TILE_COUNT = 13;
	
	public static final int LARGEST_ARMY = 7;

	public static final int COLOR_COUNT = 4;

	public static final int MAX_PYRAMID_LEVEL = 4;

	public static int INDEXER = 0;

	// -----------------------
	// GAME DATA
	// -----------------------
	public static final int ROUND_NUMBER = INDEXER++;

	// -----------------------
	// GAME STATE DATA
	// -----------------------
	public static final int STATE_RECRUIT = INDEXER++;
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
	public static final int STATE_PICK_SOURCE_TILE = INDEXER++;
	public static final int STATE_PICK_PYRAMID_COLOR = INDEXER++;
	public static final int STATE_PICK_PYRAMID_LEVEL = INDEXER++;
	public static final int STATE_PICK_ATTACKER_RECALL = INDEXER++;
	public static final int STATE_PICK_DEFENDER_RECALL = INDEXER++;
	public static final int STATE_PICK_ATTACKER_RETREAT = INDEXER++;
	public static final int STATE_PICK_DEFENDER_RETREAT = INDEXER++;

	// -----------------------
	// GAME SELECTION DATA
	// -----------------------
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
	public static final int BATTLE_ATTACKER_WON = INDEXER++; // 1 yes,  -1 no

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
	
	
	public static final int TILE_BLACK_PYRAMID_LEVEL= INDEXER;
	static {
		INDEXER += TILE_COUNT;
	}

	public static final int TILE_RED_PYRAMID_LEVEL= INDEXER;
	static {
		INDEXER += TILE_COUNT;
	}

	public static final int TILE_BLUE_PYRAMID_LEVEL= INDEXER;
	static {
		INDEXER += TILE_COUNT;
	}

	public static final int TILE_WHITE_PYRAMID_LEVEL= INDEXER;
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
	
	public static final int TOTAL_STATE_COUNT = INDEXER;




}
