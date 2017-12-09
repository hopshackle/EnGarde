package hopshackle.engarde;

import hopshackle.simulation.*;

import java.util.*;

public class EndWorld {

    public EndWorld(World w, long time) {
        TimerTask newTask = new TimerTask() {
            public void run() {
                System.out.println("End of run");
                w.worldDeath();
                w.getDBU().addUpdate("EXIT");
            }

        };
        w.setScheduledTask(newTask, time);
    }
}
