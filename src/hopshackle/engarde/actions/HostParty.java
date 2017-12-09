package hopshackle.engarde.actions;

import hopshackle.engarde.*;
import hopshackle.simulation.*;

public class HostParty extends Action<Gentleman> {

    public HostParty(Gentleman chap) {
        super(EnGardeActions.HOST_PARTY, chap, 0, 10, true);
    }

    @Override
    protected void initialisation() {
        // set location
        actor.setLocation(actor.getClub());
    }

    @Override
    protected void doStuff() {
        actor.log("Carouses at " + actor.getClub().getName());
        actor.addGold(-actor.getSocialLevel());
        actor.addStatus(1);
    }
}
