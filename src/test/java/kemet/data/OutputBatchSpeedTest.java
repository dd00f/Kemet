package kemet.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.TestMultiDataSetIterator;

import kemet.Options;
import kemet.ai.KemetNeuralNetBuilder;
import kemet.ai.KemetNeuralNetwork;
import kemet.ai.KemetRecurrentNeuralNetBuilder;
import kemet.util.ByteCanonicalForm;
import kemet.util.PolicyVector;
import kemet.util.Utilities;

/**
 * Used to test if calling the output function is a batch is faster than calling
 * it one by one
 * 
 * @author Steve
 *
 */
class OutputBatchSpeedTest {

	private ComputationGraph build;

	public boolean useRecurrentNetwork = true;

	@Test
	void test() throws InterruptedException {

		nn = new KemetNeuralNetwork();

		if (useRecurrentNetwork) {
			build = KemetRecurrentNeuralNetBuilder.build();
			nn.model = build;
			nn.addListeners();
		}

		build = nn.model;

		TwoPlayerGame gameFactory = new TwoPlayerGame();
		Options.COACH_USE_MANUAL_AI = true;
		KemetNeuralNetBuilder.NEURAL_NET_TRAIN_WITH_MASK = false;

		Options.PRINT_COACH_SEARCH_ACTIONS = true;
		Options.PRINT_COACH_SEARCH_PROBABILITIES = true;

		ByteCanonicalForm canonicalForm = gameFactory.createGame().getCanonicalForm(0);

		List<org.nd4j.linalg.dataset.api.MultiDataSet> mdsList = new ArrayList<>();
		INDArray emptyArray = Utilities.createArray(new float[1]);

		int sampleCount = 100;

		List<ByteCanonicalForm> bcfarray = new ArrayList<>();

		for (int i = 0; i < sampleCount; ++i) {
			INDArray inputArray = canonicalForm.getINDArray();
			MultiDataSet mdsToAdd = new MultiDataSet(inputArray, emptyArray);
			mdsList.add(mdsToAdd);

			bcfarray.add(canonicalForm);
		}

		Pair<PolicyVector, Float>[] predict = nn.predict(bcfarray);
		predict[sampleCount-1].getValue().toString();
		predict[sampleCount-1].getKey().printProbabilityVector();

		for (int i = 0; i < 20; ++i) {
			long startNano = System.nanoTime();
			for (org.nd4j.linalg.dataset.api.MultiDataSet multiDataSet : mdsList) {
				build.output(multiDataSet.getFeatures());
			}
			long duration = System.nanoTime() - startNano;
			long durationPerCall = duration / sampleCount;

			System.out.println("One by one speed per call nano : " + durationPerCall);

			TestMultiDataSetIterator iterator = new TestMultiDataSetIterator(5000,
					mdsList.toArray(new MultiDataSet[mdsList.size()]));

			startNano = System.nanoTime();
			INDArray[] output = build.output(iterator);

//			System.out.println("Output array count " + output.length);
//			System.out.println(output[0].getRow(0));
//			System.out.println(output[0].getRow(1));
//			System.out.println(output[1].getRow(0));
//			System.out.println(output[1].getRow(1));

			duration = System.nanoTime() - startNano;
			long durationPerCallBatched = duration / sampleCount;
			
			float ratio = ((float)durationPerCall) / ((float)durationPerCallBatched);

			System.out.println("Batch speed per call nano : " + durationPerCallBatched + ", this is " + ratio + " times faster");
		}

		// WOOHOO Batching is 20 times faster in batches of 100
		// WOOHOO Batching is more than 40 times faster in batches of 1000 !

	}

	private KemetNeuralNetwork nn;

}
