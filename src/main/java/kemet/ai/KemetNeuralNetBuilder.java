package kemet.ai;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.ElementWiseVertex;
import org.deeplearning4j.nn.conf.graph.ElementWiseVertex.Op;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ActivationLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer.Builder;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import kemet.model.BoardInventory;
import kemet.model.action.choice.ChoiceInventory;

public class KemetNeuralNetBuilder {

	public static LossFunction POLICY_LOSS_FUNCTION = LossFunctions.LossFunction.XENT;
	public static Activation POLICY_ACTIVATION = Activation.SIGMOID;
	public static LossFunction VALUE_LOSS_FUNCTION = LossFunctions.LossFunction.MSE;
	public static Activation VALUE_ACTIVATION = Activation.TANH;
	public static WeightInit WEIGHT_INIT = WeightInit.XAVIER;
	public static double NESTEROV_MOMENTUM = 0.9;
	public static double NESTEROV_LEARN_RATE = 0.006;
	public static int INPUT_SIZE = BoardInventory.TOTAL_STATE_COUNT;
	public static int OUTPUT_SIZE = ChoiceInventory.TOTAL_CHOICE;
	public static int LAYER_SIZE = INPUT_SIZE * 5;
	public static boolean VALUE_OUTPUT = true;

	// TODO default was true
	public static boolean NEURAL_NET_RESIDUAL_ACTIVATED = false;

	// Keep this at false, true destabilizes the network.
	public static boolean NEURAL_NET_TRAIN_WITH_MASK = true;

	// TODO default was true
	public static boolean NEURAL_NET_RELU_INTERNAL_LAYERS = true;

	// TODO reset back to 10 or 20
	// Number of residual blocks in the neural network.
	public static int NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT = 5;
	
	
	private ComputationGraph apply(int blocks) {

		NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();

		builder.seed(123);
		builder.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);
		// builder.updater(new Nesterovs(0.001, 0.9));

		// good for 100 input 1000 internal 60 output
		builder.updater(new Nesterovs(NESTEROV_LEARN_RATE, NESTEROV_MOMENTUM));

		// good for input 1000x1000 output
		// builder.updater(new Nesterovs(0.00020, 0.5));

		// good for input 100x1000 output
		// builder.updater(new Nesterovs(0.00020, 0.5));

		// builder.updater(new Nesterovs());
		// builder.updater(new AdaGrad());
		// builder.updater(new AdaGrad(1e-1, 1e-6));
		// builder.updater(new AdaGrad(0.006, 1e-6));

		builder.l2(1e-4);

		ComputationGraphConfiguration.GraphBuilder conf2 = builder.graphBuilder();
		conf2.addInputs("in");

		String dense0LayerName = "dense0";
		WeightInit weightInit = WEIGHT_INIT;
		// WeightInit weightInit = WeightInit.LECUN_NORMAL;
		conf2.layer(dense0LayerName, new DenseLayer.Builder().nIn(INPUT_SIZE).nOut(LAYER_SIZE)
				.activation(Activation.RELU).weightInit(weightInit).build(), "in");

		String policyInput = dense0LayerName;

		for (int i = 0; i < NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT; ++i) {
			String denseLayerName = dense0LayerName + "_" + i;

			conf2.layer(denseLayerName, new DenseLayer.Builder().nIn(LAYER_SIZE).nOut(LAYER_SIZE)
					.activation(Activation.RELU).weightInit(weightInit).build(), policyInput);

			policyInput = denseLayerName;

			if (NEURAL_NET_RESIDUAL_ACTIVATED) {

				String denseLayerNameR = dense0LayerName + "_r_" + i;
				String mergeBlockName = "merge_r_" + i;
				String mergeActivationName = "mergeActivation_r_" + i;

				Builder residualDenseBuilder = new DenseLayer.Builder().nIn(LAYER_SIZE).nOut(LAYER_SIZE)
						.weightInit(weightInit);

				if (false) {
					residualDenseBuilder.activation(Activation.RELU);
				}

				conf2.layer(denseLayerNameR, residualDenseBuilder.build(), policyInput);

				conf2.addVertex(mergeBlockName, new ElementWiseVertex(Op.Add), denseLayerName, denseLayerNameR);
				conf2.addLayer(mergeActivationName, new ActivationLayer.Builder().activation(Activation.RELU).build(),
						mergeBlockName);

				policyInput = mergeActivationName;

			}

		}

		String policyOutputLayer = "policyOutput";

		// LossFunction lossFunction =
		// LossFunctions.LossFunction.MEAN_SQUARED_LOGARITHMIC_ERROR;

//		LossFunction lossFunction = LossFunctions.LossFunction.MSE;
//		Activation activationFunction = Activation.SOFTMAX;

		LossFunction lossFunction = POLICY_LOSS_FUNCTION;
		// ILossFunction lossFunction = new LossMultiLabel();
		Activation activationFunction = POLICY_ACTIVATION;

		conf2.layer(policyOutputLayer, new OutputLayer.Builder(lossFunction).nIn(LAYER_SIZE).nOut(OUTPUT_SIZE)
				.activation(activationFunction).weightInit(weightInit).build(), policyInput);

		if (VALUE_OUTPUT) {

			String valueOutput = "valueOutput";
			LossFunction valueLossFunction = VALUE_LOSS_FUNCTION;
			// Activation valueActivationFunction = Activation.SIGMOID;

			// LossFunction valueLossFunction =
			// LossFunctions.LossFunction.MEAN_SQUARED_LOGARITHMIC_ERROR;
			Activation valueActivationFunction = VALUE_ACTIVATION;
//			
			conf2.layer(valueOutput, new OutputLayer.Builder(valueLossFunction).nIn(LAYER_SIZE).nOut(1)
					.activation(valueActivationFunction).weightInit(weightInit).build(), policyInput);

			// conf2.setOutputs(layers.toArray(new String[layers.size()]));
			conf2.setOutputs(policyOutputLayer, valueOutput);

		} else {
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

		return build.apply(NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT);

	}

}
