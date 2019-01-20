
package kemet.ai;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;
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
import kemet.util.ByteCanonicalForm;
import kemet.util.NeuralNet;
import kemet.util.PolicyVector;
import kemet.util.TrainExample;

public class KemetNeuralNetwork implements NeuralNet{
	
	private MultiLayerSpace hyperparameterSpace;

	public void create() {
		
		int boardX = BoardInventory.TOTAL_STATE_COUNT;
		int boardY = 1;
		int actionSize = ChoiceInventory.TOTAL_CHOICE;
		
		
// 		new Reshape()
		
        ParameterSpace<Double> learningRateHyperparam = new ContinuousParameterSpace(0.0001, 0.1);  //Values will be generated uniformly at random between 0.0001 and 0.1 (inclusive)
        ParameterSpace<Integer> layerSizeHyperparam = new IntegerParameterSpace(16,256);            //Integer values will be generated uniformly at random between 16 and 256 (inclusive)

        hyperparameterSpace = new MultiLayerSpace.Builder()
            //These next few options: fixed values for all models
            .weightInit(WeightInit.XAVIER)
            .regularization(true)
            .l2(0.0001)
            //Learning rate hyperparameter: search over different values, applied to all models
            .learningRate(learningRateHyperparam)
            .addLayer( new DenseLayerSpace.Builder()
                    //Fixed values for this layer:
                    .nIn(boardX)  //Fixed input: 28x28=784 pixels for MNIST
                    .activation(Activation.LEAKYRELU)
                    //One hyperparameter to infer: layer size
                    .nOut(layerSizeHyperparam)
                    .build())
            .addLayer( new OutputLayerSpace.Builder()
                .nOut(actionSize)
                .activation(Activation.SOFTMAX)
                .lossFunction(LossFunctions.LossFunction.MCXENT)
                .build())
            .build();
        
        

		
	}
	
	public int epochs = 10;
	public int batchSize = 10;

	@Override
	public void train(List<TrainExample> examples) {
		
		for( int i=0; i< epochs; ++i) {
			trainEpoch( i, examples );
			
		}
	}

	private void trainEpoch(int i, List<TrainExample> examples) {
      print("EPOCH ::: " + i);
      long dataTime = 0;
      long batchTime = 0;
      long piLosses = 0;
      long vLosses = 0;
      long endTime = System.currentTimeMillis();

      int batchIndex = 0;
      while( batchIndex < (examples.size() / batchSize)) {
    	  int[] sampleIds = createSamples(examples.size(), batchSize);
    	  
      }
      //
//      # self.sess.run(tf.local_variables_initializer())
//      while batch_idx < int(len(examples)/args.batch_size):
//          sample_ids = np.random.randint(len(examples), size=args.batch_size)
//          boards, pis, vs = list(zip(*[examples[i] for i in sample_ids]))
//
//          # predict and compute gradient and do SGD step
//          input_dict = {
//      self.nnet.input_boards: boards, 
//      self.nnet.target_pis: pis, 
//      self.nnet.target_vs: vs, 
//      self.nnet.dropout: args.dropout, 
//      self.nnet.isTraining: True}
//
//          # measure data loading time
//          data_time.update(time.time() - end)
//
//          # record loss
//          self.sess.run(self.nnet.train_step, feed_dict=input_dict)
//          pi_loss, v_loss = self.sess.run([self.nnet.loss_pi, self.nnet.loss_v], feed_dict=input_dict)
//          pi_losses.update(pi_loss, len(boards))
//          v_losses.update(v_loss, len(boards))
//
//          # measure elapsed time
//          batch_time.update(time.time() - end)
//          end = time.time()
//          batch_idx += 1
//
//          # plot progress
//          bar.suffix  = '({batch}/{size}) Data: {data:.3f}s | Batch: {bt:.3f}s | Total: {total:} | ETA: {eta:} | Loss_pi: {lpi:.4f} | Loss_v: {lv:.3f}'.format(
//                      batch=batch_idx,
//                      size=int(len(examples)/args.batch_size),
//                      data=data_time.avg,
//                      bt=batch_time.avg,
//                      total=bar.elapsed_td,
//                      eta=bar.eta_td,
//                      lpi=pi_losses.avg,
//                      lv=v_losses.avg,
//                      )
//          bar.next()
//      bar.finish()
	}
	
	public Random random = new Random();

	private int[] createSamples(int size, int batchSize2) {
		int[] retVal = new int[batchSize];
		for (int i = 0; i < retVal.length; i++) {
			retVal[i] = random.nextInt(size);
		}
		return retVal;
		
	}

	private void print(String string) {
		System.out.println(string);
	}

	@Override
	public Pair<PolicyVector, Float> predict(ByteCanonicalForm gameCanonicalForm) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void saveCheckpoint(String folder, String filename) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void loadCheckpoint(String folder, String filename) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NeuralNet clone() {
		throw new UnsupportedOperationException();
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

        


