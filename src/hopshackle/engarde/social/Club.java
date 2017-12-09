package hopshackle.engarde.social;

import hopshackle.simulation.*;
import hopshackle.engarde.*;
import java.util.*;

public class Club extends Organisation<Gentleman> {

    static List<Club> clubs = new ArrayList<>();
    static Club bawdyHouse;

    public static List<Club> allClubs() {
        return HopshackleUtilities.cloneList(clubs);
    }

    public static void instantiate(World world) {
        for (ClubID cid : ClubID.values()) {
            if (cid == ClubID.BAWDYHOUSE) {
                bawdyHouse = new Club(cid, world);
            } else {
                clubs.add(new Club(cid, world));
            }
        }
    }
    public static Club getBawdyHouse() {
        return bawdyHouse;
    }

    private ClubID clubID;

    public Club(ClubID cid, World world) {
        super(cid.toString(), new Location((world)), new ArrayList<>());
        clubID = cid;
        clubs.add(this);
    }

    public boolean isEligible(Gentleman officer) {
        return clubID.isEligible(officer);
    }

    public int getMonthlyStatus() {return clubID.monthlyStatus;}
    public int getMonthlyDues() {return clubID.monthlyDues;}
    public int getID() {return clubID.id;}
}
