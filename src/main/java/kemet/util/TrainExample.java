/*

 */
package kemet.util;

import java.io.Serializable;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.factory.Nd4j;

/**
 * TrainExample
 * 
 * @author Steve McDuff
 */
public class TrainExample implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2356489501285949798L;
	public ByteCanonicalForm gameStateS;
	public PolicyVector actionPolicyP;
	public int valueV;
	public int currentPlayer;

	public TrainExample(ByteCanonicalForm canonicalBoard, int currentPlayer, PolicyVector actionProbabilityPi,
			int value) {
		
		gameStateS = canonicalBoard;
		this.currentPlayer = currentPlayer;
		actionPolicyP = actionProbabilityPi;
		valueV = value;
		
	}
	
	public MultiDataSet convertToMultiDataSet() {
		
		
		INDArray[] features = new INDArray[] {gameStateS.getINDArray()};
		INDArray[] labels = new INDArray[] { actionPolicyP.toINDArray(), Nd4j.scalar(valueV) };
		MultiDataSet mds = new org.nd4j.linalg.dataset.MultiDataSet(features, labels);
		
		
		return mds;
	}
}
