package kemet.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import kemet.Options;
import kemet.ai.TrialPlayerAI;
import kemet.model.KemetGame;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SearchPooler {

	public static int allMovesMaskedCount;

	public NeuralNet neuralNet;

	public Map<ByteCanonicalForm, GameInformation>  pendingPredictions = new HashMap<>();

	public Map<ByteCanonicalForm, GameInformation> providedPredictions = new HashMap<>();

	private long neuralNetPredictionCount = 0;
	private long neuralNetCallCount = 0;
	private long neuralNetTotalTimeNano = 0;

	public void printStats(String prefix) {

		if (Options.PRINT_MCTS_STATS) {

			String infoMessage = "{} nnet call count = {}, " + "nnet prediction count = {}, "
					+ "avg nnet prediction per call = {}, " + "avg time per call us {}, "
					+ "avg time per prediction us {}, " + "all moves mask count {}";
			log.info(infoMessage, prefix, neuralNetCallCount, neuralNetPredictionCount,
					neuralNetPredictionCount / neuralNetCallCount,
					neuralNetTotalTimeNano / neuralNetCallCount / 1000,
					neuralNetTotalTimeNano / neuralNetPredictionCount / 1000, allMovesMaskedCount);
		}
	}

	public void addPendingPrediction(Game game, ByteCanonicalForm canonicalForm) {
		if (!providedPredictions.containsKey(canonicalForm) && ! pendingPredictions.containsKey(canonicalForm)) {
			GameInformation info = new GameInformation();
			info.byteCanonicalForm = canonicalForm;
			info.gameEnded = game.isGameEnded();
			info.validMoves = game.getValidMoves();
			info.game = game;
			info.nextPlayerIndex = game.getNextPlayer();
			pendingPredictions.put(canonicalForm, info);
		}
		else if( Options.VALIDATE_POOLED_GAMES ) {
			
			GameInformation info = providedPredictions.get(canonicalForm);
			if( info == null ) {
				info = pendingPredictions.get(canonicalForm);
			}
			
			if( ! Arrays.equals(game.getValidMoves(), info.validMoves)) {
				throw new IllegalArgumentException("2 games with the same canonical form dont have the same valid moves");
			}

			if( info.gameEnded != game.isGameEnded() ) {
				throw new IllegalArgumentException("2 games with the same canonical form dont have the same ended flag");
			}
		}
	}

	public void fetchAllPendingPredictions() {
		
		if( pendingPredictions.size() == 0 ) {
			return;
		}
		
		List<ByteCanonicalForm> preparedBoards = new ArrayList<>();
		preparedBoards.addAll( pendingPredictions.keySet() );
		
		long starNano = System.nanoTime();
		Pair<PolicyVector, Float>[] predict = neuralNet.predict(preparedBoards);
		++neuralNetCallCount;
		neuralNetPredictionCount += pendingPredictions.size();
		long duration = System.nanoTime() - starNano;
		neuralNetTotalTimeNano += duration;

		for (int i = 0; i < predict.length; i++) {
			
			Pair<PolicyVector, Float> pair = predict[i];
			ByteCanonicalForm board = preparedBoards.get(i);
			GameInformation pending = pendingPredictions.get(board);
			
			pending.fillNeuralNetResult(pair.getKey(), pair.getValue());

			providedPredictions.put(pending.byteCanonicalForm, pending);
		}
		
		pendingPredictions.clear();
	}

	public class GameInformation {

		public ByteCanonicalForm byteCanonicalForm;
		public PolicyVector policy;
		public float boardValue;
		public boolean[] validMoves;
		public boolean gameEnded = false;
		public int[] movesToReachBoard;
		public Game game;
		public int nextPlayerIndex;

		public void fillNeuralNetResult(PolicyVector inPolicy, float inValue) {
			boardValue = inValue;

			if (Options.PRINT_MCTS_FULL_PROBABILITY_VECTOR) {
				inPolicy.printProbabilityVector();
			}

			if (Options.MCTS_PREDICT_VALUE_WITH_SIMULATION) {
				boardValue = game.getSimpleValue(nextPlayerIndex, boardValue);
			}

			inPolicy.maskInvalidMoves(validMoves);

			float sum = inPolicy.sum();
			if (sum <= 0) {
				/*
				 * # if all valid moves were masked make all valid moves equally probable
				 * 
				 * # NB! All valid moves may be masked if either your NNet architecture is
				 * insufficient or you've get overfitting or something else. # If you have got
				 * dozens or hundreds of these messages you should pay attention to your NNet
				 * and/or training process.
				 */
				log.info("All valid moves were masked, do workaround where all actions get equal probability.");
				allMovesMaskedCount++;

				inPolicy.activateAllValidMoves(validMoves);
			}

			if (Options.MCTS_USE_MANUAL_AI) {
				KemetGame kg = (KemetGame) game;

				TrialPlayerAI ai = new TrialPlayerAI(kg.getPlayerByIndex(nextPlayerIndex), kg);
				ai.print = false;
				int actionIndex = ai.pickAction(kg.action.getNextPlayerChoicePick()).getIndex();

				inPolicy.boostActionIndex(actionIndex);
			}

			inPolicy.normalize();

			policy = inPolicy;
			
			if( Options.PRINT_MCTS_FULL_PROBABILITY_VECTOR)

			if (Options.MCTS_VALIDATE_MOVE_FOR_BOARD) {
				movesToReachBoard = game.getActivatedActions();
			}
			
			game = null;
		}
	}
}