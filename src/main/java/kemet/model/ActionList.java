package kemet.model;

public class ActionList {

	public static int ACTION_INDEXER = 0;

	public static final int ACTION_MOVE = ACTION_INDEXER++;

	public static final int ACTION_RECRUIT = ACTION_INDEXER++;

	public static final int ACTION_PRAY = ACTION_INDEXER++;

	public static final int ACTION_UPGRADE_PYRAMID = ACTION_INDEXER++;

	public static final int ACTION_BUY_POWER = ACTION_INDEXER;
	static {
		ACTION_INDEXER += BoardInventory.COLOR_COUNT;
	}

	public static final int ACTION_REPEAT_BUY_POWER = ACTION_INDEXER;
	static {
		ACTION_INDEXER += BoardInventory.COLOR_COUNT;
	}

	public static final int TOTAL_ACTION_COUNT = ACTION_INDEXER;

}
