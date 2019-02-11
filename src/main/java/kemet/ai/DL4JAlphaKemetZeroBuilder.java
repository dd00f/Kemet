package kemet.ai;

import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.ElementWiseVertex;
import org.deeplearning4j.nn.conf.graph.ElementWiseVertex.Op;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ActivationLayer;
import org.deeplearning4j.nn.conf.layers.BatchNormalization;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import kemet.model.BoardInventory;
import kemet.model.action.choice.ChoiceInventory;

class DL4JAlphaKemetZeroBuilder {

	private static final int LAYER_SIZE = 256;

	ComputationGraphConfiguration.GraphBuilder conf = new NeuralNetConfiguration.Builder().updater(new Sgd())
			.weightInit(WeightInit.LECUN_NORMAL).graphBuilder().setInputTypes(InputType.feedForward(BoardInventory.TOTAL_STATE_COUNT));

	private void addInputs(String name) {
		

		conf.addInputs(name);
	}

	private void addOutputs(String... names) {
		conf.setOutputs(names);
	}

	private ComputationGraphConfiguration buildAndReturn() {

		conf.toString();
		return conf.build();
	}

	private String addBatchNormBlock(String blockName, String inName, int nIn, boolean useActivation) {
		String denseName = "dense_" + blockName;
		String bnName = "batch_norm_" + blockName;
		String actName = "relu_" + blockName;

		conf.addLayer(denseName, new DenseLayer.Builder().nIn(nIn).nOut(LAYER_SIZE).build(), inName);

		conf.addLayer(bnName, new BatchNormalization.Builder().nOut(LAYER_SIZE).build(), denseName);
		if (useActivation) {
			conf.addLayer(actName, new ActivationLayer.Builder().activation(Activation.RELU).build(), bnName);
			return actName;
		} else {
			return bnName;
		}
	}

	private String addResidualBlock(int blockNumber, String inName) {
		String firstBlock = "residual_1_" + blockNumber;
		String firstOut = "relu_residual_1_" + blockNumber;
		String secondBlock = "residual_2_" + blockNumber;
		String mergeBlock = "add_" + blockNumber;
		String actBlock = "relu_" + blockNumber;

		String firstBnOut = addBatchNormBlock(firstBlock, inName, LAYER_SIZE, true);
		String secondBnOut = addBatchNormBlock(secondBlock, firstOut, LAYER_SIZE, false);
		conf.addVertex(mergeBlock, new ElementWiseVertex(Op.Add), firstBnOut, secondBnOut);
		conf.addLayer(actBlock, new ActivationLayer.Builder().activation(Activation.RELU).build(), mergeBlock);
		return actBlock;
	}

	private String addResidualTower(int numBlocks, String inName) {
		String name = inName;
		for (int i = 0; i < numBlocks; ++i) {
			name = addResidualBlock(i, name);
		}
		return name;
	}

	private String addPolicyHead(String inName) {

		String denseName = "policy_head_output_";

		conf.addLayer(denseName, new OutputLayer.Builder().nIn(LAYER_SIZE).nOut(ChoiceInventory.TOTAL_CHOICE).build(),
				inName);

		return denseName;
	}

	private String addValueHead(String inName) {

		String denseName = "value_head_dense_";
		String outputName = "value_head_output_";

		conf.addLayer(denseName, new DenseLayer.Builder().nIn(LAYER_SIZE).nOut(LAYER_SIZE).build(), inName);

		OutputLayer outputLayer = new OutputLayer.Builder().nIn(LAYER_SIZE).activation(Activation.SIGMOID)
				.lossFunction(LossFunctions.LossFunction.MEAN_ABSOLUTE_ERROR).nOut(1).build();
		conf.addLayer(outputName, outputLayer, denseName);
		return outputName;
	}

	private ComputationGraph apply(int blocks) {
		String input = "in";

		addInputs(input);
		String initBlock = "init";

		String denseOut = addBatchNormBlock(initBlock, input, BoardInventory.TOTAL_STATE_COUNT, true);
		String towerOut = addResidualTower(blocks, denseOut);
		String policyOut = addPolicyHead(towerOut);
		String valueOut = addValueHead(towerOut);
		addOutputs(policyOut, valueOut);
		ComputationGraphConfiguration buildAndReturn = buildAndReturn();
		ComputationGraph model = new ComputationGraph(buildAndReturn);
		model.init();
		model.toString();

		return model;
	}

	public static ComputationGraph build() {
		DL4JAlphaKemetZeroBuilder build = new DL4JAlphaKemetZeroBuilder();
		// return build.apply(20, 1);
		return build.apply(10);

	}

}
