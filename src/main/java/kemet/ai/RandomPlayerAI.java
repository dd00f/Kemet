package kemet.ai;

import java.util.List;
import java.util.Random;

import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.action.PlayerChoicePick;
import kemet.model.action.choice.Choice;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RandomPlayerAI extends PlayerActor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9176413876584129621L;


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
            log.warn("no choice supplied in list.");
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
