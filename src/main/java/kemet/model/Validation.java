package kemet.model;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class Validation {
	
	public static void validationFailed(String reason) {
		log.error(reason);
		
		try {
			IllegalArgumentException ex  = new IllegalArgumentException(reason);
			ex.printStackTrace();
			throw ex;
		} catch ( Exception ex ) {
			
		}
		
		System.exit(-1);
		
	}

}
