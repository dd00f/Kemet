package kemet.ai;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.graph.ElementWiseVertex;
import org.deeplearning4j.nn.conf.graph.ElementWiseVertex.Op;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ActivationLayer;
import org.deeplearning4j.nn.conf.layers.BatchNormalization;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer.Builder;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.AdaGrad;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import kemet.Options;
import kemet.model.BoardInventory;
import kemet.model.action.choice.ChoiceInventory;
import lombok.val;

public class KemetNeuralNetBuilder {

	public static final int INPUT_SIZE = BoardInventory.TOTAL_STATE_COUNT;
	public static final int OUTPUT_SIZE = ChoiceInventory.TOTAL_CHOICE;
	public static int LAYER_SIZE = INPUT_SIZE * 5;
	public static final boolean VALUE_OUTPUT = true;

//	ComputationGraphConfiguration.GraphBuilder conf = new NeuralNetConfiguration.Builder()
//			.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
//			.updater(Updater.SGD.getIUpdaterWithDefaultConfig()).weightInit(WeightInit.LECUN_NORMAL).graphBuilder()
//			.setInputTypes(InputType.feedForward(BoardInventory.TOTAL_STATE_COUNT));
//
//	private void addInputs(String name) {
//
//		conf.addInputs(name);
//	}
//
//	private void addOutputs(String... names) {
//		conf.setOutputs(names);
//	}
//
//	private ComputationGraphConfiguration buildAndReturn() {
//
//		conf.toString();
//		return conf.build();
//	}
//
//	private String addBatchNormBlock(String blockName, String inName, int nIn, boolean useActivation) {
//		String denseName = "dense_" + blockName;
//		String bnName = "batch_norm_" + blockName;
//		String actName = "relu_" + blockName;
//
//		conf.addLayer(denseName, new DenseLayer.Builder().nIn(nIn).nOut(LAYER_SIZE).build(), inName);
//
//		conf.addLayer(bnName, new BatchNormalization.Builder().nOut(LAYER_SIZE).build(), denseName);
//		if (useActivation) {
//			conf.addLayer(actName, new ActivationLayer.Builder().activation(Activation.RELU).build(), bnName);
//			return actName;
//		} else {
//			return bnName;
//		}
//	}
//
//	private String addResidualBlock(int blockNumber, String inName) {
//		String firstBlock = "residual_1_" + blockNumber;
//		String firstOut = "relu_residual_1_" + blockNumber;
//		String secondBlock = "residual_2_" + blockNumber;
//		String mergeBlock = "add_" + blockNumber;
//		String actBlock = "relu_" + blockNumber;
//
//		String firstBnOut = addBatchNormBlock(firstBlock, inName, LAYER_SIZE, true);
//		String secondBnOut = addBatchNormBlock(secondBlock, firstOut, LAYER_SIZE, false);
//		conf.addVertex(mergeBlock, new ElementWiseVertex(Op.Add), firstBnOut, secondBnOut);
//		conf.addLayer(actBlock, new ActivationLayer.Builder().activation(Activation.RELU).build(), mergeBlock);
//		return actBlock;
//	}
//
//	private String addResidualTower(int numBlocks, String inName) {
//		String name = inName;
//		for (int i = 0; i < numBlocks; ++i) {
//			name = addResidualBlock(i, name);
//		}
//		return name;
//	}
//
//	private String addPolicyHead(String inName) {
//
//		String denseName = "policy_head_output_";
//
//		conf.addLayer(denseName,
//				new OutputLayer.Builder().nIn(LAYER_SIZE).lossFunction(LossFunction.MEAN_SQUARED_LOGARITHMIC_ERROR)
//						.activation(Activation.SOFTMAX).nOut(ChoiceInventory.TOTAL_CHOICE).build(),
//				inName);
//
//		return denseName;
//
//	}
//
//	private String addValueHead(String inName) {
//
//		String denseName = "value_head_dense_";
//		String outputName = "value_head_output_";
//
//		conf.addLayer(denseName, new DenseLayer.Builder().nIn(LAYER_SIZE).nOut(LAYER_SIZE).build(), inName);
//
//		OutputLayer outputLayer = new OutputLayer.Builder().nIn(LAYER_SIZE).activation(Activation.TANH)
//				.lossFunction(LossFunctions.LossFunction.MEAN_ABSOLUTE_ERROR).nOut(1).build();
//		conf.addLayer(outputName, outputLayer, denseName);
//		return outputName;
//	}

