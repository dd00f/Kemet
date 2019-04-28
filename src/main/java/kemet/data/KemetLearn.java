package kemet.data;

import kemet.ai.KemetNeuralNetwork;
import kemet.ai.KemetRecurrentNeuralNetBuilder;
import kemet.util.Coach;
import kemet.util.GameFactory;
import kemet.util.NeuralNet;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class KemetLearn {

	public static void main(String[] args) {
		
// original values
//		public static double NESTEROV_MOMENTUM = 0.9;
//		public static double NESTEROV_LEARN_RATE = 0.006;

		KemetRecurrentNeuralNetBuilder.NESTEROV_MOMENTUM = 0.15;
		KemetRecurrentNeuralNetBuilder.NESTEROV_LEARN_RATE = 0.001;
		
	    GameFactory gameFactory = new TwoPlayerGame();
	    
	    NeuralNet neuralNet = new KemetNeuralNetwork();
	    
	    neuralNet.saveCheckpoint("./temp", "initial.nn");
	    neuralNet.loadCheckpoint("./temp", "initial.nn");
	    
	    boolean loadModel = true;
	    boolean loadTrainingExamples = true;
	    if( loadModel ) {
	    	log.info("loading previous best neural network");
	    	neuralNet.loadCheckpoint("./temp", "best.pth.tar");
	    }
	    
	    Coach coach = new Coach(gameFactory, neuralNet);
	    
	    if( loadTrainingExamples ) {
	    	coach.loadTrainExamples();
	    }
	    
	    coach.learn();
	}
	

}
