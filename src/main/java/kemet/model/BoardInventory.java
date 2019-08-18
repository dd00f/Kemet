package kemet.model;

import java.util.ArrayList;
import java.util.List;

import kemet.util.ByteCanonicalForm;

public class BoardInventory {

	public static final int MAX_ACTION_PER_TURN = 3;

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
	public static final int STATE_RECRUIT = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_FREE_RECRUIT = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_MOVE = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_BATTLE = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_UPGRADE_PYRAMID = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_INITIAL_PYRAMID = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_INITIAL_ARMY = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_ACTION_TOKEN = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_ATTACK_BATTLE_CARD = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_ATTACK_DISCARD = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_DEFENSE_BATTLE_CARD = INDEXER; 
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_DEFENSE_DISCARD = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_ARMY_SIZE = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_TILE = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_BEAST = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_SOURCE_TILE = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_PYRAMID_COLOR = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_PYRAMID_LEVEL = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_ATTACKER_RECALL = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_DEFENDER_RECALL = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_ATTACKER_RETREAT = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_DEFENDER_RETREAT = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_INITIATIVE_BATTLE_CARD = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_INITIATIVE_DISCARD = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_INITIATIVE_DAWN_TOKEN = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_INITIATIVE_ORDER = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_BATTLECARD_TO_REMOVE = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	
	public static final int STATE_BUY_POWER_COLOR = INDEXER;
	static {
		INDEXER += COLOR_COUNT;
	}

	public static final int PICKED_ACTION_IN_ORDER = INDEXER;
	static {
		INDEXER += ActionList.TOTAL_ACTION_COUNT * MAX_ACTION_PER_TURN * PLAYER_COUNT;
	}

