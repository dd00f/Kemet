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
	
	public static void main(String[] args) {
		
		System.out.println("Board Inventory");
		System.out.println("ROUND_NUMBER " + ROUND_NUMBER);
		
		System.out.println("STATE_RECRUIT " + STATE_RECRUIT);
		System.out.println("STATE_FREE_RECRUIT " + STATE_FREE_RECRUIT);
		System.out.println("STATE_MOVE " + STATE_MOVE);
		System.out.println("STATE_BATTLE " + STATE_BATTLE);
		System.out.println("STATE_UPGRADE_PYRAMID " + STATE_UPGRADE_PYRAMID);
		System.out.println("STATE_INITIAL_PYRAMID " + STATE_INITIAL_PYRAMID);
		System.out.println("STATE_INITIAL_ARMY " + STATE_INITIAL_ARMY);
		System.out.println("STATE_PICK_ACTION_TOKEN " + STATE_PICK_ACTION_TOKEN);
		System.out.println("STATE_PICK_ATTACK_BATTLE_CARD " + STATE_PICK_ATTACK_BATTLE_CARD);
		System.out.println("STATE_PICK_ATTACK_DISCARD " + STATE_PICK_ATTACK_DISCARD);
		System.out.println("STATE_PICK_DEFENSE_BATTLE_CARD " + STATE_PICK_DEFENSE_BATTLE_CARD);
		System.out.println("STATE_PICK_DEFENSE_DISCARD " + STATE_PICK_DEFENSE_DISCARD);
		System.out.println("STATE_PICK_ARMY_SIZE " + STATE_PICK_ARMY_SIZE);
		System.out.println("STATE_PICK_TILE " + STATE_PICK_TILE);
		System.out.println("STATE_PICK_BEAST " + STATE_PICK_BEAST);
		System.out.println("STATE_PICK_SOURCE_TILE " + STATE_PICK_SOURCE_TILE);
		System.out.println("STATE_PICK_PYRAMID_COLOR " + STATE_PICK_PYRAMID_COLOR);
		System.out.println("STATE_PICK_PYRAMID_LEVEL " + STATE_PICK_PYRAMID_LEVEL);
		System.out.println("STATE_PICK_ATTACKER_RECALL " + STATE_PICK_ATTACKER_RECALL);
		System.out.println("STATE_PICK_DEFENDER_RECALL " + STATE_PICK_DEFENDER_RECALL);
		System.out.println("STATE_PICK_ATTACKER_RETREAT " + STATE_PICK_ATTACKER_RETREAT);
		System.out.println("STATE_PICK_DEFENDER_RETREAT " + STATE_PICK_DEFENDER_RETREAT);
		System.out.println("STATE_PICK_INITIATIVE_BATTLE_CARD " + STATE_PICK_INITIATIVE_BATTLE_CARD);
		System.out.println("STATE_PICK_INITIATIVE_DISCARD " + STATE_PICK_INITIATIVE_DISCARD);
		System.out.println("STATE_PICK_INITIATIVE_DAWN_TOKEN " + STATE_PICK_INITIATIVE_DAWN_TOKEN);
		System.out.println("STATE_PICK_INITIATIVE_ORDER " + STATE_PICK_INITIATIVE_ORDER);
		System.out.println("STATE_PICK_BATTLECARD_TO_REMOVE " + STATE_PICK_BATTLECARD_TO_REMOVE);
		System.out.println("STATE_BUY_POWER_COLOR " + STATE_BUY_POWER_COLOR);
		System.out.println("PICKED_ACTION_IN_ORDER " + PICKED_ACTION_IN_ORDER);
		System.out.println("MAIN_TOKEN_PICKED " + MAIN_TOKEN_PICKED);
		System.out.println("PICKED_SIZE " + PICKED_SIZE);
		System.out.println("PICKED_LEVEL " + PICKED_LEVEL);
		System.out.println("MOVES_LEFT " + MOVES_LEFT);
		System.out.println("IS_FIRST_MOVE " + IS_FIRST_MOVE);
		System.out.println("BATTLE_ATTACKER_STRENGTH " + BATTLE_ATTACKER_STRENGTH);
		System.out.println("BATTLE_ATTACKER_SHIELD " + BATTLE_ATTACKER_SHIELD);
		System.out.println("BATTLE_ATTACKER_DAMAGE " + BATTLE_ATTACKER_DAMAGE);
		System.out.println("BATTLE_DEFENDER_STRENGTH " + BATTLE_DEFENDER_STRENGTH);
		System.out.println("BATTLE_DEFENDER_SHIELD " + BATTLE_DEFENDER_SHIELD);
		System.out.println("BATTLE_DEFENDER_DAMAGE " + BATTLE_DEFENDER_DAMAGE);
		System.out.println("BATTLE_ATTACKER_WON " + BATTLE_ATTACKER_WON);
		System.out.println("FREE_RECRUIT_LEFT " + FREE_RECRUIT_LEFT);
		System.out.println("TILE_PLAYER_ARMY_SIZE " + TILE_PLAYER_ARMY_SIZE);
		System.out.println("TILE_SELECTED " + TILE_SELECTED);
		System.out.println("TILE_SOURCE_SELECTED " + TILE_SOURCE_SELECTED);
		System.out.println("TILE_BLACK_PYRAMID_LEVEL " + TILE_BLACK_PYRAMID_LEVEL);
		System.out.println("TILE_RED_PYRAMID_LEVEL " + TILE_RED_PYRAMID_LEVEL);
		System.out.println("TILE_BLUE_PYRAMID_LEVEL " + TILE_BLUE_PYRAMID_LEVEL);
		System.out.println("TILE_WHITE_PYRAMID_LEVEL " + TILE_WHITE_PYRAMID_LEVEL);
		System.out.println("PLAYER_VICTORY_POINTS " + PLAYER_VICTORY_POINTS);
		System.out.println("PLAYER_BATTLE_POINTS " + PLAYER_BATTLE_POINTS);
		System.out.println("PLAYER_PRAYER_POINTS " + PLAYER_PRAYER_POINTS);
		System.out.println("PLAYER_AVAILABLE_ARMY_TOKENS " + PLAYER_AVAILABLE_ARMY_TOKENS);
		System.out.println("PLAYER_ROW_ONE_MOVE_USED " + PLAYER_ROW_ONE_MOVE_USED);
		System.out.println("PLAYER_ROW_ONE_RECRUIT_USED " + PLAYER_ROW_ONE_RECRUIT_USED);
		System.out.println("PLAYER_ROW_TWO_MOVE_USED " + PLAYER_ROW_TWO_MOVE_USED);
		System.out.println("PLAYER_ROW_TWO_UPGRADE_PYRAMID_USED " + PLAYER_ROW_TWO_UPGRADE_PYRAMID_USED);
		System.out.println("PLAYER_ROW_TWO_PRAY_USED " + PLAYER_ROW_TWO_PRAY_USED);
		System.out.println("PLAYER_ROW_THREE_PRAY_USED " + PLAYER_ROW_THREE_PRAY_USED);
		System.out.println("PLAYER_ROW_THREE_BUILD_WHITE_USED " + PLAYER_ROW_THREE_BUILD_WHITE_USED);
		System.out.println("PLAYER_ROW_THREE_BUILD_RED_USED " + PLAYER_ROW_THREE_BUILD_RED_USED);
		System.out.println("PLAYER_ROW_THREE_BUILD_BLUE_USED " + PLAYER_ROW_THREE_BUILD_BLUE_USED);
		System.out.println("PLAYER_ROW_THREE_BUILD_BLACK_USED " + PLAYER_ROW_THREE_BUILD_BLACK_USED);
		System.out.println("PLAYER_ACTION_TOKEN_LEFT " + PLAYER_ACTION_TOKEN_LEFT);
		System.out.println("PLAYER_TEMPLE_COUNT " + PLAYER_TEMPLE_COUNT);
		System.out.println("PLAYER_BATTLE_CARD_AVALIABLE " + PLAYER_BATTLE_CARD_AVALIABLE);
		System.out.println("PLAYER_DAWN_TOKEN " + PLAYER_DAWN_TOKEN);
		System.out.println("PLAYER_SILVER_TOKEN_USED " + PLAYER_SILVER_TOKEN_USED);
		System.out.println("PLAYER_GOLD_TOKEN_USED " + PLAYER_GOLD_TOKEN_USED);
		System.out.println("PLAYER_DAWN_STRENGTH " + PLAYER_DAWN_STRENGTH);
		System.out.println("PLAYER_SELECTED_ORDER " + PLAYER_SELECTED_ORDER);
		System.out.println("PLAYER_ORDER " + PLAYER_ORDER);
		System.out.println("PLAYER_POWERS " + PLAYER_POWERS);
		System.out.println("RECRUIT_BEAST " + RECRUIT_BEAST);
		System.out.println("BEAST_POSITION " + BEAST_POSITION);
		System.out.println("BEAST_AVAILABLE " + BEAST_AVAILABLE);
		System.out.println("DI_DISCARD " + DI_DISCARD);
		System.out.println("CURRENT_PLAYER_DI " + CURRENT_PLAYER_DI);
		System.out.println("CURRENT_PLAYER_ACTIVATED_DI " + CURRENT_PLAYER_ACTIVATED_DI);
		System.out.println("DI_CARD_PER_PLAYER " + DI_CARD_PER_PLAYER);
		System.out.println("STATE_VETO_PLAYER " + STATE_VETO_PLAYER);
		System.out.println("STATE_VETO_DI_CARD " + STATE_VETO_DI_CARD);
		System.out.println("STATE_PLAYER_VETO_DONE " + STATE_PLAYER_VETO_DONE);
		System.out.println("STATE_PLAYER_DOING_VETO_ON_VETO " + STATE_PLAYER_DOING_VETO_ON_VETO);
		System.out.println("MOVE_FREE_BREACH_WALL " + MOVE_FREE_BREACH_WALL);
		System.out.println("MOVE_FREE_TELEPORT " + MOVE_FREE_TELEPORT);
		System.out.println("AVAILABLE_DI " + AVAILABLE_DI);
		System.out.println("PICK_DI_STATE " + PICK_DI_STATE);
		System.out.println("PICK_DI_MOVE_REST_TO_DISCARD " + PICK_DI_MOVE_REST_TO_DISCARD);
		System.out.println("RAINING_FIRE_STATE " + RAINING_FIRE_STATE);
		System.out.println("ESCAPE_PICKED " + ESCAPE_PICKED);
		System.out.println("ESCAPE_TILE_PICKED " + ESCAPE_TILE_PICKED);
		System.out.println("STATE_PICK_ATTACKER_DIVINE_WOUND " + STATE_PICK_ATTACKER_DIVINE_WOUND);
		System.out.println("STATE_PICK_DEFENDER_DIVINE_WOUND " + STATE_PICK_DEFENDER_DIVINE_WOUND);
		System.out.println("STATE_PICK_ATTACKER_TACTICAL_CHOICE " + STATE_PICK_ATTACKER_TACTICAL_CHOICE);
		System.out.println("STATE_PICK_DEFENDER_TACTICAL_CHOICE " + STATE_PICK_DEFENDER_TACTICAL_CHOICE);
		
		
		System.out.println("TOTAL_STATE_COUNT " + TOTAL_STATE_COUNT);

	}

}


