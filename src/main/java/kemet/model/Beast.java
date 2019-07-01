package kemet.model;

import kemet.model.action.choice.ChoiceInventory;

public class Beast implements Model {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5081385684176437737L;

	public final String name;
	public final byte combatBonus;
	public final byte shieldBonus;
	public final byte moveBonus;
	public final byte damageBonus;
	public final String description;

	public final String extraPowerDescription;

	public final int index;

	public Beast(String name, int combatBonus, int shieldBonus, int moveBonus, int damageBonus, String extraPowerDescription, int index) {
		this.name = name;
		this.extraPowerDescription = extraPowerDescription;
		this.index = index;
		this.combatBonus = (byte) combatBonus;
		this.shieldBonus = (byte) shieldBonus;
		this.moveBonus = (byte) moveBonus;
		this.damageBonus = (byte) damageBonus;
		
		description = buildDescription();

	}

	public String buildDescription() {
		StringBuilder build = new StringBuilder();
		build.append(name);
		if( combatBonus > 0 ) {
			build.append(" +");
			build.append(combatBonus);
			build.append(" combat");
		}
		if( shieldBonus > 0 ) {
			build.append(" +");
			build.append(shieldBonus);
			build.append(" shield");
		}
		if( moveBonus > 0 ) {
			build.append(" +");
			build.append(moveBonus);
			build.append(" move");
		}
		if( damageBonus > 0 ) {
			build.append(" +");
			build.append(damageBonus);
			build.append(" damage");
		}
		if( extraPowerDescription != null ) {
			build.append(" ");
			build.append(extraPowerDescription);
		}
		
		return build.toString();
	}

	@Override
	public String toString() {
		return description;
	}

	public void copy(Beast copy) {
//		name = copy.name;
//		fightBonus = copy.fightBonus;
//		shieldBonus = copy.shieldBonus;
//		moveBonus = copy.moveBonus;
//		bloodBonus = copy.bloodBonus;
//				
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
//		name = null;
//		fightBonus = 0;
//		shieldBonus = 0;
//		moveBonus = 0;
//		bloodBonus = 0;
	}

	public int getRecruitChoiceIndex() {
		return index + ChoiceInventory.PICK_BEAST;
	}
}
