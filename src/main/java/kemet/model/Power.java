package kemet.model;

public class Power implements Model {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5358086685710717961L;
	
	public String name;
	public short level = 0;
	public Color color = Color.NONE;
	public Player owner = null;
	
	public void applyToPlayer() {
		
		
	}
	
	@Override
	public Model deepCacheClone() {
		return this;
	}

	@Override
	public void release() {
		
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

}
