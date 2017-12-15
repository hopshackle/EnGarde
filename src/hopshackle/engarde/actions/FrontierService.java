package hopshackle.engarde.actions;

import hopshackle.simulation.*;
import hopshackle.engarde.*;
import hopshackle.engarde.military.*;

public class FrontierService extends Action<Gentleman> {

    private static Front frontierRegiment;


    public static void setFront(Front loc) {
        frontierRegiment = loc;
    }
    public static Front getFront() {
        return frontierRegiment;
    }



    public FrontierService(Gentleman volunteer) {
        super(EnGardeActions.FIGHT_ON_FRONTIER, volunteer, 2, 38, true);
    }

    @Override
    protected void initialisation() {
        if (frontierRegiment == null) throw new AssertionError("Frontier Regiment location not initialised");
        actor.setLocation(frontierRegiment);
        actor.log("Spends month on campaign with the Frontier Regiment");
    }

    @Override
    protected void doStuff() {
        frontierRegiment.individualResults(actor);
    }

    @Override
    public void doNextDecision() {
        if (actor.isDead()) return;
        int month = (int) (actor.getWorld().getCurrentTime() % 480 / 40 + 1);
        if (getState() == State.CANCELLED || month == 3 || month == 6 || month == 9 || month == 12) {
            super.doNextDecision();
        } else {
            // stay on campaign
            (new FrontierService(actor)).addToAllPlans();
        }
    }

}
