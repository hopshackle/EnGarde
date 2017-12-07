package hopshackle.engarde;

import hopshackle.engarde.actions.EnGardeActions;
import hopshackle.simulation.*;

import java.util.*;

public class Paris extends World {

    /*
     * Each month we have a new batch of people arrive in Paris
     * */
    private static List<ActionEnum<Gentleman>> actionSet = HopshackleUtilities.listFromInstances(EnGardeActions.values());

    public static void main(String[] args) {
        ActionProcessor ap = new ActionProcessor();
        new Paris(ap, "Paris", 48l);
    }

    public Paris(ActionProcessor ap, String suffix, long end) {
        super(ap, suffix, end, new SimpleWorldLogic<Gentleman>(actionSet));

        DatabaseAccessUtility dbu = new DatabaseAccessUtility();
        Thread t = new Thread(dbu);
        t.start();
        setDatabaseAccessUtility(dbu);
        setCalendar(new FastCalendar(1600 * 48));

        ap.start();
        for (int i = 0; i < 24; i++) {
            System.out.println(getCurrentDate());
            newGentlemen(5);
            setCurrentTime(getCurrentTime() + 4);
            maintenance();
        }

    }

    private void newGentlemen(int number) {
        for (int i = 0; i < number; i++) {
            Gentleman newbie = NewCharacter.getNewbie(this);
            newbie.setAge(16 * 48);
            newbie.setDecider(new EnGardeDecider());
            this.addAgent(newbie);
            newbie.decide();
        }
    }

    public String getCurrentDate() {
        long time = getCurrentTime();
        int year = (int) (time / 48);
        int m = (int) (time % 48 / 4) + 1;
        String month = "";
        switch (m) {
            case 1:
                month = "Jan";
                break;
            case 2:
                month = "Feb";
                break;
            case 3:
                month = "Mar";
                break;
            case 4:
                month = "Apr";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "Jun";
                break;
            case 7:
                month = "Jul";
                break;
            case 8:
                month = "Aug";
                break;
            case 9:
                month = "Sep";
                break;
            case 10:
                month = "Oct";
                break;
            case 11:
                month = "Nov";
                break;
            case 12:
                month = "Dec";
                break;
            default:
                month = "UNK";
        }
        return month + " " + year;
    }

}
