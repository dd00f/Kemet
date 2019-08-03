package kemet.ai;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import kemet.model.BoardInventory;
import kemet.model.action.choice.ChoiceInventory;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class KemetRecurrentNeuralNetBuilder {

	public static LossFunction POLICY_LOSS_FUNCTION = LossFunctions.LossFunction.XENT;
	public static Activation POLICY_ACTIVATION = Activation.SIGMOID;
	public static LossFunction VALUE_LOSS_FUNCTION = LossFunctions.LossFunction.MSE;
	public static Activation VALUE_ACTIVATION = Activation.TANH;
	public static WeightInit WEIGHT_INIT = WeightInit.XAVIER;
	public static double NESTEROV_MOMENTUM = 0.9;
	public static double NESTEROV_LEARN_RATE = 0.006;
	public static int INPUT_SIZE = BoardInventory.TOTAL_STATE_COUNT;
	public static int OUTPUT_SIZE = ChoiceInventory.TOTAL_CHOICE;
	
	static {
		int TODO_RESET = 0;
	}
	//public static int LAYER_SIZE = INPUT_SIZE * 2;
	public static int LAYER_SIZE = INPUT_SIZE;
	
	public static boolean VALUE_OUTPUT = true;
	
	// TODO reset back to 10 or 20
	static {
		int TODO_RESET = 0;
	}
	// public static int NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT = 5;
	public static int NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT = 0;

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
		String inputLayerName = "in";
		conf2.addInputs(inputLayerName);
		List<String> layerList = new ArrayList<>();

		int cumulativeLayerSize = INPUT_SIZE;
		layerList.add(inputLayerName);

		String dense0LayerName = "dense";
		WeightInit weightInit = WEIGHT_INIT;
//		conf2.layer(dense0LayerName, new DenseLayer.Builder().nIn(cumulativeLayerSize).nOut(LAYER_SIZE)
//				.activation(Activation.RELU).weightInit(weightInit).build(), inputLayerName);

		String policyInput = inputLayerName;

//		layerList.add(dense0LayerName);

//		cumulativeLayerSize += LAYER_SIZE;

		for (int i = 0; i < blocks; ++i) {
			String denseLayerName = dense0LayerName + "_" + i;
			String mergeLayerName = "merge_" + i;

			conf2.layer(denseLayerName, new DenseLayer.Builder().nIn(cumulativeLayerSize).nOut(LAYER_SIZE)
					.activation(Activation.RELU).weightInit(weightInit).build(), policyInput);

			cumulativeLayerSize += LAYER_SIZE;

			layerList.add(denseLayerName);

			conf2.addVertex(mergeLayerName, new MergeVertex(), layerList.toArray(new String[layerList.size()]));

			policyInput = mergeLayerName;

		}

		String policyOutputLayer = "policyOutput";

		// LossFunction lossFunction =
		// LossFunctions.LossFunction.MEAN_SQUARED_LOGARITHMIC_ERROR;

//		LossFunction lossFunction = LossFunctions.LossFunction.MSE;
//		Activation activationFunction = Activation.SOFTMAX;

		LossFunction lossFunction = POLICY_LOSS_FUNCTION;
		// ILossFunction lossFunction = new LossMultiLabel();
		Activation activationFunction = POLICY_ACTIVATION;

		conf2.layer(policyOutputLayer, new OutputLayer.Builder(lossFunction).nIn(cumulativeLayerSize).nOut(OUTPUT_SIZE)
				.activation(activationFunction).weightInit(weightInit).build(), policyInput);

		if (VALUE_OUTPUT) {

			String valueOutput = "valueOutput";
			LossFunction valueLossFunction = VALUE_LOSS_FUNCTION;
			// Activation valueActivationFunction = Activation.SIGMOID;

			// LossFunction valueLossFunction =
			// LossFunctions.LossFunction.MEAN_SQUARED_LOGARITHMIC_ERROR;
			Activation valueActivationFunction = VALUE_ACTIVATION;
//			
			conf2.layer(valueOutput, new OutputLayer.Builder(valueLossFunction).nIn(cumulativeLayerSize).nOut(1)
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

		log.info("Created Recurrent ComputationGraph with {} inputs, {} layers of size {} and {} outputs", INPUT_SIZE,
				NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT, LAYER_SIZE, OUTPUT_SIZE);

		return model;
	}

	public static ComputationGraph build() {
		KemetRecurrentNeuralNetBuilder build = new KemetRecurrentNeuralNetBuilder();

		return build.apply(NEURAL_NETWORK_RESIDUAL_BLOCK_COUNT);

	}

}