	private ComputationGraph apply(int blocks) {
		

		NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();

		builder.seed(123);
		builder.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);
		// builder.updater(new Nesterovs(0.006, 0.9));
		builder.updater(new AdaGrad());
		
		builder.l2(1e-4);

		ComputationGraphConfiguration.GraphBuilder conf2 = builder.graphBuilder();
		conf2.addInputs("in");

		String dense0LayerName = "dense0";
		conf2.layer(dense0LayerName, new DenseLayer.Builder().nIn(INPUT_SIZE).nOut(LAYER_SIZE)
				.activation(Activation.RELU).weightInit(WeightInit.XAVIER).build(), "in");

		String policyInput = dense0LayerName;

		for (int i = 0; i < Options.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT; ++i) {
			String denseLayerName = dense0LayerName + "_" + i;

			conf2.layer(denseLayerName, new DenseLayer.Builder().nIn(LAYER_SIZE).nOut(LAYER_SIZE)
					.activation(Activation.RELU).weightInit(WeightInit.XAVIER).build(), policyInput);

			policyInput = denseLayerName;
			
			
			if( Options.NEURAL_NET_RESIDUAL_ACTIVATED ) {
				
				String denseLayerNameR = dense0LayerName + "_r_" + i;
				String mergeBlockName = "merge_r_" + i;
				String mergeActivationName = "mergeActivation_r_" + i;

				Builder residualDenseBuilder = new DenseLayer.Builder().nIn(LAYER_SIZE).nOut(LAYER_SIZE)
						.weightInit(WeightInit.XAVIER);
				
				if( false ) {
					residualDenseBuilder.activation(Activation.RELU);
				}
				
				conf2.layer(denseLayerNameR, residualDenseBuilder.build(), policyInput);

				conf2.addVertex(mergeBlockName, new ElementWiseVertex(Op.Add), denseLayerName, denseLayerNameR);
				conf2.addLayer(mergeActivationName, new ActivationLayer.Builder().activation(Activation.RELU).build(), mergeBlockName);
				

				policyInput = mergeActivationName;
				
			}
			
		}

		String policyOutputLayer = "policyOutput";

		// LossFunction lossFunction =
		// LossFunctions.LossFunction.MEAN_SQUARED_LOGARITHMIC_ERROR;

//		LossFunction lossFunction = LossFunctions.LossFunction.MCXENT;
//		Activation activationFunction = Activation.SOFTMAX;

		LossFunction lossFunction = LossFunctions.LossFunction.XENT;
		Activation activationFunction = Activation.SIGMOID;

		conf2.layer(policyOutputLayer, new OutputLayer.Builder(lossFunction).nIn(LAYER_SIZE).nOut(OUTPUT_SIZE)
				.activation(activationFunction).weightInit(WeightInit.XAVIER).build(), policyInput);

		if (VALUE_OUTPUT) {

			String valueOutput = "valueOutput";
			LossFunction valueLossFunction = LossFunctions.LossFunction.MSE;
			//Activation valueActivationFunction = Activation.SIGMOID;

			// LossFunction valueLossFunction = LossFunctions.LossFunction.MEAN_SQUARED_LOGARITHMIC_ERROR;
			Activation valueActivationFunction = Activation.TANH;
//			
			conf2.layer(valueOutput,
					new OutputLayer.Builder(valueLossFunction).nIn(LAYER_SIZE).nOut(1)
							.activation(valueActivationFunction).weightInit(WeightInit.XAVIER).build(),
					dense0LayerName);
			
			// conf2.setOutputs(layers.toArray(new String[layers.size()]));
			conf2.setOutputs(policyOutputLayer, valueOutput);

		}
		else {
			conf2.setOutputs(policyOutputLayer);

		}

		conf2.setInputTypes(InputType.feedForward(INPUT_SIZE));

//		String input = "in";
//
//		addInputs(input);
//		String initBlock = "init";
//
//		String denseOut = addBatchNormBlock(initBlock, input, BoardInventory.TOTAL_STATE_COUNT, false);
//		String towerOut = addResidualTower(blocks, denseOut);
//		String policyOut = addPolicyHead(towerOut);
//		String valueOut = addValueHead(towerOut);
//		addOutputs(policyOut, valueOut);
		ComputationGraphConfiguration buildAndReturn = conf2.build();
		ComputationGraph model = new ComputationGraph(buildAndReturn);
		model.init();

		return model;
	}

	public static ComputationGraph build() {
		KemetNeuralNetBuilder build = new KemetNeuralNetBuilder();

		return build.apply(Options.NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT);

	}

}
