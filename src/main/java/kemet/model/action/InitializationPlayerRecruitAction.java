
package kemet.model.action;

import java.util.List;

import kemet.model.Army;
import kemet.model.BoardInventory;
import kemet.model.KemetGame;
import kemet.model.Player;
import kemet.model.Tile;
import kemet.model.Validation;
import kemet.model.action.choice.Choice;
import kemet.model.action.choice.ChoiceInventory;
import kemet.model.action.choice.PlayerChoice;
import kemet.util.ByteCanonicalForm;
import kemet.util.Cache;

/**
 * InitializationPlayerRecruitAction
 * 
 * @author Steve McDuff
 */
public class InitializationPlayerRecruitAction implements Action
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7880098728096469284L;
	private KemetGame game;
    private Player player;

    private byte soldierLeft = 10;
    private byte tileIndex = 0;
	private Action parent;
	
	public static Cache<InitializationPlayerRecruitAction> CACHE = new Cache<InitializationPlayerRecruitAction>(() -> new InitializationPlayerRecruitAction());

	@Override
	public void initialize() {
		player = null;
		soldierLeft = 10;
		tileIndex = 0;
		game = null;
		parent = null;
	}
	
	@Override
	public void validate(Action expectedParent, KemetGame currentGame) {
		currentGame.validate(game);
		currentGame.validate(player);
		if( expectedParent != parent ) {
			Validation.validationFailed("Action parent isn't as expected.");
		}
	}
	
	@Override
	public InitializationPlayerRecruitAction deepCacheClone() {
		// create the object
		InitializationPlayerRecruitAction clone = CACHE.create();

		// copy all objects
		clone.player = player;
		clone.soldierLeft = soldierLeft;
		clone.tileIndex = tileIndex;
		clone.game = game;
		clone.parent = parent;

		return clone;
	}

	@Override
	public void release() {
		// null all references
		player = null;
		game = null;
		parent = null;

		CACHE.release(this);
	}

	@Override
	public void relink(KemetGame clone) {
		// relink game
		this.game = clone;

		// relink pointers
		player = clone.getPlayerByCopy(player);
	}

	

    public static InitializationPlayerRecruitAction create(KemetGame game, Player player, Action parent)
    {
    	InitializationPlayerRecruitAction create = CACHE.create();
    	create.initialize();
    	create.game = game;
    	create.player = player;
    	create.parent = parent;
    	
    	return create;
    }
    
	@Override
	public void setParent(Action parent) {
		this.parent = parent;
	}

    @Override
    public PlayerChoicePick getNextPlayerChoicePick()
    {
        if (soldierLeft == 0)
        {
            return null;
        }

        byte minimum = 0;
        byte maximum = 5;
        Tile tile = player.cityTiles.get(tileIndex);
        PlayerChoicePick pick = new PlayerChoicePick(game, player, this);

        if (tileIndex == 0)
        {

        }
        else if (tileIndex == 1)
        {
            minimum = (byte) (soldierLeft - 5);
        }
        else if (tileIndex == 2)
        {

        }

        for (int i = minimum; i <= maximum; ++i)
        {
            createInitialRecruitChoice(player, (byte) i, tile, pick.choiceList);
        }

        return pick.validate();

    }
    
	@Override
	public void fillCanonicalForm(ByteCanonicalForm cannonicalForm, int playerIndex) {
		
        if (soldierLeft == 0)
        {
            return;
        }

		cannonicalForm.set(BoardInventory.STATE_INITIAL_ARMY, player.getState(playerIndex));
		
        Tile tile = player.cityTiles.get(tileIndex);
        tile.setSelected(cannonicalForm, playerIndex, player.getState(playerIndex));
		
	}    

    public class InitialRecruitChoice extends PlayerChoice
    {

        public InitialRecruitChoice(KemetGame game, Player player)
        {
            super(game, player);
        }

        public byte soldierCount;
        public Tile tile;

        @Override
        public void choiceActivate()
        {

            tileIndex++;
            soldierLeft -= soldierCount;
            if (soldierCount > 0)
            {
                Army modifiedArmy = player.createArmy();
                modifiedArmy.moveToTile(tile);
                modifiedArmy.recruit(soldierCount);
            }

            if (tileIndex == 2 && soldierLeft > 0)
            {
                Army modifiedArmy = player.createArmy();
                modifiedArmy.moveToTile(player.cityTiles.get(tileIndex));
                modifiedArmy.recruit(soldierLeft);
                soldierLeft = 0;
            }
        }

        @Override
        public String describe()
        {
            String retVal = "Initial Recruit : recruit " + soldierCount + " soldiers " + "on tile \"" + tile.name +
                "\" creating \"" + player.getNextArmyName() + "\"";

            if (soldierCount == 0)
            {
                retVal = "Initial Recruit : no recruitment on tile \"" + tile.name + "\"";
            }

            if (tileIndex == 1 && soldierLeft - soldierCount > 0)
            {
                retVal += " and an army of size " + (soldierLeft - soldierCount) + " on tile \"" +
                    player.cityTiles.get(2).name + "\"";
            }

            return retVal;
        }

		@Override
		public int getIndex() {
			if( soldierCount == 0 ) {
				return ChoiceInventory.ZERO_ARMY_SIZE_CHOICE_INDEX;
			}
			return ChoiceInventory.ARMY_SIZE_CHOICE + soldierCount - 1;
		}

    }

    private void createInitialRecruitChoice(Player player, byte soldierCount, Tile tile, List<Choice> choiceList)
    {

        assert (tile != null);
        assert (player != null);

        InitialRecruitChoice choice = new InitialRecruitChoice(game, player);
        choice.soldierCount = soldierCount;
        choice.tile = tile;
        choiceList.add(choice);
    }

	@Override
	public Action getParent() {
		return parent;
	}
}
