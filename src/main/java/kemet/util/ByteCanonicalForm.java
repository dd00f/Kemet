package kemet.util;

import java.io.Serializable;

public class ByteCanonicalForm implements Serializable{
	
	private byte[] canonicalForm;
	
	public ByteCanonicalForm( int size ) {
		canonicalForm = new byte[size];
	}
	
	public void set( int index, byte value ) {
		canonicalForm[index] = value;
	}
	
	public byte[] getCanonicalForm() {
		return canonicalForm;
	}

}
