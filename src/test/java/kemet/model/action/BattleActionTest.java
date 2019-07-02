package kemet.model.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import kemet.data.TwoPlayerGame;
import kemet.model.Army;
import kemet.model.BattleCard;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Tile;
import kemet.model.action.choice.Choice;

public class BattleActionTest {

//	private static final int CLONE_COUNT = 1000000;
	TwoPlayerGame tpg = new TwoPlayerGame();
	private Army redArmy;
	private Army blueArmy;
	private BattleAction battle;
	private KemetGame game;
	private Player redPlayer;
	private Player bluePlayer;

	@BeforeEach
	void setupGame() {
		tpg = new TwoPlayerGame();
		tpg.createAIPlayer("red");
		tpg.createAIPlayer("blue");
		tpg.createTiles();

		game = tpg.game;

		redPlayer = game.playerByInitiativeList.get(0);
		redArmy = redPlayer.createArmy();

		redArmy.recruit((byte) 5);

		bluePlayer = game.playerByInitiativeList.get(1);
		blueArmy = bluePlayer.createArmy();

		blueArmy.recruit((byte) 5);

		game.action.chainedActions.clear();

		PlayerActionTokenPick tokenPick = PlayerActionTokenPick.create(game, bluePlayer, game.action.chainedActions);
		game.action.chainedActions.add(tokenPick);
		PlayerActionTokenPick tokenPick2 = PlayerActionTokenPick.create(game, redPlayer, game.action.chainedActions);
		game.action.chainedActions.add(tokenPick2);

		ArmyMoveAction armyMoveAction = ArmyMoveAction.create(game, bluePlayer, tokenPick);
		tokenPick.nextAction = armyMoveAction;
		battle = BattleAction.create(game, armyMoveAction);
		armyMoveAction.overridingAction = battle;

		Tile battleTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		redArmy.moveToTile(battleTile);
		battle.tile = battleTile;
		battle.defendingArmy = redArmy;
		battle.attackingArmy = blueArmy;

	}

	@Test
	public void testDeepCloning() {

		game.validate();

		for (int i = 0; i < 10000; ++i) {
			KemetGame deepCacheClone = game.deepCacheClone();
			deepCacheClone.validate();
			deepCacheClone.release();
		}

	}

