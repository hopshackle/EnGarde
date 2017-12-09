package hopshackle.engarde.actions;

import hopshackle.engarde.military.Rank;
import hopshackle.simulation.*;
import hopshackle.engarde.*;

public enum EnGardeActions implements ActionEnum<Gentleman> {

    JOIN_REGIMENT,
    JOIN_CLUB,
    REGIMENTAL_SERVICE,
    CAROUSE,
    HOST_PARTY;

    @Override
    public String getChromosomeDesc() {
        return "ENGARDE";
    }

    @Override
    public Action<Gentleman> getAction(Gentleman gentleman) {
        switch (this) {
            case JOIN_REGIMENT:
                return new JoinRegiment(gentleman);
            case JOIN_CLUB:
                return new JoinClub(gentleman);
            case REGIMENTAL_SERVICE:
                return new RegimentalService(gentleman);
            case CAROUSE:
                return new Carouse(gentleman);
            case HOST_PARTY:
                return new HostParty(gentleman);
        }
        return null;
    }

    @Override
    public boolean isChooseable(Gentleman gentleman) {
        switch (this) {
            case REGIMENTAL_SERVICE:
                if (gentleman.getRank() == Rank.PRIVATE && gentleman.getWeeksOfService() < 2) {
                    return true;
                }
                if (gentleman.getRank() == Rank.SUBALTERN && gentleman.getWeeksOfService() < 1) {
                    return true;
                }
                return false;
            case HOST_PARTY:
                if (gentleman.getClub().getID() == 0)
                    return false;       // not a member of a club
                return true;
            default:
                return true;
        }

    }

    @Override
    public Enum<?> getEnum() {
        return this;
    }
}
