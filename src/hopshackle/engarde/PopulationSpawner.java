package hopshackle.engarde;

import hopshackle.simulation.*;

import java.util.*;
import java.util.logging.*;

public class PopulationSpawner {

    protected static Logger logger = Logger.getLogger("hopshackle.simulation");

    public PopulationSpawner(Paris world, long freq, int maxIncrement, int minimumWorldPopulation) {
        TimerTask newTask = new TimerTask() {
            public void run() {
                List<Agent> allAgents = world.getAgents();

                int agentsToAdd = maxIncrement;
                if (allAgents.size() < minimumWorldPopulation) {
                    System.out.println("Spawning newbies for " + world.getCurrentDate());

                    Long delay = world.getActionProcessor().getDelay();
                    if (delay != null && delay > 100) {
                        agentsToAdd = (int) (((double) agentsToAdd) * ((10000.0 - ((double) delay)) / 10000.0));
                        logger.info("Throttled number of agents to add: " + agentsToAdd);
                    }

                    for (int loop = 0; loop < agentsToAdd; loop++) {
                        Gentleman newbie = NewCharacter.getNewbie(world);
                        newbie.setAge(16 * 480);
                        newbie.setDecider(new EnGardeDecider());
                        world.addAgent(newbie);
                        newbie.decide();
                    }
                }
            }
        };
        world.setScheduledTask(newTask, freq, freq);
    }
}
