package hopshackle.engarde;

import hopshackle.engarde.actions.EnGardeActions;
import hopshackle.engarde.military.Rank;
import hopshackle.engarde.military.Regiment;
import hopshackle.simulation.*;

import java.util.*;

public class Paris extends World {

    /*
     * Each month we have a new batch of people arrive in Paris
     * */
    private static List<ActionEnum<Gentleman>> actionSet = HopshackleUtilities.listFromInstances(EnGardeActions.values());

    public static void main(String[] args) {
        ActionProcessor ap = new ActionProcessor("EnGarde", false);
        new Paris(ap, "Paris", 480 * 100l);
    }

    public Paris(ActionProcessor ap, String suffix, long end) {
        super(ap, suffix, end, new SimpleWorldLogic<Gentleman>(actionSet));

        int minPopulationSize = SimProperties.getPropertyAsInteger("MinPopulationSize", "300");
        DatabaseAccessUtility dbu = new DatabaseAccessUtility();
        Thread t = new Thread(dbu);
        t.start();
        setDatabaseAccessUtility(dbu);
        setCalendar(new FastCalendar(1600 * 480));

        populateStartingRegimentalOfficers();
        ap.start();

        new PopulationSpawner(this, 40, 5, minPopulationSize);
        new MonthlyMaintenance(this);
    }

    private void populateStartingRegimentalOfficers() {
        for (Regiment reg : Regiment.allRegiments()) {
            List<Gentleman> officers = new ArrayList<>();
            int SL = Math.max(reg.getMinSL(Rank.COLONEL), 2);
            Gentleman colonel = new Gentleman(this, SL, SL * 100, 0);
            reg.acceptApplicant(colonel);
            for (int i = 0; i < 5; i++) reg.promote(colonel);
            officers.add(colonel);

            SL = Math.max(reg.getMinSL(Rank.LT_COLONEL), 2);
            Gentleman ltColonel = new Gentleman(this, SL, SL * 100, 0);
            reg.acceptApplicant(ltColonel);
            for (int i = 0; i < 4; i++) reg.promote(ltColonel);
            officers.add(ltColonel);

            SL = Math.max(reg.getMinSL(Rank.MAJOR), 2);
            for (int i = 0; i < 2; i++) {
                Gentleman major = new Gentleman(this, SL, SL * 100, 0);
                reg.acceptApplicant(major);
                for (int j = 0; j < 3; j++) reg.promote(major);
                officers.add(major);
            }

            SL = Math.max(reg.getMinSL(Rank.CAPTAIN), 2);
            for (int i = 0; i < 4; i++) {
                Gentleman captain = new Gentleman(this, SL, SL * 100, 0);
                reg.acceptApplicant(captain);
                for (int j = 0; j < 2; j++) reg.promote(captain);
                officers.add(captain);
            }

            for (Gentleman officer : officers) {
                this.addAgent(officer);
                officer.setDecider(new EnGardeDecider());
                officer.setAge(480 * (officer.getRank().asInteger() * 5 + 10));
                officer.decide();
            }
        }
    }

    public String getCurrentDate() {
        long time = getCurrentTime();
        int year = (int) (time / 480);
        int m = (int) (time % 480 / 40) + 1;
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
