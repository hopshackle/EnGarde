package hopshackle.engarde.actions;

import hopshackle.simulation.*;
import hopshackle.engarde.*;
import hopshackle.engarde.social.*;

import java.util.*;

public class RegimentalService extends Action<Gentleman> {

    public RegimentalService(Gentleman a) {
        super(EnGardeActions.REGIMENTAL_SERVICE, HopshackleUtilities.listFromInstance(a), new ArrayList<Gentleman>(), 2, 8, true);
    }

    public void initialisation() {
        actor.setLocation(actor.getRegiment());
    }

    public void doStuff() {
        actor.doWeekOfService();
        actor.log("Spends week on regimental duties");
    }
}
