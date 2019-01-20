/*

 */
package kemet.util;

import java.io.Serializable;

/**
 * TrainExample
 * 
 * @author Steve McDuff
 */
public class TrainExample implements Serializable{
	
	public ByteCanonicalForm gameStateS;
	public PolicyVector actionPolicyP;
	public int valueV;
	public int currentPlayer;

	public TrainExample() {
		super();
	}

	public TrainExample(ByteCanonicalForm canonicalBoard, int currentPlayer, PolicyVector actionProbabilityPi,
			int value) {
		
		gameStateS = canonicalBoard;
		this.currentPlayer = currentPlayer;
		actionPolicyP = actionProbabilityPi;
		valueV = value;
		
	}
}
