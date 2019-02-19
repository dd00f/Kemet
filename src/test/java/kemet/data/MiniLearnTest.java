package kemet.data;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.TestMultiDataSetIterator;

import kemet.Options;
import kemet.ai.KemetNeuralNetwork;
import kemet.ai.KemetNeuralNetBuilder;
import kemet.model.KemetGame;
import kemet.model.action.PlayerChoicePick;
import kemet.model.action.choice.ChoiceInventory;
import kemet.util.ByteCanonicalForm;
import kemet.util.Coach;
import kemet.util.Game;
import kemet.util.GameFactory;
import kemet.util.MCTS;
import kemet.util.NeuralNet;
import kemet.util.PolicyVector;
import kemet.util.TrainExample;
import kemet.util.Utilities;

class MiniLearnTest {

	private ComputationGraph build;
	private INDArray sTATE;
	private INDArray eXPECTED_POLICY;
	private boolean UI = false;
	private float[] stateFloats;
	private float[] policyFloats;
	private float lastValue;
	private List<MultiDataSet> setList;

	public float lowestError = 100000;
	public int bestLayerSize = 0;
	public int bestLayerCount = 0;
	public boolean bestResidual = false;

	@Test
	void test() throws InterruptedException {

		if (UI) {
			startUI();
		}

		stateFloats = createStateFloat();
		sTATE = Utilities.createArray(stateFloats);
		INDArray[] inputs = new INDArray[] { sTATE };
		policyFloats = createPolicyFloats();
		eXPECTED_POLICY = Utilities.createArray(policyFloats);
		INDArray[] outputs = new INDArray[] { eXPECTED_POLICY };

		prepareTestDataSet();

		// for the size of each layer in increments of 10
		// for the number of residual layer
		// for the activation of residual layer or not
		// run 500 training session
		// calculate average error for 500 to 1000 in increments of 100
		// pick the best net

		for (int layerSize = KemetNeuralNetBuilder.INPUT_SIZE * 1; layerSize < KemetNeuralNetBuilder.INPUT_SIZE
				* 7; layerSize += KemetNeuralNetBuilder.INPUT_SIZE) {
			for (int residualLayerCount = 0; residualLayerCount < 10; residualLayerCount += 2) {
				for (int isResidualLayerActivated = 0; isResidualLayerActivated < 2; ++isResidualLayerActivated) {
					runTestAndPickBest(layerSize, residualLayerCount, isResidualLayerActivated == 1);
				}
			}
		}

		System.out.println("Best Network : Layer Size " + bestLayerCount + " block count " + bestLayerCount
				+ " residual " + bestResidual + " error " + lowestError);

		// createNeuralNetworkFitAndPredict();

		// build.getLayers()[1].
		if (UI) {
			while (true) {
				Thread.sleep(1000);
			}
		}

	}

	private float[] createPolicyFloats() {
		float[] policy = new float[KemetNeuralNetBuilder.OUTPUT_SIZE];

		policy[0] = 0.2f;
		policy[1] = 0.7f;
		policy[2] = 0.1f;

		return policy;

		// return new float[] { 0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.0f,
		// 0.0f };
	}

	private float[] createStateFloat() {

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
		return state;
		// return new float[] { 0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f,
		// 0.9f };
	}

	public void createNeuralNetworkFitAndPredict() {
		// build = MiniKemetBuilder.build();
		build = KemetNeuralNetBuilder.build();

		printPredict(-1);

		for (int i = 0; i < 1000; ++i) {
			Collections.shuffle(setList);

			TestMultiDataSetIterator iterator = new TestMultiDataSetIterator(1,
					setList.toArray(new MultiDataSet[setList.size()]));
			build.fit(iterator);

			// build.fit(inputs, outputs);
			if (i % 100 == 0) {
				printPredict(i);
			}

		}
	}

