package hopshackle.engarde.actions;

import hopshackle.simulation.*;
import hopshackle.engarde.*;
import hopshackle.engarde.social.*;

import java.util.*;

public class RegimentalService extends Action<Gentleman> {

    public RegimentalService(Gentleman a) {
        super(EnGardeActions.REGIMENTAL_SERVICE, HopshackleUtilities.listFromInstance(a), new ArrayList<Gentleman>(), 0, 10, true);
    }

    public void initialisation() {
        actor.setLocation(actor.getRegiment());
    }

    public void doStuff() {
        actor.doWeekOfService();
        actor.log("Spends week on regimental duties");
        // TODO: Add in socialising with other people in the regiment at the time
        // problem is that when we execute, we will have some cross-over between people
        // in the regiment this week, and those in the regiment last week who have
        // not yet made their decision

        // a solution could be to launch a TimerTask for each location to trigger mid-period, when
        // we will have the correct population present in each location (Regiments and Clubs)
    }
}
