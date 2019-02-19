package kemet.util;

import org.nd4j.linalg.api.ndarray.INDArray;

public class Utilities {

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static INDArray createArray(float[] values) {
		
		return new org.nd4j.linalg.jcublas.JCublasNDArray(values);
		//return new org.nd4j.linalg.cpu.nativecpu.NDArray(values);
		
	}

	public static INDArray createArray(boolean[] validMoves) {
		float[] values = new float[validMoves.length];
		for (int i = 0; i < validMoves.length; i++) {
			boolean b = validMoves[i];
			if( b ) {
				values[i] = 1.0f;
			}
		}
		
		return createArray(values);
	}

	public static int getFirstValidMoveIndex(boolean[] validMoves) {
		for (int i = 0; i < validMoves.length; i++) {
			boolean b = validMoves[i];
			if( b ) {
				return i;
			}
		}
		return -1;
		
	}
	
}
