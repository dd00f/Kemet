package kemet.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.TestMultiDataSetIterator;

import kemet.ai.KemetNeuralNetBuilder;
import kemet.util.Utilities;

class MiniLearnTest {

	private ComputationGraph build;
	private INDArray sTATE;

	private INDArray lastState;
	private float[] lastPolicy;

	private boolean UI = false;
	private float[] stateFloats;
	private float[] policyFloats;
	private float[] policyMaskFloats;
	private float lastValue;
	private List<MultiDataSet> setList;

	public float lowestError = 100000;
	public int bestLayerSize = 0;
	public int bestLayerCount = 0;
	public boolean bestResidual = false;
	private float maximumError = 100000;

	public boolean trainWithOutputMask = true;

	@Test
	void test() throws InterruptedException {

		if (UI) {
			startUI();
		}

		// for the size of each layer in increments of 10
		// for the number of residual layer
		// for the activation of residual layer or not
		// run 500 training session
		// calculate average error for 500 to 1000 in increments of 100
		// pick the best net

		// KemetNeuralNetBuilder.OUTPUT_SIZE = 1100;
		trainIterationCount = 5;
		numberOfPredictionToCalculateError = trainIterationCount / 5;
		trainWithOutputMask = true;

		prepareTestDataSet();

//		KemetNeuralNetBuilder.WEIGHT_INIT = WeightInit.LECUN_NORMAL;
//		runTestAndPickBest(KemetNeuralNetBuilder.INPUT_SIZE * 4, 1, false);
//		runTestAndPickBest(KemetNeuralNetBuilder.INPUT_SIZE * 4, 1, false);
//
//		trainWithOutputMask = false;
//		
//		runTestAndPickBest(KemetNeuralNetBuilder.INPUT_SIZE * 4, 1, false);
//		runTestAndPickBest(KemetNeuralNetBuilder.INPUT_SIZE * 4, 1, false);
//		
//		if( ! trainWithOutputMask ) {
//			return;
//		}

		for (int layerSize = KemetNeuralNetBuilder.INPUT_SIZE; layerSize <= KemetNeuralNetBuilder.INPUT_SIZE
				* 4; layerSize += KemetNeuralNetBuilder.INPUT_SIZE) {
			for (int residualLayerCount = 2; residualLayerCount <= 6; residualLayerCount += 2) {
				for (int isResidualLayerActivated = 0; isResidualLayerActivated < 1; ++isResidualLayerActivated) {
					runTestAndPickBest(layerSize, residualLayerCount, isResidualLayerActivated == 1);
				}
			}
		}

		System.out.println("Best Network : Layer Size " + bestLayerCount + " block count " + bestLayerCount
				+ " residual " + bestResidual + " error " + lowestError);

//		KemetNeuralNetBuilder.OUTPUT_SIZE = ChoiceInventory.TOTAL_CHOICE;
//		
//		trainIterationCount = 1000;
//		
//		runTestAndPickBest(KemetNeuralNetBuilder.INPUT_SIZE * 4, 4, false);
//		
//		KemetNeuralNetBuilder.OUTPUT_SIZE = 1100;
//		
//		trainIterationCount = 1000/3;
//		
//		prepareTestDataSet();
//
//		
//		runTestAndPickBest(KemetNeuralNetBuilder.OUTPUT_SIZE * 1, 4, false);
//
//		
//		createNeuralNetworkFitAndPredict();

		// build.getLayers()[1].
		if (UI) {
			while (true) {
				Thread.sleep(1000);
			}
		}

	}

	private void createPolicyFloats() {
		float[] policy = new float[KemetNeuralNetBuilder.OUTPUT_SIZE];

		policy[0] = 0.2f;
		policy[1] = 0.7f;
		policy[2] = 0.1f;

		policyFloats = policy;

		float[] policyMask = new float[KemetNeuralNetBuilder.OUTPUT_SIZE];

		policyMask[0] = 1f;
		policyMask[1] = 1f;
		policyMask[2] = 1f;

		policyMaskFloats = policyMask;

		// return new float[] { 0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.0f,
		// 0.0f };
	}

	private void createStateFloat() {

		float[] state = new float[KemetNeuralNetBuilder.INPUT_SIZE];

		float increment = 1.0f / state.length;

		for (int i = 0; i < state.length / 2; i++) {

			// fill some that range from 0 to 1
			state[i] = increment * i;

		}

		for (int i = state.length / 2; i < state.length; i++) {

			// fill some states that are either 1, 0 or -1
			state[i] = i % 3 - 1;

		}
		stateFloats = state;
		// return new float[] { 0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f,
		// 0.9f };
	}

