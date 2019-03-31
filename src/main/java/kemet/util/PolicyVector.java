package kemet.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

import org.nd4j.linalg.api.ndarray.INDArray;

import kemet.model.action.choice.ChoiceInventory;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PolicyVector implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4843132931066392140L;

	public static long ALL_VALID_MOVES_MASKED = 0;

	public float[] vector;

	public INDArray toINDArray() {
		return Utilities.createArray(vector);
	}

	/**
	 * if all valid moves were masked make all valid moves equally probable
	 * 
	 * All valid moves may be masked if either your NNet architecture is
	 * insufficient or you've get overfitting or something else. If you have got
	 * dozens or hundreds of these messages you should pay attention to your NNet
	 * and/or training process.
	 * 
	 * @param validMoves
	 * @return true if the valid moves were patched
	 */
	public boolean patchMissingActivatedMoves(boolean[] validMoves) {

		float sum = sum();
		if (sum <= 0) {
			log.info("All valid moves were masked, do workaround where all actions get equal probability.");

			ALL_VALID_MOVES_MASKED++;
			activateAllValidMoves(validMoves);
			return true;
		}
		return false;

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
			if (probability > 0) {
				log.info(String.format("%2.2f chance of action : %s", probability * 100.0,
						currentGame.describeAction(i)));
			}
		}
	}

	public void printProbabilityVector() {
		log.info("All Probabilities : " + Arrays.toString(vector));
	}

	public void boostActionIndex(int actionIndex) {
		vector[actionIndex] += 2;
		normalize();
	}

	public static Random random = new Random();

	public int pickRandomAction() {
		float nextFloat = random.nextFloat();
		int action = 0;
		for (int i = 0; i < vector.length; i++) {
			float f = vector[i];
			if (f > 0) {
				nextFloat -= f;
				if (nextFloat <= 0) {
					action = i;
					break;
				}
			}
		}
		return action;
	}

	public int pickBestAction() {
		int action = 0;
		float highestValue = -1;
		for (int i = 0; i < vector.length; i++) {
			float f = vector[i];
			if (f > highestValue) {
				highestValue = f;
				action = i;
			}
		}
		return action;
	}

	public void fromINDArray(INDArray policyOutput) {
		boolean foundNan = false;
		vector = new float[ChoiceInventory.TOTAL_CHOICE];
		for (int i = 0; i < ChoiceInventory.TOTAL_CHOICE; ++i) {
			float floatValue = policyOutput.getFloat(i);
			if (Float.isNaN(floatValue)) {
				foundNan = true;
			}
			vector[i] = floatValue;
		}

		if (foundNan) {
			throw new IllegalArgumentException("Policy value returned a NaN value " + Arrays.toString(vector));
		}
	}

}
