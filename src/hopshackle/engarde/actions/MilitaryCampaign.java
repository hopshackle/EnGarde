package hopshackle.engarde.actions;

import hopshackle.engarde.social.SocialMeeting;
import hopshackle.simulation.*;
import hopshackle.engarde.*;
import hopshackle.engarde.military.*;

import java.util.*;

public class MilitaryCampaign extends Action<Gentleman> {

    private Front thisFront;
    private Regiment reg;

    public MilitaryCampaign(Regiment regiment) {
        super(EnGardeActions.FIGHT_ON_FRONTIER, HopshackleUtilities.listFromInstance(regiment.getCommander()),
                regiment.getCurrentMembership(), 2, 38, true);
        this.reg = regiment;
    }

    public void initialisation() {
        // set location to be a Front
        thisFront = new Front(reg.getWorld(), reg.getName(), reg, FrontierService.getFront().getDeploymentType());
        actor.log("Takes " + reg.toString() + " on campaign on the frontier");
        actor.setLocation(thisFront);
        for (Gentleman soldier : optionalActors) {
            soldier.log("Goes on campaign (frontier) with " + reg.toString());
            soldier.setLocation(thisFront);
        }
        reg.setOnCampaign(true);
    }

    public void doStuff() {
        thisFront.commandResults();
        thisFront.individualResults(actor);
        for (Gentleman soldier : HopshackleUtilities.cloneList(optionalActors))
            thisFront.individualResults(soldier);

        List<Gentleman> allPresent = HopshackleUtilities.convertList(thisFront.getAgents());
        new SocialMeeting(allPresent, +2, 0);

        thisFront.flushLog();
    }

    public void doCleanUp() {
        reg.setOnCampaign(false);
    }

    public void delete() {
        reg.setOnCampaign(false);
    }

    public void doNextDecision() {
        int month = (int) (actor.getWorld().getCurrentTime() % 480 / 40 + 1);
        if (getState() == State.CANCELLED || month == 3 || month == 6 || month == 9 || month == 12) {
            super.doNextDecision();
        } else {
            // stay on campaign (even if commanding officer has been killed)
            if (reg.getCommander() == null) {
                int SL = Math.max(reg.getMinSL(Rank.COLONEL), 2);
                Gentleman colonel = new Gentleman((Paris) reg.getWorld(), SL, SL * 100, 0);
                reg.acceptApplicant(colonel);
                for (int i = 0; i < 5; i++) reg.promote(colonel);
                colonel.setLocation(thisFront);
                colonel.setDecider(new EnGardeDecider());
                colonel.setAge(480 * (45));
                colonel.setPolicy(new ActionPolicy());
            }
            MilitaryCampaign nextMonth = new MilitaryCampaign(reg);
            nextMonth.addToAllPlans();

            List<Gentleman> nonCombatants = HopshackleUtilities.cloneList(getAllConfirmedParticipants());
            nonCombatants.removeAll(nextMonth.getAllConfirmedParticipants());
            for (Gentleman nonC : nonCombatants) {
                doNextDecision(nonC);
            }
        }
    }
}
