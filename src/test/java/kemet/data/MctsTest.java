package kemet.data;

import org.junit.jupiter.api.Test;

import kemet.util.StackingMCTS;

class StackingMCTSTest {

	public boolean useRecurrentNetwork = true;

	@Test
	void test()  {
		
		System.out.println("Board Hit count s tweak");
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.7f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 5, 0.5f, 0.7f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 4, 0.5f, 0.7f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 3, 0.5f, 0.7f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 2, 0.5f, 0.7f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 1, 0.5f, 0.7f, 1));

		System.out.println("cpuct");

		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(2.0f, 6, 0.5f, 0.7f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.5f, 6, 0.5f, 0.7f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.7f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(0.5f, 6, 0.5f, 0.7f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(0.1f, 6, 0.5f, 0.7f, 1));
		
		
		System.out.println("v from policy");

		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.7f, 0.7f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.6f, 0.7f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.7f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.4f, 0.7f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.3f, 0.7f, 1));
		
		System.out.println("v previous hit");

		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.7f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.6f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.5f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.4f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.3f, 1));


		System.out.println("s,a hit count");

		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.7f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.7f, 2));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.7f, 3));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.7f, 4));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.7f, 5));

		
		System.out.println("s,a hit count, cpuct 2");

		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(2.0f, 6, 0.5f, 0.7f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(2.0f, 6, 0.5f, 0.7f, 2));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(2.0f, 6, 0.5f, 0.7f, 3));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(2.0f, 6, 0.5f, 0.7f, 4));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(2.0f, 6, 0.5f, 0.7f, 5));

		
		System.out.println("additional hit with much better value");

		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 6, 0.5f, 0.5f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(1.0f, 7, 0.5f, 0.7f, 2));

		System.out.println("additional hit with much better value, cpuct 2");

		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(2.0f, 6, 0.5f, 0.5f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(2.0f, 7, 0.5f, 0.7f, 2));

		System.out.println("additional hit with much better value, cpuct 0.5");

		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(0.0f, 6, 0.5f, 0.5f, 1));
		System.out.println(StackingMCTS.getAdjustedActionValueForSearch(0.5f, 7, 0.5f, 0.7f, 2));

	}

}
