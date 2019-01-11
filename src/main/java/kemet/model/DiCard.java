package kemet.model;

public class DiCard implements Model {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4361788643720119972L;
	
	public Phase phase = Phase.DAWN;
	public short powerCost = 0;
	
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
