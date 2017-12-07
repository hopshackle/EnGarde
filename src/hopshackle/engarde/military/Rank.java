package hopshackle.engarde.military;

public enum Rank {

    NONE           (0, ""),
    PRIVATE         (1, "Pte"),
    SUBALTERN       (2, "Lt"),
    CAPTAIN         (3, "Cpt"),
    MAJOR           (4, "Maj"),
    LT_COLONEL      (5, "Lt-Col"),
    COLONEL         (6, "Col"),
    BRIG_GENERAL    (7, "Brig-Gen"),
    LT_GENERAL      (8, "Lt-Gen"),
    GENERAL         (9, "General"),
    FIELD_MARSHAL   (10, "Field Marshal");


    private int order;
    private String abbrev;

    Rank(int order, String abbrev) {
        this.order = order;
        this.abbrev = abbrev;
    }

    public int asInteger() {
        return order;
    }

    public String toString() {
        return abbrev;
    }
}