	public void createNeuralNetworkFitAndPredict() {
		// build = MiniKemetBuilder.build();
		build = KemetNeuralNetBuilder.build();

		System.out.println(-2 + " Predict : " + Arrays.toString(lastState.toFloatVector()) + " value " + lastValue);

		printPredict(-1);

		for (int i = 0; i < 1000; ++i) {
			Collections.shuffle(setList);

			TestMultiDataSetIterator iterator = new TestMultiDataSetIterator(5000,
					setList.toArray(new MultiDataSet[setList.size()]));
			build.fit(iterator);

			// build.fit(inputs, outputs);
			if (i % 10 == 0) {
				printPredict(i);
			}

		}
	}

	public int trainIterationCount = 1000;
	public int numberOfPredictionToCalculateError = 5;
	private float errorAverage;
	private float errorSum;
	private float error;
	private float[] lastMask;
	private int warmupTrainIterationCount = 0;

	private void runTestAndPickBest(int layerSize, int residualLayerCount, boolean residualActivated) {

		long start = System.currentTimeMillis();
		KemetNeuralNetBuilder.NEURAL_NET_RESIDUAL_ACTIVATED = residualActivated;

		KemetNeuralNetBuilder.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT = residualLayerCount;
		if (residualActivated) {
			KemetNeuralNetBuilder.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT = residualLayerCount / 2;
		}

		KemetNeuralNetBuilder.LAYER_SIZE = layerSize;

		// build = MiniKemetBuilder.build();
		build = KemetNeuralNetBuilder.build();

		// printPredict(-1);

		for (int i = 0; i < warmupTrainIterationCount; ++i) {
			Collections.shuffle(setList);

			TestMultiDataSetIterator iterator = new TestMultiDataSetIterator(5000,
					setList.toArray(new MultiDataSet[setList.size()]));
			build.fit(iterator);

			// build.fit(inputs, outputs);
			if (i % 100 == 0) {
				printPredict(i);
			}

		}

		float totalMaxError = 0;
		float totalAvgError = 0;
		float bestError = 10000;
		float bestAvgError = 10000;
		int count = 0;
		int predictInterval = numberOfPredictionToCalculateError;

		for (int i = 0; i < trainIterationCount; ++i) {
			Collections.shuffle(setList);

			TestMultiDataSetIterator iterator = new TestMultiDataSetIterator(5000,
					setList.toArray(new MultiDataSet[setList.size()]));
			build.fit(iterator);

			// build.fit(inputs, outputs);
			if (i % predictInterval == 0) {
				INDArray[] output = build.output(sTATE);
				calculateError(output, policyMaskFloats);
				totalMaxError += error;
				totalAvgError += errorAverage;

				bestError = Math.min(error, bestError);
				bestAvgError = Math.min(errorAverage, bestAvgError);
				count++;

				printPredict(i + warmupTrainIterationCount);

			}
		}

		totalMaxError = totalMaxError / count;
		totalAvgError = totalAvgError / count;

		long duration = System.currentTimeMillis() - start;

		float trainExamples = trainIterationCount * setList.size();
		float durationFloat = duration;
		float durationPerExample = durationFloat / trainExamples;

		if (lowestError > bestAvgError) {

			lowestError = bestAvgError;
			bestLayerCount = KemetNeuralNetBuilder.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT;
			bestLayerSize = KemetNeuralNetBuilder.LAYER_SIZE;
			bestResidual = KemetNeuralNetBuilder.NEURAL_NET_RESIDUAL_ACTIVATED;

			System.out.println("New Best Network found:");
		} else {

			System.out.println("Bad network:");

		}

		System.out.println("Network Parameters " + KemetNeuralNetBuilder.LAYER_SIZE + " block count "
				+ KemetNeuralNetBuilder.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT + " residual "
				+ KemetNeuralNetBuilder.NEURAL_NET_RESIDUAL_ACTIVATED + " best avg error was " + lowestError
				+ " time ms: " + duration + " train examples : " + trainExamples + " time per example : "
				+ durationPerExample);

		System.out.println("bestError " + bestError + " bestAvgError " + bestAvgError + " avgMaxError " + totalMaxError
				+ " avgAvgError " + totalAvgError);

	}

