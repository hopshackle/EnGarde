package hopshackle.engarde.social;

import hopshackle.engarde.*;
import hopshackle.engarde.military.*;

public enum ClubID {

    BAWDYHOUSE          (0, "Bawdyhouse", "Bawdy", 0, 0, 0, 0, 0),
    RED_PHILLIPPES      (1, "Red Phillippes", "RP", 3, 5, 2, 50, 150),
    FROG_AND_PEACH      (2, "The Frog & Peach", "F&P", 5, 10, 3, 100, 150),
    BLUE_GABLES         (3, "The Blue Gables", "BG", 7, 15, 4, 150, 200),
    HORSE_GUARDS        (4, "The Horse Guards", "HG", 0, 20, 4, 250, 300),
    HUNTERS             (5, "Hunter's", "H", 9, 20, 6, 200, 300),
    BOTHWELLS           (6, "Bothwell's", "B", 12, 30, 8, 10000, 500);

    public final int minimumSL, monthlyDues, monthlyStatus, gamblingLimit, gamblingDivisor, id;
    public final String fullName, abbreviation;

    ClubID(int id, String name, String abbrev, int minSL, int dues, int statusGain, int limit, int divisor) {
        this.id = id;
        fullName = name;
        abbreviation = abbrev;
        minimumSL = minSL;
        monthlyDues = dues;
        monthlyStatus = statusGain;
        gamblingLimit = limit;
        gamblingDivisor = divisor;
    }

    public boolean isEligible(Gentleman g) {
        if (g.getSocialLevel() < minimumSL) return false;
        if (g.getGold() < monthlyDues) return false;
        switch (this) {
            case BAWDYHOUSE:
                return false;       // is the default option
            case HORSE_GUARDS:
                Regiment reg = g.getRegiment();
                if (reg != null && (reg.getID() == 4 || reg.getID() == 5) && g.getRank() != Rank.PRIVATE)
                    return true;
                return false;
                // TODO: This does not yet include officers in the Brigade (i.e. the Brigadier)
            default:
                return true;
        }
    }
}
