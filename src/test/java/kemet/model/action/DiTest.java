package kemet.model.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import kemet.data.TwoPlayerGame;
import kemet.model.BattleCard;
import kemet.model.BeastList;
import kemet.model.DiCardList;
import kemet.model.KemetGame;
import kemet.model.PowerList;
import kemet.model.Tile;
import kemet.model.action.choice.ChoiceInventory;

public class DiTest extends TwoPlayerGameTest {

	@Test
	public void WAR_RAGE() {

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.WAR_RAGE, redPlayer);

		Tile from = redPlayer.cityTiles.get(1);
		Tile tileByName = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(from, tileByName, 3);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		activateDiCard(DiCardList.WAR_RAGE);
		// identical strength, WAR_RAGE should give +1
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		assertEquals(5, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		// 3 recall - 1 damage = 2 prayer bonus
		assertEquals(11, bluePlayer.getPrayerPoints());
		assertEquals(0, redPlayer.getDiCardCount());
	}

	@Test
	public void WAR_FURY() {

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.WAR_FURY, redPlayer);

		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 2);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		activateDiCard(DiCardList.WAR_FURY);
		// red : 2 army + 2 DI + 4 card = 8
		// blue : 3 army + 0 DI + 4 card = 7
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		// 1 power cost
		assertEquals(5, redPlayer.victoryPoints);
		assertEquals(4, redPlayer.getPrayerPoints());
		// 3 recall - 1 damage = 2 prayer bonus
		assertEquals(11, bluePlayer.getPrayerPoints());
		assertEquals(0, redPlayer.getDiCardCount());
	}

	@Test
	public void BLOOD_BATTLE() {

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.BLOOD_BATTLE, redPlayer);

		moveRowTwoArmy(redPlayer.cityTiles.get(1), game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE), 4);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		activateDiCard(DiCardList.BLOOD_BATTLE);
		// red : 4 army + 0 DI + 4 card = 8
		// blue : 3 army + 0 DI + 4 card = 7
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		// 0 power cost
		assertEquals(5, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		// 3 recall - 2 damage = 1 prayer bonus
		assertEquals(10, bluePlayer.getPrayerPoints());
		assertEquals(0, redPlayer.getDiCardCount());
	}

	@Test
	public void BLOOD_BATH() {

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.BLOOD_BATH, redPlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 4);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		activateDiCard(DiCardList.BLOOD_BATH);
		// red : 4 army + 0 DI + 4 card = 8
		// blue : 3 army + 0 DI + 4 card = 7
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		// 1 power cost
		assertEquals(5, redPlayer.victoryPoints);
		assertEquals(4, redPlayer.getPrayerPoints());
		// 3 recall - 3 damage = 0 prayer bonus
		assertEquals(9, bluePlayer.getPrayerPoints());

		assertEquals(3, islandTile.getArmy().armySize);
		assertEquals(0, redPlayer.getDiCardCount());
	}

	@Test
	public void BRONZE_WALL() {

		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.BRONZE_WALL, redPlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 4);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		activateDiCard(DiCardList.BRONZE_WALL);
		// red : 4 army + 0 DI + 4 card = 8
		// blue : 3 army + 0 DI + 4 card = 7
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CHARIOT_RAID_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		// 0 power cost
		assertEquals(5, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		// 3 recall - 1 damage = 2 prayer bonus
		assertEquals(11, bluePlayer.getPrayerPoints());

		// 4 army - 3 damage - 1
		assertEquals(2, islandTile.getArmy().armySize);
		assertEquals(0, redPlayer.getDiCardCount());
	}

	@Test
	public void IRON_WALL() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.IRON_WALL, redPlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 4);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		activateDiCard(DiCardList.IRON_WALL);
		// red : 4 army + 0 DI + 4 card = 8
		// blue : 3 army + 0 DI + 4 card = 7
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CHARIOT_RAID_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);

		moveRowOneZeroArmy();
		// 0 power cost
		assertEquals(5, redPlayer.victoryPoints);
		assertEquals(4, redPlayer.getPrayerPoints());
		// 3 recall - 1 damage = 2 prayer bonus
		assertEquals(11, bluePlayer.getPrayerPoints());

		// 4 army - 3 damage - 2
		assertEquals(3, islandTile.getArmy().armySize);

		assertEquals(0, redPlayer.getDiCardCount());

	}

	@Test
	public void DIVINE_PROTECTION_attack_win() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.DIVINE_PROTECTION, redPlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 4);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		activateDiCard(DiCardList.DIVINE_PROTECTION);
		// red : 4 army + 0 DI + 4 card = 8
		// blue : 3 army + 0 DI + 4 card = 7
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CHARIOT_RAID_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		// 0 power cost
		assertEquals(5, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		// 3 recall - 1 damage = 2 prayer bonus
		assertEquals(11, bluePlayer.getPrayerPoints());

		// 4 army - zero damage
		assertEquals(4, islandTile.getArmy().armySize);

		assertEquals(0, redPlayer.getDiCardCount());

	}

	@Test
	public void DIVINE_PROTECTION_attack_lose() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.DIVINE_PROTECTION, redPlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 3);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		activateDiCard(DiCardList.DIVINE_PROTECTION);
		// red : 3 army + 0 DI + 4 card = 7
		// blue : 3 army + 0 DI + 4 card = 7
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		// 0 power cost
		assertEquals(3, redPlayer.victoryPoints);

		// recall 3 - 1 damage
		assertEquals(7, redPlayer.getPrayerPoints());
		// 3 recall - 1 damage = 2 prayer bonus
		assertEquals(9, bluePlayer.getPrayerPoints());

		assertEquals(0, redPlayer.getDiCardCount());

	}

	@Test
	public void DIVINE_PROTECTION_defense_lose() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.DIVINE_PROTECTION, bluePlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 4);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		pickBattleCard(BattleCard.CHARIOT_RAID_CARD);
		pickBattleCard(BattleCard.PHALANX_DEFENSE_CARD);
		activateDiCard(DiCardList.DIVINE_PROTECTION);
		pickBattleCard(BattleCard.CHARIOT_RAID_CARD);
		pickBattleCard(BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		// 0 power cost
		assertEquals(5, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		// 3 recall - 3 damage = 0 prayer bonus
		assertEquals(9, bluePlayer.getPrayerPoints());

		// 4 army - 3 damage
		assertEquals(1, islandTile.getArmy().armySize);

		assertEquals(0, redPlayer.getDiCardCount());

	}

	@Test
	public void DIVINE_PROTECTION_defense_win() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.DIVINE_PROTECTION, bluePlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 5);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		pickBattleCard(BattleCard.CHARIOT_RAID_CARD);
		pickBattleCard(BattleCard.PHALANX_DEFENSE_CARD);
		activateDiCard(DiCardList.DIVINE_PROTECTION);
		pickBattleCard(BattleCard.CAVALRY_BLITZ_CARD);
		pickBattleCard(BattleCard.PHALANX_DEFENSE_CARD);
		// red : 5 army + 0 DI + 1 card = 6
		// blue : 3 army + 0 DI + 4 card = 7

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		// 0 power cost
		assertEquals(3, redPlayer.victoryPoints);

		// recall 3 - 1 damage
		assertEquals(9, redPlayer.getPrayerPoints());

		assertEquals(9, bluePlayer.getPrayerPoints());

		// no damage taken
		assertEquals(3, islandTile.getArmy().armySize);

		assertEquals(0, redPlayer.getDiCardCount());

	}

	@Test
	public void TACTICAL_CHOICE_attack_win() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.TACTICAL_CHOICE, redPlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 5);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		activateDiCard(DiCardList.TACTICAL_CHOICE);
		pickBattleCard(BattleCard.DEFENSIVE_RETREAT_CARD);
		pickBattleCard(BattleCard.CAVALRY_BLITZ_CARD);
		pickBattleCard(BattleCard.CAVALRY_BLITZ_CARD);
		pickBattleCard(BattleCard.PHALANX_DEFENSE_CARD);

		// red : 5 army + 0 DI + 1 card = 6
		// blue : 3 army + 0 DI + 4 card = 7
		game.activateAction(game.getNextPlayer(), ChoiceInventory.TACTICAL_CHOICE_SWAP);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		// 0 power cost
		assertEquals(5, redPlayer.victoryPoints);

	}

	@Test
	public void TACTICAL_CHOICE_attack_lose() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.TACTICAL_CHOICE, redPlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 5);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		activateDiCard(DiCardList.TACTICAL_CHOICE);
		pickBattleCard(BattleCard.DEFENSIVE_RETREAT_CARD);
		pickBattleCard(BattleCard.CAVALRY_BLITZ_CARD);
		pickBattleCard(BattleCard.CAVALRY_BLITZ_CARD);
		pickBattleCard(BattleCard.PHALANX_DEFENSE_CARD);

		// red : 5 army + 0 DI + 1 card = 6
		// blue : 3 army + 0 DI + 4 card = 7
		game.activateAction(game.getNextPlayer(), ChoiceInventory.TACTICAL_CHOICE_KEEP);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		// 0 power cost
		assertEquals(3, redPlayer.victoryPoints);

	}

	@Test
	public void TACTICAL_CHOICE_defense_win() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.TACTICAL_CHOICE, bluePlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 5);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		pickBattleCard(BattleCard.DEFENSIVE_RETREAT_CARD);
		pickBattleCard(BattleCard.CAVALRY_BLITZ_CARD);
		activateDiCard(DiCardList.TACTICAL_CHOICE);
		pickBattleCard(BattleCard.DEFENSIVE_RETREAT_CARD);
		pickBattleCard(BattleCard.CAVALRY_BLITZ_CARD);

		// red : 5 army + 0 DI + 1 card = 6
		// blue : 3 army + 0 DI + 1 card = 4
		game.activateAction(game.getNextPlayer(), ChoiceInventory.TACTICAL_CHOICE_SWAP);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		// 0 power cost
		assertEquals(3, redPlayer.victoryPoints);

	}

	@Test
	public void TACTICAL_CHOICE_defense_lose() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.TACTICAL_CHOICE, bluePlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 5);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		pickBattleCard(BattleCard.DEFENSIVE_RETREAT_CARD);
		pickBattleCard(BattleCard.CAVALRY_BLITZ_CARD);
		activateDiCard(DiCardList.TACTICAL_CHOICE);
		pickBattleCard(BattleCard.DEFENSIVE_RETREAT_CARD);
		pickBattleCard(BattleCard.CAVALRY_BLITZ_CARD);

		// red : 5 army + 0 DI + 1 card = 6
		// blue : 3 army + 0 DI + 1 card = 4
		game.activateAction(game.getNextPlayer(), ChoiceInventory.TACTICAL_CHOICE_KEEP);
		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		// 0 power cost
		assertEquals(5, redPlayer.victoryPoints);

	}

	@Test
	public void GLORY_attack_win() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.GLORY, redPlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 4);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		activateDiCard(DiCardList.GLORY);
		// red : 4 army + 0 DI + 4 card = 8
		// blue : 3 army + 0 DI + 4 card = 7
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CHARIOT_RAID_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		// 0 power cost
		assertEquals(5, redPlayer.victoryPoints);
		assertEquals(9, redPlayer.getPrayerPoints());
		// 3 recall - 1 damage = 2 prayer bonus
		assertEquals(11, bluePlayer.getPrayerPoints());

		assertEquals(0, redPlayer.getDiCardCount());

	}

	@Test
	public void GLORY_attack_lose() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.GLORY, redPlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 3);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		activateDiCard(DiCardList.GLORY);
		// red : 3 army + 0 DI + 4 card = 7
		// blue : 3 army + 0 DI + 4 card = 7
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		// 0 power cost
		assertEquals(3, redPlayer.victoryPoints);

		// recall 3 - 1 damage
		assertEquals(7, redPlayer.getPrayerPoints());
		// 3 recall - 1 damage = 2 prayer bonus
		assertEquals(9, bluePlayer.getPrayerPoints());

		assertEquals(0, redPlayer.getDiCardCount());

	}

	@Test
	public void GLORY_defense_lose() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.GLORY, bluePlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 4);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		pickBattleCard(BattleCard.CHARIOT_RAID_CARD);
		pickBattleCard(BattleCard.PHALANX_DEFENSE_CARD);
		activateDiCard(DiCardList.GLORY);
		pickBattleCard(BattleCard.CHARIOT_RAID_CARD);
		pickBattleCard(BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		// 0 power cost
		assertEquals(5, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		// 3 recall - 3 damage = 0 prayer bonus
		assertEquals(9, bluePlayer.getPrayerPoints());

		// 4 army - 3 damage
		assertEquals(1, islandTile.getArmy().armySize);

		assertEquals(0, redPlayer.getDiCardCount());

	}

	@Test
	public void GLORY_defense_win() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.GLORY, bluePlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 5);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		pickBattleCard(BattleCard.CHARIOT_RAID_CARD);
		pickBattleCard(BattleCard.PHALANX_DEFENSE_CARD);
		activateDiCard(DiCardList.GLORY);
		pickBattleCard(BattleCard.CAVALRY_BLITZ_CARD);
		pickBattleCard(BattleCard.PHALANX_DEFENSE_CARD);
		// red : 5 army + 0 DI + 1 card = 6
		// blue : 3 army + 0 DI + 4 card = 7

		// 0 power cost
		assertEquals(3, redPlayer.victoryPoints);

		// recall 3 - 1 damage
		assertEquals(9, redPlayer.getPrayerPoints());

		// glory bonus
		assertEquals(11, bluePlayer.getPrayerPoints());

		assertEquals(0, redPlayer.getDiCardCount());

	}

	@Test
	public void REINFORCEMENTS_attack_win() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.REINFORCEMENTS, redPlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 4);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		activateDiCard(DiCardList.REINFORCEMENTS);
		// red : 4 army + 0 DI + 4 card = 8
		// blue : 3 army + 0 DI + 4 card = 7
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CHARIOT_RAID_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		recruitArmy(islandTile, 3);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		// 0 power cost
		assertEquals(5, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		// 3 recall - 1 damage = 2 prayer bonus
		assertEquals(11, bluePlayer.getPrayerPoints());

		assertEquals(0, redPlayer.getDiCardCount());

	}

	@Test
	public void REINFORCEMENTS_attack_lose() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.REINFORCEMENTS, redPlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 3);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		activateDiCard(DiCardList.REINFORCEMENTS);
		// red : 3 army + 0 DI + 4 card = 7
		// blue : 3 army + 0 DI + 4 card = 7
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		// 0 power cost
		assertEquals(3, redPlayer.victoryPoints);

		// recall 3 - 1 damage
		assertEquals(7, redPlayer.getPrayerPoints());
		// 3 recall - 1 damage = 2 prayer bonus
		assertEquals(9, bluePlayer.getPrayerPoints());

		assertEquals(0, redPlayer.getDiCardCount());

	}

	@Test
	public void REINFORCEMENTS_defense_lose() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.REINFORCEMENTS, bluePlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 4);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		pickBattleCard(BattleCard.CHARIOT_RAID_CARD);
		pickBattleCard(BattleCard.PHALANX_DEFENSE_CARD);
		activateDiCard(DiCardList.REINFORCEMENTS);
		pickBattleCard(BattleCard.CHARIOT_RAID_CARD);
		pickBattleCard(BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
		// 0 power cost
		assertEquals(5, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		// 3 recall - 3 damage = 0 prayer bonus
		assertEquals(9, bluePlayer.getPrayerPoints());

		// 4 army - 3 damage
		assertEquals(1, islandTile.getArmy().armySize);

		assertEquals(0, redPlayer.getDiCardCount());

	}

	@Test
	public void REINFORCEMENTS_defense_win() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.REINFORCEMENTS, bluePlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(redPlayer.cityTiles.get(1), islandTile, 5);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		pickBattleCard(BattleCard.CHARIOT_RAID_CARD);
		pickBattleCard(BattleCard.PHALANX_DEFENSE_CARD);
		activateDiCard(DiCardList.REINFORCEMENTS);
		pickBattleCard(BattleCard.CAVALRY_BLITZ_CARD);
		pickBattleCard(BattleCard.PHALANX_DEFENSE_CARD);
		// red : 5 army + 0 DI + 1 card = 6
		// blue : 3 army + 0 DI + 4 card = 7

		recruitArmy(bluePlayer.cityTiles.get(2), 3);

		// 0 power cost
		assertEquals(3, redPlayer.victoryPoints);

		// recall 3 - 1 damage
		assertEquals(9, redPlayer.getPrayerPoints());

		// REINFORCEMENTS bonus
		assertEquals(9, bluePlayer.getPrayerPoints());

		assertEquals(0, redPlayer.getDiCardCount());

	}

	@Test
	public void SWIFTNESS() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.SWIFTNESS, redPlayer);

		startRowTwoMove();
		moveSelectTile(redPlayer.cityTiles.get(1));
		activateDiCard(DiCardList.SWIFTNESS);
		moveSelectTile(redPlayer.cityTiles.get(2));
		moveArmySize(5, false);
		moveSelectTile(redPlayer.cityTiles.get(1));
		moveArmySize(5, false);

		prayRowThree();
	}

	@Test
	public void SWIFTNESS_VETO() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.SWIFTNESS, redPlayer);
		game.giveDiCardToPlayer(DiCardList.VETO, bluePlayer);

		startRowTwoMove();
		moveSelectTile(redPlayer.cityTiles.get(1));
		activateDiCard(DiCardList.SWIFTNESS);
		activateDiCard(DiCardList.VETO);
		moveSelectTile(redPlayer.cityTiles.get(2));
		moveArmySize(5, false);
		// next move skipped due to veto
