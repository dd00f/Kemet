package kemet.ai;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.action.PlayerChoicePick;
import kemet.model.action.choice.Choice;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class HumanPlayer extends PlayerActor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1504986093888501442L;


	public HumanPlayer(Player player, KemetGame game) {
		super(player, game);
	}


    @Override
    public Choice pickAction(PlayerChoicePick pick)
    {
    	List<Choice> choiceList = pick.choiceList;
        kemet.model.action.choice.Choice retVal = null;
        
        kemet.model.action.choice.DescribeGameChoice describe = new kemet.model.action.choice.DescribeGameChoice(game, player);
        choiceList.add(describe);
        
        while (true) {
            printChoiceList(choiceList);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String choice = null;
            try {
                choice = reader.readLine();
                log.info("You entered : " + choice);

                int parseInt = Integer.parseInt(choice);
                if( parseInt > 0 && parseInt <= choiceList.size() ) {

                    retVal = choiceList.get(parseInt-1);
                }
                
                if( retVal == describe ) {
                    game.activateAction(describe);
                    retVal = null;
                }
                else if( retVal != null ) {
                    break;
                }
                else {
                    log.error("Invalid choice, try again.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Invalid choice, try again.");
            }
        }
        return retVal;
    }

}
