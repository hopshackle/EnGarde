package hopshackle.engarde.military;

import hopshackle.simulation.*;
import hopshackle.engarde.*;

public class Front extends Location {


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


    private CampaignDecisions.Deployment deploymentType;
    private EntityLog campaignLog;
    private int commanderMA, adjutantMA;
    private Gentleman commander, adjutant;
    private int result;
    private Regiment unit;

    public Front(World world, String name, CampaignDecisions.Deployment deployment) {
        super(world);
        setName(name);
        deploymentType = deployment;
        campaignLog = new EntityLog(name, world);
        commanderMA = Dice.roll(1, 6);
        adjutantMA = Dice.roll(1, 6);
    }

    public Front(World world, String name, Regiment unit, CampaignDecisions.Deployment deployment) {
        super(world);
        setName(name);
        this.unit = unit;
        deploymentType = deployment;
        campaignLog = new EntityLog(name, world);
        commander = unit.getCommander();
        adjutant = unit.getAdjutant();
        commanderMA = commander.getMilitaryAbility();
        adjutantMA = (adjutant == null) ? 0 : adjutant.getMilitaryAbility();
    }

    public CampaignDecisions.Deployment getDeploymentType() {
        return deploymentType;
    }

    public void setDeploymentType(CampaignDecisions.Deployment deploymentType) {
        this.deploymentType = deploymentType;
    }

    public void commandResults() {
        int index = getIndex(deploymentType);
        int luck = Dice.roll(1, 6);
        int effectiveMA = commanderMA;
        if (adjutantMA >= commanderMA + 2) effectiveMA++;
        result = (int) (6.7 - (effectiveMA / 3.0) - (luck / 1.5));
        result = Math.max(result, 0);
        result = Math.min(result, 5);
        campaignLog.log(resultDescription[index][result] + " [MA: " + commanderMA + "/" + adjutantMA + "]");
        if (commander != null) {
            commander.log("Commands " + this.toString() + " with a result of " + resultDescription[index][result]);
        }

        if (commander == null) {
            if (Dice.roll(2, 12) >= death[index][result] + 2 - 1) {
                campaignLog.log("Regiment commander dies in battle");
                commanderMA = Dice.roll(1, 6);
            }
            if (Dice.roll(2, 12) >= death[index][result] + 1 - 1) {
                campaignLog.log("Regimental adjutant dies in battle");
                adjutantMA = Dice.roll(1, 6);
            }
        }

    }

    public void individualResults(Gentleman actor) {
        int[] unitMod = {-1, 0, 1, -1};
        if (unit == null) {
            // Frontier - leave as default
        } else {
            unitMod[0] = unit.deathMod();
            unitMod[1] = unit.mentionMod();
            unitMod[2] = unit.promotionMod();
            unitMod[3] = unit.plunderMod();
        }
        int deploymentIndex = getIndex(deploymentType);
        int deathRoll = Dice.roll(2, 6);
        if (deathRoll >= death[deploymentIndex][result] + actor.getRank().getDeathMod() + unitMod[0]) {
            actor.die("Killed in battle on the Frontier");
            campaignLog.log(actor + " dies in battle");
        } else {
            // not dead
            int mentionsRoll = Dice.roll(2, 6);
            if (mentionsRoll >= mentions[deploymentIndex][result] + unitMod[1]) {
                actor.mentionedInDispatches();
                campaignLog.log(actor + " mentioned in dispatches");
            }
            int promotionRoll = Dice.roll(2, 6);
            if (promotionRoll >= promotion[deploymentIndex][result] + actor.getRank().getPromotionMod() + unitMod[2]) {
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
            int plunderRoll = Dice.roll(2, 6);
            if (plunderRoll >= plunder[deploymentIndex][result] + actor.getRank().getPlunderMod() + unitMod[3]) {
                int plunder = Dice.roll(plunderDice[deploymentIndex][result], 6) * plunderMultiplier[deploymentIndex][result];
                if (plunder > 0) {
                    actor.log("Loots " + plunder + " crowns from the battlefield.");
                    campaignLog.log(actor + " loots " + plunder + " crowns in plunder");
                    actor.addGold(plunder);
                }
            }
        }
    }

    public void log(String msg) {
        campaignLog.log(msg);
    }
    public void flushLog() {
        campaignLog.flush();
    }

    private static int getIndex(CampaignDecisions.Deployment deployment) {
        switch (deployment) {
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
