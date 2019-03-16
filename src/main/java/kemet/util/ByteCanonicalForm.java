package kemet.util;

import java.io.Serializable;
import java.util.Arrays;

import org.nd4j.linalg.api.ndarray.INDArray;

import lombok.Getter;
import lombok.Setter;

public class ByteCanonicalForm implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7173237257258957668L;
	
	@Getter
	@Setter
	private byte[] canonicalForm;
	
	private int hashCode = 0;
	
	private boolean finalized = false;
	
	public ByteCanonicalForm( int size ) {
		canonicalForm = new byte[size];
	}
	
	public void set( int index, byte value ) {
		if( finalized ) {
			throw new IllegalStateException("ByteCanonicalForm is finalized");
		}
		canonicalForm[index] = value;
	}
	
	@Override
	public void finalize() {
		hashCode = 0;
		finalized = true;
	}
	
	@Override
	public int hashCode() {
		if( hashCode == 0 || ! finalized ) {
			hashCode = Arrays.hashCode(canonicalForm);
		}
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ByteCanonicalForm) {
        	ByteCanonicalForm aString = (ByteCanonicalForm)obj;
            return Arrays.equals(canonicalForm, aString.canonicalForm);
        }
        return false;
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

//	public String toCanonicalString() {
//		String canonicalString = Utilities.bytesToHex(canonicalForm);
//
//		return canonicalString;
//	}

}
