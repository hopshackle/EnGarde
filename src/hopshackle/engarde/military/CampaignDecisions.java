package hopshackle.engarde.military;

import hopshackle.engarde.actions.FrontierService;
import hopshackle.simulation.*;
import hopshackle.engarde.*;
import java.util.*;

public class CampaignDecisions {

    public enum Deployment  {
        SIEGE, DEFENCE, ASSAULT, FIELD;
    }

    private Deployment currentFrontierDeployment;
    private Paris paris;
    private EntityLog campaignLog;

    public CampaignDecisions(Paris paris) {

        campaignLog = new EntityLog("CampaignHistory", paris);
        this.paris = paris;
        currentFrontierDeployment = Deployment.DEFENCE;
        updateFronts();

        TimerTask quarterlyDecision = new TimerTask() {
            public void run() {
                // for the moment, we just have a frontier decision each season
                int roll = Dice.roll(1, 6);
                switch (roll) {
                    case 1:
                    case 4:
                        currentFrontierDeployment = Deployment.SIEGE;
                        break;
                    case 2:
                    case 5:
                        currentFrontierDeployment = Deployment.DEFENCE;
                        break;
                    case 3:
                        currentFrontierDeployment = Deployment.ASSAULT;
                        break;
                    case 6:
                        currentFrontierDeployment = Deployment.FIELD;
                        break;
                }
                updateFronts();
            }
        };
        paris.setScheduledTask(quarterlyDecision, 118, 120);

        TimerTask monthlyCampaignResults = new TimerTask() {
            public void run() {
                // for the moment, we just have a frontier decision each season
                FrontierService.overallResults();
            }
        };
        paris.setScheduledTask(monthlyCampaignResults, 37, 40);
    }

    private void updateFronts() {
        Front currentFront = FrontierService.getFront();
        if (currentFront == null ) {
            FrontierService.setFront(new Front(paris, "Frontier Regiment", currentFrontierDeployment));
        } else {
            currentFront.setDeploymentType(currentFrontierDeployment);
        }
        campaignLog.log("Next Frontier campaign: " + currentFrontierDeployment);
        campaignLog.flush();
    }

    public Deployment getCurrentFrontierDeployment() {
        return currentFrontierDeployment;
    }
}
