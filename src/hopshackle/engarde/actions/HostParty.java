package hopshackle.engarde.actions;

import hopshackle.engarde.*;
import hopshackle.simulation.*;
import hopshackle.engarde.social.*;
import java.util.*;

public class HostParty extends Action<Gentleman> {

    private Club location;
    private EnGardeActions typeOfParty;

    public HostParty(EnGardeActions type, Gentleman host, List<Gentleman> guests) {
        super(type, HopshackleUtilities.listFromInstance(host), guests, 2, 8, true);
        location = actor.getClub();
        if (location == null)
            throw new AssertionError("Party hosted by " + host + " must take place somewhere!");
        typeOfParty = type;
    }

    @Override
    protected void initialisation() {
        // set location
        for (Agent attendee : getAllConfirmedParticipants()) {
            attendee.setLocation(location);
        }
    }

    @Override
    protected void doStuff() {
        switch (typeOfParty) {
            case HOST_INTIMATE_PARTY:
                actor.log("Hosts small party at " + location.getName() + " for " + (getAllConfirmedParticipants().size() - 1) + " friends");
                break;
            case HOST_EXCLUSIVE_PARTY:
                actor.log("Hosts party at " + location.getName() + " for " + (getAllConfirmedParticipants().size() - 1) + " in extended social circle");
                break;
            case HOST_INCLUSIVE_PARTY:
                actor.log("Hosts large party at " + location.getName() + " for " + (getAllConfirmedParticipants().size() - 1) + " acquaintances");
                break;
        }
        actor.addGold(-actor.getSocialLevel());
        actor.addStatus(1);

        int hostSL = actor.getSocialLevel();
        int clubRank = location.getID();
        for (Gentleman guest : getAllConfirmedParticipants()) {
            if (guest == actor) continue;
            guest.log("Is guest of " + actor + " at " + location);
            guest.addStatus(1);
            guest.addGold(-hostSL);
            int guestClubRank = guest.getClub().getID();
            guest.addStatus(Math.max(clubRank - guestClubRank, 0));

            Gentleman higher = null, lower = null;
            if (guest.getSocialLevel() > hostSL) {
                higher = guest;
                lower = actor;
            } else {
                higher = actor;
                lower = guest;
            }
            int slDiff = Math.abs(hostSL - guest.getSocialLevel());
            lower.addStatus(slDiff / 2);
            if (slDiff < 4) higher.addStatus( 1);
            if (slDiff > 6) higher.addStatus(-1);
            if (slDiff > 8) higher.addStatus(-1);
            if (slDiff > 10) higher.addStatus(-1);
        }

        // Then everyone meets new people
        new SocialMeeting(getAllConfirmedParticipants(), +2, 0);
    }

    public Gentleman getHost() {
        return actor;
    }
    public Club getLocation() {
        return location;
    }

    public String toString() {
        return "Party in " + actor.getWorld().getDate(getStartTime()) + " at " + location.toString() + " hosted by " + getHost().toString();
    }
}
