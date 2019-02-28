package kemet.model;

import kemet.Options;
import kemet.util.ByteCanonicalForm;

public class KemetByteCanonicalForm extends ByteCanonicalForm {

	private static final int MAX_ARMY_SIZE_PICK = 7;

	public KemetByteCanonicalForm(int size) {
		super(size);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5776464919210108540L;
	private static final float MAX_LEVEL = 4;
	private static final float MAX_BATTLE_TOTAL_STRENGTH = 20;
	private static final float MAX_BATTLE_DAMAGE = 10;
	private static final float MAX_BATTLE_SHIELD = 10;
	public static final float MAXIMUM_ARMY_TOKENS = 15;
	private static final float MAXIMUM_TEMPLE_COUNT = 3;

	@Override
	public float[] getFloatCanonicalForm() {
		float[] floatCanonicalForm = super.getFloatCanonicalForm();

		// rectify some values to be on a scale of -1 to 1

		floatCanonicalForm[BoardInventory.ROUND_NUMBER] = floatCanonicalForm[BoardInventory.ROUND_NUMBER]
				/ Options.GAME_TURN_LIMIT;

		floatCanonicalForm[BoardInventory.PICKED_SIZE] = floatCanonicalForm[BoardInventory.PICKED_SIZE]
				/ MAX_ARMY_SIZE_PICK;

		floatCanonicalForm[BoardInventory.PICKED_LEVEL] = floatCanonicalForm[BoardInventory.PICKED_LEVEL] / MAX_LEVEL;

		floatCanonicalForm[BoardInventory.MOVES_LEFT] = floatCanonicalForm[BoardInventory.MOVES_LEFT] / 5;

		floatCanonicalForm[BoardInventory.BATTLE_ATTACKER_STRENGTH] = floatCanonicalForm[BoardInventory.BATTLE_ATTACKER_STRENGTH]
				/ MAX_BATTLE_TOTAL_STRENGTH;
		floatCanonicalForm[BoardInventory.BATTLE_ATTACKER_SHIELD] = floatCanonicalForm[BoardInventory.BATTLE_ATTACKER_SHIELD]
				/ MAX_BATTLE_SHIELD;
		floatCanonicalForm[BoardInventory.BATTLE_ATTACKER_DAMAGE] = floatCanonicalForm[BoardInventory.BATTLE_ATTACKER_DAMAGE]
				/ MAX_BATTLE_DAMAGE;
		floatCanonicalForm[BoardInventory.BATTLE_DEFENDER_STRENGTH] = floatCanonicalForm[BoardInventory.BATTLE_DEFENDER_STRENGTH]
				/ MAX_BATTLE_TOTAL_STRENGTH;
		floatCanonicalForm[BoardInventory.BATTLE_DEFENDER_SHIELD] = floatCanonicalForm[BoardInventory.BATTLE_DEFENDER_SHIELD]
				/ MAX_BATTLE_SHIELD;
		floatCanonicalForm[BoardInventory.BATTLE_DEFENDER_DAMAGE] = floatCanonicalForm[BoardInventory.BATTLE_DEFENDER_DAMAGE]
				/ MAX_BATTLE_DAMAGE;

		floatCanonicalForm[BoardInventory.BATTLE_DEFENDER_SHIELD] = floatCanonicalForm[BoardInventory.BATTLE_DEFENDER_SHIELD]
				/ 5;

		adjustRange(floatCanonicalForm, BoardInventory.TILE_PLAYER_ARMY_SIZE,
				BoardInventory.PLAYER_COUNT * BoardInventory.TILE_COUNT, MAX_ARMY_SIZE_PICK);

		adjustRange(floatCanonicalForm, BoardInventory.TILE_BLACK_PYRAMID_LEVEL, BoardInventory.TILE_COUNT, MAX_LEVEL);
		adjustRange(floatCanonicalForm, BoardInventory.TILE_RED_PYRAMID_LEVEL, BoardInventory.TILE_COUNT, MAX_LEVEL);
		adjustRange(floatCanonicalForm, BoardInventory.TILE_BLUE_PYRAMID_LEVEL, BoardInventory.TILE_COUNT, MAX_LEVEL);
		adjustRange(floatCanonicalForm, BoardInventory.TILE_WHITE_PYRAMID_LEVEL, BoardInventory.TILE_COUNT, MAX_LEVEL);

		adjustRange(floatCanonicalForm, BoardInventory.PLAYER_VICTORY_POINTS, BoardInventory.PLAYER_COUNT,
				KemetGame.VICTORY_POINTS_OBJECTIVE);
		adjustRange(floatCanonicalForm, BoardInventory.PLAYER_BATTLE_POINTS, BoardInventory.PLAYER_COUNT,
				KemetGame.VICTORY_POINTS_OBJECTIVE);
		adjustRange(floatCanonicalForm, BoardInventory.PLAYER_PRAYER_POINTS, BoardInventory.PLAYER_COUNT,
				Player.MAXIMUM_PRAYER_POINTS);
		adjustRange(floatCanonicalForm, BoardInventory.PLAYER_AVAILABLE_ARMY_TOKENS, BoardInventory.PLAYER_COUNT,
				MAXIMUM_ARMY_TOKENS);
		adjustRange(floatCanonicalForm, BoardInventory.PLAYER_ACTION_TOKEN_LEFT, BoardInventory.PLAYER_COUNT,
				Player.ACTION_TOKEN_COUNT);

		adjustRange(floatCanonicalForm, BoardInventory.PLAYER_TEMPLE_COUNT, BoardInventory.PLAYER_COUNT,
				MAXIMUM_TEMPLE_COUNT);

		return floatCanonicalForm;
	}

	private void adjustRange(float[] floatCanonicalForm, int startIndex, int range, float rectificationFactor) {
		int stopI = startIndex + range;
		for (int i = startIndex; i < stopI; ++i) {
			floatCanonicalForm[i] /= rectificationFactor;
		}
	}

	public static String describeBoard( float[] board ) {
		StringBuilder build = new StringBuilder();

		printValue(board, build, BoardInventory.ROUND_NUMBER,  " ROUND_NUMBER ", Options.GAME_TURN_LIMIT);
		
		printValue(board, build, BoardInventory.STATE_RECRUIT,  " STATE_RECRUIT ", 1);
		printValue(board, build, BoardInventory.STATE_MOVE,  " STATE_MOVE ", 1);
		printValue(board, build, BoardInventory.STATE_BATTLE,  " STATE_BATTLE ", 1);
		printValue(board, build, BoardInventory.STATE_UPGRADE_PYRAMID,  " STATE_UPGRADE_PYRAMID ", 1);
		printValue(board, build, BoardInventory.STATE_INITIAL_PYRAMID,  " STATE_INITIAL_PYRAMID ", 1);
		printValue(board, build, BoardInventory.STATE_INITIAL_ARMY,  " STATE_INITIAL_ARMY ", 1);
		printValue(board, build, BoardInventory.STATE_PICK_ACTION_TOKEN,  " STATE_PICK_ACTION_TOKEN ", 1);
		printValue(board, build, BoardInventory.STATE_PICK_ATTACK_DISCARD,  " STATE_PICK_ATTACK_DISCARD ", 1);
		printValue(board, build, BoardInventory.STATE_PICK_DEFENSE_BATTLE_CARD,  " STATE_PICK_DEFENSE_BATTLE_CARD ", 1);
		printValue(board, build, BoardInventory.STATE_PICK_DEFENSE_DISCARD,  " STATE_PICK_DEFENSE_DISCARD ", 1);
		printValue(board, build, BoardInventory.STATE_PICK_ARMY_SIZE,  " STATE_PICK_ARMY_SIZE ", 1);
		printValue(board, build, BoardInventory.STATE_PICK_TILE,  " STATE_PICK_TILE ", 1);
		printValue(board, build, BoardInventory.STATE_PICK_SOURCE_TILE,  " STATE_PICK_SOURCE_TILE ", 1);
		printValue(board, build, BoardInventory.STATE_PICK_PYRAMID_COLOR,  " STATE_PICK_PYRAMID_COLOR ", 1);
		printValue(board, build, BoardInventory.STATE_PICK_PYRAMID_LEVEL,  " STATE_PICK_PYRAMID_LEVEL ", 1);
		printValue(board, build, BoardInventory.STATE_PICK_ATTACKER_RECALL,  " STATE_PICK_ATTACKER_RECALL ", 1);
		printValue(board, build, BoardInventory.STATE_PICK_DEFENDER_RECALL,  " STATE_PICK_DEFENDER_RECALL ", 1);
		printValue(board, build, BoardInventory.STATE_PICK_ATTACKER_RETREAT,  " STATE_PICK_ATTACKER_RETREAT ", 1);
		printValue(board, build, BoardInventory.STATE_PICK_DEFENDER_RETREAT,  " STATE_PICK_DEFENDER_RETREAT ", 1);
		
		printValue(board, build, BoardInventory.PICKED_SIZE,  " PICKED_SIZE ", 1);
		printValue(board, build, BoardInventory.PICKED_LEVEL,  " PICKED_LEVEL ", 1);
		printValue(board, build, BoardInventory.MOVES_LEFT,  " MOVES_LEFT ", 1);
		printValue(board, build, BoardInventory.IS_FIRST_MOVE,  " IS_FIRST_MOVE ", 1);
		printValue(board, build, BoardInventory.BATTLE_ATTACKER_STRENGTH,  " BATTLE_ATTACKER_STRENGTH ", 1);
		printValue(board, build, BoardInventory.BATTLE_ATTACKER_SHIELD,  " BATTLE_ATTACKER_SHIELD ", 1);
		printValue(board, build, BoardInventory.BATTLE_ATTACKER_DAMAGE,  " BATTLE_ATTACKER_DAMAGE ", 1);
		printValue(board, build, BoardInventory.BATTLE_DEFENDER_STRENGTH,  " BATTLE_DEFENDER_STRENGTH ", 1);
		printValue(board, build, BoardInventory.BATTLE_DEFENDER_SHIELD,  " BATTLE_DEFENDER_SHIELD ", 1);
		printValue(board, build, BoardInventory.BATTLE_DEFENDER_DAMAGE,  " BATTLE_DEFENDER_DAMAGE ", 1);
		printValue(board, build, BoardInventory.BATTLE_ATTACKER_WON,  " BATTLE_ATTACKER_WON ", 1);
		
		printTilePlayerValue(board, build, BoardInventory.TILE_PLAYER_ARMY_SIZE,  " TILE_PLAYER_ARMY_SIZE ", 1);

		printTileValue(board, build, BoardInventory.TILE_SELECTED,  " TILE_SELECTED ", 1);
		printTileValue(board, build, BoardInventory.TILE_SOURCE_SELECTED,  " TILE_SOURCE_SELECTED ", 1);
		printTileValue(board, build, BoardInventory.TILE_BLACK_PYRAMID_LEVEL,  " TILE_BLACK_PYRAMID_LEVEL ", 1);
		printTileValue(board, build, BoardInventory.TILE_RED_PYRAMID_LEVEL,  " TILE_RED_PYRAMID_LEVEL ", 1);
		printTileValue(board, build, BoardInventory.TILE_BLUE_PYRAMID_LEVEL,  " TILE_BLUE_PYRAMID_LEVEL ", 1);
		printTileValue(board, build, BoardInventory.TILE_WHITE_PYRAMID_LEVEL,  " TILE_WHITE_PYRAMID_LEVEL ", 1);

		printPlayerValue(board, build, BoardInventory.PLAYER_VICTORY_POINTS,  " PLAYER_VICTORY_POINTS ", 1);
		printPlayerValue(board, build, BoardInventory.PLAYER_BATTLE_POINTS,  " PLAYER_BATTLE_POINTS ", 1);
		printPlayerValue(board, build, BoardInventory.PLAYER_PRAYER_POINTS,  " PLAYER_PRAYER_POINTS ", 1);
		printPlayerValue(board, build, BoardInventory.PLAYER_AVAILABLE_ARMY_TOKENS,  " PLAYER_AVAILABLE_ARMY_TOKENS ", 1);
		printPlayerValue(board, build, BoardInventory.PLAYER_ROW_ONE_MOVE_USED,  " PLAYER_ROW_ONE_MOVE_USED ", 1);
		printPlayerValue(board, build, BoardInventory.PLAYER_ROW_ONE_RECRUIT_USED,  " PLAYER_ROW_ONE_RECRUIT_USED ", 1);
		printPlayerValue(board, build, BoardInventory.PLAYER_ROW_TWO_MOVE_USED,  " PLAYER_ROW_TWO_MOVE_USED ", 1);
		printPlayerValue(board, build, BoardInventory.PLAYER_ROW_TWO_UPGRADE_PYRAMID_USED,  " PLAYER_ROW_TWO_UPGRADE_PYRAMID_USED ", 1);
		printPlayerValue(board, build, BoardInventory.PLAYER_ROW_TWO_PRAY_USED,  " PLAYER_ROW_TWO_PRAY_USED ", 1);
		printPlayerValue(board, build, BoardInventory.PLAYER_ROW_THREE_PRAY_USED,  " PLAYER_ROW_THREE_PRAY_USED ", 1);
		printPlayerValue(board, build, BoardInventory.PLAYER_ROW_THREE_BUILD_WHITE_USED,  " PLAYER_ROW_THREE_BUILD_WHITE_USED ", 1);
		printPlayerValue(board, build, BoardInventory.PLAYER_ROW_THREE_BUILD_RED_USED,  " PLAYER_ROW_THREE_BUILD_RED_USED ", 1);
		printPlayerValue(board, build, BoardInventory.PLAYER_ROW_THREE_BUILD_BLUE_USED,  " PLAYER_ROW_THREE_BUILD_BLUE_USED ", 1);
		printPlayerValue(board, build, BoardInventory.PLAYER_ROW_THREE_BUILD_BLACK_USED,  " PLAYER_ROW_THREE_BUILD_BLACK_USED ", 1);
		printPlayerValue(board, build, BoardInventory.PLAYER_ACTION_TOKEN_LEFT,  " PLAYER_ACTION_TOKEN_LEFT ", 1);
		printPlayerValue(board, build, BoardInventory.PLAYER_TEMPLE_COUNT,  " PLAYER_TEMPLE_COUNT ", 1);
		printPlayerBattleCardValue(board, build, BoardInventory.PLAYER_TEMPLE_COUNT,  " PLAYER_TEMPLE_COUNT ", 1);

		return build.toString();
	}

	private static void printTileValue(float[] board, StringBuilder build, int startIndex, String description, int multiplier) {

			for (int j = 0; j < BoardInventory.TILE_COUNT; ++j) {
				int index = startIndex + j;
				printValue(board, build, index, description + " " + j, multiplier);
			}
	}

	private static void printPlayerValue(float[] board, StringBuilder build, int startIndex, String description,
			int multiplier) {

		for (int i = 0; i < BoardInventory.PLAYER_COUNT; ++i) {
				int index = startIndex + i;
				printValue(board, build, index, description + " " + i , multiplier);
		}

	}
	
	
	private static void printPlayerBattleCardValue(float[] board, StringBuilder build, int startIndex, String description,
			int multiplier) {

		for (int i = 0; i < BoardInventory.PLAYER_COUNT; ++i) {
			for (int j = 0; j < BattleCard.INDEXER; ++j) {
				int index = startIndex + i * j;
				printValue(board, build, index, description + " " + i + " " + j, multiplier);
			}
		}

	}

	
	private static void printTilePlayerValue(float[] board, StringBuilder build, int startIndex, String description,
			int multiplier) {

		for (int i = 0; i < BoardInventory.PLAYER_COUNT; ++i) {
			for (int j = 0; j < BoardInventory.TILE_COUNT; ++j) {
				int index = startIndex + i * j;
				printValue(board, build, index, description + " " + i + " " + j, multiplier);
			}
		}

	}

	private static void printValue(float[] board, StringBuilder build, int index, String description, int multiplier) {
		build.append("Index ");
		build.append(index);
		build.append(" value ");
		build.append(board[index]);
		build.append(description);
		build.append(board[index] * multiplier);
		build.append("\n");
	}

}
