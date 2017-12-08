package hopshackle.engarde.military;

import hopshackle.engarde.Gentleman;
import hopshackle.simulation.*;

import java.util.*;

public class Regiment extends Organisation<Gentleman> {

    static List<Regiment> regiments = new ArrayList<Regiment>();

    public static void instantiate(World world) {
        for (RegimentID rid : RegimentID.values()) {
            if (rid != RegimentID.NONE)
                regiments.add(new Regiment(rid, world));
        }
    }

    public static List<Regiment> allRegiments() {
        return HopshackleUtilities.cloneList(regiments);
    }

    private RegimentID rid;
    private long colonelID, ltColonelID;
    private long[] majorID = new long[2];
    private long[] captainID = new long[6];

    private Gentleman colonel, ltColonel;
    private Gentleman[] major = new Gentleman[2];
    private Gentleman[] captain = new Gentleman[6];

    private Gentleman commander;
    private Gentleman adjutant;

    private Gentleman[] battalionCommanders = new Gentleman[3];

    private Regiment(RegimentID regimentID, World world) {
        super(regimentID.toString(), new Location(world), new ArrayList<>());
        rid = regimentID;
    }

    public int getID() {
        return rid.getID();
    }

    public int getMinSL(Rank r) {
        return rid.minSL(r);
    }

    public int getMonthlyPay(Rank r) {
        return rid.monthlyPay(r);
    }

    public int getMonthlyStatus(Rank r) {
        return rid.monthlyStatus(r);
    }

    public boolean isCavalry() {
        return rid.isCavalry();
    }

    public int getCommissionCost(Rank r) {
        int retValue = rid.commissionPurchaseCost(r);
        if (!isCavalry() && r == Rank.CAPTAIN) retValue += 100; // a horse
        if (r == Rank.MAJOR) retValue += 200;   // 2 more horses
        return retValue;
    }

    public void promote(Gentleman officer) {
        if (!isCurrentMember(officer))
            throw new AssertionError(officer + " is not a member of " + this + " to be promoted");
        switch (officer.getRank()) {
            case PRIVATE:
                officer.setRank(Rank.SUBALTERN);
                break;
            case SUBALTERN:
                if (hasVacancy(Rank.CAPTAIN)) {
                    officer.setRank(Rank.CAPTAIN);
                    setJuniorCaptain(officer);
                    officer.log("Promoted to Captain");
                } else {
                    // TODO: gain additional mention instead
                }
                break;
            case CAPTAIN:
                if (hasVacancy(Rank.MAJOR)) {
                    officer.setRank(Rank.MAJOR);
                    setJuniorMajor(officer);
                    officer.log("Promoted to Major");
                } else {
                    // TODO: gain additional mention instead
                }
                break;
            case MAJOR:
                if (hasVacancy(Rank.LT_COLONEL)) {
                    officer.setRank(Rank.LT_COLONEL);
                    for (int i = 0; i < 2; i++)
                        if (major[i] == officer) {
                            major[i] = null;
                            majorID[i] = 0;
                        }
                    ltColonel = officer;
                    ltColonelID = officer.getUniqueID();
                    officer.log("Promoted to Lt-Colonel");
                } else {
                    // TODO: gain additional mention instead
                }
                break;
            case LT_COLONEL:
                if (hasVacancy(Rank.COLONEL)) {
                    officer.setRank(Rank.COLONEL);
                    colonel = officer;
                    colonelID = officer.getUniqueID();
                    ltColonel = null;
                    ltColonelID = 0;
                    officer.log("Promoted to Colonel");
                } else {
                    // TODO: gain additional mention instead
                }
                break;
            case COLONEL:
                officer.setRank(Rank.BRIG_GENERAL);
                colonel = null;
                colonelID = 0;
                break;
            default:
                throw new AssertionError("Cannot be promoted from " + officer.getRank());
        }
        tidyUpOfficers();
    }

    public boolean hasVacancy(Rank rank) {
        switch (rank) {
            case PRIVATE:
            case SUBALTERN:
                return true;
            case CAPTAIN:
                return captainID[5] == 0;
            case MAJOR:
                return majorID[1] == 0;
            case LT_COLONEL:
                return ltColonelID == 0;
            case COLONEL:
                return colonelID == 0;
            default:
                throw new AssertionError(rank + " not relevant to a regiment");
        }
    }

    private void setJuniorCaptain(Gentleman officer) {
        int lowestFreeIndex = 6;
        for (int i = 5; i >= 0; i--)
            if (captainID[i] == 0) lowestFreeIndex = i;
        if (lowestFreeIndex == 6)
            throw new AssertionError("No Free Captain slots in " + this);
        captainID[lowestFreeIndex] = officer.getUniqueID();
        captain[lowestFreeIndex] = officer;
    }

    private void setJuniorMajor(Gentleman officer) {
        int lowestFreeIndex = 2;
        for (int i = 1; i >= 0; i--)
            if (majorID[i] == 0) lowestFreeIndex = i;
        if (lowestFreeIndex == 6)
            throw new AssertionError("No Free Major slots in " + this);
        majorID[lowestFreeIndex] = officer.getUniqueID();
        major[lowestFreeIndex] = officer;
        for (int i = 0; i < 6; i++) {
            if (captainID[i] == officer.getUniqueID()) {
                captainID[i] = 0;
                captain[i] = null;
            }
        }
    }

    private void tidyUpOfficers() {
        if (majorID[0] == 0 && majorID[1] != 0) {
            majorID[0] = majorID[1];
            major[0] = major[1];
            majorID[1] = 0;
            major[1] = null;
        }
        for (int i = 0; i < 6; i++) {
            if (captainID[i] == 0) {
                for (int j = i+1; j < 6; j++) {
                    if (captainID[j] != 0) {
                        captainID[i] = captainID[j];
                        captain[i] = captain[j];
                        break;
                    }
                }
            }
        }
        determineCommandingOfficers();
    }

    private void determineCommandingOfficers() {
        commander = null;
        battalionCommanders = new Gentleman[3];
        int seniority = 0;
        if (colonelID != 0) {
            seniority++;
            commander = colonel;
        }
        if (ltColonelID != 0) {
            if (seniority == 0)
                commander = ltColonel;
            else
                battalionCommanders[0] = ltColonel;
            seniority++;
        }
        for (int i = 0; i < 2; i++) {
            if (majorID[i] != 0) {
                if (seniority == 0) commander = major[i];
                if (seniority > 0) battalionCommanders[seniority-1] = major[i];
                seniority++;
            }
        }
        for (int i = 0; i < 6; i++) {
            if (seniority > 2) return;
            if (captainID[i] != 0) {
                if (seniority == 0) commander = captain[i];
                if (seniority > 0) battalionCommanders[seniority-1] = captain[i];
                seniority++;
            }
        }
    }

    public boolean applyToRegiment(Gentleman applicant) {
        int target = rid.recruitmentTarget(applicant.getSocialLevel());
        if (applicant.getRegiment() != null)
            target += 2;
        if (Dice.roll(1, 6) >= target) {
            acceptApplicant(applicant);
            return true;
        }
        return false;
    }

    public void acceptApplicant(Gentleman applicant) {
        applicant.log("Applies successfully to " + this.toString());
        this.newMember(applicant);
        applicant.setRank(Rank.PRIVATE);
        applicant.setRegiment(this);
        if (isCavalry()) applicant.addGold(-100);
    }
}
