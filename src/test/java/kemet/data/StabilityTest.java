package kemet.data;

import java.util.ArrayList;
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

import kemet.Options;
import kemet.ai.KemetNeuralNetBuilder;
import kemet.ai.KemetNeuralNetwork;
import kemet.ai.KemetRecurrentNeuralNetBuilder;
import kemet.util.Coach;
import kemet.util.TrainExample;

class StabilityTest {

	private ComputationGraph build;

	private boolean UI = true;

	public boolean useRecurrentNetwork = true;

	@Test
	void test() throws InterruptedException {

		// for the size of each layer in increments of 10
		// for the number of residual layer
		// for the activation of residual layer or not
		// run 500 training session
		// calculate average error for 500 to 1000 in increments of 100
		// pick the best net

		nn = new KemetNeuralNetwork();

		if (useRecurrentNetwork) {
			build = KemetRecurrentNeuralNetBuilder.build();
			nn.model = build;
			nn.addListeners();
		}

		build = nn.model;

		if (UI) {
			startUI();
		}

		coach = new Coach(new TwoPlayerGame(), nn);
		Options.COACH_USE_MANUAL_AI = true;
		KemetNeuralNetBuilder.NEURAL_NET_TRAIN_WITH_MASK = false;
		
		Options.PRINT_COACH_SEARCH_ACTIONS = true;
		Options.PRINT_COACH_SEARCH_PROBABILITIES = true;
		
		executeEpisode = coach.executeEpisode();

		createAdjustedTrainDataset();

		int sampleIndex = trainArrays.length / 2;
		printSample(sampleIndex);

		for (int i = 0; i < 10000; ++i) {
			TestMultiDataSetIterator fitIterator = new TestMultiDataSetIterator(5000, trainArrays);
			build.fit(fitIterator, 100);
			score(i);
		}

		// build.getLayers()[1].
		if (UI) {
			while (true) {
				Thread.sleep(1000);
			}
		}

	}

	private void score(int iteration) {

		double totalScore = 0;
		for (int i = 0; i < trainArrays.length; i++) {
			MultiDataSet f = trainArrays[i];
			totalScore += build.score(f);
		}
		double averageScore = totalScore / trainArrays.length;
		System.out.println(iteration + " average score is : " + averageScore);

		int sampleIndex = trainArrays.length / 2;
		printSample(sampleIndex);
		printSample(sampleIndex + 1);
		printSample(sampleIndex + 2);

	}

	private void printSample(int sampleIndex) {
		MultiDataSet sample = trainArrays[sampleIndex];

		INDArray[] output = build.output(sample.getFeatures()[0]);

		System.out.println("------- " + sampleIndex + " ------------------");

		System.out.println("State    S " + sample.getFeatures()[0]);

		System.out.println("Expected V " + sample.getLabels()[1]);
		System.out.println("Actual   V " + output[1]);

		if (KemetNeuralNetBuilder.NEURAL_NET_TRAIN_WITH_MASK) {
			System.out.println("Mask     P " + sample.getLabelsMaskArrays()[0]);
		}
		System.out.println("Expected P " + sample.getLabels()[0]);
		System.out.println("Actual   P " + output[0]);
	}

	private void createAdjustedTrainDataset() {
		List<MultiDataSet> setList = new ArrayList<>();

		float trainCount = executeEpisode.size();
		int i = 0;
		float iFloat = 0;

		for (TrainExample trainExample : executeEpisode) {
			i++;
			iFloat = i;
			// adjust the value based on the depth of the game
			trainExample.valueV = trainExample.valueV * iFloat / trainCount;
			setList.add(trainExample.convertToMultiDataSet());
		}

		trainArrays = setList.toArray(new MultiDataSet[setList.size()]);
	}

	private KemetNeuralNetwork nn;

	private Coach coach;

	private List<TrainExample> executeEpisode;

	private MultiDataSet[] trainArrays;

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

}