//		moveSelectTile(redPlayer.cityTiles.get(1));
//		moveArmySize(5, false);

		assertEquals(0, bluePlayer.getDiCardCount());
		assertEquals(0, redPlayer.getDiCardCount());

		prayRowThree();
	}

	@Test
	public void SWIFTNESS_VETO_SKIP() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.SWIFTNESS, redPlayer);
		game.giveDiCardToPlayer(DiCardList.VETO, bluePlayer);

		startRowTwoMove();
		moveSelectTile(redPlayer.cityTiles.get(1));
		activateDiCard(DiCardList.SWIFTNESS);
		skipVeto();

		moveSelectTile(redPlayer.cityTiles.get(2));
		moveArmySize(5, false);
		moveSelectTile(redPlayer.cityTiles.get(1));
		moveArmySize(5, false);

		assertEquals(1, bluePlayer.getDiCardCount());
		assertEquals(0, redPlayer.getDiCardCount());

		prayRowThree();
	}

	@Test
	public void SWIFTNESS_VETO_THE_VETO() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.SWIFTNESS, redPlayer);
		game.giveDiCardToPlayer(DiCardList.VETO, redPlayer);
		game.giveDiCardToPlayer(DiCardList.VETO, bluePlayer);

		startRowTwoMove();
		moveSelectTile(redPlayer.cityTiles.get(1));
		activateDiCard(DiCardList.SWIFTNESS);
		activateDiCard(DiCardList.VETO);
		activateDiCard(DiCardList.VETO);
		moveSelectTile(redPlayer.cityTiles.get(2));
		moveArmySize(5, false);
		moveSelectTile(redPlayer.cityTiles.get(1));
		moveArmySize(5, false);

		assertEquals(0, bluePlayer.getDiCardCount());
		assertEquals(0, redPlayer.getDiCardCount());

		prayRowThree();
	}

	@Test
	public void SWIFTNESS_SKIP_VETO_THE_VETO() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.SWIFTNESS, redPlayer);
		game.giveDiCardToPlayer(DiCardList.VETO, redPlayer);
		game.giveDiCardToPlayer(DiCardList.VETO, bluePlayer);

		startRowTwoMove();
		moveSelectTile(redPlayer.cityTiles.get(1));
		activateDiCard(DiCardList.SWIFTNESS);
		activateDiCard(DiCardList.VETO);
		skipVeto();
		moveSelectTile(redPlayer.cityTiles.get(2));
		moveArmySize(5, false);
