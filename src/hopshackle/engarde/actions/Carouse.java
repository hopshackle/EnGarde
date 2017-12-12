package hopshackle.engarde.actions;

import hopshackle.simulation.*;
import hopshackle.engarde.*;
import hopshackle.engarde.social.*;

public class Carouse extends Action<Gentleman> {

    public Carouse(Gentleman chap) {
        super(EnGardeActions.CAROUSE, chap, 2, 8, true);
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