	@Test
	void testAttackerWinsAttackerDestroyed() {

		battle.attackingUsedBattleCard = bluePlayer.getBattleCard(BattleCard.SACRIFICIAL_CHARGE);
		battle.defendingUsedBattleCard = redPlayer.getBattleCard(BattleCard.CHARIOT_RAID);
		battle.attackingDiscardBattleCard = bluePlayer.getBattleCard(BattleCard.CHARIOT_RAID);
		battle.defendingDiscardBattleCard = redPlayer.getBattleCard(BattleCard.SACRIFICIAL_CHARGE);

		assertSame(battle.tile.getArmy(), redArmy);

		battle.resolveBattle();

		assertSame(battle.tile.getArmy(), redArmy);

		assertEquals(1, redPlayer.templeOccupationPoints);
		assertEquals(0, bluePlayer.templeOccupationPoints);
		assertEquals(0, bluePlayer.battlePoints);

		assertEquals(true, battle.attackerWins);
		assertEquals(true, battle.attackerDestroyed);
		assertEquals(false, battle.defenderDestroyed);
		assertEquals(false, battle.defenderRetreatPicked);
		assertEquals(true, battle.defenderRetreatTilePicked);
		assertEquals(true, battle.attackerRetreatPicked);
		assertEquals(true, battle.attackerRetreatTilePicked);

		PlayerChoicePick nextPlayerChoicePick = battle.getNextPlayerChoicePick();
		assertSame(nextPlayerChoicePick.player, redPlayer);
		Choice choice = nextPlayerChoicePick.choiceList.get(0);
		assertEquals(true, choice instanceof BattleAction.RecallArmyChoice);

		battle.defenderRecall = false;
		battle.defenderRetreatPicked = true;

		nextPlayerChoicePick = battle.getNextPlayerChoicePick();
		assertEquals(null, nextPlayerChoicePick);

	}

//	@Test
//	void testTempleIslandBattleAiResponse() {
//
//		BattleCard attackCard = bluePlayer.getBattleCard(BattleCard.SACRIFICIAL_CHARGE);
//		battle.attackingUsedBattleCard = attackCard;
//		BattleCard discardCard = bluePlayer.getBattleCard(BattleCard.CHARIOT_RAID);
//		battle.attackingDiscardBattleCard = discardCard;
//		bluePlayer.useBattleCard(attackCard);
//		bluePlayer.useBattleCard(discardCard);
//
//		TrialPlayerAI redDefendingAi = new TrialPlayerAI(redPlayer, game);
//		TrialPlayerAI blueAttackingAi = new TrialPlayerAI(bluePlayer, game);
//
//		Choice pickAction = redDefendingAi.pickAction(battle.getNextPlayerChoicePick());
//		BattleAction.PickBattleCardChoice cardChoice = (PickBattleCardChoice) pickAction;
//		assertEquals(BattleCard.CAVALRY_BLITZ, cardChoice.card.name);
//
//		// prove the choice of attacking cards doesn't impact the AI choices.
//		battle.attackingUsedBattleCard = discardCard;
//		battle.attackingDiscardBattleCard = attackCard;
//
//		Choice pickAction2 = redDefendingAi.pickAction(battle.getNextPlayerChoicePick());
//		BattleAction.PickBattleCardChoice cardChoice2 = (PickBattleCardChoice) pickAction2;
//		assertEquals(BattleCard.CAVALRY_BLITZ, cardChoice2.card.name);
//
//		battle.attackingUsedBattleCard = null;
//		battle.attackingDiscardBattleCard = null;
//
//		Choice pickAction3 = blueAttackingAi.pickAction(battle.getNextPlayerChoicePick());
//		BattleAction.PickBattleCardChoice cardChoice3 = (PickBattleCardChoice) pickAction3;
//		assertEquals(BattleCard.CAVALRY_BLITZ, cardChoice3.card.name);
//
//	}

//	@Test
//	void testTempleIslandBattleAiResponseFactorInHiddenCards() {
//
//		BattleCard attackCard = bluePlayer.getBattleCard(BattleCard.SACRIFICIAL_CHARGE);
//		battle.attackingUsedBattleCard = attackCard;
//		BattleCard discardCard = bluePlayer.getBattleCard(BattleCard.CHARIOT_RAID);
//		battle.attackingDiscardBattleCard = discardCard;
//		bluePlayer.useBattleCard(attackCard);
//		bluePlayer.discardBattleCard(discardCard);
//
//		TrialPlayerAI redDefendingAi = new TrialPlayerAI(redPlayer, game);
//		// TrialPlayerAI blueAttackingAi = new TrialPlayerAI(bluePlayer, game);
//
//		Choice pickAction = redDefendingAi.pickAction(battle.getNextPlayerChoicePick());
//		BattleAction.PickBattleCardChoice cardChoice = (PickBattleCardChoice) pickAction;
//		assertEquals(BattleCard.CAVALRY_BLITZ, cardChoice.card.name);
//
//	}

//	List<Beast> cache = new ArrayList<>();
//
//	public Beast fetchFromCache() {
//		if (cache.size() == 0) {
//			return new Beast();
//		}
//		return cache.remove(cache.size() - 1);
//	}
//
//	public void releaseToCache(Beast beast) {
//		cache.add(beast);
//	}
//
//	@Test
//	void speedTestSerializationUtilClone() {
//
//		Beast originalBeast = new Beast();
//		originalBeast.bloodBonus = 3;
//		originalBeast.fightBonus = 4;
//		originalBeast.moveBonus = 5;
//		originalBeast.name = "lasdjfkljasd flkj";
//		originalBeast.shieldBonus = 6;
//
//		int copyCount = 1000000;
//
//		for (int i = 0; i < copyCount; i++) {
//			SerializationUtils.clone(originalBeast);
//		}
//
//	}

//	@Test
//	void speedTestCache() {
//
//		Beast originalBeast = new Beast();
//		originalBeast.bloodBonus = 3;
//		originalBeast.fightBonus = 4;
//		originalBeast.moveBonus = 5;
//		originalBeast.name = "lasdjfkljasd flkj";
//		originalBeast.shieldBonus = 6;
//
//		int copyCount = CLONE_COUNT;
//
//		for (int i = 0; i < copyCount; i++) {
//			Beast fetchFromCache = fetchFromCache();
//			fetchFromCache.copy(originalBeast);
//			releaseToCache(fetchFromCache);
//		}
//	}

//	@Test
//	void testtest() throws InterruptedException {
//		
//		String format = String.format("Explored %1$,d choices in %2$02dh%3$02dm%4$02ds , recent speed : %5$,d choice per second. Best Outcome : %6$s",
//				 100000000000l , 1, 2 , 3, 123123 , "[1,2,3]");
//		System.out.println(format);
//		
//		Thread.sleep(1000);
//	}

}
