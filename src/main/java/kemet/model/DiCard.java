package kemet.model;

public class DiCard implements Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4361788643720119972L;

	public final Phase phase;
	public final byte powerCost;
	public final String name;
	public final String description;
	public final int index;

	public DiCard(String name, byte cost, Phase phase, String description, int index) {
		this.name = name;
		this.powerCost = cost;
		this.phase = phase;
		this.description = description;
		this.index = index;
	}

	@Override
	public String toString() {
		return "Divine Intervention Card " + name + ", index " + index + ", cost " + powerCost + ", phase : " + phase
				+ ", description : " + description;
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

}
