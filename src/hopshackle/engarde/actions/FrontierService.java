package hopshackle.engarde.actions;

import hopshackle.simulation.*;
import hopshackle.engarde.*;
import hopshackle.engarde.military.*;

public class FrontierService extends Action<Gentleman> {

    private static Front frontierRegiment;
    private static EntityLog campaignLog;
    private static int commanderMA;
    private static int adjutantMA;
    private static int result;

    public static final int[][] death = {{10, 8, 11, 11, 9, 1}, {9, 8, 7, 9, 8, 6}, {9, 8, 10, 10, 6, 7}, {10, 10, 9, 8, 7, 6}};
    public static final int[][] mentions = {{11, 9, 12, 12, 10, 8}, {9, 7, 6, 11, 9, 8}, {9, 10, 12, 12, 7, 11}, {9, 10, 12, 12, 10, 7}};
    public static final int[][] promotion = {{9, 7, 10, 10, 8, 6}, {8, 7, 6, 8, 7, 5}, {8, 7, 9, 9, 5, 6}, {9, 9, 8, 7, 6, 5}};
    public static final int[][] plunder = {{9, 8, 11, 12, 99, 99}, {4, 5, 6, 99, 99, 99}, {7, 9, 12, 12, 99, 99}, {8, 9, 10, 11, 12, 99}};
    public static final int[][] plunderDice = {{3, 4, 2, 1, 0, 0}, {4, 6, 4, 0, 0, 0}, {2, 2, 1, 1, 0, 0}, {2, 2, 2, 1, 1}};
    public static final int[][] plunderMultiplier = {{100, 100, 100, 100, 0, 0}, {100, 100, 100, 0, 0, 0}, {100, 50, 50, 50, 0, 0}, {50, 100, 50, 50, 50, 0}};
    public static final String[][] resultDescription =
            {{"Success - siege works", "Success - storming party", "Inconclusive", "Inconclusive", "Broken by sorty", "Scattered by sorty"},
                    {"Victorious", "Bloody victory", "Pyhrric victory", "Inconclusive", "Replused", "Crushed"},
                    {"Enemy scattered by sorty", "Enemy broken by sorty", "Inconclusive", "Inconclusive", "Falls to storming party", "Falls to siege works"},
                    {"Enemy crushed", "Enemy driven from field", "Inconclusive", "Inconclusive", "Driven from field", "Crushed"}};

    public static void setFront(Front loc) {
        frontierRegiment = loc;
        if (campaignLog == null)
            campaignLog = new EntityLog("FrontierHistory", loc.getWorld());
        commanderMA = Dice.roll(1, 6);
        adjutantMA = Dice.roll(1, 6);
    }
    public static Front getFront() {
        return frontierRegiment;
    }

    public static void overallResults() {
        int luck = Dice.roll(1, 6);
        int effectiveMA = commanderMA;
        if (adjutantMA >= commanderMA + 2) effectiveMA++;
        result = (int) (6.7 - (effectiveMA / 3.0) - (luck / 1.5));
        result = Math.max(result, 0);
        result = Math.min(result, 5);
        campaignLog.log(resultDescription[getIndex()][result] + " [MA: " + commanderMA + "/" + adjutantMA +"]");

        if (Dice.roll(2, 12) >= death[getIndex()][result] + 2 - 1) {
            campaignLog.log("Regiment commander dies in battle");
            commanderMA = Dice.roll(1, 6);
        } else if (Dice.roll(2, 12) >= promotion[getIndex()][result] + 1) {
            campaignLog.log("Regiment commander promoted to Brigadier");
            commanderMA = Dice.roll(1, 6);
        }
        if (Dice.roll(2, 12) >= death[getIndex()][result] + 1 - 1) {
            campaignLog.log("Regimental adjutant dies in battle");
            adjutantMA = Dice.roll(1, 6);
        }
    }

    public FrontierService(Gentleman volunteer) {
        super(EnGardeActions.FIGHT_ON_FRONTIER, volunteer, 2, 38, true);
    }

    @Override
    protected void initialisation() {
        if (frontierRegiment == null) throw new AssertionError("Frontier Regiment location not initialised");
        actor.setLocation(frontierRegiment);
        actor.log("Spends month on campaign with the Frontier Regiment");
    }

    @Override
    protected void doStuff() {
        int deploymentIndex = getIndex();
        int deathRoll = Dice.roll(2, 12);
        if (deathRoll >= death[deploymentIndex][result] + actor.getRank().getDeathMod() - 1) {
            actor.die("Killed in battle on the Frontier");
            campaignLog.log(actor + " dies in battle");
        } else {
            // not dead
            actor.log("Fights on frontier (and survives)");
            int mentionsRoll = Dice.roll(2, 12);
            if (mentionsRoll >= mentions[deploymentIndex][result]) {
                actor.mentionedInDispatches();
                campaignLog.log(actor + " mentioned in dispatches");
            }
            int promotionRoll = Dice.roll(2, 12);
            if (promotionRoll >= promotion[deploymentIndex][result] + actor.getRank().getPromotionMod() + 1) {
                Rank initial = actor.getRank();
                if (actor.getRegiment() == null) {
                    actor.mentionedInDispatches();
                    campaignLog.log(actor + " mentioned in lieu of promotion");
                } else {
                    actor.getRegiment().promote(actor);
                    if (initial != actor.getRank()) {
                        campaignLog.log(actor + " promoted from " + initial);
                    } else {
                        campaignLog.log(actor + " mentioned in lieu of promotion");
                    }
                }
            }
            int plunderRoll = Dice.roll(2, 12);
            if (plunderRoll >= plunder[deploymentIndex][result] + actor.getRank().getPlunderMod() - 1) {
                int plunder = Dice.roll(plunderDice[deploymentIndex][result], 6) * plunderMultiplier[deploymentIndex][result];
                if (plunder > 0) {
                    actor.log("Loots " + plunder + " crowns from the battlefield.");
                    campaignLog.log(actor + " loots " + plunder + " crowns in plunder");
                    actor.addGold(plunder);
                }
            }
        }
    }

    @Override
    public void doNextDecision() {
        if (actor.isDead()) return;
        int month = (int) (actor.getWorld().getCurrentTime() % 480 / 40 + 1);
        if (month == 3 || month == 6 || month == 9 || month == 12) {
            super.doNextDecision();
        } else {
            // stay on campaign
            (new FrontierService(actor)).addToAllPlans();
        }
    }

    private static int getIndex() {
        switch (frontierRegiment.getDeploymentType()) {
            case SIEGE:
                return 0;
            case ASSAULT:
                return 1;
            case DEFENCE:
                return 2;
            case FIELD:
                return 3;
        }
        return 0;
    }

}
