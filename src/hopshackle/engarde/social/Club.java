package hopshackle.engarde.social;

import hopshackle.simulation.*;
import hopshackle.engarde.*;

import java.util.*;

public class Club extends Organisation<Gentleman> {

    static List<Club> clubs = new ArrayList<>();

    public static List<Club> allClubs() {
        return HopshackleUtilities.cloneList(clubs);
    }

    public static Club bestClubEligibleFor(Gentleman gentleman) {
        Club bestClub = null;
        for (Club c : clubs) {
            if (c.isEligible(gentleman) && (bestClub == null || bestClub.getID() < c.getID()))
                bestClub = c;
        }
        return bestClub;
    }


    public static void instantiate(World world) {
        for (ClubID cid : ClubID.values()) {
            clubs.add(new Club(cid, world));
        }
    }

    private ClubID clubID;

    public Club(ClubID cid, World world) {
        super(cid.fullName, world, new ArrayList<>());
        clubID = cid;
    }

    public boolean isEligible(Gentleman officer) {
        return clubID.isEligible(officer);
    }

    public int getMonthlyStatus() {
        return clubID.monthlyStatus;
    }

    public int getMonthlyDues() {
        return clubID.monthlyDues;
    }

    public int getID() {
        return clubID.id;
    }

}
