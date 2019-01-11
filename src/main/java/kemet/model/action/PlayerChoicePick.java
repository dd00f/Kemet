
package kemet.model.action;

import java.util.ArrayList;
import java.util.List;

import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.action.choice.Choice;

/**
 * PlayerActionPick
 * 
 * @author Steve McDuff
 */
public class PlayerChoicePick
{

    public Player player;
    public KemetGame game;
    public List<Choice> choiceList = new ArrayList<>();
	public Action action;
    
    public PlayerChoicePick(KemetGame game, Player player, Action action) {
		this.game = game;
		this.player = player;
		this.action = action;
    	
    }
    
    public PlayerChoicePick validate() {
        
        if( choiceList.size() == 0 ) {
            
            return null;
        }
        return this;
    }
}
