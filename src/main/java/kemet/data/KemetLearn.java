package kemet.data;

import kemet.ai.KemetNeuralNetBuilder;
import kemet.ai.KemetNeuralNetwork;
import kemet.util.Coach;
import kemet.util.GameFactory;
import kemet.util.NeuralNet;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class KemetLearn {

	public static void main(String[] args) {
		
//		Logger log = LogManager.getLogger("hello.world");
//		log.error("error");
//		log.warn("warn");
//		log.info("info");
//		log.debug("debug");
//		log.trace("trace");
//		log.fatal("fatal");
		
//		KemetNeuralNetBuilder.NESTEROV_LEARN_RATE = 0.002;
//		KemetNeuralNetBuilder.NESTEROV_MOMENTUM = 0.5;
		
	    GameFactory gameFactory = new TwoPlayerGame();
	    
	    NeuralNet neuralNet = new KemetNeuralNetwork();
	    
	    neuralNet.saveCheckpoint("./temp", "initial.nn");
	    neuralNet.loadCheckpoint("./temp", "initial.nn");
	    
	    boolean loadModel = false;
	    boolean loadTrainingExamples = false;
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
	
//	args = dotdict({
//	    'numIters': 1000,
//	    'numEps': 100,
//	    'tempThreshold': 15,
//	    'updateThreshold': 0.6,
//	    'maxlenOfQueue': 200000,
//	    'numMCTSSims': 25,
//	    'arenaCompare': 40,
//	    'cpuct': 1,
//
//	    'checkpoint': './temp/',
//	    'load_model': False,
//	    'load_folder_file': ('/dev/models/8x100x50','best.pth.tar'),
//	    'numItersForTrainExamplesHistory': 20,
//	})


	
}
