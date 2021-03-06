package kemet.model.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;

import kemet.Options;
import kemet.data.TwoPlayerGame;
import kemet.model.BattleCard;
import kemet.model.Beast;
import kemet.model.Color;
import kemet.model.DiCard;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Power;
import kemet.model.Tile;
import kemet.model.action.choice.ChoiceInventory;
import kemet.util.ByteCanonicalForm;
import kemet.util.CopyableRandom;
import kemet.util.StackingMCTS;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TwoPlayerGameTest {

	TwoPlayerGame tpg = new TwoPlayerGame();

	private KemetGame game;
	public KemetGame reversedGame;
	public Player redPlayer;
	public Player bluePlayer;

	@BeforeEach
	void setupGame() {

		tpg = new TwoPlayerGame();
		tpg.createAIPlayer("red");
		tpg.createAIPlayer("blue");
		tpg.createTiles();

		game = tpg.game;
		game.setInitialSeed(CopyableRandom.generateSeed(12341234));

		// game.setPrintActivations(false);

		redPlayer = game.playerByInitiativeList.get(0);
		bluePlayer = game.playerByInitiativeList.get(1);

		reversedGame = game.deepCacheClone();
		Player reversedRedPlayer = reversedGame.playerByInitiativeList.get(0);
		Player reversedBluePlayer = reversedGame.playerByInitiativeList.get(1);
		reversedGame.playerByInitiativeList.clear();
		reversedGame.playerByInitiativeList.add(reversedBluePlayer);
		reversedGame.playerByInitiativeList.add(reversedRedPlayer);

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

		replayGameUntilInitialState();

//		Choice Index List
//		PASS_CHOICE_INDEX 0
//		ARMY_SIZE_CHOICE 1
//		PICK_TILE_CHOICE 9
//		PICK_COLOR_CHOICE 22
//		PICK_PYRAMID_LEVEL_CHOICE 26
//		PICK_BATTLE_CARD_CHOICE 30
//		RECALL_CHOICE 40
//		PICK_ROW_ONE_MOVE 41
//		PICK_ROW_ONE_RECRUIT 42
//		PICK_ROW_TWO_MOVE 43
//		PICK_ROW_TWO_UPGRADE_PYRAMID 44
//		PICK_ROW_TWO_PRAY 45
//		PICK_ROW_THREE_PRAY 46
//		PICK_ROW_THREE_BUILD_WHITE 47
//		PICK_ROW_THREE_BUILD_RED 48
//		PICK_ROW_THREE_BUILD_BLUE 49
//		PICK_ROW_THREE_BUILD_BLACK 50
//		DONE_PICKING 51
//		PICK_DAWN_TOKEN 52
//		PICK_PLAYER_ORDER 66
//		PASS_RECRUIT_CHOICE_INDEX 64
//		PASS_RECALL_CHOICE_INDEX 65
//		BUY_POWER 66
//		LAST_INDEX 106

	}

	private void replayGameUntilInitialState() {
//		game.replayMultipleActions(new int[] { 27, 23, 22, 27, 25, 24, 5, 5, 5, 5, 44, 14, 29, 44, 14, 29, 46, 46, 45,
//				45, 41, 14, 11, 5, 0, 41, 14, 13, 5, 43, 11, 0, 43, 0, 37, 36, 37, 36, 66, 45, 45, 46, 46, 44, 15, 29,
//				44, 15, 29, 41, 0, 41, 0, 42, 155, 42, 155, 34, 35, 34, 35, 66 });
//		

		// initialization
		pickPyramidLevel(2);
		pickPyramidColor(Color.RED);
		pickPyramidColor(Color.BLACK);
		pickPyramidLevel(2);
		pickPyramidColor(Color.BLUE);
		pickPyramidColor(Color.WHITE);
		recruitArmySize(5);
		recruitArmySize(5);
		recruitArmySize(5);
		recruitArmySize(5);

		// turn one
		upgradePyramid(4, redPlayer.cityTiles.get(0));
		upgradePyramid(4, bluePlayer.cityTiles.get(0));
		prayRowThree();
		prayRowThree();
		prayRowTwo();
		prayRowTwo();

		startRowOneMove();
		moveFirstTile(redPlayer.cityTiles.get(0), game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE), 5);
		endMove();

		startRowOneMove();
		moveFirstTile(bluePlayer.cityTiles.get(0), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 5);

		moveRowTwoZeroArmy();
		moveRowTwoZeroArmy();

		activateTemple(true);

		// turn two
		battlePick(BattleCard.FERVENT_PURGE_CARD, BattleCard.MIXED_TACTICS_CARD, BattleCard.FERVENT_PURGE_CARD,
				BattleCard.MIXED_TACTICS_CARD);
		pickPlayerOrder(1);
		prayRowThree();
		prayRowThree();

		endDiCardSelection();

		prayRowTwo();
		prayRowTwo();
		endDiCardSelection();

		upgradePyramid(4, redPlayer.cityTiles.get(1));
		upgradePyramid(4, bluePlayer.cityTiles.get(1));

		endDiCardSelection();
		moveRowOneZeroArmy();
		moveRowOneZeroArmy();

		endDiCardSelection();
		moveRowTwoZeroArmy();
		moveRowTwoZeroArmy();
		endDiCardSelection();

		activateTemple(true);

		// turn three
		battlePick(BattleCard.CHARIOT_RAID_CARD, BattleCard.SHIELD_PUSH_CARD, BattleCard.CHARIOT_RAID_CARD,
				BattleCard.SHIELD_PUSH_CARD);
		pickPlayerOrder(1);

		resetDiCards();

	}

	public void giveDiCardToPlayer(DiCard diCard, Player player) {
		game.giveDiCardToPlayer(diCard, player);
		reversedGame.giveDiCardToPlayer(diCard, getReversedPlayer(player));
	}

	public Player getReversedPlayer(Player player) {
		Player reversedPlayerZero = reversedGame.playerByInitiativeList.get(0);
		if (reversedPlayerZero.getIndex() == player.getIndex()) {
			return reversedGame.playerByInitiativeList.get(1);
		}
		return reversedPlayerZero;
	}

	public void resetDiCards() {
		game.resetDiCards();
		reversedGame.resetDiCards();
	}

	private void endDiCardSelection() {
		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.STOP_PICKING_DI_CARDS);
	}

	public void activateTemple(boolean activated) {
		if (activated) {
			activateActionOnGame(game.getNextPlayer(), ChoiceInventory.ACTIVATE_OPTIONAL_TEMPLE);
		} else {
			activateActionOnGame(game.getNextPlayer(), ChoiceInventory.DONT_ACTIVATE_OPTIONAL_TEMPLE);
		}
	}

	public void prayRowThree() {
		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.PICK_ROW_THREE_PRAY);
		game.getNextPlayerChoicePick();
	}

	public void prayRowTwo() {
		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.PICK_ROW_TWO_PRAY);
		game.getNextPlayerChoicePick();
	}

	public void prayGold() {
		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.PICK_GOLD_PRAY);
		game.getNextPlayerChoicePick();
	}

	public void buyPowerTile(Power powerToBuy) {
		startBuyPowerTile(powerToBuy);
		endBuyPowerTile(powerToBuy);
	}

	public void endBuyPowerTile(Power powerToBuy) {
		activateActionOnGame(game.getNextPlayer(), powerToBuy.getActionIndex());
	}

	public void startBuyPowerTile(Power powerToBuy) {
		int buyPowerActionIndex = PlayerActionTokenPick.getBuyPowerActionIndex(powerToBuy.color);
		activateActionOnGame(game.getNextPlayer(), buyPowerActionIndex);
	}

	public void upgradePyramid(int level, Color color, Tile tile) {
		startUpgradePyramid();
		moveSelectTile(tile);
		pickPyramidLevel(level);
		pickPyramidColor(color);
	}

	public void pickPlayerOrder(int order) {
		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.PICK_PLAYER_ORDER + order - 1);
	}

	public void upgradePyramid(int level, Tile tile) {
		startUpgradePyramid();
		moveSelectTile(tile);
		pickPyramidLevel(level);
	}

	public void startUpgradePyramid() {
		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.PICK_ROW_TWO_UPGRADE_PYRAMID);
	}

	public void endUpgradePyramid() {
		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.UPGRADE_NOTHING);
	}

	public void pickPyramidColor(Color color) {
		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.PICK_COLOR_CHOICE + color.ordinal());
	}

	public void pickPyramidLevel(int level) {
		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.PICK_PYRAMID_LEVEL_CHOICE + level - 1);
	}

	public void startRecruit() {
		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.PICK_ROW_ONE_RECRUIT);
	}

	public void endMove() {
		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.ZERO_ARMY_SIZE_CHOICE_INDEX);
	}

	public void endRecruit() {
		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.END_RECRUIT);
	}

	public void moveRowOneZeroArmy() {
		startRowOneMove();
		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.ZERO_ARMY_SIZE_CHOICE_INDEX);
	}

	public void startRowOneMove() {
		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.PICK_ROW_ONE_MOVE);
	}

	public void moveRowTwoZeroArmy() {
		startRowTwoMove();
		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.ZERO_ARMY_SIZE_CHOICE_INDEX);
	}

	public void startRowTwoMove() {
		int nextPlayer = game.getNextPlayer();
		int pickRowTwoMove = ChoiceInventory.PICK_ROW_TWO_MOVE;
		activateActionOnGame(nextPlayer, pickRowTwoMove);
	}

	public static boolean CLONE_GAME_ON_ALL_MOVES = true;

	public static boolean CANNONICAL_ON_ALL_MOVES = true;

	public static boolean COMPARE_REVERSED_GAME = true;

	@SuppressWarnings("null")
	public void activateActionOnGame(int nextPlayer, int pickIndex) {
		KemetGame deepCacheClone = null;

		game.resetCachedChoices();
		PlayerChoicePick nextPlayerChoicePick = game.getNextPlayerChoicePick();
		ByteCanonicalForm originalCanonicalForm = game.getCanonicalForm(nextPlayer);

		if (COMPARE_REVERSED_GAME) {
			int reversedNextPlayer = reversedGame.getNextPlayer();
			assertTrue(reversedNextPlayer != nextPlayer, "Current player index must be reversed.");
			ByteCanonicalForm reversedCanonicalForm = reversedGame.getCanonicalForm(reversedNextPlayer);
			validateCanonicalFormEquals(reversedCanonicalForm, originalCanonicalForm);

			reversedGame.validate();
			reversedGame.activateAction(reversedNextPlayer, pickIndex);
		}

		int size = nextPlayerChoicePick.choiceList.size();
		if (size <= 1) {
			fail("getNextPlayerChoicePick has size of " + size + "\n" + nextPlayerChoicePick);
		}

		game.validate();
		int choiceCount = size;
		if (CLONE_GAME_ON_ALL_MOVES) {

			deepCacheClone = game.deepCacheClone();
			deepCacheClone.validate();
			PlayerChoicePick cloneNextPick = deepCacheClone.getNextPlayerChoicePick();
			int newCount = cloneNextPick.choiceList.size();

			if (CANNONICAL_ON_ALL_MOVES) {
				validateCanonicalFormEquals(game.getCanonicalForm(nextPlayer),
						deepCacheClone.getCanonicalForm(nextPlayer));
			}

			deepCacheClone.activateAction(nextPlayer, pickIndex);
			deepCacheClone.validate();
			if (CANNONICAL_ON_ALL_MOVES) {
				validateCanonicalForm(deepCacheClone.getCanonicalForm(nextPlayer));
			}
			if (newCount != choiceCount) {

				log.info("Original Game");
				PlayerChoicePick.logChoiceList(nextPlayerChoicePick.choiceList);
				log.info("Cloned Game");
				PlayerChoicePick.logChoiceList(cloneNextPick.choiceList);
				System.out.println("Game choice");

				System.out.println(nextPlayerChoicePick);
				System.out.println("Clone choice");
				System.out.println(cloneNextPick);

				fail("previous count " + choiceCount + " doesnt match new count " + newCount);
			}

			deepCacheClone = game.deepCacheClone();
			newCount = cloneNextPick.choiceList.size();
			if (newCount != choiceCount) {
				log.info("Original Game");
				PlayerChoicePick.logChoiceList(nextPlayerChoicePick.choiceList);

				log.info("Cloned Game");
				PlayerChoicePick.logChoiceList(cloneNextPick.choiceList);

				fail("previous count " + choiceCount + " doesnt match new count " + newCount);
			}

			if (CANNONICAL_ON_ALL_MOVES) {
				validateCanonicalForm(deepCacheClone.getCanonicalForm(nextPlayer));
			}
		}

		if (CANNONICAL_ON_ALL_MOVES) {
			validateCanonicalForm(game.getCanonicalForm(nextPlayer));

			ByteCanonicalForm postCloneMoveCanonicalForm = game.getCanonicalForm(nextPlayer);
			validateCanonicalFormEquals(originalCanonicalForm, postCloneMoveCanonicalForm);

		}
		game.activateAction(nextPlayer, pickIndex);

		PlayerChoicePick nextPlayerChoicePick2 = game.getNextPlayerChoicePick();
		ByteCanonicalForm newForm1 = game.getCanonicalForm(game.getNextPlayer());
		game.resetCachedChoices();
		PlayerChoicePick nextPlayerChoicePick3 = game.getNextPlayerChoicePick();
		ByteCanonicalForm newForm2 = game.getCanonicalForm(game.getNextPlayer());

		assertEquals(nextPlayerChoicePick2.player.getIndex(), nextPlayerChoicePick3.player.getIndex());
		assertEquals(nextPlayerChoicePick2.choiceList.size(), nextPlayerChoicePick3.choiceList.size());
		assertTrue(newForm1.equals(newForm2));

		game.validate();

		if (CANNONICAL_ON_ALL_MOVES) {
			validateCanonicalForm(game.getCanonicalForm(game.getNextPlayer()));
		}

		if (CLONE_GAME_ON_ALL_MOVES) {
			deepCacheClone.validate();
			deepCacheClone.activateAction(nextPlayer, pickIndex);
			deepCacheClone.validate();
		}
	}

	private void validateCanonicalFormEquals(ByteCanonicalForm canonicalForm, ByteCanonicalForm canonicalForm2) {
		// TODO Auto-generated method stub
		byte[] canonicalForm3 = canonicalForm.getCanonicalForm();
		byte[] canonicalForm4 = canonicalForm2.getCanonicalForm();
		StringBuilder build = new StringBuilder();
		boolean failed = false;
		for (int i = 0; i < canonicalForm3.length; i++) {
			byte b1 = canonicalForm3[i];
			byte b2 = canonicalForm4[i];
			if (b1 != b2) {
				build.append("Clone has different canonical value at index : " + i + ", original : " + b1 + ", clone : " + b2 + "\n");
				failed = true;
			}
		}
		
		if( failed ) {
			
			fail( build.toString());
		}

	}

	private void validateCanonicalForm(ByteCanonicalForm canonicalForm) {
		float[] floatCanonicalForm = canonicalForm.getFloatCanonicalForm();
		for (int i = 0; i < floatCanonicalForm.length; i++) {
			float f = floatCanonicalForm[i];

			if (Options.CANONICAL_FLATTEN_TO_ONE) {
				if (f > 1) {
					fail("Float canonical value bigger than 1 : " + f + " at index " + i);
				}
			}

			// TODO enable once all negative states are eliminated.
//			if( f < 0 ) {
//				fail("Float canonical value smaller than zero : " + f + " at index " + i);
//			}

			if (f < -1) {
				fail("Float canonical value smaller than -1 : " + f + " at index " + i);
			}

			if (f < 0) {
				fail("Float canonical value smaller than 0 : " + f + " at index " + i);
			}
		}
	}

	public void moveRowTwoArmy(Tile from, Tile to, int size) {
		moveRowTwoArmy(from, to, size, false);
	}

	public void moveFirstTile(Tile from, Tile to, int size) {
		moveFirstTile(from, to, size, false);
	}

	public void moveRowOneArmy(Tile from, Tile to, int size) {
		moveRowOneArmy(from, to, size, false);
	}

	public void moveRowTwoArmy(Tile from, Tile to, int size, boolean moveBeast) {
		startRowTwoMove();
		moveSelectTile(from);
		moveSelectTile(to);
		moveArmySize(size, moveBeast);
	}

	public void moveSelectTile(Tile to) {
		activateActionOnGame(game.getNextPlayer(), to.getPickChoiceIndex(game.getNextPlayer()));
	}

	public void moveArmySize(int size, boolean moveBeast) {
		activateActionOnGame(game.getNextPlayer(), getMoveArmySizeWithBeastChoiceIndex(size, moveBeast));
	}

	public void moveFirstTile(Tile from, Tile to, int size, boolean moveBeast) {
		moveSelectTile(from);
		moveSelectTile(to);
		moveArmySize(size, moveBeast);
	}

	public void moveRowOneArmy(Tile from, Tile to, int size, boolean moveBeast) {
		startRowOneMove();
		moveSelectTile(from);
		moveSelectTile(to);
		moveArmySize(size, moveBeast);
	}

	public void skipRecruitBeast() {
		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.KEEP_BEAST);
	}

	public void recruitBeastToTile(Tile to) {
		moveSelectTile(to);
	}

	public void rainingFireToTile(Tile to) {
		moveSelectTile(to);
	}

	public void recruitBeast(Beast beast) {
		activateActionOnGame(game.getNextPlayer(), beast.getRecruitChoiceIndex());
	}

	public void moveNextTile(Tile to, int size) {
		moveNextTile(to, size, false);
	}

	public void moveEscapeTile(Tile to) {
		activateActionOnGame(game.getNextPlayer(), to.getEscapeChoiceIndex(game.getNextPlayer()));
	}

	public void moveNextTile(Tile to, int size, boolean moveBeast) {
		moveSelectTile(to);
		moveArmySize(size, moveBeast);
	}

	private int getMoveArmySizeWithBeastChoiceIndex(int size, boolean moveBeast) {
		if (size == 0) {
			if (moveBeast) {
				return ChoiceInventory.ONLY_BEAST_MOVE;
			}
			return ChoiceInventory.ZERO_ARMY_SIZE_CHOICE_INDEX;
		}

		if (moveBeast) {
			return ChoiceInventory.ARMY_SIZE_WITH_BEAST_CHOICE + size - 1;
		}
		return ChoiceInventory.ARMY_SIZE_CHOICE + size - 1;
	}

	public void battlePick(BattleCard attackCard, BattleCard attackDiscard, BattleCard defenseCard,
			BattleCard defenseDiscard) {
		pickBattleCard(attackCard);
		pickBattleCard(attackDiscard);
		pickBattleCard(defenseCard);
		pickBattleCard(defenseDiscard);

	}

	public void useDiCardOnDivineWound(DiCard diCard) {
		activateActionOnGame(game.getNextPlayer(), diCard.getDivineWoundChoiceIndex());
	}

	public void activateDiCard(DiCard diCard) {
		activateActionOnGame(game.getNextPlayer(), diCard.getActivateChoiceIndex());
	}

	public void pickDiCard(DiCard diCard) {
		activateActionOnGame(game.getNextPlayer(), diCard.getPickChoiceIndex());
	}

	public void endDivineWound() {
		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.END_DIVINE_WOUND);
	}

	public void skipVeto() {
		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.SKIP_DI_VETO);
	}

	public void pickBattleCard(BattleCard battleCard) {
		activateActionOnGame(game.getNextPlayer(), battleCard.getPickChoiceIndex());
	}

	public void recruitArmy(Tile tile, int size) {
		moveSelectTile(tile);
		recruitArmySize(size);
	}

	public void recruitArmySize(int size) {
		activateActionOnGame(game.getNextPlayer(), getMoveArmySizeWithBeastChoiceIndex(size, false));
	}

	public void startRecruitGold() {

		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.PICK_GOLD_RECRUIT);

	}

	public void startMoveGold() {

		activateActionOnGame(game.getNextPlayer(), ChoiceInventory.PICK_GOLD_MOVE);

	}

	public void replayMultipleActions(int[] actions) {
		for (int i = 0; i < actions.length; i++) {
			int actionIndex = actions[i];

			activateActionOnGame(game.getNextPlayer(), actionIndex);
		}

	}

	public byte getCanonicalValue(int canonicalIndex) {
		// game.resetCachedChoices();
		int nextPlayer = game.getNextPlayer();
		ByteCanonicalForm canonicalForm = game.getCanonicalForm(nextPlayer);
		byte[] canonicalForm2 = canonicalForm.getCanonicalForm();
		byte returnValue = canonicalForm2[canonicalIndex];
		return returnValue;
	}

	public PlayerChoicePick getNextPlayerChoicePick() {
		return game.getNextPlayerChoicePick();
	}

	public int getNextPlayer() {
		return game.getNextPlayer();
	}

	public Tile getTileByName(String tileName) {

		return game.getTileByName(tileName);
	}

	public void movePowerToPlayer(Player player, Power power) {

		game.movePowerToPlayer(player, power);
		reversedGame.movePowerToPlayer(getReversedPlayer(player), power);

	}

	public void enterSimulationMode(int playerIndex, StackingMCTS mcts, long newSeed) {

		game.enterSimulationMode(playerIndex, mcts, newSeed);
		reversedGame.enterSimulationMode(getReversedPlayer(playerIndex), mcts, newSeed);
	}

	public int getReversedPlayer(int playerIndex) {
		if (playerIndex == 0) {
			return 1;
		}
		return 0;
	}

	public void resetCachedChoices() {
		game.resetCachedChoices();
	}

	public byte[] getDiscardedDiCardList() {
		return game.discardedDiCardList;
	}

	public byte[] getVisionDiCardList() {
		return game.visionDiCardList;
	}

	public byte[] getAvailableDiCardList() {
		return game.availableDiCardList;
	}

	public void recuperateAllBattleCards() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		reversedGame.playerByInitiativeList.get(0).recuperateAllBattleCards();
		reversedGame.playerByInitiativeList.get(1).recuperateAllBattleCards();
	}

	public void endTurnSkipDiCard() {
		activateActionOnGame(getNextPlayer(), ChoiceInventory.STOP_PICKING_DI_CARDS);

	}
}
