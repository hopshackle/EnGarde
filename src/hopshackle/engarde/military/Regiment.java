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

    private boolean onCampaign;

    private EntityLog regimentalLog;

    private Regiment(RegimentID regimentID, World world) {
        super(regimentID.toString(), world, new ArrayList<>());
        rid = regimentID;
        regimentalLog = new EntityLog(rid.toString(), world);
    }

    public int getID() {
        return rid.getID();
    }

    public String abbrev() {
        return rid.abbrev();
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
                officer.log("Promoted to Subaltern");
                log(officer.toString() + " promoted to Subaltern.");
                officer.setRank(Rank.SUBALTERN);
                break;
            case SUBALTERN:
                if (hasVacancy(Rank.CAPTAIN)) {
                    officer.log("Promoted to Captain");
                    log(officer.toString() + " promoted to Captain.");
                    officer.setRank(Rank.CAPTAIN);
                    setJuniorCaptain(officer);
                } else {
                    officer.mentionedInDispatches();
                }
                break;
            case CAPTAIN:
                if (hasVacancy(Rank.MAJOR)) {
                    officer.log("Promoted to Major");
                    log(officer.toString() + " promoted to Major.");
                    officer.setRank(Rank.MAJOR);
                    setJuniorMajor(officer);
                } else {
                    officer.mentionedInDispatches();
                }
                break;
            case MAJOR:
                if (hasVacancy(Rank.LT_COLONEL)) {
                    officer.log("Promoted to Lt-Colonel");
                    log(officer.toString() + " promoted to Lt-Colonel.");
                    officer.setRank(Rank.LT_COLONEL);
                    for (int i = 0; i < 2; i++)
                        if (major[i] == officer) {
                            major[i] = null;
                            majorID[i] = 0;
                        }
                    ltColonel = officer;
                    ltColonelID = officer.getUniqueID();
                } else {
                    officer.mentionedInDispatches();
                }
                break;
            case LT_COLONEL:
                if (hasVacancy(Rank.COLONEL)) {
                    officer.log("Promoted to Colonel");
                    log(officer.toString() + " promoted to Colonel.");
                    officer.setRank(Rank.COLONEL);
                    colonel = officer;
                    colonelID = officer.getUniqueID();
                    ltColonel = null;
                    ltColonelID = 0;
                } else {
                    officer.mentionedInDispatches();
                }
                break;
            case COLONEL:
                officer.log("Promoted to Brigadier-General");
                log(officer.toString() + " promoted to Brigadier-General.");
                officer.setRank(Rank.BRIG_GENERAL);
                colonel = null;
                colonelID = 0;
                officer.setRegiment(null);
                break;
            case BRIG_GENERAL:
                officer.log("Promoted to Lt-General");
                officer.setRank(Rank.LT_GENERAL);
                break;
            case LT_GENERAL:
                officer.log("Promoted to General");
                officer.setRank(Rank.GENERAL);
                break;
            default:
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
        if (lowestFreeIndex == 2)
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

    public void endOfMonthCommissions() {
        if (colonelID == 0 && ltColonelID != 0) {
            if (ltColonel.getSocialLevel() >= getMinSL(Rank.COLONEL) && ltColonel.getGold() >= getCommissionCost(Rank.COLONEL)) {
                ltColonel.log("Buys Colonelcy");
                log(ltColonel.toString() + " buys Colonelcy");
                ltColonel.addGold(-getCommissionCost(Rank.COLONEL));
                promote(ltColonel);
            }
        }
        for (int i = 0; i < 2; i++) {
            if (ltColonelID == 0 && majorID[i] != 0) {
                if (major[i].getSocialLevel() >= getMinSL(Rank.LT_COLONEL) && major[i].getGold() >= getCommissionCost(Rank.LT_COLONEL)) {
                    major[i].log("Buys Lt-Colonelcy");
                    log(major[i].toString() + " buys Lt-Colonelcy");
                    major[i].addGold(-getCommissionCost(Rank.LT_COLONEL));
                    promote(major[i]);
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            if (majorID[1] == 0 && captainID[i] != 0) {
                if (captain[i].getSocialLevel() >= getMinSL(Rank.MAJOR) && captain[i].getGold() >= getCommissionCost(Rank.MAJOR)) {
                    if (captain[i].getRank() != Rank.CAPTAIN)
                        throw new AssertionError(captain[i] + " is occupying captain slot " + (i + 1));
                    captain[i].log("Buys Majority");
                    log(captain[i].toString() + " buys Majority");
                    captain[i].addGold(-getCommissionCost(Rank.MAJOR));
                    promote(captain[i]);
                }
            }
        }
        List<Gentleman> allSubalterns = new ArrayList<>();
        List<Gentleman> allPrivates = new ArrayList<>();
        for (Gentleman g : getCurrentMembership()) {
            if (g.getRank() == Rank.SUBALTERN)
                allSubalterns.add(g);
            if (g.getRank() == Rank.PRIVATE)
                allPrivates.add(g);
        }
        Collections.sort(allSubalterns, new Comparator<Gentleman>() {
            @Override
            public int compare(Gentleman o1, Gentleman o2) {
                return -(o1.getSocialLevel() + o1.getMentions() / 2 + o1.getAge() / 5) +
                        (o2.getSocialLevel() + o2.getMentions() / 2 + o2.getAge() / 5);
            }
        });

        for (Gentleman subaltern : allSubalterns) {
            if (captainID[5] == 0) {
                if (subaltern.getSocialLevel() >= getMinSL(Rank.CAPTAIN) && subaltern.getGold() >= getCommissionCost(Rank.CAPTAIN)) {
                    subaltern.log("Buys Captaincy");
                    log(subaltern.toString() + " buys Captaincy");
                    subaltern.addGold(-getCommissionCost(Rank.CAPTAIN));
                    promote(subaltern);
                }
            } else {
                break;
            }
        }
        for (Gentleman pte : allPrivates) {
            if (pte.getSocialLevel() >= getMinSL(Rank.SUBALTERN) && pte.getGold() >= getCommissionCost(Rank.SUBALTERN)) {
                pte.log("Buys Subalterncy");
                log(pte.toString() + " buys Subalterncy");
                pte.addGold(-getCommissionCost(Rank.SUBALTERN));
                promote(pte);
            }
        }
    }

    @Override
    public void memberLeaves(Gentleman g) {
        super.memberLeaves(g);
        if (g.isDead())
            log(g.toString() + " dies");
        else
            log(g.toString() + " leaves regiment");
        if (colonel == g) {
            colonel = null;
            colonelID = 0;
        }
        if (ltColonel == g) {
            ltColonel = null;
            ltColonelID = 0;
        }
        for (int i = 0; i < 2; i++) {
            if (major[i] == g) {
                major[i] = null;
                majorID[i] = 0;
            }
        }
        for (int i = 0; i < 6; i++) {
            if (captain[i] == g) {
                captain[i] = null;
                captainID[i] = 0;
            }
        }
        tidyUpOfficers();
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
                for (int j = i + 1; j < 6; j++) {
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
                if (seniority > 0) battalionCommanders[seniority - 1] = major[i];
                seniority++;
            }
        }
        for (int i = 0; i < 6; i++) {
            if (seniority > 3) return;
            if (captainID[i] != 0) {
                if (seniority == 0) commander = captain[i];
                if (seniority > 0 && seniority < 3) battalionCommanders[seniority - 1] = captain[i];
                if (seniority == 3) adjutant = captain[i];
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
        log(applicant.toString() + " joins Regiment");
        this.newMember(applicant);
        applicant.setRegiment(this);
        applicant.setRank(Rank.PRIVATE);
        if (isCavalry()) applicant.addGold(-100);
    }

    public int relationshipWith(Regiment other) {
        return rid.relationShipWith(other.rid);
    }

    public Gentleman getCommander() {
        return commander;
    }

    public Gentleman getAdjutant() {
        return adjutant;
    }

    public void log(String msg) {
        regimentalLog.log(msg);
    }

    public void flushLog() {
        regimentalLog.flush();
    }

    public boolean onCampaign() {
        return onCampaign;
    }

    public void setOnCampaign(boolean flag) {
        onCampaign = flag;
    }

    public int deathMod() {
        return rid.getDeathMod();
    }

    public int mentionMod() {
        return rid.getMentionMod();
    }

    public int promotionMod() {
        return rid.getPromotionMod();
    }

    public int plunderMod() {
        return rid.getPlunderMod();
    }
}
