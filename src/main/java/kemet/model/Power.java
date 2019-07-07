package kemet.model;

import kemet.model.action.Action;
import kemet.model.action.DoneAction;
import kemet.model.action.choice.ChoiceInventory;

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

	/**
	 * @param player the player on which to apply the power.
	 */
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

	public int getActionIndex() {
		return ChoiceInventory.BUY_POWER + index;
	}

	/**
	 * @param player the player for which to create the next action.
	 * @param game   the current game.
	 */
	public Action createNextAction(Player player, Action parent, KemetGame game) {
		return DoneAction.create(parent);
	}

}
