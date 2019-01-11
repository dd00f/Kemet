package kemet.ai;

import java.io.Serializable;
import java.util.List;

import kemet.data.choice.Choice;
import kemet.data.state.State;
import kemet.model.Game;
import kemet.model.Player;
import kemet.model.action.PlayerChoicePick;

public abstract class PlayerActor implements Serializable
{

    public Player player;
    public Game game;

    public PlayerActor(Player player, Game game)
    {
        this.player = player;
        this.game = game;

    }

    public abstract Choice pickActionOld(List<Choice> choiceList);

    public State pickActionAndActivateOld(List<Choice> choiceList)
    {
        return pickActionOld(choiceList).activate();

    }

    public void printChoiceListOld(List<Choice> choiceList)
    {
        System.out.println("");
        System.out.println(player.name + " with " + player.actionTokenLeft + " actions left and " +
            player.getPrayerPoints() + " prayer points. Pick Choice : ");
        int count = 1;
        for (Choice choice : choiceList)
        {
            System.out.println("  " + count++ + " " + choice);
        }
    }
    
    public void printChoiceList(List<kemet.model.action.choice.Choice> choiceList)
    {
        System.out.println("");
        System.out.println(player.name + " with " + player.actionTokenLeft + " actions left and " +
            player.getPrayerPoints() + " prayer points. Pick Choice : ");
        int count = 1;
        for (kemet.model.action.choice.Choice choice : choiceList)
        {
            System.out.println("  " + count++ + " " + choice);
        }
    }

    public void pickActionAndActivate(PlayerChoicePick pick)
    {

        pickActionAndActivate(pick.choiceList);

    }
    
    public abstract kemet.model.action.choice.Choice pickAction(List<kemet.model.action.choice.Choice> choiceList);


    private void pickActionAndActivate(List<kemet.model.action.choice.Choice> choiceList)
    {
        pickAction(choiceList).activate();
    }

}