//Board Inventory
//ROUND_NUMBER 0
//STATE_RECRUIT 1
//STATE_FREE_RECRUIT 2
//STATE_MOVE 3
//STATE_BATTLE 4
//STATE_UPGRADE_PYRAMID 5
//STATE_INITIAL_PYRAMID 6
//STATE_INITIAL_ARMY 7
//STATE_PICK_ACTION_TOKEN 8
//STATE_PICK_ATTACK_BATTLE_CARD 9
//STATE_PICK_ATTACK_DISCARD 10
//STATE_PICK_DEFENSE_BATTLE_CARD 11
//STATE_PICK_DEFENSE_DISCARD 12
//STATE_PICK_ARMY_SIZE 13
//STATE_PICK_TILE 14
//STATE_PICK_BEAST 15
//STATE_PICK_SOURCE_TILE 16
//STATE_PICK_PYRAMID_COLOR 17
//STATE_PICK_PYRAMID_LEVEL 18
//STATE_PICK_ATTACKER_RECALL 19
//STATE_PICK_DEFENDER_RECALL 20
//STATE_PICK_ATTACKER_RETREAT 21
//STATE_PICK_DEFENDER_RETREAT 22
//STATE_PICK_INITIATIVE_BATTLE_CARD 23
//STATE_PICK_INITIATIVE_DISCARD 24
//STATE_PICK_INITIATIVE_DAWN_TOKEN 25
//STATE_PICK_INITIATIVE_ORDER 26
//STATE_PICK_BATTLECARD_TO_REMOVE 27
//STATE_BUY_POWER_COLOR 28
//PICKED_ACTION_IN_ORDER 32
//MAIN_TOKEN_PICKED 68
//PICKED_SIZE 69
//PICKED_LEVEL 70
//MOVES_LEFT 71
//IS_FIRST_MOVE 72
//BATTLE_ATTACKER_STRENGTH 73
//BATTLE_ATTACKER_SHIELD 74
//BATTLE_ATTACKER_DAMAGE 75
//BATTLE_DEFENDER_STRENGTH 76
//BATTLE_DEFENDER_SHIELD 77
//BATTLE_DEFENDER_DAMAGE 78
//BATTLE_ATTACKER_WON 79
//FREE_RECRUIT_LEFT 80
//TILE_PLAYER_ARMY_SIZE 81
//TILE_SELECTED 107
//TILE_SOURCE_SELECTED 120
//TILE_BLACK_PYRAMID_LEVEL 133
//TILE_RED_PYRAMID_LEVEL 146
//TILE_BLUE_PYRAMID_LEVEL 159
//TILE_WHITE_PYRAMID_LEVEL 172
//PLAYER_VICTORY_POINTS 185
//PLAYER_BATTLE_POINTS 187
//PLAYER_PRAYER_POINTS 189
//PLAYER_AVAILABLE_ARMY_TOKENS 191
//PLAYER_ROW_ONE_MOVE_USED 193
//PLAYER_ROW_ONE_RECRUIT_USED 195
//PLAYER_ROW_TWO_MOVE_USED 197
//PLAYER_ROW_TWO_UPGRADE_PYRAMID_USED 199
//PLAYER_ROW_TWO_PRAY_USED 201
//PLAYER_ROW_THREE_PRAY_USED 203
//PLAYER_ROW_THREE_BUILD_WHITE_USED 205
//PLAYER_ROW_THREE_BUILD_RED_USED 207
//PLAYER_ROW_THREE_BUILD_BLUE_USED 209
//PLAYER_ROW_THREE_BUILD_BLACK_USED 211
//PLAYER_ACTION_TOKEN_LEFT 213
//PLAYER_TEMPLE_COUNT 215
//PLAYER_BATTLE_CARD_AVALIABLE 217
//PLAYER_DAWN_TOKEN 237
//PLAYER_SILVER_TOKEN_USED 239
//PLAYER_GOLD_TOKEN_USED 241
//PLAYER_DAWN_STRENGTH 243
//PLAYER_SELECTED_ORDER 245
//PLAYER_ORDER 249
//PLAYER_POWERS 253
//RECRUIT_BEAST 381
//BEAST_POSITION 391
//BEAST_AVAILABLE 651
//DI_DISCARD 671
//CURRENT_PLAYER_DI 691
//CURRENT_PLAYER_ACTIVATED_DI 711
//DI_CARD_PER_PLAYER 721
//STATE_VETO_PLAYER 723
//STATE_VETO_DI_CARD 725
//STATE_PLAYER_VETO_DONE 735
//STATE_PLAYER_DOING_VETO_ON_VETO 737
//MOVE_FREE_BREACH_WALL 739
//MOVE_FREE_TELEPORT 740
//AVAILABLE_DI 741
//PICK_DI_STATE 761
//PICK_DI_MOVE_REST_TO_DISCARD 762
//RAINING_FIRE_STATE 763
//ESCAPE_PICKED 764
//ESCAPE_TILE_PICKED 765
//STATE_PICK_ATTACKER_DIVINE_WOUND 766
//STATE_PICK_DEFENDER_DIVINE_WOUND 767
//STATE_PICK_ATTACKER_TACTICAL_CHOICE 768
//STATE_PICK_DEFENDER_TACTICAL_CHOICE 769
//TOTAL_STATE_COUNT 770