
package kemet.ai;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.TestMultiDataSetIterator;

import kemet.model.action.choice.ChoiceInventory;
import kemet.util.ByteCanonicalForm;
import kemet.util.NeuralNet;
import kemet.util.PolicyVector;
import kemet.util.TrainExample;

public class KemetNeuralNetwork implements NeuralNet {

	public ComputationGraph model;

	public static int epochs = 10;
	public static int batchSize = 10;

	public KemetNeuralNetwork() {
		model = DL4JAlphaKemetZeroBuilder.build();
		addListeners();
	}
	
	public KemetNeuralNetwork(ComputationGraph model) {
		this.model = model;
		addListeners();
	}

	private void addListeners() {
		model.addListeners(new PerformanceListener(4, true));
		model.addListeners(new CollectScoresListener(4, true));
		model.addListeners(new TimeIterationListener(4));
	}

	@Override
	public void train(List<TrainExample> examples) {

		for (int i = 0; i < epochs; ++i) {
			trainEpoch(i, examples);

		}
	}

	private void trainEpoch(int i, List<TrainExample> examples) {

		List<MultiDataSet> setList = new ArrayList<>();

		for (TrainExample trainExample : examples) {
			setList.add(trainExample.convertToMultiDataSet());
		}
		
		Collections.shuffle(setList);

		TestMultiDataSetIterator iterator = new TestMultiDataSetIterator(1,
				setList.toArray(new MultiDataSet[setList.size()]));
		model.fit(iterator);

	}

	public static Random random = new Random();

	public void print(String string) {
		System.out.println(string);
	}

	@Override
	public Pair<PolicyVector, Float> predict(ByteCanonicalForm gameCanonicalForm) {

		float[] val = gameCanonicalForm.getFloatCanonicalForm();

		INDArray array = new NDArray(val);
		INDArray[] outputArray = model.output(array);
		INDArray policyOutput = outputArray[0];
		INDArray valueOutput = outputArray[0];

		PolicyVector vector = new PolicyVector();
		vector.vector = new float[ChoiceInventory.TOTAL_CHOICE];
		for (int i = 0; i < ChoiceInventory.TOTAL_CHOICE; ++i) {
			vector.vector[i] = policyOutput.getFloat(i);
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
