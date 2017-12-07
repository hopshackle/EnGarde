package hopshackle.engarde;

import hopshackle.simulation.*;

public class NewCharacter {

    static int[] initial = {10, 10, 25, 150, 250, 500, 40, 40, 250, 250, 500, 750, 40, 40, 250, 500, 750, 750};
    static int[] allowance = {0, 0, 5, 20, 50, 100, 0, 0, 50, 50, 100, 125, 0, 0, 50, 100, 125, 125};
    static int[] inheritance = {0, 0, 100, 750, 1500, 4000, 100, 100, 1500, 1500, 4000, 5000, 100, 100, 1500, 4000, 5000, 5000};
    static int[] socialLevel = {2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 5, 0, 0, 0, 0, 0, 0};

    public static Gentleman getNewbie(World w) {

        int baseRoll = Dice.roll(1, 18);
        int sibling = Dice.roll(1, 6);
        int orphan = Dice.roll(1, 6);

        int initialCash = initial[baseRoll - 1];
        int initialAllowance = allowance[baseRoll - 1];
        int initialSL = socialLevel[baseRoll - 1];

        if (sibling == 1) { // first son
            initialCash *= 1.1;
            initialAllowance *= 1.1;
            if (orphan == 1) {
                initialAllowance = 0;
                initialCash += inheritance[baseRoll-1];
            }
        }
        if (baseRoll > 12) {
            // noble
            initialSL = 5 + Dice.roll(1, 6);
        }
        if (sibling > 4) { // bastard
            initialCash *= 0.9;
            initialAllowance *= 0.9;
            initialSL--;        // not canon
        }

        return new Gentleman(w, initialSL, initialCash, initialAllowance);
        // TODO: Noble titles not yet implemented as part of inheritance
    }
}
