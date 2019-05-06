package kemet.model.action;

import org.junit.jupiter.api.BeforeEach;

import kemet.data.TwoPlayerGame;
import kemet.model.BattleCard;
import kemet.model.Color;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Power;
import kemet.model.Tile;
import kemet.model.action.choice.ChoiceInventory;

public class TwoPlayerGameTest {

	TwoPlayerGame tpg = new TwoPlayerGame();

	public KemetGame game;
	public Player redPlayer;
	public Player bluePlayer;

	@BeforeEach
	void setupGame() {
		tpg = new TwoPlayerGame();
		tpg.createAIPlayer("red");
		tpg.createAIPlayer("blue");
		tpg.createTiles();

		game = tpg.game;
		//game.setPrintActivations(false);

		redPlayer = game.playerByInitiativeList.get(0);
		bluePlayer = game.playerByInitiativeList.get(1);

		// initialize a game so that
		// red has level 4 pyramid red & black
		// blue has level 4 pyramid blue & white

		// Player : red
		// 7 prayer points.
		// 3 victory points.
		// 1 temple occupation points.
		// 0 temple permanent points.
		// 0 battle points.
		// 2 high level pyramid occupation points.
		// 0 initiative tokens.
		// Used Battle Cards : 7 4
		// Discard Battle Cards : 6 5
		// Available Battle Cards : 0 1 2 3
		// Army : red army 1 of size 5 on tile : Medium Temple with temple of 3 bonus
		// prayer points.
		// Army : red army 2 of size 5 on tile : red district 2 with pyramid BLACK of
		// level 4
		// Player : blue
		// 9 prayer points.
		// 3 victory points.
		// 1 temple occupation points.
		// 0 temple permanent points.
		// 0 battle points.
		// 2 high level pyramid occupation points.
		// 0 initiative tokens.
		// Used Battle Cards : 7 4
		// Discard Battle Cards : 6 5
		// Available Battle Cards : 0 1 2 3
		// Army : blue army 1 of size 3 on tile : Island Temple with temple of 5 bonus
		// prayer points.
		// Army : blue army 2 of size 5 on tile : blue district 2 with pyramid WHITE of
		// level 4

		game.replayMultipleActions(new int[] { 27, 23, 22, 27, 25, 24, 5, 5, 5, 5, 42, 14, 29, 42, 14, 29, 44, 44, 43,
				43, 39, 14, 11, 5, 0, 39, 14, 13, 5, 41, 11, 0, 41, 0, 37, 36, 37, 36, 60, 43, 43, 44, 44, 42, 15, 29,
				42, 15, 29, 39, 0, 39, 0, 40, 0, 40, 0, 34, 35, 34, 35, 60 });

	}

	public void prayRowThree() {
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PICK_ROW_THREE_PRAY);
	}

	public void buyPowerTile(Power white1Priest1) {
		game.activateAction(game.getNextPlayer(), white1Priest1.getActionIndex());
	}

	public void upgradePyramid(int level, Color black, Tile tile) {
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PICK_ROW_TWO_UPGRADE_PYRAMID);
		game.activateAction(game.getNextPlayer(), tile.getPickChoiceIndex(game.getNextPlayer()));
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PICK_PYRAMID_LEVEL_CHOICE + level - 1);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PICK_COLOR_CHOICE + black.ordinal());
	}

	public void startRecruit() {
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PICK_ROW_ONE_RECRUIT);
	}
	
	public void endRecruit() {
		game.activateAction(game.getNextPlayer(), ChoiceInventory.ZERO_ARMY_SIZE_CHOICE_INDEX);
	}

	public void moveRowOneZeroArmy() {
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PICK_ROW_ONE_MOVE);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.ZERO_ARMY_SIZE_CHOICE_INDEX);
	}

	public void moveRowTwoZeroArmy() {
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PICK_ROW_TWO_MOVE);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.ZERO_ARMY_SIZE_CHOICE_INDEX);
	}

	public void moveRowTwoArmy(Tile from, Tile to, int size) {
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PICK_ROW_TWO_MOVE);
		game.activateAction(game.getNextPlayer(), from.getPickChoiceIndex(game.getNextPlayer()));
		game.activateAction(game.getNextPlayer(), to.getPickChoiceIndex(game.getNextPlayer()));
		game.activateAction(game.getNextPlayer(), ChoiceInventory.ARMY_SIZE_CHOICE + size - 1);
	}

	public void moveRowOneArmy(Tile from, Tile to, int size) {
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PICK_ROW_ONE_MOVE);
		game.activateAction(game.getNextPlayer(), from.getPickChoiceIndex(game.getNextPlayer()));
		game.activateAction(game.getNextPlayer(), to.getPickChoiceIndex(game.getNextPlayer()));
		game.activateAction(game.getNextPlayer(), ChoiceInventory.ARMY_SIZE_CHOICE + size - 1);
	}

	public void moveNextTile(Tile to, int size) {
		game.activateAction(game.getNextPlayer(), to.getPickChoiceIndex(game.getNextPlayer()));
		game.activateAction(game.getNextPlayer(), ChoiceInventory.ARMY_SIZE_CHOICE + size - 1);
	}


	public void battlePick(BattleCard attackCard, BattleCard attackDiscard, BattleCard defenseCard,
			BattleCard defenseDiscard) {
		game.activateAction(game.getNextPlayer(), attackCard.getPickChoiceIndex());
		game.activateAction(game.getNextPlayer(), attackDiscard.getPickChoiceIndex());
		game.activateAction(game.getNextPlayer(), defenseCard.getPickChoiceIndex());
		game.activateAction(game.getNextPlayer(), defenseDiscard.getPickChoiceIndex());

	}

	
	
	
	public void recruitArmy(Tile tile, int size) {
		game.activateAction(game.getNextPlayer(), tile.getPickChoiceIndex(game.getNextPlayer()));
		game.activateAction(game.getNextPlayer(), ChoiceInventory.ARMY_SIZE_CHOICE + size -1);
	}

}
