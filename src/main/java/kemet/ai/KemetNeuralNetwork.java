
package kemet.ai;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.optimize.listeners.CollectScoresListener;
import org.deeplearning4j.optimize.listeners.PerformanceListener;
import org.deeplearning4j.optimize.listeners.TimeIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.TestMultiDataSetIterator;

import kemet.Options;
import kemet.model.action.choice.ChoiceInventory;
import kemet.util.ByteCanonicalForm;
import kemet.util.NeuralNet;
import kemet.util.PolicyVector;
import kemet.util.TrainExample;
import kemet.util.Utilities;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class KemetNeuralNetwork implements NeuralNet {

	public ComputationGraph model;

	public static int epochs = Options.NEURAL_NET_TRAIN_EPOCH;
	public static int batchSize = 10;

	public KemetNeuralNetwork() {
		 model = KemetNeuralNetBuilder.build();
		addListeners();
	}
	
	public KemetNeuralNetwork(ComputationGraph model) {
		this.model = model;
		addListeners();
	}

	private void addListeners() {
		model.addListeners(new PerformanceListener(50, true));
		model.addListeners(new CollectScoresListener(50, true));
		// model.addListeners(new TimeIterationListener(50));
	}

	@Override
	public void train(List<TrainExample> examples) {

		List<MultiDataSet> setList = new ArrayList<>();

		for (TrainExample trainExample : examples) {
			setList.add(trainExample.convertToMultiDataSet());
		}
		
		long totalTime = 0;
		long end = System.currentTimeMillis();
		long start = end;
		for (int i = 1; i <= epochs; ++i) {
			trainEpoch(i, setList);
			
			// bookkeeping + plot progress
			long now = System.currentTimeMillis();
			// eps_time.update(now - end);

			end = now;
			long duration = end - start;
			totalTime += duration;
			long average = totalTime / i;
			long timeLeft = average * (epochs -i);
			
			float durationFloat = duration;

			int setListSize = setList.size();
			float setListSizeFloat = setListSize;
			float durationPerData = durationFloat / setListSizeFloat;
			log.info(i + "/" + epochs + " | train eps time " + duration + "ms | total " + totalTime + "ms | ETA " + timeLeft
					+ "ms, train list size : " + setListSize + " time per input ms = " + durationPerData);

			start = now;
		}
		
				
	}

	private void trainEpoch(int i, List<MultiDataSet> setList) {

		Collections.shuffle(setList);

		TestMultiDataSetIterator iterator = new TestMultiDataSetIterator(1,
				setList.toArray(new MultiDataSet[setList.size()]));
		model.fit(iterator);

	}

	public static Random random = new Random();


	@Override
	public Pair<PolicyVector, Float> predict(ByteCanonicalForm gameCanonicalForm) {

		float[] val = gameCanonicalForm.getFloatCanonicalForm();

		INDArray array = Utilities.createArray(val);
		INDArray[] outputArray = model.output(array);
		INDArray policyOutput = outputArray[0];
		INDArray valueOutput = outputArray[1];

		boolean foundNan = false;
		PolicyVector vector = new PolicyVector();
		vector.vector = new float[ChoiceInventory.TOTAL_CHOICE];
		for (int i = 0; i < ChoiceInventory.TOTAL_CHOICE; ++i) {
			float floatValue = policyOutput.getFloat(i);
			if(Float.isNaN(floatValue)) {
				foundNan = true;
			}
			vector.vector[i] = floatValue;
		}
		
		if( foundNan ) {
			throw new IllegalArgumentException("Policy value returned a NaN value " + Arrays.toString(vector.vector));
		}

		float value = valueOutput.getFloat(0);

		Pair<PolicyVector, Float> pair = new MutablePair<>(vector, value);
		return pair;
	}

	@Override
	public void saveCheckpoint(String folder, String filename) {

		try {
			model.save(new File(folder + "/" + filename), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadCheckpoint(String folder, String filename) {

		try {
			model = ComputationGraph.load(new File(folder + "/" + filename), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public NeuralNet clone() {
		ComputationGraph clone = model.clone();
		KemetNeuralNetwork retVal = new KemetNeuralNetwork(clone);
		return retVal;
	}

}
