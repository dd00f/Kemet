
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
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.TestMultiDataSetIterator;

import kemet.Options;
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
		if (Options.USE_RECURRENT_NEURAL_NET) {
			model = KemetRecurrentNeuralNetBuilder.build();
		} else {
			model = KemetNeuralNetBuilder.build();
		}
		addListeners();
	}

	public KemetNeuralNetwork(ComputationGraph model) {
		this.model = model;
		addListeners();
	}

	public void addListeners() {
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
		int setListSize = setList.size();
		log.info("START training {} examples over {} epochs.", setListSize, epochs);

		if (Options.NEURAL_NET_SHUFFLE_BETWEEN_EPOCH) {

			long totalTime = 0;
			long end = System.currentTimeMillis();
			long start = end;
			for (int i = 1; i <= epochs; ++i) {
				trainEpoch(setList);

				// bookkeeping + plot progress
				long now = System.currentTimeMillis();
				// eps_time.update(now - end);

				end = now;
				long duration = end - start;
				totalTime += duration;
				long average = totalTime / i;
				long timeLeft = average * (epochs - i);

				float durationFloat = duration;

				float setListSizeFloat = setListSize;
				float durationPerData = durationFloat / setListSizeFloat;
				log.info(i + "/" + epochs + " | train eps time " + duration + "ms | total " + totalTime + "ms | ETA "
						+ timeLeft + "ms, train list size : " + setListSize + " time per input ms = "
						+ durationPerData);
				String message = "{} / {}  | train eps time {}ms | total {}ms | ETA {}ms, train list size : {} time per input ms = {}";
				log.info(message, i, epochs, duration, totalTime, timeLeft, setListSize, durationPerData);

				start = now;
			}

		} else {
			long totalTime = 0;
			long end = System.currentTimeMillis();
			long start = end;
			TestMultiDataSetIterator iterator = new TestMultiDataSetIterator(5000,
					setList.toArray(new MultiDataSet[setList.size()]));
			model.fit(iterator, epochs);

			// bookkeeping + plot progress
			long now = System.currentTimeMillis();

			end = now;
			totalTime = end - start;

			float durationFloat = totalTime;

			float setListSizeFloat = setListSize;
			float durationPerData = durationFloat / epochs / setListSizeFloat;
			float timePerEpoch = durationFloat / epochs;
			String message = "END   trained {} epoch | total {} ms | train list size : {} | time per epoch ms = {}ms | time per input ms = {}";
			log.info(message, epochs, totalTime, setListSize, timePerEpoch, durationPerData);

		}
	}

	private void trainEpoch( List<MultiDataSet> setList) {

		Collections.shuffle(setList);

		TestMultiDataSetIterator iterator = new TestMultiDataSetIterator(5000,
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

		PolicyVector vector = new PolicyVector();
		vector.fromINDArray(policyOutput);

		float value = valueOutput.getFloat(0);

		Pair<PolicyVector, Float> pair = new MutablePair<>(vector, value);
		return pair;
	}

	@Override
	public Pair<PolicyVector, Float>[] predict(List<ByteCanonicalForm> gameCanonicalForm) {

		MultiDataSet[] mdsArray = convertByteCanonicalFormToMultiDataSet(gameCanonicalForm);
		TestMultiDataSetIterator testMultiDataSetIterator = new TestMultiDataSetIterator(5000, mdsArray);
		INDArray[] output = model.output(testMultiDataSetIterator);

		Pair<PolicyVector, Float>[] retVal = convertOutputArrayToPolicyValuePairs(output);
		return retVal;
	}

	private Pair<PolicyVector, Float>[] convertOutputArrayToPolicyValuePairs(INDArray[] output) {
		INDArray policyArray = output[0];
		INDArray valueArray = output[1];

		int outputCount = policyArray.rows();

		@SuppressWarnings("unchecked")
		Pair<PolicyVector, Float>[] retVal = new Pair[outputCount];

		for (int i = 0; i < outputCount; ++i) {

			PolicyVector vector = new PolicyVector();
			vector.fromINDArray(policyArray.getRow(i));

			float value = valueArray.getRow(i).getFloat(0);

			Pair<PolicyVector, Float> pair = new MutablePair<>(vector, value);

			retVal[i] = pair;
		}

		return retVal;
	}

	private static INDArray dummyArray = Utilities.createArray(new float[1]);

	private MultiDataSet[] convertByteCanonicalFormToMultiDataSet(List<ByteCanonicalForm> gameCanonicalForm) {

		int size = gameCanonicalForm.size();
		MultiDataSet[] retVal = new MultiDataSet[size];

		for (int i = 0; i < size; i++) {
			ByteCanonicalForm byteCanonicalForm = gameCanonicalForm.get(i);
			INDArray indArray = byteCanonicalForm.getINDArray();
			retVal[i] = new org.nd4j.linalg.dataset.MultiDataSet(indArray, dummyArray);
		}

		return retVal;
	}

	@Override
	public void saveCheckpoint(String folder, String filename) {

		try {
			File file = new File(folder + "/" + filename);
			model.save(file, true);
			long size = file.length();
			log.info("Saved neural network checkpoint file {} {}, {} bytes", folder, filename, size);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadCheckpoint(String folder, String filename) {

		try {
			File previousCheckpointFile = new File(folder + "/" + filename);
			if (previousCheckpointFile.exists()) {
				model = ComputationGraph.load(previousCheckpointFile, true);
				log.info("Loaded neural network checkpoint file {} {}", folder, filename);
			} else {
				log.info("No neural network checkpoint file found in {} {}, starting from scratch.", folder, filename);
			}
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
