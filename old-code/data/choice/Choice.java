package kemet.data.choice;

import kemet.data.state.State;
import kemet.model.Player;

public interface Choice {
	
	public State activate();
	
	public String describe();

}
