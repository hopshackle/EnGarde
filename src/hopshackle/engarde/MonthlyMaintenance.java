package hopshackle.engarde;

import hopshackle.engarde.military.Regiment;
import hopshackle.simulation.*;

import java.util.*;

public class MonthlyMaintenance {

    public MonthlyMaintenance(World world) {
        TimerTask newTask = new TimerTask() {
            public void run() {
                List<Agent> allAgents = world.getAgentsIncludingChildLocations();
                System.out.println("Monthly maintenance at " + world.getCurrentTime() + " for " + allAgents.size());
                for (Agent a : allAgents) {
                    if (a instanceof Gentleman) {
                        Gentleman g = (Gentleman)a;
                        g.monthlyMaintenance();
                    }
                }

                for (Regiment reg : Regiment.allRegiments()) {
                    reg.endOfMonthCommissions();
                }
            }

        };
        world.setScheduledTask(newTask, 41, 40);
    }
}
