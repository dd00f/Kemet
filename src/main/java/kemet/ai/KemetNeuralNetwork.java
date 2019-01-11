
package kemet.ai;

import org.deeplearning4j.arbiter.MultiLayerSpace;
import org.deeplearning4j.arbiter.layers.DenseLayerSpace;
import org.deeplearning4j.arbiter.layers.OutputLayerSpace;
import org.deeplearning4j.arbiter.optimize.api.ParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.continuous.ContinuousParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.integer.IntegerParameterSpace;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ops.impl.shape.Reshape;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import kemet.model.BoardInventory;
import kemet.model.action.choice.ChoiceInventory;

public class KemetNeuralNetwork {
	
	public void create() {
		
		int boardX = BoardInventory.TOTAL_STATE_COUNT;
		int boardY = 1;
		int actionSize = ChoiceInventory.TOTAL_CHOICE;
		
		
// 		new Reshape()
		
        ParameterSpace<Double> learningRateHyperparam = new ContinuousParameterSpace(0.0001, 0.1);  //Values will be generated uniformly at random between 0.0001 and 0.1 (inclusive)
        ParameterSpace<Integer> layerSizeHyperparam = new IntegerParameterSpace(16,256);            //Integer values will be generated uniformly at random between 16 and 256 (inclusive)

        MultiLayerSpace hyperparameterSpace = new MultiLayerSpace.Builder()
            //These next few options: fixed values for all models
            .weightInit(WeightInit.XAVIER)
            .regularization(true)
            .l2(0.0001)
            //Learning rate hyperparameter: search over different values, applied to all models
            .learningRate(learningRateHyperparam)
            .addLayer( new DenseLayerSpace.Builder()
                    //Fixed values for this layer:
                    .nIn(784)  //Fixed input: 28x28=784 pixels for MNIST
                    .activation(Activation.LEAKYRELU)
                    //One hyperparameter to infer: layer size
                    .nOut(layerSizeHyperparam)
                    .build())
            .addLayer( new OutputLayerSpace.Builder()
                .nOut(10)
                .activation(Activation.SOFTMAX)
                .lossFunction(LossFunctions.LossFunction.MCXENT)
                .build())
            .build();

		
	}
	
	
}


//import sys
//sys.path.append('..')
//from utils import *
//
//import tensorflow as tf
//
//class OthelloNNet():
//    def __init__(self, game, args):
//        # game params
//        self.board_x, self.board_y = game.getBoardSize()
//        self.action_size = game.getActionSize()
//        self.args = args
//
//        # Neural Net
//        self.input_boards = Input(shape=(self.board_x, self.board_y))    # s: batch_size x board_x x board_y
//
//        x_image = Reshape((self.board_x, self.board_y, 1))(self.input_boards)                # batch_size  x board_x x board_y x 1
//        h_conv1 = Activation('relu')(BatchNormalization(axis=3)(Conv2D(args.num_channels, 3, padding='same', use_bias=False)(x_image)))         # batch_size  x board_x x board_y x num_channels
//        h_conv2 = Activation('relu')(BatchNormalization(axis=3)(Conv2D(args.num_channels, 3, padding='same', use_bias=False)(h_conv1)))         # batch_size  x board_x x board_y x num_channels
//        h_conv3 = Activation('relu')(BatchNormalization(axis=3)(Conv2D(args.num_channels, 3, padding='valid', use_bias=False)(h_conv2)))        # batch_size  x (board_x-2) x (board_y-2) x num_channels
//        h_conv4 = Activation('relu')(BatchNormalization(axis=3)(Conv2D(args.num_channels, 3, padding='valid', use_bias=False)(h_conv3)))        # batch_size  x (board_x-4) x (board_y-4) x num_channels
//        h_conv4_flat = Flatten()(h_conv4)       
//        s_fc1 = Dropout(args.dropout)(Activation('relu')(BatchNormalization(axis=1)(Dense(1024, use_bias=False)(h_conv4_flat))))  # batch_size x 1024
//        s_fc2 = Dropout(args.dropout)(Activation('relu')(BatchNormalization(axis=1)(Dense(512, use_bias=False)(s_fc1))))          # batch_size x 1024
//        self.pi = Dense(self.action_size, activation='softmax', name='pi')(s_fc2)   # batch_size x self.action_size
//        self.v = Dense(1, activation='tanh', name='v')(s_fc2)                    # batch_size x 1
//
//        self.model = Model(inputs=self.input_boards, outputs=[self.pi, self.v])
//        self.model.compile(loss=['categorical_crossentropy','mean_squared_error'], optimizer=Adam(args.lr))

        


