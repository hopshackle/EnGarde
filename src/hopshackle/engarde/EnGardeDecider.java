package hopshackle.engarde;

import hopshackle.engarde.actions.*;
import hopshackle.engarde.military.Rank;
import hopshackle.simulation.*;

import java.util.*;

public class EnGardeDecider extends BaseDecider<Gentleman> {

    public EnGardeDecider() {
        super(new StateFactory<Gentleman>() {
            @Override
            public State<Gentleman> getCurrentState(Gentleman agent) {
                return null;
            }

            @Override
            public <V extends GeneticVariable<Gentleman>> List<V> getVariables() {
                return null;
            }

            @Override
            public StateFactory<Gentleman> cloneWithNewVariables(List<GeneticVariable<Gentleman>> newVar) {
                return this;
            }
        });
    }

    @Override
    public double valueOption(ActionEnum<Gentleman> option, Gentleman decidingAgent) {
        switch ((EnGardeActions) option) {
            case JOIN_REGIMENT:
                if (decidingAgent.getRegiment() == null)
                    return 10.0;
                if (decidingAgent.getSocialLevel() - decidingAgent.getRegiment().getMinSL(decidingAgent.getRank()) > 7)
                    return 1.0;
                return -5.0;
            case REGIMENTAL_SERVICE:
                int currentWeek = (int) (decidingAgent.getWorld().getCurrentTime() % 40) / 10 + 1;
                if (currentWeek == 4) return 0.0;
                    // bit of a fudge this. weeksOfService is only cleared after the decision for the next month has been taken
                int currentService = decidingAgent.getWeeksOfService();
                if (decidingAgent.getRank() == Rank.PRIVATE && currentService < 2) {
                    if (1 - currentWeek + currentService < 0)
                        return 100.0;
                    return 0.0;
                }
                if (decidingAgent.getRank() == Rank.SUBALTERN && currentService < 1) {
                    if (2 - currentWeek + currentService < 0)
                        return 100.0;
                    return 0.0;
                }
                return -10;
            case HOST_INTIMATE_PARTY:
                return -1;
            case HOST_EXCLUSIVE_PARTY:
                return -3;
            case HOST_INCLUSIVE_PARTY:
                return -5;
            case FIGHT_ON_FRONTIER:
                if (decidingAgent.getGold() < decidingAgent.getSocialLevel() * 2)
                    return 2.0;
                if (decidingAgent.getGold() < 0)
                    return 10.0;
                return -10;
            default:
                return 0;
        }
    }

    @Override
    public double valueOption(ActionEnum<Gentleman> option, State<Gentleman> state) {
        return 0;
    }

    @Override
    public List<Double> valueOptions(List<ActionEnum<Gentleman>> options, Gentleman decidingAgent) {
        List<Double> retValue = new ArrayList<>();
        for (ActionEnum<Gentleman> option : options) {
            retValue.add(valueOption(option, decidingAgent));
        }
        return retValue;
    }

    @Override
    public void learnFrom(ExperienceRecord<Gentleman> exp, double maxResult) {
    }
}
