package hopshackle.engarde;

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
        return 0;
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
    public void learnFrom(ExperienceRecord<Gentleman> exp, double maxResult) {}
}
