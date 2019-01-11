package kemet.model;

public enum Phase implements Model {

	DAWN, DAY, NIGHT, COMBAT;
	
	@Override
	public Model deepCacheClone() {
		return this;
	}

	@Override
	public void release() {
		
	}

	@Override
	public void initialize() {

	}
}
