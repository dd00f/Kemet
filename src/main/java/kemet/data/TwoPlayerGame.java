package kemet.data;

import kemet.ai.HumanPlayer;
import kemet.ai.RandomPlayerAI;
import kemet.ai.TrialPlayerAI;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Tile;
import kemet.model.action.PlayerChoicePick;

public class TwoPlayerGame
{

    public static final String ISLAND_TEMPLE = "Island Temple";
    public static final String MEDIUM_TEMPLE_ENTRANCE = "Medium Temple Entrance";
	public static final String MEDIUM_TEMPLE = "Medium Temple";
	public static final String SMALL_TEMPLE = "Small Temple";
	public static final String MIDDLE_OBELISK = "Middle Obelisk";
	public KemetGame game = KemetGame.create();

    public static void main(String[] args)
    {
        TwoPlayerGame twoPlayerGame = new TwoPlayerGame();
        twoPlayerGame.initializeGame();
        twoPlayerGame.runGame();
    }

    public void runGame()
    {
       
        while (true)
        {
            PlayerChoicePick nextPlayerChoicePick = game.action.getNextPlayerChoicePick();
            if( nextPlayerChoicePick == null ) {
                return;
            }
            
            nextPlayerChoicePick.player.actor.pickActionAndActivate(nextPlayerChoicePick);
        }

    }

    private void initializeGame()
    {
        createPlayers();
        createTiles();
    }

    private void createPlayers()
    {
        // createAIPlayer("red");
        createTrialAIPlayer("red");
        
        createTrialAIPlayer("blue");
        //createHumanPlayer("blue");

    }

    public Player createHumanPlayer(String name)
    {
        Player player = Player.create();
        player.name = name;
        player.game = game;
        player.actor = new HumanPlayer(player, game);
        game.playerByInitiativeList.add(player);

        return player;
    }

    public Player createTrialAIPlayer(String name)
    {
        Player player = Player.create();
        player.name = name;
        player.game = game;
        player.actor = new TrialPlayerAI(player, game);
        game.playerByInitiativeList.add(player);

        return player;
    }
    
    public Player createAIPlayer(String name)
    {
        Player player = Player.create();
        player.name = name;
        player.game = game;
        player.actor = new RandomPlayerAI(player, game);
        game.playerByInitiativeList.add(player);

        return player;
    }

    public void createTiles()
    {
    	int indexer = 0;

        Tile midObelisk = Tile.create(indexer++);
        midObelisk.name = MIDDLE_OBELISK;
        midObelisk.hasObelisk = true;

        Tile smallTemple = Tile.create(indexer++);
        smallTemple.name = SMALL_TEMPLE;
        smallTemple.hasTemple = true;
        smallTemple.templeBonusPrayer = 2;
        smallTemple.templePaireable = true;
        smallTemple.hasObelisk = true;

        Tile mediumTemple = Tile.create(indexer++);
        mediumTemple.name = MEDIUM_TEMPLE;
        mediumTemple.hasTemple = true;
        mediumTemple.templeBonusPrayer = 3;
        mediumTemple.templePaireable = true;
        mediumTemple.hasObelisk = true;

        Tile mediumTempleEntrance = Tile.create(indexer++);
        mediumTempleEntrance.name = MEDIUM_TEMPLE_ENTRANCE;

        Tile islandTemple = Tile.create(indexer++);
        islandTemple.name = ISLAND_TEMPLE;
        islandTemple.hasTemple = true;
        islandTemple.templeBonusPrayer = 5;
        islandTemple.templePaireable = true;
        islandTemple.hasObelisk = true;
        islandTemple.templeArmyCost = 1;

        game.tileList.add(midObelisk);
        game.tileList.add(smallTemple);
        game.tileList.add(mediumTemple);
        game.tileList.add(mediumTempleEntrance);
        game.tileList.add(islandTemple);

        connectTiles(midObelisk, smallTemple);
        connectTiles(midObelisk, mediumTempleEntrance);
        connectTiles(mediumTempleEntrance, mediumTemple);

        for (Player player : game.playerByInitiativeList)
        {

            Tile district1 = Tile.create(indexer++);
            district1.name = player.name + " district 1";
            district1.isWalled = true;
            district1.owningPlayer = player;

            Tile district2 = Tile.create(indexer++);
            district2.name = player.name + " district 2";
            district2.isWalled = true;
            district2.owningPlayer = player;

            Tile district3 = Tile.create(indexer++);
            district3.name = player.name + " district 3";
            district3.isWalled = true;
            district3.owningPlayer = player;

            Tile cityFront = Tile.create(indexer++);
            cityFront.name = player.name + " city front";
            cityFront.owningPlayer = player;

            district1.connectedTiles.add(district2);
            district1.connectedTiles.add(district3);
            district1.connectedTiles.add(cityFront);
            district1.districtIndex=0;

            district2.connectedTiles.add(district1);
            district2.connectedTiles.add(district3);
            district2.connectedTiles.add(cityFront);
            district2.districtIndex=1;

            district3.connectedTiles.add(district1);
            district3.connectedTiles.add(district2);
            district3.connectedTiles.add(cityFront);
            district3.districtIndex=2;

            cityFront.connectedTiles.add(district1);
            cityFront.connectedTiles.add(district2);
            cityFront.connectedTiles.add(district3);
            cityFront.districtIndex=3;

            connectTiles(cityFront, smallTemple);
            connectTiles(cityFront, midObelisk);
            connectTiles(cityFront, mediumTempleEntrance);

            game.tileList.add(district1);
            game.tileList.add(district2);
            game.tileList.add(district3);
            game.tileList.add(cityFront);

            player.cityTiles.add(district1);
            player.cityTiles.add(district2);
            player.cityTiles.add(district3);
            player.cityFront = cityFront;

        }

    }

    private void connectTiles(Tile firstTile, Tile secondTile)
    {
        firstTile.connectedTiles.add(secondTile);
        secondTile.connectedTiles.add(firstTile);
    }

}
