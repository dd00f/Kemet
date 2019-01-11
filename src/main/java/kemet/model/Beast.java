package kemet.model;

public class Beast implements Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5081385684176437737L;
	
	public String name;
	public byte fightBonus = 0;
	public byte shieldBonus = 0;
	public byte moveBonus = 0;
	public byte bloodBonus = 0;

	@Override
	public String toString() {
		return name;
	}
	
	public void copy(Beast copy) {
		name = copy.name;
		fightBonus = copy.fightBonus;
		shieldBonus = copy.shieldBonus;
		moveBonus = copy.moveBonus;
		bloodBonus = copy.bloodBonus;
				
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
		name = null;
		fightBonus = 0;
		shieldBonus = 0;
		moveBonus = 0;
		bloodBonus = 0;
	}
}
