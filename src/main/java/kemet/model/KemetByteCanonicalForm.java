package kemet.model;

import kemet.Options;
import kemet.util.ByteCanonicalForm;


public class KemetByteCanonicalForm extends ByteCanonicalForm{

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
		
		floatCanonicalForm[BoardInventory.ROUND_NUMBER] = floatCanonicalForm[BoardInventory.ROUND_NUMBER] / Options.GAME_TURN_LIMIT;

		floatCanonicalForm[BoardInventory.PICKED_SIZE] = floatCanonicalForm[BoardInventory.PICKED_SIZE] / MAX_ARMY_SIZE_PICK;

		floatCanonicalForm[BoardInventory.PICKED_LEVEL] = floatCanonicalForm[BoardInventory.PICKED_LEVEL] / MAX_LEVEL;

		floatCanonicalForm[BoardInventory.MOVES_LEFT] = floatCanonicalForm[BoardInventory.MOVES_LEFT] / 5;
		
		floatCanonicalForm[BoardInventory.BATTLE_ATTACKER_STRENGTH] = floatCanonicalForm[BoardInventory.BATTLE_ATTACKER_STRENGTH] / MAX_BATTLE_TOTAL_STRENGTH;
		floatCanonicalForm[BoardInventory.BATTLE_ATTACKER_SHIELD] = floatCanonicalForm[BoardInventory.BATTLE_ATTACKER_SHIELD] / MAX_BATTLE_SHIELD;
		floatCanonicalForm[BoardInventory.BATTLE_ATTACKER_DAMAGE] = floatCanonicalForm[BoardInventory.BATTLE_ATTACKER_DAMAGE] / MAX_BATTLE_DAMAGE;
		floatCanonicalForm[BoardInventory.BATTLE_DEFENDER_STRENGTH] = floatCanonicalForm[BoardInventory.BATTLE_DEFENDER_STRENGTH] / MAX_BATTLE_TOTAL_STRENGTH;
		floatCanonicalForm[BoardInventory.BATTLE_DEFENDER_SHIELD] = floatCanonicalForm[BoardInventory.BATTLE_DEFENDER_SHIELD] / MAX_BATTLE_SHIELD;
		floatCanonicalForm[BoardInventory.BATTLE_DEFENDER_DAMAGE] = floatCanonicalForm[BoardInventory.BATTLE_DEFENDER_DAMAGE] / MAX_BATTLE_DAMAGE;
		
		
		floatCanonicalForm[BoardInventory.BATTLE_DEFENDER_SHIELD] = floatCanonicalForm[BoardInventory.BATTLE_DEFENDER_SHIELD] / 5;
		
		adjustRange( floatCanonicalForm, BoardInventory.TILE_PLAYER_ARMY_SIZE, BoardInventory.PLAYER_COUNT * BoardInventory.TILE_COUNT, MAX_ARMY_SIZE_PICK );

		adjustRange( floatCanonicalForm, BoardInventory.TILE_BLACK_PYRAMID_LEVEL, BoardInventory.TILE_COUNT, MAX_LEVEL );
		adjustRange( floatCanonicalForm, BoardInventory.TILE_RED_PYRAMID_LEVEL, BoardInventory.TILE_COUNT, MAX_LEVEL );
		adjustRange( floatCanonicalForm, BoardInventory.TILE_BLUE_PYRAMID_LEVEL, BoardInventory.TILE_COUNT, MAX_LEVEL );
		adjustRange( floatCanonicalForm, BoardInventory.TILE_WHITE_PYRAMID_LEVEL, BoardInventory.TILE_COUNT, MAX_LEVEL );

		adjustRange( floatCanonicalForm, BoardInventory.PLAYER_VICTORY_POINTS, BoardInventory.PLAYER_COUNT, KemetGame.VICTORY_POINTS_OBJECTIVE );
		adjustRange( floatCanonicalForm, BoardInventory.PLAYER_BATTLE_POINTS, BoardInventory.PLAYER_COUNT, KemetGame.VICTORY_POINTS_OBJECTIVE );
		adjustRange( floatCanonicalForm, BoardInventory.PLAYER_PRAYER_POINTS, BoardInventory.PLAYER_COUNT, Player.MAXIMUM_PRAYER_POINTS );
		adjustRange( floatCanonicalForm, BoardInventory.PLAYER_AVAILABLE_ARMY_TOKENS, BoardInventory.PLAYER_COUNT, MAXIMUM_ARMY_TOKENS );
		adjustRange( floatCanonicalForm, BoardInventory.PLAYER_ACTION_TOKEN_LEFT, BoardInventory.PLAYER_COUNT, Player.ACTION_TOKEN_COUNT );

		adjustRange( floatCanonicalForm, BoardInventory.PLAYER_TEMPLE_COUNT, BoardInventory.PLAYER_COUNT, MAXIMUM_TEMPLE_COUNT );

		return floatCanonicalForm;
	}


	private void adjustRange(float[] floatCanonicalForm, int startIndex, int stopIndex, float rectificationFactor) {
		for( int i = startIndex; i< stopIndex; ++i) {
			floatCanonicalForm[i] /= rectificationFactor;
		}
		
	}
}
