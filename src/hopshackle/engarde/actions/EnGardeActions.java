package hopshackle.engarde.actions;

import hopshackle.simulation.*;
import hopshackle.engarde.*;

public enum EnGardeActions implements ActionEnum<Gentleman> {

    JOIN_REGIMENT;

    @Override
    public String getChromosomeDesc() {
        return "ENGARDE";
    }

    @Override
    public Action<Gentleman> getAction(Gentleman gentleman) {
        switch (this) {
            case JOIN_REGIMENT:
                return new JoinRegiment(gentleman, 1);
        }
        return null;
    }

    @Override
    public boolean isChooseable(Gentleman gentleman) {
        return true;
    }

    @Override
    public Enum<?> getEnum() {
        return this;
    }
}
