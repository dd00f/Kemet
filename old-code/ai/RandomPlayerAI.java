package kemet.ai;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import kemet.data.choice.Choice;
import kemet.model.Game;
import kemet.model.Player;

public class RandomPlayerAI extends PlayerActor {

	public static final Logger LOGGER = Logger.getLogger(RandomPlayerAI.class.getName());

	private Random r = new Random();

	public RandomPlayerAI(Player player, Game game) {
		super( player, game );
		
	}

	@Override
	public Choice pickActionOld(List<Choice> choiceList) {

		if (true) {
			printChoiceListOld(choiceList);
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

    @Override
    public kemet.model.action.choice.Choice pickAction(List<kemet.model.action.choice.Choice> choiceList)
    {
        if (true) {
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
