package hopshackle.engarde.military;

import hopshackle.engarde.Gentleman;
import hopshackle.simulation.*;

import java.util.*;

public class Regiment extends Organisation<Gentleman> {

    static List<Regiment> regiments = new ArrayList<Regiment>();

    static {
        for (RegimentID rid : RegimentID.values()) {
            if (rid != RegimentID.NONE)
                regiments.add(new Regiment(rid));
        }
    }

    public static List<Regiment> allRegiments() {
        return HopshackleUtilities.cloneList(regiments);
    }

    private RegimentID rid;

    private Regiment(RegimentID regimentID) {
        super(regimentID.toString(), null, new ArrayList<>());
        rid = regimentID;
    }

    public int getID() {
        return rid.getID();
    }
    public int getMinSL(Rank r) { return rid.minSL(r);}
    public int getMonthlyPay(Rank r) {return rid.monthlyPay(r);}
    public int getMonthlyStatus(Rank r) {return rid.monthlyStatus(r);}
    public boolean isCavalry() { return rid.isCavalry();}
    public int getCommissionCost(Rank r) {return rid.commissionPurchaseCost(r);}
    public boolean applyToRegiment(Gentleman applicant) {
        int target = rid.recruitmentTarget(applicant.getSocialLevel());
        if (Dice.roll(1, 6) >= target) {
            applicant.log("Applies successfully to " + this.toString());
            this.newMember(applicant);
            applicant.setRank(Rank.PRIVATE);
            applicant.setRegiment(this);
            if (isCavalry()) applicant.addGold(-100);
            return true;
        }
        return false;
    }
}
