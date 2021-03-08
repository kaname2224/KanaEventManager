package fr.kaname.kanaeventmanager.managers;

import fr.kaname.kanaeventmanager.KanaEventManager;
import fr.kaname.kanaeventmanager.object.eventObject;
import fr.kaname.kanaeventmanager.object.playerRank;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class DatabaseManager {

    private KanaEventManager plugin;
    private Statement statement;
    private Statement checker;
    private String eventTable;
    private String scoreTable;
    private String logsTable;
    private String LastVersion;

    public DatabaseManager(KanaEventManager plugin) {
        this.plugin = plugin;
    }

    private String getEventTable() {
        return eventTable;
    }

    public String getScoreTable() {
        return scoreTable;
    }

    public String getLogsTable() {
        return logsTable;
    }

    private Statement getStatement() {
        return statement;
    }

    public void ConnectDatabase() {

        Configuration config = plugin.getConfig();
        String host = config.getString("database.host");
        String user = config.getString("database.user");
        String pass = config.getString("database.password");
        String db = config.getString("database.database");
        String port = config.getString("database.port");

        eventTable = config.getString("database.tables.event");
        scoreTable = config.getString("database.tables.score");
        logsTable = config.getString("database.tables.logs");

        try {
            this.statement = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + db, user, pass).createStatement();
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            this.checker = DriverManager.getConnection("jdbc:mysql://webcord.fr:3306/devblog", "checker", "").createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkConnection() {
        boolean success = false;
        try {
            ResultSet resultSet = this.getStatement().executeQuery("SELECT * FROM " + this.eventTable);
            success = true;
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }

        return success;
    }

    public String GetPluginLatestVersion() {
        try {
            ResultSet Version = this.checker.executeQuery("SELECT * FROM plugins WHERE `nom` = '" + plugin.getName() + "'");
            Version.next();
            LastVersion = Version.getString("version");
            return LastVersion;


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    private void createTable() throws SQLException {
        this.getStatement().execute("CREATE TABLE IF NOT EXISTS `" + this.getEventTable() + "` (" +
                "`id` INT NOT NULL AUTO_INCREMENT," +
                "`Name` VARCHAR(45) NULL," +
                "`DisplayName` VARCHAR(45) NULL," +
                "`Broadcast` TEXT NULL," +
                "`LocX` DOUBLE NULL," +
                "`LocY` DOUBLE NULL," +
                "`LocZ` DOUBLE NULL," +
                "PRIMARY KEY (`id`))");

        this.getStatement().execute("CREATE TABLE IF NOT EXISTS `" + this.getScoreTable() + "` (" +
                "`playerUUID` VARCHAR(45) NOT NULL," +
                "`playerName` VARCHAR(45) NOT NULL," +
                "`score` INT(11) NOT NULL DEFAULT 0," +
                "PRIMARY KEY (`playerUUID`))");

        this.getStatement().execute("CREATE TABLE IF NOT EXISTS `" + this.getLogsTable() + "` (" +
                "`logID` INT(11) NOT NULL AUTO_INCREMENT," +
                "`PlayerName` VARCHAR(45) NOT NULL," +
                "`eventName` VARCHAR(45) NOT NULL," +
                "`time` TIMESTAMP NOT NULL," +
                "`winners` TEXT NOT NULL," +
                "`rewards` TEXT NOT NULL," +
                "`isBeta` BOOLEAN DEFAULT 0," +
                "`CommandsRewardsSend` TEXT NULL," +
                "`isRewardsSendSuccess` BOOLEAN DEFAULT 0," +
                "PRIMARY KEY (`logID`))");
    }

    public void logEvent(boolean isWinnerCommand, String eventName, Player eventOwner, List<OfflinePlayer> winners, List<String> rewards, boolean isBetaEvent) {

        int BetaEventValue = isBetaEvent ? 1 : 0;

        StringBuilder winnersFormatString = new StringBuilder();
        StringBuilder rewardsFormatString = new StringBuilder();

        for (OfflinePlayer winner : winners) {
            winnersFormatString.append(winner.getName()).append("/");
        }

        for (String reward : rewards) {
            rewardsFormatString.append(reward).append("/");
        }

        if (isWinnerCommand) {
            try {
                this.getStatement().execute("INSERT INTO " + this.getLogsTable() + "(`PlayerName`,`eventName`,`time`,`winners`,`rewards`,`isBeta`)" +
                        "VALUES('" + eventOwner.getName() + "','" + eventName + "',CURRENT_TIMESTAMP, '" + winnersFormatString +
                        "','" + rewardsFormatString + "','" + BetaEventValue + "')"
                );

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void getLastEvent() {

        ResultSet result = null;

        try {
            result = this.getStatement().executeQuery("SELECT * FROM");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<String> getEventList() {
        List<String> EventList = new ArrayList<>();
        try {
            ResultSet datas = this.getStatement().executeQuery("SELECT * FROM " + this.getEventTable());
            while (datas.next()) {
                EventList.add(datas.getString("Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return EventList;
    }

    public List<playerRank> getScoresList() {
        List<playerRank> classement = new ArrayList<>(10);
        try {
            ResultSet datas = this.getStatement().executeQuery("SELECT * FROM " + this.getScoreTable() + " ORDER BY score DESC");
            int i = 0;
            while (datas.next()) {
                playerRank rank = new playerRank(datas.getInt("score"), UUID.fromString(datas.getString("playerUUID")), i+1);
                classement.add(rank);
                i++;
                if (i == 10) {
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classement;
    }

    public List<UUID> getPlayerStored() {
        List<UUID> uuids = new ArrayList<>();
        try {
            ResultSet datas = this.getStatement().executeQuery("SELECT * FROM " + this.getScoreTable());
            while (datas.next()) {
                uuids.add(UUID.fromString(datas.getString("playerUUID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return uuids;
    }

    public eventObject getEvent(String eventName) {
        eventObject event = null;
        try {
            ResultSet datas = this.getStatement().executeQuery("SELECT * FROM " + this.getEventTable() + " WHERE `Name` = '" + eventName + "'");
            if (datas.next()) {

                String broadcast = datas.getString("Broadcast");
                String displayName = datas.getString("DisplayName");
                Double locX = datas.getDouble("LocX");
                Double locY = datas.getDouble("LocY");
                Double locZ = datas.getDouble("LocZ");

                event = new eventObject(eventName, broadcast, locX, locY, locZ, displayName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return event;
    }

    public UUID getPlayerUuid(String playerName) {

        UUID playerUUID = null;
        try {
            ResultSet datas = this.getStatement().executeQuery("SELECT * FROM `" + this.getScoreTable() + "` WHERE `playerName` = '" +
                    playerName + "'");

            if (datas.next()) {
                playerUUID = UUID.fromString(datas.getString("playerUUID"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playerUUID;
    }

    public void createEvent(String Name, String displayName, String Broadcast, Double LocX, Double LocY, Double LocZ) {
        try {
            this.getStatement().execute("INSERT INTO `" + this.getEventTable() + "` (`Name`, `DisplayName`, `Broadcast`, `LocX`, `LocY`, `LocZ`)" +
                    "VALUES ('" + Name + "','" + displayName + "','" + Broadcast + "','" + LocX + "','" + LocY + "','" + LocZ + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(String Name) {
        try {
            this.getStatement().execute("DELETE FROM `" + this.getEventTable() + "` WHERE `Name` = '" + Name + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayerScore(Player player) {
        try {
            this.getStatement().execute("INSERT INTO `" + this.getScoreTable() + "` (`playerUUID`, `playerName`) VALUES ('" +
                    player.getUniqueId().toString() + "', '" + player.getName() + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addToScore(UUID playerUuid, int amount) {
        try {
            this.getStatement().execute("UPDATE `" + this.getScoreTable() + "` SET `score`=score+" + amount + " WHERE `playerUUID`='"
                    + playerUuid.toString() + "'");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void incrementScore(UUID playerUuid) {
        addToScore(playerUuid, 1);
    }

    public void setScore(UUID playerUuid, int score) {
        try {
            this.getStatement().execute("UPDATE `" + this.getScoreTable() + "` SET `score`= '" + score + "' WHERE `playerUUID`='"
                    + playerUuid.toString() + "'");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getScore(UUID playerUuid) {

        int score = 0;
        try {
            ResultSet datas = this.getStatement().executeQuery("SELECT * FROM `" + this.getScoreTable() + "` WHERE `playerUUID` = '" +
                    playerUuid.toString() + "'");

            if (datas.next()) {
                score = datas.getInt("score");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return score;
    }

    public void removeToScore(UUID playerUuid, int amount) {
        try {
            this.getStatement().execute("UPDATE `" + this.getScoreTable() + "` SET `score`=score-" + amount + " WHERE `playerUUID`='"
                    + playerUuid.toString() + "'");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
