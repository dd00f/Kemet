package kemet.ai;

import java.util.HashMap;
import java.util.Map;

import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.InputPreProcessor;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.ElementWiseVertex;
import org.deeplearning4j.nn.conf.graph.ElementWiseVertex.Op;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ActivationLayer;
import org.deeplearning4j.nn.conf.layers.BatchNormalization;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.preprocessor.CnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Sgd;

import kemet.model.BoardInventory;
import kemet.model.action.choice.ChoiceInventory;

class DL4JAlphaKemetZeroBuilder2D {

	private static final int LAYER_SIZE = 256;
	private static final int BOARD_X = BoardInventory.TOTAL_STATE_COUNT;
	private static final int BOARD_Y = 1;
	private static final int BOARD_Z = 1;
	
	ComputationGraphConfiguration.GraphBuilder conf = new NeuralNetConfiguration.Builder().updater(new Sgd())
			.weightInit(WeightInit.LECUN_NORMAL)
			.graphBuilder().setInputTypes(InputType.convolutional(BOARD_X, BOARD_Y, BOARD_Z));

	private void addInputs(String name) {
		conf.addInputs(name);
	}

	private void addOutputs(String... names) {
		conf.setOutputs(names);
	}

	private ComputationGraphConfiguration buildAndReturn() {

		return conf.build();
	}

	private String addConvBatchNormBlock(String blockName, String inName, int nIn, boolean useActivation,
			int[] kernelSize, int[] strides, ConvolutionMode convolutionMode) {
		String convName = "conv_" + blockName;
		String bnName = "batch_norm_" + blockName;
		String actName = "relu_" + blockName;

		conf.addLayer(convName, new ConvolutionLayer.Builder().kernelSize(kernelSize).stride(strides)
				.convolutionMode(convolutionMode).nIn(nIn).nOut(LAYER_SIZE).build(), inName);

		conf.addLayer(bnName, new BatchNormalization.Builder().nOut(LAYER_SIZE).build(), convName);
		if (useActivation) {
			conf.addLayer(actName, new ActivationLayer.Builder().activation(Activation.RELU).build(), bnName);
			return actName;
		} else {
			return bnName;
		}
	}

	private String addResidualBlock(int blockNumber, String inName, int[] kernelSize, int[] strides,
			ConvolutionMode convolutionMode) {
		String firstBlock = "residual_1_" + blockNumber;
		String firstOut = "relu_residual_1_" + blockNumber;
		String secondBlock = "residual_2_" + blockNumber;
		String mergeBlock = "add_" + blockNumber;
		String actBlock = "relu_" + blockNumber;

		String firstBnOut = addConvBatchNormBlock(firstBlock, inName, LAYER_SIZE, true, kernelSize, strides, convolutionMode);
		String secondBnOut = addConvBatchNormBlock(secondBlock, firstOut, LAYER_SIZE, false, kernelSize, strides,
				convolutionMode);
		conf.addVertex(mergeBlock, new ElementWiseVertex(Op.Add), firstBnOut, secondBnOut);
		conf.addLayer(actBlock, new ActivationLayer.Builder().activation(Activation.RELU).build(), mergeBlock);
		return actBlock;
	}

	private String addResidualTower(int numBlocks, String inName, int[] kernelSize, int[] strides,
			ConvolutionMode convolutionMode) {
		String name = inName;
		for (int i = 0; i < numBlocks; ++i) {
			name = addResidualBlock(i, name, kernelSize, strides, convolutionMode);
		}
		return name;
	}

//	private String addConvolutionalTower(int numBlocks, String inName, int[] kernelSize, int[] strides,
//			ConvolutionMode convolutionMode) {
//		String name = inName;
//		for (int i = 0; i < numBlocks; ++i) {
//			name = addConvBatchNormBlock(Integer.toString(i), name, 256, true, kernelSize, strides, convolutionMode);
//		}
//		return name;
//	}

	private String addPolicyHead(String inName, int[] kernelSize, int[] strides, ConvolutionMode convolutionMode) {
		String convName = "policy_head_conv_";
		String bnName = "policy_head_batch_norm_";
		String actName = "policy_head_relu_";
		String denseName = "policy_head_output_";

		conf.addLayer(convName, new ConvolutionLayer.Builder().kernelSize(kernelSize).stride(strides)
				.convolutionMode(convolutionMode).nOut(2).nIn(LAYER_SIZE).build(), inName);
		conf.addLayer(bnName, new BatchNormalization.Builder().nOut(2).build(), convName);
		conf.addLayer(actName, new ActivationLayer.Builder().activation(Activation.RELU).build(), bnName);
		conf.addLayer(denseName, new OutputLayer.Builder().nIn(2 * BOARD_X * BOARD_Y).nOut(ChoiceInventory.TOTAL_CHOICE ).build(), actName);
		Map<String, InputPreProcessor> map = new HashMap<>();
		map.put(denseName, new CnnToFeedForwardPreProcessor(BOARD_X, BOARD_Y, 2));
		conf.setInputPreProcessors(map);
		return denseName;
	}

	private String addValueHead(String inName, int[] kernelSize, int[] strides, ConvolutionMode convolutionMode) {
		String convName = "value_head_conv_";
		String bnName = "value_head_batch_norm_";
		String actName = "value_head_relu_";
		String denseName = "value_head_dense_";
		String outputName = "value_head_output_";

		conf.addLayer(convName, new ConvolutionLayer.Builder().kernelSize(kernelSize).stride(strides)
				.convolutionMode(convolutionMode).nOut(1).nIn(LAYER_SIZE).build(), inName);
		conf.addLayer(bnName, new BatchNormalization.Builder().nOut(1).build(), convName);
		conf.addLayer(actName, new ActivationLayer.Builder().activation(Activation.RELU).build(), bnName);
		conf.addLayer(denseName, new DenseLayer.Builder().nIn(BOARD_X * BOARD_Y).nOut(LAYER_SIZE).build(), actName);
		Map<String, InputPreProcessor> map = new HashMap<>();
		map.put(denseName, new CnnToFeedForwardPreProcessor(BOARD_X, BOARD_Y, 1));

		conf.setInputPreProcessors(map);
		conf.addLayer(outputName, new OutputLayer.Builder().nIn(LAYER_SIZE).nOut(1).build(), denseName);
		return outputName;
	}

	private ComputationGraph apply(int blocks, int numPlanes) {
		String input = "in";

		addInputs(input);
		String initBlock = "init";
		int[] kernel3x3 = new int[] { 3, 3 };
		int[] kernel1x1 = new int[] { 1, 1 };
		int[] strides = new int[] { 1, 1 };
		String convOut = addConvBatchNormBlock(initBlock, input, numPlanes, true, kernel3x3,
				strides, ConvolutionMode.Same);
		String towerOut = addResidualTower(blocks, convOut, kernel3x3, strides,
				ConvolutionMode.Same);
		String policyOut = addPolicyHead(towerOut, kernel3x3, strides, ConvolutionMode.Same);
		String valueOut = addValueHead(towerOut, kernel1x1, strides, ConvolutionMode.Same);
		addOutputs(policyOut, valueOut);

		ComputationGraph model = new ComputationGraph(buildAndReturn());
		model.init();

		return model;
	}
	
	public static ComputationGraph build() {
		DL4JAlphaKemetZeroBuilder2D build = new DL4JAlphaKemetZeroBuilder2D();
		return build.apply(20, 1);
		
	}

}