//		moveSelectTile(redPlayer.cityTiles.get(1));
//		moveArmySize(5, false);

		assertEquals(1, redPlayer.getDiCardCount());
		assertEquals(0, bluePlayer.getDiCardCount());

		prayRowThree();
	}

	@Test
	public void PRAYER_during_build() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.PRAYER, redPlayer);

		assertEquals(7, redPlayer.getPrayerPoints());

		startBuyPowerTile(PowerList.RED_1_CHARGE_1);
		activateDiCard(DiCardList.PRAYER);
		assertEquals(9, redPlayer.getPrayerPoints());
		endBuyPowerTile(PowerList.RED_1_CHARGE_1);
		assertEquals(8, redPlayer.getPrayerPoints());

		prayRowThree();
	}

	@Test
	public void PRAYER_during_recruit() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.PRAYER, redPlayer);

		assertEquals(7, redPlayer.getPrayerPoints());

		startRecruit();
		activateDiCard(DiCardList.PRAYER);
		assertEquals(9, redPlayer.getPrayerPoints());
		endRecruit();

		prayRowThree();
	}

	@Test
	public void PRAYER_during_choicePick() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.PRAYER, redPlayer);

		assertEquals(7, redPlayer.getPrayerPoints());

		activateDiCard(DiCardList.PRAYER);
		startRecruit();
		assertEquals(9, redPlayer.getPrayerPoints());
		endRecruit();

		prayRowThree();
	}

	@Test
	public void PRAYER_during_upgradePyramid() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.PRAYER, redPlayer);

		assertEquals(7, redPlayer.getPrayerPoints());

		startUpgradePyramid();
		activateDiCard(DiCardList.PRAYER);
		endUpgradePyramid();

		assertEquals(9, redPlayer.getPrayerPoints());

		prayRowThree();
	}

	@Test
	public void MANA_THEFT() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.MANA_THEFT, redPlayer);

		assertEquals(7, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		activateDiCard(DiCardList.MANA_THEFT);
		assertEquals(8, redPlayer.getPrayerPoints());
		assertEquals(8, bluePlayer.getPrayerPoints());

		prayRowThree();
	}

	@Test
	public void TELEPORTATION() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.movePowerToPlayer(redPlayer, PowerList.RED_1_GOD_SPEED);
		game.giveDiCardToPlayer(DiCardList.TELEPORTATION, redPlayer);

		Tile islandTile = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);

		moveRowTwoArmy(redPlayer.cityTiles.get(1), redPlayer.cityFront, 4);

		assertEquals(7, redPlayer.getPrayerPoints());
		activateDiCard(DiCardList.TELEPORTATION);
		moveNextTile(islandTile, 4);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(6, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());
	}

	@Test
	public void OPEN_GATES() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.movePowerToPlayer(redPlayer, PowerList.RED_1_GOD_SPEED);
		game.giveDiCardToPlayer(DiCardList.OPEN_GATES, redPlayer);

		Tile obeliskTile = game.getTileByName(TwoPlayerGame.MIDDLE_OBELISK);

		moveRowTwoArmy(redPlayer.cityTiles.get(1), obeliskTile, 4);

		assertEquals(5, redPlayer.getPrayerPoints());
		activateDiCard(DiCardList.OPEN_GATES);
		assertEquals(4, redPlayer.getPrayerPoints());
		moveNextTile(bluePlayer.cityFront, 4);

		// breached walls
		moveNextTile(bluePlayer.cityTiles.get(2), 4);

	}

	@Test
	public void REINFORCEMENTS() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.ENLISTMENT, redPlayer);
		assertEquals(7, redPlayer.getPrayerPoints());
		activateDiCard(DiCardList.ENLISTMENT);

		recruitArmy(redPlayer.cityTiles.get(2), 2);

		moveRowOneZeroArmy();

		assertEquals(7, redPlayer.getPrayerPoints());
	}

	@Test
	public void DIVINE_MEMORY() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.DIVINE_MEMORY, redPlayer);

		DiCardList.moveDiCard(game.availableDiCardList, game.discardedDiCardList, 0, KemetGame.AVAILABLE_DI_CARDS,
				KemetGame.DISCARDED_DI_CARDS, "seeding test", game);
		DiCardList.moveDiCard(game.availableDiCardList, game.discardedDiCardList, 1, KemetGame.AVAILABLE_DI_CARDS,
				KemetGame.DISCARDED_DI_CARDS, "seeding test", game);
		assertEquals(7, redPlayer.getPrayerPoints());
		activateDiCard(DiCardList.DIVINE_MEMORY);
		assertEquals(1, game.discardedDiCardList[0]);
		assertEquals(0, redPlayer.diCards[0]);

		pickDiCard(DiCardList.GLORY);

		assertEquals(0, game.discardedDiCardList[0]);
		assertEquals(1, redPlayer.diCards[0]);

		moveRowOneZeroArmy();

		assertEquals(6, redPlayer.getPrayerPoints());
	}

	@Test
	public void DIVINE_MEMORY_EMPTY() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.DIVINE_MEMORY, redPlayer);
		assertEquals(1, redPlayer.diCards[DiCardList.DIVINE_MEMORY.index]);
		assertEquals(7, redPlayer.getPrayerPoints());
		activateDiCard(DiCardList.DIVINE_MEMORY);

		moveRowOneZeroArmy();

		// only one card can come back.
		assertEquals(1, redPlayer.diCards[DiCardList.DIVINE_MEMORY.index]);

		assertEquals(6, redPlayer.getPrayerPoints());
	}

	@Test
	public void RAINING_FIRE() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.RAINING_FIRE, redPlayer);
		assertEquals(7, redPlayer.getPrayerPoints());

		Tile tileByName = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);

		assertEquals(3, tileByName.getArmy().armySize);
		activateDiCard(DiCardList.RAINING_FIRE);

		rainingFireToTile(tileByName);

		assertEquals(2, tileByName.getArmy().armySize);

		moveRowOneZeroArmy();

		assertEquals(6, redPlayer.getPrayerPoints());
	}

	@Test
	public void RAINING_FIRE_skip_devourer() {
		bluePlayer.recuperateAllBattleCards();
		redPlayer.recuperateAllBattleCards();

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.RAINING_FIRE, redPlayer);
		assertEquals(7, redPlayer.getPrayerPoints());

		Tile tileByName = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		tileByName.getArmy().beast = BeastList.BLACK_4_DEVOURER;

		assertEquals(3, tileByName.getArmy().armySize);
		activateDiCard(DiCardList.RAINING_FIRE);

		try {
			rainingFireToTile(tileByName);
			fail("should skip devourer");
		} catch (Exception ex) {

		}
	}

	@Test
	public void ESCAPE_one_choice() {

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.ESCAPE, redPlayer);
		
		prayRowThree();
		
		// blue attacks middle temple
		Tile from = bluePlayer.cityTiles.get(1);
		Tile tileByName = game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE);
		Tile entrance = game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE_ENTRANCE);
		moveRowTwoArmy(from, tileByName, 3);

		assertEquals( null, entrance.getArmy());

		activateDiCard(DiCardList.ESCAPE);
		prayRowTwo();
		
		// no choice, red army moves to medium temple entrance
		assertEquals( 5, entrance.getArmy().armySize);
	}
	
	@Test
	public void ESCAPE_multi_choice() {

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.ESCAPE, redPlayer);
		
		
		Tile from = bluePlayer.cityTiles.get(1);
		Tile tileByName = game.getTileByName(TwoPlayerGame.MIDDLE_OBELISK);
		Tile entrance = game.getTileByName(TwoPlayerGame.MEDIUM_TEMPLE_ENTRANCE);

		// teleport to middle obelisk
		moveRowOneArmy(redPlayer.cityTiles.get(1), tileByName, 5);
		endMove();

		// blue attacks middle obelisk
		moveRowTwoArmy(from, tileByName, 3);

		assertEquals( null, entrance.getArmy());

		activateDiCard(DiCardList.ESCAPE);
		
		moveEscapeTile(entrance);
		
		prayRowTwo();
		
		// no choice, red army moves to medium temple entrance
		assertEquals( 5, entrance.getArmy().armySize);
	}
	
	@Test
	public void ESCAPE_no_choice() {

		game.resetDiCards();
		game.giveDiCardToPlayer(DiCardList.ESCAPE, bluePlayer);

		Tile from = redPlayer.cityTiles.get(1);
		Tile tileByName = game.getTileByName(TwoPlayerGame.ISLAND_TEMPLE);
		moveRowTwoArmy(from, tileByName, 3);

		// moved by teleport, only 1 cost
		assertEquals(3, redPlayer.victoryPoints);
		assertEquals(5, redPlayer.getPrayerPoints());
		assertEquals(9, bluePlayer.getPrayerPoints());

		// ensure no escape choice is given
		
		battlePick(BattleCard.CAVALRY_BLITZ_CARD, BattleCard.PHALANX_DEFENSE_CARD, BattleCard.CAVALRY_BLITZ_CARD,
				BattleCard.PHALANX_DEFENSE_CARD);

		game.activateAction(game.getNextPlayer(), ChoiceInventory.PASS_RECALL_CHOICE_INDEX);
	}

	
}
