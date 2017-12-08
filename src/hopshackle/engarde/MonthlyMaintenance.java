package hopshackle.engarde;

import hopshackle.simulation.*;

import java.util.*;

public class MonthlyMaintenance {

    public MonthlyMaintenance(World world) {
        TimerTask newTask = new TimerTask() {
            public void run() {
                List<Agent> allAgents = world.getAgents();
 //               System.out.println("Monthly maintenance for " + world.getCurrentDate());
                for (Agent a : allAgents) {
                    if (a instanceof Gentleman) {
                        Gentleman g = (Gentleman)a;
                        g.monthlyMaintenance();
                    }
                }
            }

        };
        world.setScheduledTask(newTask, 43, 40);
    }
}
