package kemet.model;

public class Validation {
	
	public static void validationFailed(String reason) {
		System.out.println(reason);
		
		try {
			IllegalArgumentException ex  = new IllegalArgumentException(reason);
			ex.printStackTrace();
			throw ex;
		} catch ( Exception ex ) {
			
		}
		
		System.exit(-1);
		
	}

}
