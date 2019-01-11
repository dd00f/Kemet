package kemet.ai;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.action.PlayerChoicePick;
import kemet.model.action.choice.Choice;

public class RandomPlayerAI extends PlayerActor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9176413876584129621L;

	public static final Logger LOGGER = Logger.getLogger(RandomPlayerAI.class.getName());

	private Random r = new Random();
	
	public boolean printChoices = true;

	public RandomPlayerAI(Player player, KemetGame game) {
		super( player, game );
		
	}

    @Override
    public Choice pickAction(PlayerChoicePick pick)
    {
    	List<Choice> choiceList = pick.choiceList;
    	
        if (printChoices) {
            printChoiceList(choiceList);
        }

        if (choiceList.size() == 1) {
            return choiceList.get(0);
        }

        if (choiceList.size() == 0) {
            LOGGER.warning("no choice supplied in list.");
            try {
                NullPointerException ex = new NullPointerException();
                throw ex;
            } catch (Exception ex) {

            }
        }

        int low = 0;
        int high = choiceList.size();
        int result = r.nextInt(high - low) + low;

        return choiceList.get(result);
    }


}
