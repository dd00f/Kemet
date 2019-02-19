package kemet.ai;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
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
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import kemet.Options;
import lombok.val;

public class MiniKemetBuilder {

	public static final int INPUT_LAYER_SIZE = 10;
	public static final int OUTPUT_LAYER_SIZE = 10;
	public static final int INTERNAL_LAYER_SIZE = 40;

	ComputationGraphConfiguration.GraphBuilder conf = new NeuralNetConfiguration.Builder()
			.seed(123)
			//.gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
			//.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
			.updater(Updater.SGD.getIUpdaterWithDefaultConfig())
			.weightInit(WeightInit.LECUN_NORMAL)
			.graphBuilder()
			.setInputTypes(InputType.feedForward(INPUT_LAYER_SIZE));

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

		conf.addLayer(denseName, new DenseLayer.Builder().nIn(nIn).nOut(INTERNAL_LAYER_SIZE).build(), inName);

		conf.addLayer(bnName, new BatchNormalization.Builder().nOut(INTERNAL_LAYER_SIZE).build(), denseName);
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

		String firstBnOut = addBatchNormBlock(firstBlock, inName, INTERNAL_LAYER_SIZE, Options.NEURAL_NET_RELU_INTERNAL_LAYERS);
		
		if(!  Options.NEURAL_NET_RESIDUAL_ACTIVATED ) {
			return firstBnOut;
		}
		
		String secondBnOut = addBatchNormBlock(secondBlock, firstOut, INTERNAL_LAYER_SIZE, false);
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

		Activation activation = Activation.SIGMOID;
		LossFunction lossFunction = LossFunction.XENT;

//		Activation activation = Activation.SOFTMAX;
//		LossFunction lossFunction = LossFunction.MEAN_SQUARED_LOGARITHMIC_ERROR;
//
//		Activation activation = Activation.SOFTMAX;
//		LossFunction lossFunction = LossFunction.MEAN_SQUARED_LOGARITHMIC_ERROR;

		
		conf.addLayer(denseName,
				new OutputLayer.Builder().nIn(INTERNAL_LAYER_SIZE).lossFunction(lossFunction)
						.activation(activation).nOut(OUTPUT_LAYER_SIZE).build(),
				inName);

		return denseName;

	}

	private ComputationGraph apply(int blocks) {
		String input = "in";

		addInputs(input);
		String initBlock = "init";

		String denseOut = addBatchNormBlock(initBlock, input, INPUT_LAYER_SIZE, Options.NEURAL_NET_RELU_INTERNAL_LAYERS);
		String towerOut = addResidualTower(blocks, denseOut);
		String policyOut = addPolicyHead(towerOut);
		//String valueOut = addValueHead(towerOut);
		addOutputs(policyOut);
		ComputationGraphConfiguration buildAndReturn = buildAndReturn();
		ComputationGraph model = new ComputationGraph(buildAndReturn);
		model.init();
		model.toString();

		return model;
	}

	public static ComputationGraph build() {
		MiniKemetBuilder build = new MiniKemetBuilder();

		return build.apply(Options.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT);

	}

}