	// -----------------------
	// GAME SELECTION DATA
	// -----------------------
	public static final int MAIN_TOKEN_PICKED = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
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
	public static final int BATTLE_DEFENDER_WON = INDEXER++; // 1 yes, -1 no
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
		INDEXER += PLAYER_COUNT * TILE_COUNT;
	}

	// tile selected as source for a movement
	public static final int TILE_SOURCE_SELECTED = INDEXER;
	static {
		INDEXER += PLAYER_COUNT * TILE_COUNT;
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

	// 1 for every card available to the current player
	public static final int PLAYER_BATTLE_CARD_AVALIABLE = INDEXER;
	static {
		INDEXER += BattleCard.INDEXER;
	}
	
	// 1 for every card not in the discard pile for all players
	public static final int PLAYER_BATTLE_CARD_VISIBLE = INDEXER;
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

	public static final int PICK_DI_STATE = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int PICK_DI_MOVE_REST_TO_DISCARD = INDEXER++;
	public static final int RAINING_FIRE_STATE = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int ESCAPE_PICKED = INDEXER++;
	public static final int ESCAPE_TILE_PICKED = INDEXER++;
	public static final int STATE_PICK_ATTACKER_DIVINE_WOUND = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_DEFENDER_DIVINE_WOUND = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_ATTACKER_TACTICAL_CHOICE = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_DEFENDER_TACTICAL_CHOICE = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_PICK_ESCAPE = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_ESCAPE_SELECT_TILE = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}
	public static final int STATE_ACTIVATE_OPTIONAL_TEMPLE = INDEXER;
	static {
		INDEXER += PLAYER_COUNT;
	}

	public static final int TOTAL_STATE_COUNT = INDEXER;

	public static final List<Integer> NUMBER_LIST = new ArrayList<>();
	public static final List<String> NAME_LIST = new ArrayList<>();

	public static void registerNumber(String name, int number) {

		if (!NUMBER_LIST.isEmpty()) {
			Integer last = NUMBER_LIST.get(NUMBER_LIST.size() - 1);
			if (number <= last) {
				String message = "number " + number + " smaller than " + last + ", new name " + name + " previous name "
						+ NAME_LIST.get(NAME_LIST.size() - 1);
				throw new IllegalStateException(message);
			}
		}

		NUMBER_LIST.add(number);
		NAME_LIST.add(name);
	}

	static {
		registerNumber("ROUND_NUMBER ", ROUND_NUMBER);

		registerNumber("STATE_RECRUIT ", STATE_RECRUIT);
		registerNumber("STATE_FREE_RECRUIT ", STATE_FREE_RECRUIT);
		registerNumber("STATE_MOVE ", STATE_MOVE);
		registerNumber("STATE_BATTLE ", STATE_BATTLE);
		registerNumber("STATE_UPGRADE_PYRAMID ", STATE_UPGRADE_PYRAMID);
		registerNumber("STATE_INITIAL_PYRAMID ", STATE_INITIAL_PYRAMID);
		registerNumber("STATE_INITIAL_ARMY ", STATE_INITIAL_ARMY);
		registerNumber("STATE_PICK_ACTION_TOKEN ", STATE_PICK_ACTION_TOKEN);
		registerNumber("STATE_PICK_ATTACK_BATTLE_CARD ", STATE_PICK_ATTACK_BATTLE_CARD);
		registerNumber("STATE_PICK_ATTACK_DISCARD ", STATE_PICK_ATTACK_DISCARD);
		registerNumber("STATE_PICK_DEFENSE_BATTLE_CARD ", STATE_PICK_DEFENSE_BATTLE_CARD);
		registerNumber("STATE_PICK_DEFENSE_DISCARD ", STATE_PICK_DEFENSE_DISCARD);
		registerNumber("STATE_PICK_ARMY_SIZE ", STATE_PICK_ARMY_SIZE);
		registerNumber("STATE_PICK_TILE ", STATE_PICK_TILE);
		registerNumber("STATE_PICK_BEAST ", STATE_PICK_BEAST);
		registerNumber("STATE_PICK_SOURCE_TILE ", STATE_PICK_SOURCE_TILE);
		registerNumber("STATE_PICK_PYRAMID_COLOR ", STATE_PICK_PYRAMID_COLOR);
		registerNumber("STATE_PICK_PYRAMID_LEVEL ", STATE_PICK_PYRAMID_LEVEL);
		registerNumber("STATE_PICK_ATTACKER_RECALL ", STATE_PICK_ATTACKER_RECALL);
		registerNumber("STATE_PICK_DEFENDER_RECALL ", STATE_PICK_DEFENDER_RECALL);
		registerNumber("STATE_PICK_ATTACKER_RETREAT ", STATE_PICK_ATTACKER_RETREAT);
		registerNumber("STATE_PICK_DEFENDER_RETREAT ", STATE_PICK_DEFENDER_RETREAT);
		registerNumber("STATE_PICK_INITIATIVE_BATTLE_CARD ", STATE_PICK_INITIATIVE_BATTLE_CARD);
		registerNumber("STATE_PICK_INITIATIVE_DISCARD ", STATE_PICK_INITIATIVE_DISCARD);
		registerNumber("STATE_PICK_INITIATIVE_DAWN_TOKEN ", STATE_PICK_INITIATIVE_DAWN_TOKEN);
		registerNumber("STATE_PICK_INITIATIVE_ORDER ", STATE_PICK_INITIATIVE_ORDER);
		registerNumber("STATE_PICK_BATTLECARD_TO_REMOVE ", STATE_PICK_BATTLECARD_TO_REMOVE);
		registerNumber("STATE_BUY_POWER_COLOR ", STATE_BUY_POWER_COLOR);
		registerNumber("PICKED_ACTION_IN_ORDER ", PICKED_ACTION_IN_ORDER);
		registerNumber("MAIN_TOKEN_PICKED ", MAIN_TOKEN_PICKED);
		registerNumber("PICKED_SIZE ", PICKED_SIZE);
		registerNumber("PICKED_LEVEL ", PICKED_LEVEL);
		registerNumber("MOVES_LEFT ", MOVES_LEFT);
		registerNumber("IS_FIRST_MOVE ", IS_FIRST_MOVE);
		registerNumber("BATTLE_ATTACKER_STRENGTH ", BATTLE_ATTACKER_STRENGTH);
		registerNumber("BATTLE_ATTACKER_SHIELD ", BATTLE_ATTACKER_SHIELD);
		registerNumber("BATTLE_ATTACKER_DAMAGE ", BATTLE_ATTACKER_DAMAGE);
		registerNumber("BATTLE_DEFENDER_STRENGTH ", BATTLE_DEFENDER_STRENGTH);
		registerNumber("BATTLE_DEFENDER_SHIELD ", BATTLE_DEFENDER_SHIELD);
		registerNumber("BATTLE_DEFENDER_DAMAGE ", BATTLE_DEFENDER_DAMAGE);
		registerNumber("BATTLE_ATTACKER_WON ", BATTLE_ATTACKER_WON);
		registerNumber("BATTLE_DEFENDER_WON ", BATTLE_DEFENDER_WON);
		registerNumber("FREE_RECRUIT_LEFT ", FREE_RECRUIT_LEFT);
		registerNumber("TILE_PLAYER_ARMY_SIZE ", TILE_PLAYER_ARMY_SIZE);
		registerNumber("TILE_SELECTED ", TILE_SELECTED);
		registerNumber("TILE_SOURCE_SELECTED ", TILE_SOURCE_SELECTED);
		registerNumber("TILE_BLACK_PYRAMID_LEVEL ", TILE_BLACK_PYRAMID_LEVEL);
		registerNumber("TILE_RED_PYRAMID_LEVEL ", TILE_RED_PYRAMID_LEVEL);
		registerNumber("TILE_BLUE_PYRAMID_LEVEL ", TILE_BLUE_PYRAMID_LEVEL);
		registerNumber("TILE_WHITE_PYRAMID_LEVEL ", TILE_WHITE_PYRAMID_LEVEL);
		registerNumber("PLAYER_VICTORY_POINTS ", PLAYER_VICTORY_POINTS);
		registerNumber("PLAYER_BATTLE_POINTS ", PLAYER_BATTLE_POINTS);
		registerNumber("PLAYER_PRAYER_POINTS ", PLAYER_PRAYER_POINTS);
		registerNumber("PLAYER_AVAILABLE_ARMY_TOKENS ", PLAYER_AVAILABLE_ARMY_TOKENS);
		registerNumber("PLAYER_ROW_ONE_MOVE_USED ", PLAYER_ROW_ONE_MOVE_USED);
		registerNumber("PLAYER_ROW_ONE_RECRUIT_USED ", PLAYER_ROW_ONE_RECRUIT_USED);
		registerNumber("PLAYER_ROW_TWO_MOVE_USED ", PLAYER_ROW_TWO_MOVE_USED);
		registerNumber("PLAYER_ROW_TWO_UPGRADE_PYRAMID_USED ", PLAYER_ROW_TWO_UPGRADE_PYRAMID_USED);
		registerNumber("PLAYER_ROW_TWO_PRAY_USED ", PLAYER_ROW_TWO_PRAY_USED);
		registerNumber("PLAYER_ROW_THREE_PRAY_USED ", PLAYER_ROW_THREE_PRAY_USED);
		registerNumber("PLAYER_ROW_THREE_BUILD_WHITE_USED ", PLAYER_ROW_THREE_BUILD_WHITE_USED);
		registerNumber("PLAYER_ROW_THREE_BUILD_RED_USED ", PLAYER_ROW_THREE_BUILD_RED_USED);
		registerNumber("PLAYER_ROW_THREE_BUILD_BLUE_USED ", PLAYER_ROW_THREE_BUILD_BLUE_USED);
		registerNumber("PLAYER_ROW_THREE_BUILD_BLACK_USED ", PLAYER_ROW_THREE_BUILD_BLACK_USED);
		registerNumber("PLAYER_ACTION_TOKEN_LEFT ", PLAYER_ACTION_TOKEN_LEFT);
		registerNumber("PLAYER_TEMPLE_COUNT ", PLAYER_TEMPLE_COUNT);
		registerNumber("PLAYER_BATTLE_CARD_AVALIABLE ", PLAYER_BATTLE_CARD_AVALIABLE);
		registerNumber("PLAYER_DAWN_TOKEN ", PLAYER_DAWN_TOKEN);
		registerNumber("PLAYER_SILVER_TOKEN_USED ", PLAYER_SILVER_TOKEN_USED);
		registerNumber("PLAYER_GOLD_TOKEN_USED ", PLAYER_GOLD_TOKEN_USED);
		registerNumber("PLAYER_DAWN_STRENGTH ", PLAYER_DAWN_STRENGTH);
		registerNumber("PLAYER_SELECTED_ORDER ", PLAYER_SELECTED_ORDER);
		registerNumber("PLAYER_ORDER ", PLAYER_ORDER);
		registerNumber("PLAYER_POWERS ", PLAYER_POWERS);
		registerNumber("RECRUIT_BEAST ", RECRUIT_BEAST);
		registerNumber("BEAST_POSITION ", BEAST_POSITION);
		registerNumber("BEAST_AVAILABLE ", BEAST_AVAILABLE);
		registerNumber("DI_DISCARD ", DI_DISCARD);
		registerNumber("CURRENT_PLAYER_DI ", CURRENT_PLAYER_DI);
		registerNumber("CURRENT_PLAYER_ACTIVATED_DI ", CURRENT_PLAYER_ACTIVATED_DI);
		registerNumber("DI_CARD_PER_PLAYER ", DI_CARD_PER_PLAYER);
		registerNumber("STATE_VETO_PLAYER ", STATE_VETO_PLAYER);
		registerNumber("STATE_VETO_DI_CARD ", STATE_VETO_DI_CARD);
		registerNumber("STATE_PLAYER_VETO_DONE ", STATE_PLAYER_VETO_DONE);
		registerNumber("STATE_PLAYER_DOING_VETO_ON_VETO ", STATE_PLAYER_DOING_VETO_ON_VETO);
		registerNumber("MOVE_FREE_BREACH_WALL ", MOVE_FREE_BREACH_WALL);
		registerNumber("MOVE_FREE_TELEPORT ", MOVE_FREE_TELEPORT);
		registerNumber("AVAILABLE_DI ", AVAILABLE_DI);
		registerNumber("PICK_DI_STATE ", PICK_DI_STATE);
		registerNumber("PICK_DI_MOVE_REST_TO_DISCARD ", PICK_DI_MOVE_REST_TO_DISCARD);
		registerNumber("RAINING_FIRE_STATE ", RAINING_FIRE_STATE);
		registerNumber("ESCAPE_PICKED ", ESCAPE_PICKED);
		registerNumber("ESCAPE_TILE_PICKED ", ESCAPE_TILE_PICKED);
		registerNumber("STATE_PICK_ATTACKER_DIVINE_WOUND ", STATE_PICK_ATTACKER_DIVINE_WOUND);
		registerNumber("STATE_PICK_DEFENDER_DIVINE_WOUND ", STATE_PICK_DEFENDER_DIVINE_WOUND);
		registerNumber("STATE_PICK_ATTACKER_TACTICAL_CHOICE ", STATE_PICK_ATTACKER_TACTICAL_CHOICE);
		registerNumber("STATE_PICK_DEFENDER_TACTICAL_CHOICE ", STATE_PICK_DEFENDER_TACTICAL_CHOICE);
		registerNumber("STATE_PICK_ESCAPE ", STATE_PICK_ESCAPE);
		registerNumber("STATE_ESCAPE_SELECT_TILE ", STATE_ESCAPE_SELECT_TILE);
		registerNumber("STATE_ACTIVATE_OPTIONAL_TEMPLE ", STATE_ACTIVATE_OPTIONAL_TEMPLE);
	}

	public static String describeCanonicalFormat(ByteCanonicalForm canonicalForm) {

		byte[] bs = canonicalForm.getCanonicalForm();

		return describeCanonicalFormat(bs);

	}

	public static String describeCanonicalFormat(byte[] bs) {

		StringBuilder build = new StringBuilder();

		for (int i = 0; i < bs.length; i++) {
			byte b = bs[i];
			if (b != 0) {

				getDescription(i, build);

				build.append(" is ");
				build.append(b);
				build.append("\n");
			}

		}

		return build.toString();
	}

	private static void getDescription(int index, StringBuilder build) {
		int previous = 0;
		for (int i = 0; i < NUMBER_LIST.size(); i++) {
			Integer next = NUMBER_LIST.get(i);

			if (next > index) {
				int nameIndex = i - 1;
				int delta = index - previous;
				fillName(build, index, nameIndex, delta);
				return;
			}
			previous = next;
		}
		int delta = index - previous;
		fillName(build, index, NAME_LIST.size() - 1, delta);
	}

	private static void fillName(StringBuilder build, int index, int nameIndex, int delta) {
		build.append(index);
		build.append(" ");
		build.append(NAME_LIST.get(nameIndex));
		build.append(" + ");
		build.append(delta);
	}

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
		System.out.println("BATTLE_DEFENDER_WON " + BATTLE_DEFENDER_WON);
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
		System.out.println("STATE_PICK_ESCAPE " + STATE_PICK_ESCAPE);
		System.out.println("STATE_ESCAPE_SELECT_TILE " + STATE_ESCAPE_SELECT_TILE);
		System.out.println("STATE_ACTIVATE_OPTIONAL_TEMPLE " + STATE_ACTIVATE_OPTIONAL_TEMPLE);

		
		System.out.println("TOTAL_STATE_COUNT " + TOTAL_STATE_COUNT);

	}

}

