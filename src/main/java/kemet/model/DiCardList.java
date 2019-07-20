package kemet.model;

import kemet.util.ByteCanonicalForm;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DiCardList {

	public static int DI_INDEXER = 0;

	public static final DiCard GLORY = new DiCard("Glory", (byte) 0, Phase.COMBAT,
			"Gain 4 prayer if you win the battle.", DI_INDEXER++);
	public static final DiCard DIVINE_PROTECTION = new DiCard("Divine Protection", (byte) 0, Phase.COMBAT,
			"If you win the battle, don't take any damage.", DI_INDEXER++);
	public static final DiCard REINFORCEMENTS = new DiCard("Reinforcements", (byte) 0, Phase.COMBAT,
			"If you win the battle, recruit 3 units anywhere before retreat.", DI_INDEXER++);
	public static final DiCard TACTICAL_CHOICE = new DiCard("Tactical Choice", (byte) 0, Phase.COMBAT,
			"Play after battle card selection : you can switch your revealed battle card with the discard.",
			DI_INDEXER++);
	public static final DiCard WAR_RAGE = new DiCard("War Rage", (byte) 0, Phase.COMBAT, "+1 Strength.", DI_INDEXER++);
	public static final DiCard WAR_FURY = new DiCard("War Fury", (byte) 1, Phase.COMBAT, "+2 Strength.", DI_INDEXER++);
	public static final DiCard BLOOD_BATTLE = new DiCard("Blood Battle", (byte) 0, Phase.COMBAT, "+1 Damage.",
			DI_INDEXER++);
	public static final DiCard BLOOD_BATH = new DiCard("Blood Bath", (byte) 1, Phase.COMBAT, "+2 Damage.",
			DI_INDEXER++);
	public static final DiCard BRONZE_WALL = new DiCard("Bronze Wall", (byte) 0, Phase.COMBAT, "+1 Shield.",
			DI_INDEXER++);
	public static final DiCard IRON_WALL = new DiCard("Iron Wall", (byte) 1, Phase.COMBAT, "+2 Shield.", DI_INDEXER++);

	public static final DiCard SWIFTNESS = new DiCard("Swiftness", (byte) 0, Phase.DAY, "+1 move.", DI_INDEXER++);
	public static final DiCard DIVINE_MEMORY = new DiCard("Divine Memory", (byte) 1, Phase.DAY,
			"Take back one DI from discard pile.", DI_INDEXER++);
	public static final DiCard RAINING_FIRE = new DiCard("Raining Fire", (byte) 1, Phase.DAY, "Destroy one enemy unit.",
			DI_INDEXER++);
	public static final DiCard PRAYER = new DiCard("Prayer", (byte) 0, Phase.DAY, "Gain 2 prayer points.",
			DI_INDEXER++);
	public static final DiCard ENLISTMENT = new DiCard("Enlistment", (byte) 0, Phase.DAY, "Recruit 2 units anywhere.",
			DI_INDEXER++);
	public static final DiCard MANA_THEFT = new DiCard("Mana Theft", (byte) 0, Phase.DAY,
			"Each opponent loses 1 prayer point, you gain 1 prayer point.", DI_INDEXER++);
	public static final DiCard TELEPORTATION = new DiCard("Teleportation", (byte) 1, Phase.DAY,
			"Teleport any army to an obelisk. Played with a movement action.", DI_INDEXER++);
	public static final DiCard OPEN_GATES = new DiCard("Open gates", (byte) 1, Phase.DAY,
			"Ignore wall effects.  Played with a movement action.", DI_INDEXER++);
	public static final DiCard VETO = new DiCard("Veto", (byte) 0, Phase.INTERRUPT,
			"Cancel a DI card, not during battle.", DI_INDEXER++);
	public static final DiCard ESCAPE = new DiCard("Escape", (byte) 0, Phase.INTERRUPT,
			"No battle takes place, move to a free territory.", DI_INDEXER++);

	public static final int TOTAL_DI_CARD_TYPE_COUNT = DI_INDEXER;

	public static final int TOTAL_BATTLE_DI_CARD_TYPE_COUNT = 10;

	public static final int TOTAL_NON_BATTLE_DI_CARD_TYPE_COUNT = TOTAL_DI_CARD_TYPE_COUNT - TOTAL_BATTLE_DI_CARD_TYPE_COUNT;

	public static final int MAX_DI_CARD_COUNT = 4;

	public static final int TOTAL_DI_COUNT;

	public static final DiCard[] CARDS;

	static {
		CARDS = new DiCard[TOTAL_DI_CARD_TYPE_COUNT];

		fillCards(GLORY);
		fillCards(DIVINE_PROTECTION);
		fillCards(REINFORCEMENTS);
		fillCards(TACTICAL_CHOICE);
		fillCards(WAR_RAGE);

		fillCards(WAR_FURY);
		fillCards(BLOOD_BATTLE);
		fillCards(BLOOD_BATH);
		fillCards(BRONZE_WALL);
		fillCards(IRON_WALL);

		fillCards(SWIFTNESS);
		fillCards(DIVINE_MEMORY);
		fillCards(RAINING_FIRE);
		fillCards(PRAYER);
		fillCards(ENLISTMENT);

		fillCards(MANA_THEFT);
		fillCards(TELEPORTATION);
		fillCards(OPEN_GATES);
		fillCards(VETO);
		fillCards(ESCAPE);

		for (int i = 0; i < CARDS.length; i++) {
			DiCard diCard = CARDS[i];
			if (diCard == null) {
				throw new IllegalStateException("No DI card registered at index " + i);
			}
		}

		byte[] cards = new byte[TOTAL_DI_CARD_TYPE_COUNT];
		initializeGame(cards);

		TOTAL_DI_COUNT = sumArray(cards);
	}

//	- Game feature : DI Cards - Battle
//	- 2x : glory : cost 0 : gain 4 prayer if you win the battle.
//	- 2x : divine protection : cost 0 : if you win battle, don't take any damage.
//	- 2x : reinforcements : cost 0 : if you win battle, recruit 3 units anywhere before retreat.
//	- 2x : tactical choice : cost 0 : play after battle cards : you can switch your revealed battle card with the discard.
//	- 3x : War rage : cost 0 : +1 strength 
//	- 3x : War fury : cost 1 : +2 strength 
//	- 3x : Blood battle : cost 0 : +1 damage 
//	- 3x : Bloodbath : cost 1 : +2 damage 
//	- 3x : Bronze Wall : cost 0 : +1 shield 
//	- 3x : Iron Wall : cost 1 : +2 shield
//	 
//
//- Game feature : DI Cards - Non-Battle
//	- 2x : swiftness : cost 0 : beginning move action : +1 move.
//	- 2x : divine memory : cost 1: take back one DI from discard pile.
//	- 4x : Raining Fire : cost 1 : destroy one enemy unit.
//	- 2x : Prayer : cost 0 : gain 2 prayer point
//	- 4x : Enlistment : cost 0 : recruit 2 units anywhere.
//	- 2x : Mana Theft : cost 0 : each opponent loses 1 pp, you gain 1pp.
//	- 3x : Teleportation : cost 1 : Teleport any army to an obelisk. Played with a movement action.
//	- 1x : Open gates : cost 1 : Ignore wall effects.  Played with a movement action.
//	- 2x : Veto : cost 0 : Cancel a DI card, not during battle.
//	- 1x : Escape : cost 0 : No battle takes place, move to a free territory.

	public static void fillCanonicalForm(byte[] diCardArray, ByteCanonicalForm canonicalForm, int offset) {
		for (int i = 0; i < diCardArray.length; ++i) {
			canonicalForm.set(offset + i, diCardArray[i]);
		}
	}

	public static void fillBattleCanonicalForm(byte[] diCardArray, ByteCanonicalForm canonicalForm, int offset) {
		for (int i = 0; i < TOTAL_BATTLE_DI_CARD_TYPE_COUNT; ++i) {
			canonicalForm.set(offset + i, diCardArray[i]);
		}
	}

	private static void fillCards(DiCard card) {
		if (CARDS[card.index] != null) {
			throw new IllegalArgumentException("CARDS array already has a card in " + card);
		}
		CARDS[card.index] = card;

	}

	public static void fillArray(byte[] destination, byte value) {
		for (int i = 0; i < destination.length; ++i) {
			destination[i] = value;
		}
	}

	public static byte sumArray(byte[] destination) {
		byte sum = 0;
		for (int i = 0; i < destination.length; ++i) {
			sum += destination[i];
		}
		return sum;
	}

	public static void copyArray(byte[] source, byte[] destination) {
		for (int i = 0; i < source.length; ++i) {
			destination[i] = source[i];
		}
	}

	public static void moveAllDiCard(byte[] source, byte[] destination, String sourceName, String destinationName,
			String reason, KemetGame game) {
		for (int i = 0; i < source.length; i++) {
			while (source[i] > 0) {
				moveDiCard(source, destination, i, sourceName, destinationName, reason, game);
			}
		}
	}

	public static void moveDiCard(byte[] source, byte[] destination, int index, String sourceName,
			String destinationName, String reason, KemetGame game) {
		if (index < 0) {
			log.info("No DI cards are available to move.");
			return;
		}
		source[index] -= 1;
		destination[index] += 1;

		if (game.printActivations) {
			String cardName = CARDS[index].name;
			game.printEvent("Moving DI Card \"" + cardName + "\" from " + sourceName + " to " + destinationName
					+ " due to " + reason);
		}

		if (source[index] < 0) {
			throw new IllegalStateException("DI Card index " + index + " has a negative count " + source[index]);
		}
	}
	
	public static void describeDiCards(byte[] cards, StringBuilder builder ) {
		boolean first = true;
		for (int i = 0; i < cards.length; i++) {
			byte b = cards[i];
			if( b > 0 ) {
				
				if( first ) {
					first = false;
				} else {
					builder.append(", ");
				}
				
				builder.append(b);
				builder.append(" ");
				builder.append(CARDS[i].name);
			}
			
		}
		
	}

	public static void moveRandomDiCard(byte[] source, byte[] destination, String sourceName, String destinationName,
			String reason, KemetGame game, boolean recuperateDiscardedCardsWhenSourceEmpty) {
		int indexToMove = getIndexToMove(source, game);
		if (indexToMove < 0 && recuperateDiscardedCardsWhenSourceEmpty) {
			game.moveDiscardedDiCardsToAvailableDiCardList();
			indexToMove = getIndexToMove(source, game);
		}

		moveDiCard(source, destination, indexToMove, sourceName, destinationName, reason, game);
	}

	public static int getIndexToMove(byte[] source, KemetGame game) {
		byte sumArray = sumArray(source);

		if (sumArray <= 0) {
			return -1;
		}

		int indexCount = game.random.nextInt(sumArray);
		int indexToMove = -1;

		for (int i = 0; i < source.length; ++i) {
			byte sourceValue = source[i];
			if (sourceValue > 0) {
				indexCount -= sourceValue;
				if (indexCount <= 0) {
					indexToMove = i;
					break;
				}
			}
		}

		if (indexToMove == -1) {
			log.error("Non empty DI card list couldn't find random card index to return, random {}", indexCount);
		}

		return indexToMove;
	}

	public static void initializeGame(byte[] availableDiCardList) {
		availableDiCardList[0] = 2;
		availableDiCardList[1] = 2;
		availableDiCardList[2] = 2;
		availableDiCardList[3] = 2;
		availableDiCardList[4] = 3;
		availableDiCardList[5] = 3;
		availableDiCardList[6] = 3;
		availableDiCardList[7] = 3;
		availableDiCardList[8] = 3;
		availableDiCardList[9] = 3;

		availableDiCardList[10] = 2;
		availableDiCardList[11] = 2;
		availableDiCardList[12] = 4;
		availableDiCardList[13] = 2;
		availableDiCardList[14] = 4;
		availableDiCardList[15] = 2;
		availableDiCardList[16] = 3;
		availableDiCardList[17] = 1;
		availableDiCardList[18] = 2;
		availableDiCardList[19] = 1;
	}

}
