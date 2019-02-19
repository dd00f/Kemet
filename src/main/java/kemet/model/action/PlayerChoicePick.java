
package kemet.model.action;

import java.util.ArrayList;
import java.util.List;

import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.action.choice.Choice;
import lombok.extern.log4j.Log4j2;

/**
 * PlayerActionPick
 * 
 * @author Steve McDuff
 */
@Log4j2
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
    
    public static void print(List<Choice> choiceList) {
    	
        int count = 1;
        for (kemet.model.action.choice.Choice choice : choiceList)
        {
            log.info("  " + count++ + " " + choice);
        }
    	
    }
}
