package hopshackle.engarde;

import hopshackle.engarde.dao.GentlemanDAO;
import hopshackle.engarde.military.*;
import hopshackle.engarde.social.*;
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

    private int socialLevel, statusPoints, militaryAbility;
    private int income;
    private Rank rank;
    private String forename, surname;
    private List<Organisation> organisations = new ArrayList<>();
    private boolean justArrived = true;
    private int weeksOfService = 0;
    private int mentions = 0;
    private int currentMention[] = {0, 0, 0};

    public Gentleman(Paris world, int socialLevel, int gold, int income) {
        super(world);
        birth = world.getCurrentTime();
        this.forename = fornamer.getName();
        this.surname = surnamer.getName();
        this.socialLevel = socialLevel;
        addGold(gold);
        this.income = income;
        setRank(Rank.NONE);
        this.setDebugLocal(true);
        this.setDebugDecide(false);
        this.locationDebug = false;
        log(getName() + " arrives in Paris [" + getUniqueID() + "]");
        setClub(Club.bestClubEligibleFor(this));
        militaryAbility = Dice.roll(1, 6);
    }

    public void setSocialLevel(int newSL) {
        socialLevel = newSL;
    }

    public int getSocialLevel() {
        return socialLevel;
    }

    public int getMilitaryAbility() {
        return militaryAbility;
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

    public Club getClub() {
        Club retValue = null;
        for (Organisation org : organisations) {
            if (org instanceof Club) {
                if (retValue != null) {
                    throw new AssertionError("Should only be member of one club: " + retValue.toString() + " : " + org.toString());
                }
                retValue = (Club) org;
            }
        }
        return retValue;
    }

    public void setRegiment(Regiment newReg) {
        if (getRegiment() != null) {
            getRegiment().memberLeaves(this);
            organisations.remove(getRegiment());
        }
        if (newReg != null) organisations.add(newReg);
    }

    public void setClub(Club newClub) {
        Club oldClub = getClub();
        if (oldClub == newClub) return;
        if (oldClub != null) {
            organisations.remove(oldClub);
            oldClub.memberLeaves(this);
        }
        if (newClub != null) {
            organisations.add(newClub);
            newClub.newMember(this);
            log("Joins " + newClub.toString());
        }
    }

    public int getBirthYear() {
        return (int) (super.getBirth() / 480);
    }

    public int getDeathYear() {
        if (!isDead()) return 0;
        return (int) (death / 480);
    }

    public void maintenance() {
        logger.flush();
    }

    public void doWeekOfService() {
        weeksOfService++;
    }

    public int getWeeksOfService() {
        return weeksOfService;
    }

    public void addStatus(int statusGain) {
        statusPoints += statusGain;
    }

    public void mentionedInDispatches() {
        mentions++;
        int result = Dice.roll(1, 6);
        currentMention[0] += result - 1;
        log("Mentioned in Dispatches: " + result);
    }

    public int getMentions() {
        return mentions;
    }

    public boolean atFront() {
        return getLocation() instanceof Front;
    }

    public void monthlyMaintenance() {

        if (justArrived) {
            justArrived = false;
            setClub(Club.bestClubEligibleFor(this));
            return;
        }

        boolean atFront = getLocation() instanceof Front;

        Regiment reg = getRegiment();
        Rank rank = getRank();
        int monthlyIncome = getIncome();
        monthlyIncome += (reg != null) ? reg.getMonthlyPay(rank) : 0;
        monthlyIncome += rank.getSalary();
        int monthlyExpenditure = 0;

        if (!atFront) {

            monthlyExpenditure = getSocialLevel() * 2;

            if (rank.asInteger() > 0) {
                if (rank == Rank.CAPTAIN || (reg != null && reg.isCavalry()))
                    monthlyExpenditure += 5; // Horse + Groom
                if (rank.asInteger() > 3)
                    monthlyExpenditure += 6; // +2 Horses
                if (reg != null)
                    addStatus(reg.getMonthlyStatus(rank));
            }

            if (monthlyIncome - monthlyExpenditure > getGold()) {
                monthlyExpenditure = 0;
                socialLevel--;
                log("Unable to support self in Paris. Slips to SL " + socialLevel);
            }

            Club club = getClub();
            if (club != null) {
                monthlyExpenditure += club.getMonthlyDues();
                addStatus(club.getMonthlyStatus());
            }

            addStatus(currentMention[0] + currentMention[1] + currentMention[2]);
            addStatus(mentions);
            addStatus(rank.getStatus());

            log(String.format("Income: %d, Outgoings: %d, SP: %d", monthlyIncome, monthlyExpenditure, statusPoints));

            if (statusPoints >= 3 * socialLevel) {
                socialLevel++;
                log("Increases social level to " + socialLevel + " (SP: " + statusPoints + ")");
            }
            if (statusPoints < socialLevel) {
                socialLevel--;
                log("Decreases social level to " + socialLevel + " (SP: " + statusPoints + ")");
            }
            setClub(Club.bestClubEligibleFor(this));
        }

        currentMention[2] = currentMention[1];
        currentMention[1] = currentMention[0];
        currentMention[0] = 0;

        addGold(monthlyIncome - monthlyExpenditure);

        weeksOfService = 0;
        statusPoints = 0;

        agentWriter.write(this, getWorld().toString());
    }

    @Override
    public void die(String reason) {
        super.die(reason);
        for (Organisation org : organisations) {
            org.memberLeaves(this);
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", getRank().toString(), getName(), getRegiment() == null ? "" : "(" + getRegiment().abbrev() + ")").trim();
    }
}
