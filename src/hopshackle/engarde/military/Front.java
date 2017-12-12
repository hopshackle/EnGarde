package hopshackle.engarde.military;

import hopshackle.simulation.*;

public class Front extends Location {

    private CampaignDecisions.Deployment deploymentType;

    public Front(World world, String name, CampaignDecisions.Deployment deployment) {
        super(world);
        setName(name);
        deploymentType = deployment;
    }

    public CampaignDecisions.Deployment getDeploymentType() {
        return deploymentType;
    }

    public void setDeploymentType(CampaignDecisions.Deployment deploymentType) {
        this.deploymentType = deploymentType;
    }
}
