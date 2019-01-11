package kemet.model;

import java.io.Serializable;

public interface Model extends Serializable {
	
	public Model deepCacheClone();
	
	public void release();
	
	public void initialize();


}