	private void runTestAndPickBest(int layerSize, int residualLayerCount, boolean residualActivated) {

		long start = System.currentTimeMillis();
		Options.NEURAL_NET_RESIDUAL_ACTIVATED = residualActivated;

		Options.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT = residualLayerCount;
		if (residualActivated) {
			Options.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT = residualLayerCount / 2;
		}

		KemetNeuralNetBuilder.LAYER_SIZE = layerSize;

		// build = MiniKemetBuilder.build();
		build = KemetNeuralNetBuilder.build();

		// printPredict(-1);

		for (int i = 0; i < 500; ++i) {
			Collections.shuffle(setList);

			TestMultiDataSetIterator iterator = new TestMultiDataSetIterator(1,
					setList.toArray(new MultiDataSet[setList.size()]));
			build.fit(iterator);

			// build.fit(inputs, outputs);
//			if (i % 100 == 0) {
//				printPredict(i);
//			}

		}

		float errors = 0;
		int count = 0;

		for (int i = 0; i < 500; ++i) {
			Collections.shuffle(setList);

			TestMultiDataSetIterator iterator = new TestMultiDataSetIterator(1,
					setList.toArray(new MultiDataSet[setList.size()]));
			build.fit(iterator);

			// build.fit(inputs, outputs);
			if (i % 100 == 0) {
				INDArray[] output = build.output(sTATE);
				errors += calculateError(output);
				count++;
			}
		}

		errors = errors / count;

		long duration = System.currentTimeMillis() - start;

		float trainExamples = 1000 * 10;
		float durationFloat = duration;
		float durationPerExample = durationFloat / trainExamples;

		if (lowestError > errors) {

			lowestError = errors;
			bestLayerCount = Options.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT;
			bestLayerSize = KemetNeuralNetBuilder.LAYER_SIZE;
			bestResidual = Options.NEURAL_NET_RESIDUAL_ACTIVATED;

			System.out.println("New Best Network found : Layer Size " + KemetNeuralNetBuilder.LAYER_SIZE
					+ " block count " + Options.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT + " residual "
					+ Options.NEURAL_NET_RESIDUAL_ACTIVATED + " error " + errors + " time ms: " + duration
					+ " train examples : " + trainExamples + " time per example : " + durationPerExample);

		} else {

			System.out.println("Bad network : Layer Size " + KemetNeuralNetBuilder.LAYER_SIZE + " block count "
					+ Options.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT + " residual " + Options.NEURAL_NET_RESIDUAL_ACTIVATED
					+ " error " + errors + " best error was " + lowestError + " time ms: " + duration
					+ " train examples : " + trainExamples + " time per example : " + durationPerExample);

		}

	}

	private void prepareTestDataSet() {
		float[] trainStateFloat = stateFloats;
		float[] trainPolicyFloat = policyFloats;

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

			lastValue = value;
			value -= 0.2f;

			MultiDataSet mds = new org.nd4j.linalg.dataset.MultiDataSet(trainInput, trainLabels);

			setList.add(mds);
		}
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
		INDArray[] output = build.output(sTATE);
		String string1 = Arrays.toString(output[0].toFloatVector());
		String string2 = Arrays.toString(output[1].toFloatVector());
		System.out.println(i + " Predict : " + string1 + " value " + string2);

		calculateError(output);

//		for (int j = 0; j < output.length; j++) {
//			INDArray indArray = output[j];
//			System.out.println(i + " Predict");
//			System.out.println(i + Arrays.toString(indArray.toFloatVector()));
//			
//		}

	}

	private float calculateError(INDArray[] output) {
		float[] policy = output[0].toFloatVector();
		float[] value = output[1].toFloatVector();

		float error = 0;

		for (int i = 0; i < KemetNeuralNetBuilder.OUTPUT_SIZE; ++i) {
			error += Math.abs(policyFloats[i] - policy[i]);
		}

		error += Math.abs(lastValue - value[0]);
		return error;
	}

	public float[] predict(INDArray values) {
		INDArray[] output = build.output(values);
		INDArray indArray = output[0];
		return indArray.toFloatVector();
	}

}
