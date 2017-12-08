
package hopshackle.engarde.dao;

import hopshackle.engarde.*;
import hopshackle.simulation.*;

public class GentlemanDAO implements DAO<Gentleman>, DAODuplicateUpdate {

    public String getTableCreationSQL(String tableSuffix) {

        return  "CREATE TABLE IF NOT EXISTS Gentlemen_" + tableSuffix +
                " ( id 		INT			PRIMARY KEY,"		+
                " name      VARCHAR(100)    NOT NULL," +
                " SL        INT             NOT NULL," +
                " gold      INT             NOT NULL," +
                " income    INT             NOT NULL," +
                " rank      INT             NOT NULL," +
                " regiment  INT             NOT NULL," +
                " club      INT             NOT NULL," +
                " birthYear INT             NOT NULL," +
                " deathYear INT             NOT NULL" +
                ");";
    }

    public String getTableUpdateSQL(String tableSuffix) {
        return "INSERT INTO Gentlemen_" + tableSuffix +
                " (id, name, SL, gold, income, rank, regiment, club, birthYear, deathYear" +
                ") VALUES";
    }

    public String getValues(Gentleman agent) {

        return String.format(" (%d, '%s', %d, %d, %d, %d, %d, %d, %d, %d)",
                agent.getUniqueID(),
                agent.getName(),
                agent.getSocialLevel(),
                (int) agent.getGold(),
                agent.getIncome(),
                agent.getRank().asInteger(),
                agent.getRegiment() == null ? 0 : agent.getRegiment().getID(),
                agent.getClub() == null ? 0 : agent.getClub().getID(),
                agent.getBirthYear(),
                agent.getDeathYear()
        );
    }

    public String getOnDuplicateKey() {
        return String.format(" ON DUPLICATE KEY UPDATE " +
                        "name = VALUES(id), name = VALUES(name), SL = VALUES(SL), gold = VALUES(gold), income = VALUES(income), " +
                        "rank = VALUES(rank), regiment = VALUES(regiment), club = VALUES(club), birthYear = VALUES(birthYear), " +
                        "deathYear = VALUES(deathYear)"
        );
    }

    public String getTableDeletionSQL(String tableSuffix) {
        return "DROP TABLE IF EXISTS Gentlemen_" + tableSuffix + ";";
    }
}
