package kemet.data;

import kemet.ai.KemetNeuralNetwork;
import kemet.model.KemetGame;
import kemet.util.Coach;
import kemet.util.Game;
import kemet.util.NeuralNet;

public class KemetLearn {

	public static void main(String[] args) {
	    Game game = KemetGame.create();
	    
	    NeuralNet neuralNet = new KemetNeuralNetwork();
	    
	    boolean loadModel = false;
	    if( loadModel ) {
	    	neuralNet.loadCheckpoint("./", "neuralnet.storage");
	    }
	    
	    Coach coach = new Coach(game, neuralNet);
	    
	    if( loadModel ) {
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
