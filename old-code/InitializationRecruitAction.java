package kemet.model.action;

import kemet.model.Game;
import kemet.model.Player;

public class InitializationRecruitAction extends ChainedAction
{

    /**
     * 
     */
    private static final long serialVersionUID = 5798076013017724149L;

    public InitializationRecruitAction(Game game, Action parent)
    {
        super(game, parent);

        for (Player player : game.playerByInitiativeList)
        {
            add(new InitializationPlayerRecruitAction(game, player, parent));
        }

    }

}