	private void prepareTestDataSet() {

		createStateFloat();
		sTATE = Utilities.createArray(stateFloats);
		createPolicyFloats();

		float[] trainStateFloat = stateFloats;
		float[] trainPolicyFloat = policyFloats;
		float[] trainPolicyMaskFloat = policyMaskFloats;

		setList = new ArrayList<>();

		float value = 1f;

		for (int i = 0; i < 10; ++i) {

			trainStateFloat = shiftArrayRightByOne(trainStateFloat);
			INDArray trainState = Utilities.createArray(trainStateFloat);
			INDArray[] trainInput = new INDArray[] { trainState };

			trainPolicyFloat = shiftArrayRightByOne(trainPolicyFloat);
			INDArray trainPolicy = Utilities.createArray(trainPolicyFloat);
			INDArray[] trainLabels = new INDArray[] { trainPolicy, Utilities.createArray(new float[] { value }) };

			System.out.println("Train State " + i + " " + Arrays.toString(trainStateFloat) + " Train policy "
					+ Arrays.toString(trainPolicyFloat) + " Train value " + value);

			trainPolicyMaskFloat = shiftArrayRightByOne(trainPolicyMaskFloat);
			lastValue = value;
			lastState = trainState;
			lastPolicy = trainPolicyFloat;
			lastMask = trainPolicyMaskFloat;

			value -= 0.2f;

			INDArray[] trainLabelsMask = null;
			if (trainWithOutputMask) {
				INDArray trainPolicyMask = Utilities.createArray(trainPolicyFloat);
				trainLabelsMask = new INDArray[] { trainPolicyMask, Utilities.createArray(new float[] { 1 }) };
			}

			MultiDataSet mds = new org.nd4j.linalg.dataset.MultiDataSet(trainInput, trainLabels, null, trainLabelsMask);

			setList.add(mds);
		}

		setList.addAll(setList);// 20
		setList.addAll(setList);// 40
		setList.addAll(setList);// 80
		setList.addAll(setList);// 160
		setList.addAll(setList);// 320
		setList.addAll(setList);// 640
		setList.addAll(setList);// 1280
		setList.addAll(setList);// 2560

	}

	private void startUI() {

		// Initialize the user interface backend
		UIServer uiServer = UIServer.getInstance();

		// Configure where the network information (gradients, score vs. time etc) is to
		// be stored. Here: store in memory.
		StatsStorage statsStorage = new InMemoryStatsStorage(); // Alternative: new FileStatsStorage(File), for saving
																// and loading later

		// Attach the StatsStorage instance to the UI: this allows the contents of the
		// StatsStorage to be visualized
		uiServer.attach(statsStorage);

		// Then add the StatsListener to collect this information from the network, as
		// it trains
		build.setListeners(new StatsListener(statsStorage));
	}

	public float[] shiftArrayRightByOne(final float[] source) {

		int length = source.length;
		float[] array = new float[length];
		array[0] = source[length - 1];

		for (int i = 0; i < length - 1; ++i) {
			array[i + 1] = source[i];
		}

		return array;
	}

	private void printPredict(int i) {
		INDArray[] output = build.output(lastState);
		float[] floatVector = output[0].toFloatVector();
		String string2 = Arrays.toString(output[1].toFloatVector());
		calculateError(output, lastMask);

		System.out.println(i + " Predict : " + floatVector[10] + " " + floatVector[11] + " " + floatVector[12]
				+ " value " + string2 + " error max " + error + " error avg " + errorAverage);

//		String string1 = Arrays.toString(floatVector);
//		System.out.println(i + " Predict : " + string1
//				+ " value " + string2 + " error max " + error + " error avg " + errorAverage);

		if (error < maximumError) {
			maximumError = error;
			System.out.println(i + "Best Error at " + error);

		}

//		for (int j = 0; j < output.length; j++) {
//			INDArray indArray = output[j];
//			System.out.println(i + " Predict");
//			System.out.println(i + Arrays.toString(indArray.toFloatVector()));
//			
//		}

	}

	private void calculateError(INDArray[] output, float[] policyMaskFloats2) {
		float[] policy = output[0].toFloatVector();
		float[] value = output[1].toFloatVector();

		error = 0;
		errorSum = 0;
		errorAverage = 0;
		int measuredCount = 1;

		for (int i = 0; i < KemetNeuralNetBuilder.OUTPUT_SIZE; ++i) {
			float currentError = Math.abs(lastPolicy[i] - policy[i]);

			if (!trainWithOutputMask || policyMaskFloats2[i] > 0.1) {

				error = Math.max(currentError, error);
				errorSum += currentError;
				measuredCount++;
			}
		}

		float valueError = Math.abs(value[0] - this.lastValue);
		error = Math.max(valueError, error);
		errorSum += valueError;
		errorAverage = errorSum / ((float) measuredCount);

		// error /= KemetNeuralNetBuilder.OUTPUT_SIZE + 1;
		// return error;
	}

	public float[] predict(INDArray values) {
		INDArray[] output = build.output(values);
		INDArray indArray = output[0];
		return indArray.toFloatVector();
	}

}
