package kemet.util;

import java.io.Serializable;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.NDArray;

public class PolicyVector implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4843132931066392140L;
	public float[] vector;

	public INDArray toINDArray() {
		return new NDArray(vector);
	}

	public void activateAllValidMoves(boolean[] validMoves) {
		for (int i = 0; i < validMoves.length; i++) {
			boolean valid = validMoves[i];
			if (valid) {
				vector[i] = 1;
			}
		}
	}

	public void maskInvalidMoves(boolean[] validMoves) {
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
		if (sum > 0) {
			for (int i = 0; i < vectorToNormalize.length; i++) {
				vectorToFill[i] = vectorToNormalize[i] / sum;
			}
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
			if (f > maxF) {
				maxF = f;
				max = i;
			}
		}
		return max;
	}

	public void printActionProbabilities(Game currentGame) {
		for (int i = 0; i < vector.length; i++) {
			float probability = vector[i];
			if( probability > 0 ) {
				print( String.format("%2.2f chance of action : %s", probability*100.0, currentGame.describeAction(i)));
			}
		}
	}

	private void print(String format) {
		System.out.println(format);
		
	}

}
