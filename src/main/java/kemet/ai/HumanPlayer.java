package kemet.ai;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.action.PlayerChoicePick;
import kemet.model.action.choice.Choice;

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
                System.out.println("You entered : " + choice);

                int parseInt = Integer.parseInt(choice);
                if( parseInt > 0 && parseInt <= choiceList.size() ) {

                    retVal = choiceList.get(parseInt-1);
                }
                
                if( retVal == describe ) {
                    describe.activate();
                    retVal = null;
                }
                else if( retVal != null ) {
                    break;
                }
                else {
                    System.out.println("Invalid choice, try again.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Invalid choice, try again.");
            }
        }
        return retVal;
    }

}
