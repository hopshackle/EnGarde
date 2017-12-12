package hopshackle.engarde.social;

import hopshackle.engarde.*;
import hopshackle.engarde.military.*;
import hopshackle.simulation.HopshackleUtilities;

import java.util.*;

public class WeeklySocialInteractions {

    public WeeklySocialInteractions(Paris paris) {
        TimerTask task = new TimerTask() {
            public void run() {
                // iterate through each regiment, and have everybody there meet
                for (Regiment reg : Regiment.allRegiments()) {
                    List<Gentleman> allPresent = HopshackleUtilities.convertList(reg.getAgents());
                    new SocialMeeting(allPresent, -2, -2);
                }

                // iterate through each club, and have everybody there meet
                for (Club c : Club.allClubs()) {
                    List<Gentleman> allPresent = HopshackleUtilities.convertList(c.getAgents());
                    new SocialMeeting(allPresent, -1, -1);
                }
            }
        };

        paris.setScheduledTask(task, 7, 10);
    }

}
