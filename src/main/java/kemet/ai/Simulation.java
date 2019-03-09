package kemet.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.SerializationUtils;

import kemet.Options;
import kemet.model.Army;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Tile;
import kemet.model.action.Action;
import kemet.model.action.BattleAction;
import kemet.model.action.BattleAction.PickBattleCardChoice;
import kemet.model.action.PlayerActionTokenPick;
import kemet.model.action.PlayerChoicePick;
import kemet.model.action.choice.Choice;
import kemet.model.action.choice.EndTurnChoice;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Simulation {

	private static final int WIN_SCORE = 1000000;

	private AtomicLong exploredChoiceCount = new AtomicLong(0);

	private int maxSimulationDepth = Options.SIMULATION_MAX_SIMULATION_DEPTH;

	private Player player;

	private KemetGame originalGame;

	private int[] bestActionStack;
	private float bestScore = 0;

	public Simulation(Player player) {
		this.player = player;
		originalGame = player.game;
		bestActionStack = createActionStack();

	}

	public void populateBestActionStack(List<Integer> list) {
		for (int i = 0; i < bestActionStack.length; i++) {
			int value = bestActionStack[i];
			if (value == -1) {
				return;
			}
			list.add(value);
		}
	}

	private int[] createActionStack() {
		int[] retVal = new int[maxSimulationDepth];
		Arrays.fill(retVal, -1);
		return retVal;
	}

	public class MultiThreadCounter {

		private int counter = 0;

		public void incrementChoiceCount() {
			counter++;

			if (counter % 1000 == 0) {
				incrementSharedCounter();
			}
		}

		private void incrementSharedCounter() {
			while (true) {
				long previousValue = exploredChoiceCount.get();
				long newValue = previousValue + counter;
				if (exploredChoiceCount.compareAndSet(previousValue, newValue)) {

					if (newValue >= nextPrintCount) {
						nextPrintCount += Options.SIMULATION_CHOICE_COUNT_PRINT_INTERVAL;
						printExploredCount();
					}
					counter = 0;

					return;
				}
			}
		}
	}
	
	private long nextPrintCount = Options.SIMULATION_CHOICE_COUNT_PRINT_INTERVAL;

	public class SimulationStep {

		private KemetGame gameClone;
		private final int choiceToPick;
		private final int depth;
		private final int depthLeft;
		private final String prefix;
		private final boolean stackActions;
		private boolean keepStackActions;
		private final int[] currentActionStack;
		private final MultiThreadCounter counter;
		private float calculateGameScore;

		public SimulationStep(KemetGame gameToClone, int choiceToPick, int depth, int depthLeft, String previousPrefix,
				boolean stackActions, int[] actions, MultiThreadCounter counter) {
			this.counter = counter;
			this.choiceToPick = choiceToPick;
			this.depth = depth;
			this.depthLeft = depthLeft;

			String prefixConnector = " ";
			if (!stackActions) {
				prefixConnector = "_";
			}

			this.prefix = previousPrefix + prefixConnector + choiceToPick;
			this.stackActions = stackActions;
			this.currentActionStack = actions;
			if (Options.SIMULATION_USE_COPY_OVER_STREAMING) {
				gameClone = gameToClone.deepCacheClone();

				if (Options.SIMULATION_VALIDATE_GAME_AFTER_CLONE) {
					gameClone.validate();
				}
			} else {
				gameClone = SerializationUtils.clone(gameToClone);
			}
			keepStackActions = stackActions;
		}

		public float simulateChoice() {

			if (stackActions) {
				currentActionStack[depth - 1] = choiceToPick;
			}

			gameClone.printActivations = false;
			PlayerChoicePick currentPick = gameClone.action.getNextPlayerChoicePick();
			if (currentPick != null) {

				Choice choice = currentPick.choiceList.get(choiceToPick);

				checkToHideAttackerBattleCard(depth, currentPick);

				gameClone.activateAction(choice);

				counter.incrementChoiceCount();

				if (depthLeft > 1) {
					PlayerChoicePick nextPick = gameClone.action.getNextPlayerChoicePick();
					if (simulationContinues(nextPick)) {

						float simulateNextChoice = simulateNextChoice(nextPick);
						clearSimulation();
						return simulateNextChoice;
					}
				}
			}

			calculateGameScore = calculateGameScore(gameClone);

			// simulation ended, check outcome
			if (stackActions) {
				setBestChoice(calculateGameScore, currentActionStack);
			}

			if (stackActions) {
				currentActionStack[depth - 1] = -1;
			}

			clearSimulation();

			return calculateGameScore;
		}

		private void clearSimulation() {
			// clear the simulation
			if (Options.SIMULATION_USE_COPY_OVER_STREAMING) {
				gameClone.release();
			}
			gameClone = null;
		}

		private float simulateNextChoice(PlayerChoicePick nextPick) {
			if (isChoiceSkipped(nextPick)) {
				int choiceIndex = getMostSimpleChoice(nextPick);
				keepStackActions = false;
				SimulationStep step = new SimulationStep(gameClone, choiceIndex, depth + 1, depthLeft - 1, prefix,
						keepStackActions, currentActionStack, counter);

				return step.simulateChoice();
			}

			boolean isPickOpponent = isPickOpponent(nextPick);
			if (isPickOpponent) {
				keepStackActions = false;
			}
			boolean isOpponentPickingBattleCard = isOpponentPickingBattleCard(nextPick);
			float currentScore = 0;
			if (isPickOpponent && !isOpponentPickingBattleCard) {
				currentScore = WIN_SCORE;
			}

			if (isOpponentPickingBattleCard) {
				nextPick.player.recoverAllDiscardedBattleCards();
				nextPick = gameClone.action.getNextPlayerChoicePick();
			}

			int choiceCount = nextPick.choiceList.size();
			for (int i = 0; i < choiceCount; i++) {
				String newPrefix = printChoiceEntry(depthLeft, prefix, nextPick, i);

				SimulationStep step = new SimulationStep(gameClone, i, depth + 1, depthLeft - 1, prefix,
						keepStackActions, currentActionStack, counter);

				float newScore = step.simulateChoice();

				currentScore = adjustScore(isPickOpponent, isOpponentPickingBattleCard, currentScore, newScore);

				printChoiceExit(nextPick, i, newPrefix, newScore);
			}

			if (isOpponentPickingBattleCard) {
				// average things out based on their possible choices
				currentScore /= choiceCount;
			}

			// choices beyond this point are randomized.
			if (stackActions && !keepStackActions) {
				setBestChoice(currentScore, currentActionStack);
			}
			return currentScore;
		}

		public void closeCounter() {
			counter.incrementSharedCounter();
		}
	}

	private void setBestChoice(float calculateGameScore, int[] newActionStack) {
		if (calculateGameScore > bestScore) {
			setBestChoiceSync(  calculateGameScore, newActionStack);
		}
	}
	
	private synchronized void setBestChoiceSync(float calculateGameScore, int[] newActionStack) {
		if (calculateGameScore > bestScore) {
			for (int i = 0; i < newActionStack.length; i++) {
				bestActionStack[i] = newActionStack[i];
			}
			bestScore = calculateGameScore;
		}
	}

	private int getMostSimpleChoice(PlayerChoicePick nextPick) {
		List<Choice> choiceList = nextPick.choiceList;
		for (int i = 0; i < choiceList.size(); i++) {
			Choice choice = choiceList.get(i);
			if (choice instanceof EndTurnChoice) {
				return i;
			}
		}
		return 0;
	}

	public int pickNextAction(PlayerChoicePick pick) {

		startTimeNano = System.nanoTime();
		lastPrintTime = startTimeNano;

		List<Choice> choiceList = pick.choiceList;

		if (choiceList.size() == 1) {
			return 0;
		}

		if (choiceList.size() == 0) {
			log.warn("no choice supplied in list.");
			try {
				NullPointerException ex = new NullPointerException();
				throw ex;
			} catch (Exception ex) {

			}
		}

		int bestChoiceIndex = 0;
		float currentScore = 0;

		actionTokenPick = getActionTokenPick(pick);

		// TODO when choices are close to each other, randomize action

		int depthLeft = maxSimulationDepth;

		List<SimulationStep> simulationList = new ArrayList<>();

		for (int i = 0; i < choiceList.size(); i++) {
			String newPrefix = printChoiceEntry(depthLeft, " ", pick, i);

			int[] simulationActionStack = createActionStack();
			SimulationStep step = new SimulationStep(originalGame, i, 1, depthLeft, "  ", true, simulationActionStack,
					new MultiThreadCounter());

			simulationList.add(step);

			if (!Options.SIMULATION_MULTI_THREAD) {

				float newScore = step.simulateChoice();

				step.closeCounter();

				if (newScore > currentScore) {
					bestChoiceIndex = i;
					currentScore = newScore;
				}

				printChoiceExit(pick, i, newPrefix, newScore);
			}
		}

		if (Options.SIMULATION_MULTI_THREAD) {
			simulationList.parallelStream().forEach(m -> {
				m.simulateChoice();
				m.closeCounter();
			});
			
			for (SimulationStep simulationStep : simulationList) {
				if (simulationStep.calculateGameScore > currentScore) {
					bestChoiceIndex = simulationStep.choiceToPick;
					currentScore = simulationStep.calculateGameScore;
				}
			}
		}

		printExploredCount();
		exploredChoiceCount.set(0);
		actionTokenPick = null;

		return bestChoiceIndex;
	}
	
	private long lastPrintCount = 0;
	
	private long lastPrintTime = 0;

	private void printExploredCount() {
		
		if( ! print ) {
			return;
		}
		
		long nanoTime = System.nanoTime();
		long durationMs = (nanoTime - startTimeNano) / 1000000;
		long recentDurationMs = (nanoTime - lastPrintTime) / 1000000;
		lastPrintTime = nanoTime;
		if (durationMs == 0) {
			durationMs = 1;
		}
		if (recentDurationMs == 0) {
			recentDurationMs = 1;
		}
		long totalChoiceCount = exploredChoiceCount.get();
		long recentChoiceCount = totalChoiceCount - lastPrintCount;
		lastPrintCount = totalChoiceCount;
		long choicePerSecond = 1000 * recentChoiceCount / recentDurationMs;

		int seconds = (int) (durationMs / 1000) % 60;
		int minutes = (int) ((durationMs / (1000 * 60)) % 60);
		int hours = (int) ((durationMs / (1000 * 60 * 60)) % 24);

		String bestOutcome = printBestOutcomeToString();

		String format = String.format("Explored %1$,d choices in %2$02dh%3$02dm%4$02ds , recent speed : %5$,d choice per second. Best Outcome : %6$s",
				totalChoiceCount , hours, minutes , seconds, choicePerSecond , bestOutcome);
		
		log.info( format );
		//print("Explored " + exploredChoiceCount + " choices in " + hours + "h" + minutes + "m" + seconds
		///			+ "s , recent speed : " + choicePerSecond + " choice per second. Best Outcome : " + bestOutcome);
	}

	private String printBestOutcomeToString() {
		ArrayList<Integer> bestStack = new ArrayList<>();
		populateBestActionStack(bestStack);
		return bestStack.toString();
	}

	private float adjustScore(boolean isPickOpponent, boolean isOpponentPickingBattleCard, float currentScore,
			float newScore) {
		if (isPickOpponent) {
			if (isOpponentPickingBattleCard) {
				// during battles, use an average of the score of each battle card the opponent
				// may pick
				currentScore += newScore;
			} else if (newScore < currentScore) {
				// opponents pick the best choice for them
				currentScore = newScore;
			}
		}

		else {
			if (newScore > currentScore) {
				currentScore = newScore;
			}
		}
		return currentScore;
	}

	private boolean isOpponentPickingBattleCard(PlayerChoicePick currentPick) {
		Choice choice = currentPick.choiceList.get(0);
		if (choice instanceof BattleAction.PickBattleCardChoice && isPickOpponent(currentPick)) {
			return true;
		}
		return false;
	}

	private void checkToHideAttackerBattleCard(int depth, PlayerChoicePick currentPick) {
		if (depth == 1) {
			Choice choice = currentPick.choiceList.get(0);
			if (choice instanceof BattleAction.PickBattleCardChoice) {
				BattleAction.PickBattleCardChoice pickCard = (PickBattleCardChoice) choice;
				if (!pickCard.isDiscard && !pickCard.isAttacker && pickCard.player.name.equals(player.name)) {
					// hide the attacker battle card
					BattleAction battle = (BattleAction) currentPick.action;
					battle.attackingArmy.owningPlayer.returnUsedBattleCard(battle.attackingUsedBattleCard);
					battle.attackingUsedBattleCard = null;
				}
			}
		}
	}

	private boolean isPickOpponent(PlayerChoicePick nextPick) {
		return !nextPick.player.name.equals(player.name);
	}

	private boolean isChoiceSkipped(PlayerChoicePick nextPick) {
		if (nextPick.choiceList.get(0) instanceof PickBattleCardChoice) {
			PickBattleCardChoice choice = (PickBattleCardChoice) nextPick.choiceList.get(0);
			return choice.isDiscard;
		}
		return false;
	}

	private void printChoiceExit(PlayerChoicePick nextPick, int i, String newPrefix, float newScore) {
		if (!Options.SIMULATION_PRINT_STEP_EXIT) {
			return;
		}

		StringBuilder build = new StringBuilder();
		build.append(newPrefix);
		build.append(" score ");
		build.append(newScore);
		build.append(" : ");
		build.append(nextPick.choiceList.get(i));
		log.info(build.toString());
	}


	@SuppressWarnings("unused")
	private String printChoiceEntry(int depthLeft, String prefix, PlayerChoicePick nextPick, int i) {
		String newPrefix = null;

		if (Options.SIMULATION_PRINT_STEP_ENTRY || Options.SIMULATION_PRINT_STEP_EXIT) {

			newPrefix = prefix + " " + i;
			if (Options.SIMULATION_PRINT_STEP_ENTRY && depthLeft > 1) {

				StringBuilder build = new StringBuilder();
				build.append(newPrefix);
				build.append(" : ");
				build.append(nextPick.choiceList.get(i));
				log.info(build.toString());
			}
		}
		return newPrefix;
	}

	private boolean simulationContinues(PlayerChoicePick nextPick) {

		if (nextPick == null || nextPick.game.roundNumber != originalGame.roundNumber) {
			// round number changed, stop the simulation.
			return false;
		}
		
		if( Options.SIMULATION_END_AFTER_BATTLE) {
			if (nextPick.game.battleCount != originalGame.battleCount) {
				return false;
			}
		}
			
		if (nextPick != null) {
			if (actionTokenPick == null) {
				if (nextPick.player.isSamePlayer(player)) {
					return true;
				}
			} else {
				if (isPartOfActionTokenPick(nextPick)) {
					return true;
				}
			}
		}

		if (Options.SIMULATE_FULL_TURN) {
			return true;
		}

		return false;
	}

	private boolean isPartOfActionTokenPick(PlayerChoicePick nextPick) {
		if (actionTokenPick == null) {
			return false;
		}

		PlayerActionTokenPick currentAction = getActionTokenPick(nextPick);
		return currentAction.getPlayer().name.equals(actionTokenPick.getPlayer().name);
	}

	private PlayerActionTokenPick getActionTokenPick(PlayerChoicePick nextPick) {
		Action action = nextPick.action;
		while (action != null) {
			if (action instanceof PlayerActionTokenPick) {
				PlayerActionTokenPick pick = (PlayerActionTokenPick) action;
				return pick;
			}
			action = action.getParent();
		}
		return null;
	}

	public static String[] indent = new String[100];
	private PlayerActionTokenPick actionTokenPick;

	private long startTimeNano;

	public boolean print;

	static {
		indent[0] = "  ";
		for (int i = 1; i < 100; ++i) {
			indent[i] = indent[i - 1] + " ";
		}
	}

	public static String getIndent(int depth) {
		return indent[depth];
	}

	private float calculateGameScore(KemetGame clone) {

		// if winner, return max score
		if (clone.winner != null && clone.winner.isSamePlayer(player)) {
			return WIN_SCORE;
		}

		Player playerByName = clone.getPlayerByName(player.name);
		float playerScore = calculatePlayerScore(playerByName);

		playerScore = adjustScoreAgainstOpponents(clone, playerByName, playerScore);

		return playerScore;
	}

	private float adjustScoreAgainstOpponents(KemetGame clone, Player playerByName, float playerScore) {
		for (Player opponent : clone.playerByInitiativeList) {
			if (!opponent.name.equals(player.name)) {
				float opponentScore = calculatePlayerScore(playerByName);
				if (opponentScore > playerScore) {
					// opponent outscores us
					// reduce the current player score proportionally
					playerScore = playerScore * (playerScore / opponentScore);
				}
			}
		}
		return playerScore;
	}

	public static float calculatePlayerScore(Player playerToScore) {
		float retVal = 0;
		// VP
		retVal += playerToScore.victoryPoints * 1000;

		// BATTLE VP
		retVal += playerToScore.battlePoints * 100;

		// number of temples ( bonus for two )

		int templeCount = 0;

		// strength of army on board
		for (Army army : playerToScore.armyList) {
			retVal += army.armySize * army.armySize * army.armySize;

			if (army.tile != null) {
				if (army.tile.hasTemple) {
					retVal += 10 * army.tile.templeBonusPrayer * army.armySize;
					retVal += 30 * army.tile.templeBonusPoints * army.armySize;

				}
				if (army.tile.templePaireable) {
					templeCount++;
				}

				// occupy somebody else's pyramid
				// occupy ANY pyramid
				// if (army.tile.owningPlayer != playerByName)
				{
					retVal += army.tile.getPyramidLevel() * army.armySize;
				}
			}
		}

		// bonus to occupy two temples
		if (templeCount >= 2) {
			retVal += 200;
		}

		// value of pyramids
		for (Tile tile : playerToScore.cityTiles) {
			if (tile.getArmy() != null && tile.getArmy().owningPlayer != playerToScore) {
				// tile owned by enemy
			} else {
				retVal += tile.getPyramidLevel() * tile.getPyramidLevel();
			}
		}

		// prayer points
		retVal += playerToScore.getPrayerPoints() * 2;

		// dawn tokens
		retVal += playerToScore.initiativeTokens;

		return retVal;
	}

}