//Board Inventory
//ROUND_NUMBER 0
//STATE_RECRUIT 1
//STATE_FREE_RECRUIT 3
//STATE_MOVE 5
//STATE_BATTLE 7
//STATE_UPGRADE_PYRAMID 9
//STATE_INITIAL_PYRAMID 11
//STATE_INITIAL_ARMY 13
//STATE_PICK_ACTION_TOKEN 15
//STATE_PICK_ATTACK_BATTLE_CARD 17
//STATE_PICK_ATTACK_DISCARD 19
//STATE_PICK_DEFENSE_BATTLE_CARD 21
//STATE_PICK_DEFENSE_DISCARD 23
//STATE_PICK_ARMY_SIZE 25
//STATE_PICK_TILE 27
//STATE_PICK_BEAST 29
//STATE_PICK_SOURCE_TILE 31
//STATE_PICK_PYRAMID_COLOR 33
//STATE_PICK_PYRAMID_LEVEL 35
//STATE_PICK_ATTACKER_RECALL 37
//STATE_PICK_DEFENDER_RECALL 39
//STATE_PICK_ATTACKER_RETREAT 41
//STATE_PICK_DEFENDER_RETREAT 43
//STATE_PICK_INITIATIVE_BATTLE_CARD 45
//STATE_PICK_INITIATIVE_DISCARD 47
//STATE_PICK_INITIATIVE_DAWN_TOKEN 49
//STATE_PICK_INITIATIVE_ORDER 51
//STATE_PICK_BATTLECARD_TO_REMOVE 53
//STATE_BUY_POWER_COLOR 55
//PICKED_ACTION_IN_ORDER 59
//MAIN_TOKEN_PICKED 131
//PICKED_SIZE 133
//PICKED_LEVEL 134
//MOVES_LEFT 135
//IS_FIRST_MOVE 136
//BATTLE_ATTACKER_STRENGTH 137
//BATTLE_ATTACKER_SHIELD 138
//BATTLE_ATTACKER_DAMAGE 139
//BATTLE_DEFENDER_STRENGTH 140
//BATTLE_DEFENDER_SHIELD 141
//BATTLE_DEFENDER_DAMAGE 142
//BATTLE_ATTACKER_WON 143
//BATTLE_DEFENDER_WON 144
//FREE_RECRUIT_LEFT 145
//TILE_PLAYER_ARMY_SIZE 146
//TILE_SELECTED 172
//TILE_SOURCE_SELECTED 198
//TILE_BLACK_PYRAMID_LEVEL 224
//TILE_RED_PYRAMID_LEVEL 237
//TILE_BLUE_PYRAMID_LEVEL 250
//TILE_WHITE_PYRAMID_LEVEL 263
//PLAYER_VICTORY_POINTS 276
//PLAYER_BATTLE_POINTS 278
//PLAYER_PRAYER_POINTS 280
//PLAYER_AVAILABLE_ARMY_TOKENS 282
//PLAYER_ROW_ONE_MOVE_USED 284
//PLAYER_ROW_ONE_RECRUIT_USED 286
//PLAYER_ROW_TWO_MOVE_USED 288
//PLAYER_ROW_TWO_UPGRADE_PYRAMID_USED 290
//PLAYER_ROW_TWO_PRAY_USED 292
//PLAYER_ROW_THREE_PRAY_USED 294
//PLAYER_ROW_THREE_BUILD_WHITE_USED 296
//PLAYER_ROW_THREE_BUILD_RED_USED 298
//PLAYER_ROW_THREE_BUILD_BLUE_USED 300
//PLAYER_ROW_THREE_BUILD_BLACK_USED 302
//PLAYER_ACTION_TOKEN_LEFT 304
//PLAYER_TEMPLE_COUNT 306
//PLAYER_BATTLE_CARD_AVALIABLE 308
//PLAYER_DAWN_TOKEN 338
//PLAYER_SILVER_TOKEN_USED 340
//PLAYER_GOLD_TOKEN_USED 342
//PLAYER_DAWN_STRENGTH 344
//PLAYER_SELECTED_ORDER 346
//PLAYER_ORDER 350
//PLAYER_POWERS 354
//RECRUIT_BEAST 482
//BEAST_POSITION 492
//BEAST_AVAILABLE 752
//DI_DISCARD 772
//CURRENT_PLAYER_DI 792
//CURRENT_PLAYER_ACTIVATED_DI 812
//DI_CARD_PER_PLAYER 822
//STATE_VETO_PLAYER 824
//STATE_VETO_DI_CARD 826
//STATE_PLAYER_VETO_DONE 836
//STATE_PLAYER_DOING_VETO_ON_VETO 838
//MOVE_FREE_BREACH_WALL 840
//MOVE_FREE_TELEPORT 841
//AVAILABLE_DI 842
//PICK_DI_STATE 862
//PICK_DI_MOVE_REST_TO_DISCARD 864
//RAINING_FIRE_STATE 865
//ESCAPE_PICKED 867
//ESCAPE_TILE_PICKED 868
//STATE_PICK_ATTACKER_DIVINE_WOUND 869
//STATE_PICK_DEFENDER_DIVINE_WOUND 871
//STATE_PICK_ATTACKER_TACTICAL_CHOICE 873
//STATE_PICK_DEFENDER_TACTICAL_CHOICE 875
//STATE_PICK_ESCAPE 877
//STATE_ESCAPE_SELECT_TILE 879
//STATE_ACTIVATE_OPTIONAL_TEMPLE 881
//TOTAL_STATE_COUNT 883
