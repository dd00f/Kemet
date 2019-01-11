package kemet.ai;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import kemet.data.choice.Choice;
import kemet.data.choice.DescribeGameChoice;
import kemet.model.Game;
import kemet.model.Player;

public class HumanPlayer extends PlayerActor {

	public HumanPlayer(Player player, Game game) {
		super(player, game);
	}

	@Override
	public Choice pickActionOld(List<Choice> choiceList) {

		Choice retVal = null;
		
		DescribeGameChoice describe = new DescribeGameChoice(game, player);
		choiceList.add(describe);
		
		while (true) {
			printChoiceListOld(choiceList);
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

    @Override
    public kemet.model.action.choice.Choice pickAction(List<kemet.model.action.choice.Choice> choiceList)
    {
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
