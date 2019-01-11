package kemet.util;

public class ByteCanonicalForm {
	
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
