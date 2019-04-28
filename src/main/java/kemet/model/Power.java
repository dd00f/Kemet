package kemet.model;

public class Power implements Model {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5358086685710717961L;
	
	public String name;
	public byte level = 0;
	public Color color = Color.NONE;
	public String description;

	public int index;
	

	
	public Power(int index, String name, byte level, Color color, String description) {
		this.index = index;
		this.name = name;
		this.level = level;
		this.color = color;
		this.description = description;
	}
	
	public void applyToPlayer(Player player) {
		
		
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

	}
	
	@Override
	public String toString() {
		return index + " : " + color + " : " + level + " : " + name + " : " + description;
	}

}
