package hopshackle.engarde.actions;

import hopshackle.simulation.*;
import hopshackle.engarde.*;

public class ActionPolicy extends Policy<Action<Gentleman>> {

    public ActionPolicy() {
        super("action");
    }

    @Override
    public double getValue(Action<Gentleman> proposal, Agent agent) {
        if (agent instanceof Gentleman) {
            Gentleman g = (Gentleman) agent;
            if (proposal instanceof FrontierService)
                return 100.0;
            if (proposal instanceof RegimentalService)
                return 50.0;
            if (proposal instanceof HostParty) {
                HostParty party = (HostParty) proposal;
                if (party.getHost() == agent)
                    return 4.0 + 2.0 * (party.getAllConfirmedParticipants().size() - 1);
                int slDiff =  party.getHost().getSocialLevel() - g.getSocialLevel();
                int clubDiff = party.getLocation().getID() - g.getClub().getID();
                return 5.0 + slDiff + clubDiff;
            }
            if (proposal instanceof JoinRegiment && g.getRegiment() == null)
                return 25.0;
        }
        // otherwise use default value
        return 0.0;
    }

}
