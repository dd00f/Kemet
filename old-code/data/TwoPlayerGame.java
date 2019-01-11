package kemet.data;

import org.apache.commons.lang3.SerializationUtils;

import kemet.ai.HumanPlayer;
import kemet.ai.RandomPlayerAI;
import kemet.model.Game;
import kemet.model.Player;
import kemet.model.Tile;
import kemet.model.action.PlayerChoicePick;

public class TwoPlayerGame
{

    public Game game = new Game();

    public static void main(String[] args)
    {
        TwoPlayerGame twoPlayerGame = new TwoPlayerGame();
        twoPlayerGame.runGame();
    }

    public void runGame()
    {
        createPlayers();
        createTiles();

        // GameState initialState = new GameState();
        // initialState.game = game;
        // initialState.triggerNextAction();
        
        Game clone = SerializationUtils.clone(game);
        clone.getClass();

        while (true)
        {
            PlayerChoicePick nextPlayerChoicePick = game.action.getNextPlayerChoicePick();
            if( nextPlayerChoicePick == null ) {
                return;
            }
            
            nextPlayerChoicePick.player.actor.pickActionAndActivate(nextPlayerChoicePick);
        }

    }

    private void createPlayers()
    {
        createAIPlayer("red");
        createHumanPlayer("blue");

    }

    private Player createHumanPlayer(String name)
    {
        Player player = new Player();
        player.name = name;
        player.actor = new HumanPlayer(player, game);
        game.playerByInitiativeList.add(player);

        return player;
    }

    private Player createAIPlayer(String name)
    {
        Player player = new Player();
        player.name = name;
        player.actor = new RandomPlayerAI(player, game);
        game.playerByInitiativeList.add(player);

        return player;
    }

    private void createTiles()
    {

        Tile midObelisk = new Tile();
        midObelisk.name = "Middle Obelisk";
        midObelisk.hasObelisk = true;

        Tile smallTemple = new Tile();
        smallTemple.name = "Small Temple";
        smallTemple.hasTemple = true;
        smallTemple.templeBonusPrayer = 2;
        smallTemple.templePaireable = true;
        smallTemple.hasObelisk = true;

        Tile mediumTemple = new Tile();
        mediumTemple.name = "Medium Temple";
        mediumTemple.hasTemple = true;
        mediumTemple.templeBonusPrayer = 3;
        mediumTemple.templePaireable = true;
        mediumTemple.hasObelisk = true;

        Tile mediumTempleEntrance = new Tile();
        mediumTempleEntrance.name = "Medium Temple Entrance";

        Tile islandTemple = new Tile();
        islandTemple.name = "Island Temple";
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

            Tile district1 = new Tile();
            district1.name = player.name + " district 1";
            district1.owningPlayer = player;

            Tile district2 = new Tile();
            district2.name = player.name + " district 2";
            district2.owningPlayer = player;

            Tile district3 = new Tile();
            district3.name = player.name + " district 3";
            district3.owningPlayer = player;

            Tile cityFront = new Tile();
            cityFront.name = player.name + " city front";
            cityFront.owningPlayer = player;

            district1.connectedTiles.add(district2);
            district1.connectedTiles.add(district3);
            district1.connectedTiles.add(cityFront);

            district2.connectedTiles.add(district1);
            district2.connectedTiles.add(district3);
            district2.connectedTiles.add(cityFront);

            district3.connectedTiles.add(district1);
            district3.connectedTiles.add(district2);
            district3.connectedTiles.add(cityFront);

            cityFront.connectedTiles.add(district1);
            cityFront.connectedTiles.add(district2);
            cityFront.connectedTiles.add(district3);

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

        }

    }

    private void connectTiles(Tile firstTile, Tile secondTile)
    {
        firstTile.connectedTiles.add(secondTile);
        secondTile.connectedTiles.add(firstTile);
    }

}
