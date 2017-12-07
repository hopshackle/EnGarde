package hopshackle.engarde;

import hopshackle.engarde.dao.GentlemanDAO;
import hopshackle.engarde.military.*;
import hopshackle.simulation.*;

import java.io.*;
import java.util.*;



public class Gentleman extends Agent implements Persistent {

    /*
    * We can use the following field in Agent:
    *   location information
    *   inventory
    *   gold
    *   age (birth and death)
    *   [ultimately] parents and children
    *
    *
    * Then we keep track of regiment and club via memberships of those organisations.
    * Plus military rank, income, social status.
    * */
    protected static Name fornamer = new Name(new File(baseDir + File.separator + "FrenchMaleNames.txt"));
    protected static Name surnamer = new Name(new File(baseDir + File.separator + "FrenchSurnames.txt"));
    private static DatabaseWriter<Gentleman> agentWriter = new DatabaseWriter<>(new GentlemanDAO());

    private int socialLevel;
    private int income;
    private Rank rank;
    private String forename, surname;
    private List<Organisation> organisations = new ArrayList<>();

    public Gentleman(World world, int socialLevel, int gold, int income) {
        super(world);
        birth = world.getCurrentTime();
        this.forename = fornamer.getName();
        this.surname = surnamer.getName();
        this.socialLevel = socialLevel;
        addGold(gold);
        this.income = income;
        setRank(Rank.NONE);
        this.setDebugLocal(true);
        log(getName() + " arrives in Paris");
    }

    public void setSocialLevel(int newSL) {
        socialLevel = newSL;
    }
    public int getSocialLevel() {
        return socialLevel;
    }
    public void setIncome(int i) {
        income = i;
    }
    public int getIncome() {
        return income;
    }
    public void setRank(Rank r) {
        rank = r;
    }
    public Rank getRank() {
        return rank;
    }
    public String getName() {
        return forename + " " + surname;
    }

    public String toString() {
        return getName();
    }

    public Regiment getRegiment() {
        Regiment retValue = null;
        for (Organisation org : organisations) {
            if (org instanceof Regiment) {
                if (retValue != null) throw new AssertionError("Should only be member of one regiment");
                retValue = (Regiment) org;
            }
        }
        return retValue;
    }

    public void setRegiment(Regiment newReg) {
        if (getRegiment() != null) {
            organisations.remove(getRegiment());
        }
        organisations.add(newReg);
    }

    public int getBirthYear() {
        return (int) (super.getBirth() / 48);
    }
    public int getDeathYear() {
        if (!isDead()) return 0;
        return (int) (death / 48);
    }

    public void maintenance() {
        logger.flush();
        agentWriter.write(this, getWorld().toString());
    }
}