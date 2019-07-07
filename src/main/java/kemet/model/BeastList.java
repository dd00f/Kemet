package kemet.model;

public class BeastList {

	public static int BEAST_INDEXER = 0;

	
	public static final String RED_3_ROYAL_SCARAB_NAME = "Royal Scarab";
	public static final Beast RED_3_ROYAL_SCARAB = new Beast(RED_3_ROYAL_SCARAB_NAME, 2, 0, 2, 0, null, BEAST_INDEXER++);

	public static final String RED_4_GIANT_SCORPION_NAME = "Giant Scorpion";
	public static final Beast RED_4_GIANT_SCORPION = new Beast(RED_4_GIANT_SCORPION_NAME, 2, 0, 1, 2, null, BEAST_INDEXER++);

	public static final String RED_4_PHOENIX_POWER = "Ignore Walls";
	public static final String RED_4_PHOENIX_NAME = "Phoenix";
	public static final Beast RED_4_PHOENIX = new Beast(RED_4_PHOENIX_NAME, 2, 0, 1, 0, RED_4_PHOENIX_POWER, BEAST_INDEXER++);

	public static final String BLUE_2_ANCESTRAL_ELEPHANT_NAME = "Ancestral Elephant";
	public static final Beast BLUE_2_ANCESTRAL_ELEPHANT = new Beast(BLUE_2_ANCESTRAL_ELEPHANT_NAME, 1, 1, 1, 0, null, BEAST_INDEXER++);

	public static final String BLUE_2_DEEP_DESERT_SNAKE_POWER = "Ignore Enemy Beast";
	public static final String BLUE_2_DEEP_DESERT_SNAKE_NAME = "Deep Desert Snake";
	public static final Beast BLUE_2_DEEP_DESERT_SNAKE = new Beast(BLUE_2_DEEP_DESERT_SNAKE_NAME, 0, 0, 1, 0,
			BLUE_2_DEEP_DESERT_SNAKE_POWER, BEAST_INDEXER++);

	public static final String BLUE_4_SPHINX_POWER = "+1 VP";
	public static final String BLUE_4_SPHINX_NAME = "Sphinx";
	public static final Beast BLUE_4_SPHINX = new Beast(BLUE_4_SPHINX_NAME, 2, 0, 0, 0,
			BLUE_4_SPHINX_POWER, BEAST_INDEXER++);
	
	public static final String BLACK_2_KHNUM_SPHINX_POWER = "Opponents pay 2 power to move on Khnum's Sphinx";
	public static final String BLACK_2_KHNUM_SPHINX_NAME = "Khnum's Sphinx";
	public static final Beast BLACK_2_KHNUM_SPHINX = new Beast(BLACK_2_KHNUM_SPHINX_NAME, 1, 0, 1, 0,
			BLACK_2_KHNUM_SPHINX_POWER, BEAST_INDEXER++);
	
	public static final String BLACK_3_GRIFFIN_SPHINX_POWER = "Teleport from Obelisk";
	public static final String BLACK_3_GRIFFIN_SPHINX_NAME = "Griffin Sphinx";
	public static final Beast BLACK_3_GRIFFIN_SPHINX = new Beast(BLACK_3_GRIFFIN_SPHINX_NAME, 2, 0, 0, 0,
			BLACK_3_GRIFFIN_SPHINX_POWER, BEAST_INDEXER++);
	
	public static final String BLACK_4_DEVOURER_POWER = "+1 VP on battle victory & damage 2 enemy units. Immune to damage outside of battle.";
	public static final String BLACK_4_DEVOURER_NAME = "Devourer";
	public static final Beast BLACK_4_DEVOURER = new Beast(BLACK_4_DEVOURER_NAME, 2, 0, 1, 0,
			BLACK_4_DEVOURER_POWER, BEAST_INDEXER++);
	
	public static final String WHITE_4_MUMMY_POWER = "+1 DI card at night";
	public static final String WHITE_4_MUMMY_NAME = "Mummy";
	public static final Beast WHITE_4_MUMMY = new Beast(WHITE_4_MUMMY_NAME, 2, 0, 1, 0,
			WHITE_4_MUMMY_POWER, BEAST_INDEXER++);
	
}
