package kemet.util;

import java.io.Serializable;

public class PolicyVector implements Serializable {
	
	public float[] vector;
	
	public void activateAllValidMoves( boolean[] validMoves) {
		for (int i = 0; i < validMoves.length; i++) {
			boolean valid = validMoves[i];
			if (valid) {
				vector[i] = 1;
			}
		}
	}

	public void maskInvalidMoves( boolean[] validMoves) {
		for (int i = 0; i < validMoves.length; i++) {
			boolean valid = validMoves[i];
			if (!valid) {
				vector[i] = 0;
			}
		}
	}
	
	public float sum() {
		return sum(vector);
	}

	public void normalize() {
		normalize(vector, vector);
	}
	
	public static void normalize(float[] vectorToNormalize, float[] vectorToFill) {
		float sum = sum(vectorToNormalize);
		for (int i = 0; i < vectorToNormalize.length; i++) {
			vectorToFill[i] = vectorToNormalize[i] / sum;
		}
	}
	
	public static float sum(float[] array) {
		float retVal = 0;
		for (float f : array) {
			retVal += f;
		}
		return retVal;
	}

	public int getMaximumMove() {
		int max = 0;
		float maxF = 0;
		for (int i = 0; i < vector.length; i++) {
			float f = vector[i];
			if( f > maxF ) {
				maxF = f;
				max = i;
			}
		}
		return max;
	}

	
}
