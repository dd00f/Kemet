package kemet.data.state;

import kemet.model.Game;

public class NightState implements State {

	public Game game;

	@Override
	public State triggerNextAction() {

		game.resetAvailableActions();

		game.provideNightPrayerPoints();

		game.provideNightTempleVictoryPoints();

		game.provideNightTemplePrayerPoints();

		game.provideNightDiCards();

		game.activateNightPowers();

		return null;
	}



}
