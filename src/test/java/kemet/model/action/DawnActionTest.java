package kemet.model.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kemet.data.TwoPlayerGame;
import kemet.model.KemetGame;
import kemet.model.Player;

public class DawnActionTest {

	TwoPlayerGame tpg = new TwoPlayerGame();

	private DawnAction action;
	private KemetGame game;
	private Player redPlayer;
	private Player bluePlayer;
	private Player greenPlayer;
	private Player yellowPlayer;

	@BeforeEach
	void setupGame() {
		tpg = new TwoPlayerGame();
		tpg.createAIPlayer("red");
		tpg.createAIPlayer("blue");
		tpg.createAIPlayer("green");
		tpg.createAIPlayer("yellow");
		tpg.createTiles();

		game = tpg.game;

		redPlayer = game.playerByInitiativeList.get(0);
		redPlayer.initiativeTokens = 5;
		bluePlayer = game.playerByInitiativeList.get(1);
		bluePlayer.initiativeTokens = 5;
		greenPlayer = game.playerByInitiativeList.get(2);
		greenPlayer.initiativeTokens = 5;
		yellowPlayer = game.playerByInitiativeList.get(3);
		yellowPlayer.initiativeTokens = 5;

		game.action.chainedActions.clear();

		action = DawnAction.create(game, game.action.chainedActions);

		game.action.chainedActions.add(action);
	}

	@Test
	void testDawnBattle() {
		PlayerChoicePick nextPlayerChoicePick = null;

		pickDefaultDawnOptions(1);

		// pick order
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, redPlayer);
		assertEquals(4, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(0));

		// pick order
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, bluePlayer);
		assertEquals(3, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(0));

		// pick order
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, greenPlayer);
		assertEquals(2, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(0));
		assertNull(action.getNextPlayerChoicePick());

		assertSame(redPlayer, game.playerByInitiativeList.get(0));
		assertSame(bluePlayer, game.playerByInitiativeList.get(1));
		assertSame(greenPlayer, game.playerByInitiativeList.get(2));
		assertSame(yellowPlayer, game.playerByInitiativeList.get(3));
	}

	@Test
	void testDawnBattleReverseOrder() {
		PlayerChoicePick nextPlayerChoicePick = null;

		pickDefaultDawnOptions(1);

		// pick order
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, redPlayer);
		assertEquals(4, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(3));

		// pick order
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, bluePlayer);
		assertEquals(3, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(2));

		// pick order
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, greenPlayer);
		assertEquals(2, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(1));
		assertNull(action.getNextPlayerChoicePick());

		assertSame(yellowPlayer, game.playerByInitiativeList.get(0));
		assertSame(greenPlayer, game.playerByInitiativeList.get(1));
		assertSame(bluePlayer, game.playerByInitiativeList.get(2));
		assertSame(redPlayer, game.playerByInitiativeList.get(3));
	}

	@Test
	void testDawnBattleScatteredOrder() {
		PlayerChoicePick nextPlayerChoicePick = null;

		pickDefaultDawnOptions(1);

		// pick order
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, redPlayer);
		assertEquals(4, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(3));

		// pick order
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, bluePlayer);
		assertEquals(3, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(0));

		// pick order
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, greenPlayer);
		assertEquals(2, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(1));
		assertNull(action.getNextPlayerChoicePick());

		assertSame(bluePlayer, game.playerByInitiativeList.get(0));
		assertSame(yellowPlayer, game.playerByInitiativeList.get(1));
		assertSame(greenPlayer, game.playerByInitiativeList.get(2));
		assertSame(redPlayer, game.playerByInitiativeList.get(3));
	}
	
	
	@Test
	void testDawnBattleGreenFirst() {
		PlayerChoicePick nextPlayerChoicePick = null;

		pickDefaultDawnOptions(2);

		// pick order
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, greenPlayer);
		assertEquals(4, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(0));

		// pick order
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, redPlayer);
		assertEquals(3, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(0));

		// pick order
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, bluePlayer);
		assertEquals(2, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(0));
		assertNull(action.getNextPlayerChoicePick());

		assertSame(greenPlayer, game.playerByInitiativeList.get(0));
		assertSame(redPlayer, game.playerByInitiativeList.get(1));
		assertSame(bluePlayer, game.playerByInitiativeList.get(2));
		assertSame(yellowPlayer, game.playerByInitiativeList.get(3));
	}
	
	@Test
	void testDawnBattleGreenLast() {
		PlayerChoicePick nextPlayerChoicePick = null;

		pickDefaultDawnOptions(0);

		// pick order
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, redPlayer);
		assertEquals(4, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(0));

		// pick order
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, bluePlayer);
		assertEquals(3, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(0));

		// pick order
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, yellowPlayer);
		assertEquals(2, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(0));
		assertNull(action.getNextPlayerChoicePick());

		assertSame(redPlayer, game.playerByInitiativeList.get(0));
		assertSame(bluePlayer, game.playerByInitiativeList.get(1));
		assertSame(yellowPlayer, game.playerByInitiativeList.get(2));
		assertSame(greenPlayer, game.playerByInitiativeList.get(3));
	}

	private void pickDefaultDawnOptions(int greenDawnTokenCount) {
		PlayerChoicePick nextPlayerChoicePick = null;

		// pick battle card
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, yellowPlayer);
		assertEquals(8, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(0));

		// pick discard
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, yellowPlayer);
		assertEquals(7, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(0));
		assertEquals(6, yellowPlayer.availableBattleCards.size());

		// pick dawn token
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, yellowPlayer);
		assertEquals(6, nextPlayerChoicePick.choiceList.size());
		assertEquals(5, yellowPlayer.initiativeTokens);
		game.activateAction(nextPlayerChoicePick.choiceList.get(1));
		assertEquals(4, yellowPlayer.initiativeTokens);

		// pick battle card
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, greenPlayer);
		assertEquals(8, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(0));

		// pick discard
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, greenPlayer);
		assertEquals(7, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(0));
		assertEquals(6, greenPlayer.availableBattleCards.size());

		// pick dawn token
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, greenPlayer);
		assertEquals(6, nextPlayerChoicePick.choiceList.size());
		assertEquals(5, greenPlayer.initiativeTokens);
		game.activateAction(nextPlayerChoicePick.choiceList.get(greenDawnTokenCount));
		assertEquals(5 - greenDawnTokenCount, greenPlayer.initiativeTokens);

		// pick battle card
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, bluePlayer);
		assertEquals(8, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(0));

		// pick discard
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, bluePlayer);
		assertEquals(7, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(0));
		assertEquals(6, bluePlayer.availableBattleCards.size());

		// pick dawn token
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, bluePlayer);
		assertEquals(6, nextPlayerChoicePick.choiceList.size());
		assertEquals(5, bluePlayer.initiativeTokens);
		game.activateAction(nextPlayerChoicePick.choiceList.get(1));
		assertEquals(4, bluePlayer.initiativeTokens);

		// pick battle card
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, redPlayer);
		assertEquals(8, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(0));

		// pick discard
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, redPlayer);
		assertEquals(7, nextPlayerChoicePick.choiceList.size());
		game.activateAction(nextPlayerChoicePick.choiceList.get(0));
		assertEquals(6, redPlayer.availableBattleCards.size());

		// pick dawn token
		nextPlayerChoicePick = game.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, redPlayer);
		assertEquals(6, nextPlayerChoicePick.choiceList.size());
		assertEquals(5, redPlayer.initiativeTokens);
		game.activateAction(nextPlayerChoicePick.choiceList.get(1));
		assertEquals(4, redPlayer.initiativeTokens);
	}

}
