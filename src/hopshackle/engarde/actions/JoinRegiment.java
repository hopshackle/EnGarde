package hopshackle.engarde.actions;

import hopshackle.engarde.military.Rank;
import hopshackle.engarde.military.Regiment;
import hopshackle.simulation.*;
import hopshackle.engarde.*;

import java.util.*;

public class JoinRegiment extends Action<Gentleman> {

    public JoinRegiment(Gentleman a) {
        super(EnGardeActions.JOIN_REGIMENT, HopshackleUtilities.listFromInstance(a), new ArrayList<Gentleman>(), 0, 10, true);
    }

    protected void doStuff() {
        /* We apply to a random regiment that is better than the current one
         *  TODO: Make this more intelligent and/or apply personality traits for risk-taking and chutzpah */
        Regiment current = actor.getRegiment();
        int cash = (int) actor.getGold();
        boolean considerCavalry = (cash < 200) ? false : true;
        Regiment target = null;
        List<Regiment> possibleRegiments = new ArrayList<>();
        int currentRegimentId = (current == null) ? 99 : current.getID();
        int currentMonthlyStatus = (current == null) ? -1 : current.getMonthlyStatus(actor.getRank());
        for (Regiment r : Regiment.allRegiments()) {
            if (r.getID() < currentRegimentId
                    && r.getMinSL(Rank.PRIVATE) <= actor.getSocialLevel()
                    && r.getMonthlyStatus(Rank.PRIVATE) > currentMonthlyStatus
                    && (considerCavalry || !r.isCavalry())
                    ) {
                possibleRegiments.add(r);
            }
        }
        if (!possibleRegiments.isEmpty()) {
            target = possibleRegiments.get(Dice.roll(1, possibleRegiments.size()) - 1);

            boolean success = target.applyToRegiment(actor);
            if (success) purchaseRank();
        }
        return;
    }


    private void purchaseRank() {
        int cash = (int) actor.getGold();
        Regiment reg = actor.getRegiment();
        if (actor.getRank() == Rank.PRIVATE && actor.getSocialLevel() >= reg.getMinSL(Rank.SUBALTERN) && cash > reg.getCommissionCost(Rank.SUBALTERN)) {
            actor.log("Buys Subalterncy");
            actor.addGold(-reg.getCommissionCost(Rank.SUBALTERN));
            actor.setRank(Rank.SUBALTERN);
        }
        cash = (int) actor.getGold();
        if (actor.getRank() == Rank.SUBALTERN && reg.hasVacancy(Rank.CAPTAIN) &&
                actor.getSocialLevel() >= reg.getMinSL(Rank.SUBALTERN) && cash > reg.getCommissionCost(Rank.CAPTAIN)) {
            actor.log("Buys Captaincy");
            actor.addGold(-reg.getCommissionCost(Rank.CAPTAIN));
            actor.setRank(Rank.CAPTAIN);
            reg.promote(actor);
        }
    }
}
