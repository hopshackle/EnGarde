package hopshackle.engarde.actions;

import hopshackle.engarde.military.Rank;
import hopshackle.engarde.social.SocialMeeting;
import hopshackle.simulation.*;
import hopshackle.engarde.*;

import java.util.*;

public enum EnGardeActions implements ActionEnum<Gentleman> {

    JOIN_REGIMENT,
    REGIMENTAL_SERVICE,
    CAROUSE,
    HOST_INTIMATE_PARTY,
    HOST_INCLUSIVE_PARTY,
    HOST_EXCLUSIVE_PARTY,
    FIGHT_ON_FRONTIER;

    @Override
    public String getChromosomeDesc() {
        return "ENGARDE";
    }

    @Override
    public Action<Gentleman> getAction(Gentleman gentleman) {
        switch (this) {
            case JOIN_REGIMENT:
                return new JoinRegiment(gentleman);
            case REGIMENTAL_SERVICE:
                return new RegimentalService(gentleman);
            case CAROUSE:
                return new Carouse(gentleman);
            case HOST_INTIMATE_PARTY:
                List<Agent> guests = gentleman.getRelationships(Relationship.FRIEND);
                return new HostParty(HOST_INTIMATE_PARTY, gentleman, HopshackleUtilities.convertList(guests));
            case HOST_INCLUSIVE_PARTY:
                List<Agent> friends = gentleman.getRelationships(Relationship.FRIEND);
                Set<Agent> guestSet = new HashSet<>();
                for (Agent friend : friends) {
                    guestSet.add(friend);
                    for (Agent friendOfFriend : friend.getRelationships(Relationship.FRIEND)) {
                        if (gentleman.getRelationshipWith(friendOfFriend) != Relationship.ENEMY)
                            guestSet.add(friendOfFriend);
                    }
                }
                guestSet.remove(gentleman);
                guests = HopshackleUtilities.convertSetToList(guestSet);
                return new HostParty(HOST_INCLUSIVE_PARTY, gentleman, HopshackleUtilities.convertList(guests));
            case HOST_EXCLUSIVE_PARTY:
                friends = gentleman.getRelationships(Relationship.FRIEND);
                guestSet = new HashSet<>();
                for (Agent friend : friends) {
                    guestSet.add(friend);
                    for (Agent fof : friend.getRelationships(Relationship.FRIEND)) {
                        Gentleman friendOfFriend = (Gentleman) fof;
                        if (gentleman.getSocialLevel() - friendOfFriend.getSocialLevel() <= 3)
                            if (SocialMeeting.relationshipModifier(gentleman, friendOfFriend) >= 0)
                                guestSet.add(friendOfFriend);
                    }
                }
                guestSet.remove(gentleman);
                guests = HopshackleUtilities.convertSetToList(guestSet);
                return new HostParty(HOST_EXCLUSIVE_PARTY, gentleman, HopshackleUtilities.convertList(guests));
            case FIGHT_ON_FRONTIER:
                return new FrontierService(gentleman);
        }
        return null;
    }

    @Override
    public boolean isChooseable(Gentleman gentleman) {
        int currentWeek = (int) (gentleman.getWorld().getCurrentTime() % 40) / 10 + 1;
        switch (this) {
            case REGIMENTAL_SERVICE:
                if (gentleman.getRank() == Rank.PRIVATE && gentleman.getWeeksOfService() < 2) {
                    return true;
                }
                if (gentleman.getRank() == Rank.SUBALTERN && gentleman.getWeeksOfService() < 1) {
                    return true;
                }
                if ((gentleman.getRank() == Rank.PRIVATE || gentleman.getRank() == Rank.SUBALTERN) && currentWeek == 4)
                    return true;
                return false;
            case HOST_INTIMATE_PARTY:
            case HOST_EXCLUSIVE_PARTY:
            case HOST_INCLUSIVE_PARTY:
                if (gentleman.getClub().getID() == 0)
                    return false;       // not a member of a club
                if (gentleman.getRelationships(Relationship.FRIEND).isEmpty())  // no friends to invite
                    return false;
                return true;
            case FIGHT_ON_FRONTIER:
                if (gentleman.atFront())
                    return false;
                if (currentWeek != 4)
                    return false;
            default:
                return true;
        }

    }

    @Override
    public Enum<?> getEnum() {
        return this;
    }
}
