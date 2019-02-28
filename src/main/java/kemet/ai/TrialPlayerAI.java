package kemet.ai;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.action.PlayerChoicePick;
import kemet.model.action.choice.Choice;

public class TrialPlayerAI extends PlayerActor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2564094457500391132L;

	// Logger initialization example
	private static final Logger LOGGER = LogManager.getLogger(TrialPlayerAI.class);

	public List<Integer> plannedChoices = new ArrayList<>();
	
	private boolean printChoices = false;

	public TrialPlayerAI(Player player, KemetGame game) {
		super(player, game);
	}

	@Override
	public Choice pickAction(PlayerChoicePick pick) {

		if (!plannedChoices.isEmpty()) {
			return pickFromPlan(pick);
		}

		List<Choice> choiceList = pick.choiceList;
		if (printChoices) {
			printChoiceList(choiceList);
		}

		printPlayerStatus();

		if (choiceList.size() == 1) {
			return choiceList.get(0);
		}

		if (choiceList.size() == 0) {
			LOGGER.warn("no choice supplied in list.");
			try {
				NullPointerException ex = new NullPointerException();
				throw ex;
			} catch (Exception ex) {

			}
		}

		Simulation simulation = new Simulation(player);
		simulation.print = print;
		simulation.pickNextAction(pick);
		simulation.populateBestActionStack(plannedChoices);

		return pickFromPlan(pick);
	}

	private Choice pickFromPlan(PlayerChoicePick pick) {
		Integer plannedIndex = plannedChoices.remove(0);
		return pick.choiceList.get(plannedIndex);
	}

}
