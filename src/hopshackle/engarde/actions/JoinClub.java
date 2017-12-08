package hopshackle.engarde.actions;

import hopshackle.simulation.*;
import hopshackle.engarde.*;
import hopshackle.engarde.social.*;

import java.util.*;

public class JoinClub extends Action<Gentleman> {

    public JoinClub(Gentleman a, long startOffset) {
        super(EnGardeActions.JOIN_CLUB, HopshackleUtilities.listFromInstance(a), new ArrayList<Gentleman>(), startOffset, 10, true);
    }

    public void doStuff() {
        Club bestClub = null;
        Club currentClub = actor.getClub();
        for (Club c : Club.allClubs()) {
            if (c.isEligible(actor) && c != currentClub) {
                if (currentClub == null)
                    bestClub = c;
                else if (bestClub != null && bestClub.getMonthlyStatus() > currentClub.getMonthlyStatus())
                    bestClub = c;
            }
        }
        if (bestClub != null) {
            bestClub.newMember(actor);
            actor.setClub(bestClub);
            actor.log("Joins " + bestClub.toString());
        }
    }
}
