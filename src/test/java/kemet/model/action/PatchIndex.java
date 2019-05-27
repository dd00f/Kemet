package kemet.model.action;

import java.util.Arrays;

public class PatchIndex {

	public static void main(String[] args) {
		
		int[] arrayToPatch  = new int[] { 27, 23, 22, 27, 25, 24, 5, 5, 5, 5, 42, 14, 29, 42, 14, 29, 44, 44, 43,
				43, 39, 14, 11, 5, 0, 39, 14, 13, 5, 41, 11, 0, 41, 0, 37, 36, 37, 36, 60, 43, 43, 44, 44, 42, 15, 29,
				42, 15, 29, 39, 0, 39, 0, 40, 0, 40, 0, 34, 35, 34, 35, 60 };
		
		for (int i = 0; i < arrayToPatch.length; i++) {
			int j = arrayToPatch[i];
			if( j >= 38 ) {
				arrayToPatch[i] = j+2;
			}
		}
		
		System.out.println(Arrays.toString(arrayToPatch));
		
	}
	
}
