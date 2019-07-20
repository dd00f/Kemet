package kemet.model;

import kemet.model.action.choice.ChoiceInventory;

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
		return name + ", index " + index + ", cost " + powerCost + ", phase : " + phase + ", description : "
				+ description;
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

	public int getActivateChoiceIndex() {
		return ChoiceInventory.ACTIVATE_DI_CARD + index;
	}
	
	public int getDivineWoundChoiceIndex() {
		return ChoiceInventory.DIVINE_WOUND_DI_CARD + index;
	}
	
	public int getVetoIndex() {
		return BoardInventory.STATE_VETO_DI_CARD + index - DiCardList.TOTAL_BATTLE_DI_CARD_TYPE_COUNT;
	}

	public int getPickChoiceIndex() {
		return ChoiceInventory.PICK_DI_CARD + index;
	}

}
