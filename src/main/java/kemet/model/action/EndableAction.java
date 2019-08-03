package kemet.model.action;

import kemet.model.KemetGame;

public abstract class EndableAction implements Action, Endable
{

    /**
     * 
     */
    private static final long serialVersionUID = -93038657746935945L;

    private boolean ended = false;

    @Override
    public void end()
    {
        ended = true;
    }

    @Override
    public boolean isEnded()
    {
        return ended;
    }
    
    @Override
    public void relink(KemetGame clone) {
    	// nothing to do
    	
    }
    
    public void copy(EndableAction copy) {
    	copy.ended = ended;
    }
    
    public void clear() {
    	
    }
    
    @Override
    public final void initialize() {
    	ended = false;
    	internalInitialize();
    }
    
    public abstract void internalInitialize();

    @Override
    public void enterSimulationMode(int playerIndex) {
    	
    }
    
}
