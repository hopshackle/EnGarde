package hopshackle.engarde.military;

public enum Rank {

    NONE           (0, "", 0, 0, 0, 0, 0),
    PRIVATE         (1, "Pte", 0, 0, 0, 0, 0),
    SUBALTERN       (2, "Lt", 1, 0, -1, 0, 0),
    CAPTAIN         (3, "Cpt", 1, 0, -1, 0, 0),
    MAJOR           (4, "Maj", 2, 0, -2, 0, 0),
    LT_COLONEL      (5, "Lt-Col", 2, 0, -2, 0, 0),
    COLONEL         (6, "Col", 2, 0, -2, 0, 0),
    BRIG_GENERAL    (7, "Brig-Gen", 3, -1, -4, 30, 9),
    LT_GENERAL      (8, "Lt-Gen", 4, 0, -5, 35, 9),
    GENERAL         (9, "General", 5, 0, -6, 40, 10),
    FIELD_MARSHAL   (10, "Field Marshal", 6, 0, -8, 45, 20);


    private int order, salary, status;
    private String abbrev;
    private int death, promotion, plunder;

    Rank(int order, String abbrev, int deathMod, int promotionMod, int plunderMod, int salary, int status) {
        this.order = order;
        this.abbrev = abbrev;
        death =deathMod;
        promotion = promotionMod;
        plunder = plunderMod;
        this.salary = salary;
        this.status = status;
    }

    public int asInteger() {
        return order;
    }
    public int getDeathMod() {return death;}
    public int getPromotionMod() {return promotion;}
    public int getPlunderMod() {return plunder;}
    public int getSalary() { return salary;}
    public int getStatus() { return status;}
    public String toString() {
        return abbrev;
    }
}
