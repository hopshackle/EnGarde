package hopshackle.engarde.military;

public enum RegimentID {

    RFG         (1, "RFG", "Royal Foot Guards", 0, 7, false),
    CG          (2, "CG", "Cardinal's Guards", 1, 6, false),
    KM          (3, "KM", "King's Musketeers", 1, 6, false),
    DG          (4, "DG", "Dragoon Guards", 2, 5, true),
    QOC         (5, "QOC", "Queen' Own Carabiniers", 3, 4, true),
    ALC         (6, "ALC", "Archduke Leopold's Cuirassiers", 3, 4, true),
    CPC         (7, "CPC", "Crown Prince Cuirassiers", 4, 3, true),
    RM          (8, "RM", "Royal Marines", 5, 2, false),
    GDMD        (9, "GDMD", "Grand Duke Max's Dragoons", 5, 2, true),
    PM          (10, "PM", "Picardy Musketeers", 6, 1, false),
    F13         (11, "13th", "13th Fusiliers", 7, 0,  false),
    PLLD        (12, "PLLD", "Princess Louisa's Light Dragoons", 7, 0, true),
    F53         (13, "53rd", "53rd Fusiliers", 8, -1, false),
    M27         (14, "27th", "27th Musketeers", 8, -1, false),
    A4          (15, "4th", "4th Arquebusiers", 8, -1, false),
    A69         (16, "69th", "69th Arquebusiers", 9, -2, false),
    G           (17, "GASC", "Gascon Regiment", 9, -2, false),
    NONE        (0, "", "", 0, -10, false);

    private int id;
    private String abbrev, fullName;
    private boolean isCavalry;
    private int type, baseSelection;

    static private int[] minSLPrivate = {7, 6, 5, 4, 3, 2, 1, 1, 0, 0};
    static private int[] monthlyPayPrivate = {12, 10, 10, 8, 8, 6, 4, 4, 2, 2};
    static private int[] monthlyStatusPrivate = {6, 5, 4, 3, 3, 2, 1, 0, -1, -2};
    static private int[] subalternPurchase = {140, 130, 120, 110, 100, 90, 80, 70, 60, 50};

    RegimentID(int id, String abbrev, String fullName, int type, int baseSelection, boolean isCavalry) {
        this.id = id;
        this.abbrev = abbrev;
        this.fullName = fullName;
        this.type = type;
        this.isCavalry = isCavalry;
        this.baseSelection = baseSelection;
    }

    public int getID() {
        return id;
    }

    public String abbrev() {
        return abbrev;
    }

    public String toString() {
        return fullName;
    }

    public boolean isCavalry() { return isCavalry;}

    public int commissionPurchaseCost(Rank r) {
        switch (r) {
            case SUBALTERN:
                return subalternPurchase[type];
            case CAPTAIN:
                return subalternPurchase[type] + 10;
            case MAJOR:
                return subalternPurchase[type] + 30;
            case LT_COLONEL:
                return subalternPurchase[type] + 50;
            case COLONEL:
                return subalternPurchase[type] + 100;
            default:
                throw new AssertionError("Cannot buy commission for " + r);
        }

    }

    public int recruitmentTarget(int SL) {
        int retValue = (baseSelection - SL) / 2 + 5;
        if (retValue > 5) retValue = 99;
        return retValue;
    }
    public int minSL(Rank rank) { return minSLPrivate[type] + rank.asInteger() - 1;}
    public int monthlyPay(Rank rank) {
        int retValue = monthlyPayPrivate[type];
        int r = rank.asInteger() - 1;
        retValue += r * 2;
        if (r > 1) retValue += r * 2;
        return retValue;
    }
    public int monthlyStatus(Rank rank) {return Math.max(monthlyStatusPrivate[type] + rank.asInteger() - 1, 0);}
}
