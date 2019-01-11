package kemet.model;

public enum Color implements Model {
	
	BLACK, RED, WHITE, BLUE, NONE;

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
