package kemet.util;

import java.io.Serializable;

import org.nd4j.linalg.api.ndarray.INDArray;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class ByteCanonicalForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7173237257258957668L;
	
	@Getter
	@Setter
	private byte[] canonicalForm;
	
	public ByteCanonicalForm( int size ) {
		canonicalForm = new byte[size];
	}
	
	public void set( int index, byte value ) {
		canonicalForm[index] = value;
	}
	
//	public byte[] getCanonicalForm() {
//		return canonicalForm;
//	}

	public float[] getFloatCanonicalForm() {
		
		byte[] canonicalForm = getCanonicalForm();
		
		float[] values = new float[canonicalForm.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = canonicalForm[i];
		}
		
		return values;
		
	}
	
	
	public INDArray getINDArray() {
		INDArray array = Utilities.createArray(getFloatCanonicalForm());
		return array;
		
	}

}
