package fr.kaname.kanaeventmanager.managers;

import fr.kaname.kanaeventmanager.KanaEventManager;
import fr.kaname.kanaeventmanager.object.eventObject;
import fr.kaname.kanaeventmanager.object.playerRank;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    private KanaEventManager plugin;
    private Statement checker;
    private String eventTable;
    private String scoreTable;
    private String logsTable;
    private String LastVersion;

    private String JDBCUrl;
    private String JDBCUsername;
    private String JDBCPassword;


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

    private Statement getStatement() throws SQLException {
        Statement statement = null;
        try {
            statement = DriverManager.getConnection(this.JDBCUrl, this.JDBCUsername, this.JDBCPassword).createStatement();
        } catch (SQLException var3) {
            var3.printStackTrace();
        }
        return statement;
    }

    private void sendData(String query) {
        plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                Statement statement = this.getStatement();
                statement.execute(query);
                statement.close();
            } catch (SQLException var3) {
                var3.printStackTrace();
            }
        });
    }

    public void ConnectDatabase() {

        Configuration config = plugin.getConfig();
        String host = config.getString("database.host");
        String user = config.getString("database.user");
        String pass = config.getString("database.password");
        String dbName = config.getString("database.database");
        String port = config.getString("database.port");

        eventTable = config.getString("database.tables.event");
        scoreTable = config.getString("database.tables.score");
        logsTable = config.getString("database.tables.logs");

        try {
            this.JDBCUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
            this.JDBCUsername = user;
            this.JDBCPassword = pass;
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
//
//        try {
//            this.checker = DriverManager.getConnection("jdbc:mysql://webcord.fr:3306/devblog", "checker", "").createStatement();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    public boolean checkConnection() {
        boolean success = false;
        try {
            Statement statement = this.getStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + this.eventTable);
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
        String query = "CREATE TABLE IF NOT EXISTS `" + this.getEventTable() + "` (" +
                "`id` INT NOT NULL AUTO_INCREMENT," +
                "`Name` VARCHAR(45) NULL," +
                "`DisplayName` VARCHAR(45) NULL," +
                "`Broadcast` TEXT NULL," +
                "`LocX` DOUBLE NULL," +
                "`LocY` DOUBLE NULL," +
                "`LocZ` DOUBLE NULL," +
                "PRIMARY KEY (`id`)" +
                ");";
        sendData(query);

        query = "CREATE TABLE IF NOT EXISTS `" + this.getScoreTable() + "` (" +
                "`playerUUID` VARCHAR(45) NOT NULL," +
                "`playerName` VARCHAR(45) NOT NULL," +
                "`score` INT(11) NOT NULL DEFAULT 0," +
                "PRIMARY KEY (`playerUUID`)" +
                ");";
        sendData(query);

        query = "CREATE TABLE IF NOT EXISTS `" + this.getLogsTable() + "` (" +
                "`logID` INT(11) NOT NULL AUTO_INCREMENT," +
                "`Organizer` VARCHAR(45) NOT NULL," +
                "`eventID` INT NOT NULL," +
                "`time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "`isBeta` BOOLEAN DEFAULT 0," +
                "PRIMARY KEY (`logID`)," +
                "FOREIGN KEY (`eventID`) REFERENCES " + this.getEventTable() + "(`id`)" +
                ");";
        sendData(query);

        query = "CREATE TABLE IF NOT EXISTS `" + this.getLogsTable() + "_rewards` (" +
                "`rewardID` INT(11) NOT NULL AUTO_INCREMENT," +
                "`LogID` INT(11) NOT NULL," +
                "`WinnerUUID` VARCHAR(45) NOT NULL," +
                "`RewardKey` VARCHAR(25) NOT NULL," +
                "`RewardAmount` int(11) NOT NULL," +
                "PRIMARY KEY (`rewardID`)," +
                "FOREIGN KEY (`logID`) REFERENCES " + this.getLogsTable() + "(`logID`)" +
                ");";
        sendData(query);
    }

    public int logEvent(boolean isWinnerCommand, int eventID, Player eventOwner, boolean isBetaEvent) {

        int BetaEventValue = isBetaEvent ? 1 : 0;
        int logID = -1;
        if (isWinnerCommand) {
            try {
                String query = "INSERT INTO " + this.getLogsTable() + "(`Organizer`,`eventID`,`isBeta`)" +
                        "VALUES('" + eventOwner.getName() + "','" + eventID + "','" + BetaEventValue + "')";
                sendData(query);

                Statement statement = this.getStatement();
                ResultSet result = statement.executeQuery("SELECT * FROM `" + this.getLogsTable() + "` ORDER BY `logID` DESC");
                if (result.next()) {
                    logID = result.getInt("logID");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return logID;
    }

    public void logRewards(int logID, UUID winnerUUID, String rewardKey, int amount) {

        String query = "INSERT INTO " + this.getLogsTable() + "_rewards(`LogID`,`winnerUUID`,`RewardKey`, `RewardAmount`)" +
                "VALUES('" + logID + "','" + winnerUUID + "','" + rewardKey + "','" + amount + "')";
        sendData(query);
    }

    public void getLastEvent() {

        ResultSet result = null;
        try {
            Statement statement = this.getStatement();
            result = statement.executeQuery("SELECT * FROM");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public List<String> getEventList() {
        List<String> EventList = new ArrayList<>();
        try {
            Statement statement = this.getStatement();
            ResultSet datas = statement.executeQuery("SELECT * FROM " + this.getEventTable());
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
            Statement statement = this.getStatement();
            ResultSet datas = statement.executeQuery("SELECT * FROM " + this.getScoreTable() + " ORDER BY score DESC");
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
            Statement statement = this.getStatement();
            ResultSet datas = statement.executeQuery("SELECT * FROM " + this.getScoreTable());
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
            Statement statement = this.getStatement();
            ResultSet datas = statement.executeQuery("SELECT * FROM " + this.getEventTable() + " WHERE `Name` = '" + eventName + "'");
            if (datas.next()) {

                int ID = datas.getInt("id");
                String broadcast = datas.getString("Broadcast");
                String displayName = datas.getString("DisplayName");
                Double locX = datas.getDouble("LocX");
                Double locY = datas.getDouble("LocY");
                Double locZ = datas.getDouble("LocZ");

                event = new eventObject(ID, eventName, broadcast, locX, locY, locZ, displayName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return event;
    }

    public UUID getPlayerUuid(String playerName) {

        UUID playerUUID = null;
        try {
            Statement statement = this.getStatement();
            ResultSet datas = statement.executeQuery("SELECT * FROM `" + this.getScoreTable() + "` WHERE `playerName` = '" +
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
        String query = "INSERT INTO `" + this.getEventTable() + "` (`Name`, `DisplayName`, `Broadcast`, `LocX`, `LocY`, `LocZ`)" +
                "VALUES ('" + Name + "','" + displayName + "','" + Broadcast + "','" + LocX + "','" + LocY + "','" + LocZ + "')";
        sendData(query);
    }

    public void deleteEvent(String Name) {
        String query = "DELETE FROM `" + this.getEventTable() + "` WHERE `Name` = '" + Name + "'";
            sendData(query);
    }

    public void createPlayerScore(Player player) {
        String query = "INSERT INTO `" + this.getScoreTable() + "` (`playerUUID`, `playerName`) VALUES ('" +
                player.getUniqueId().toString() + "', '" + player.getName() + "')";
        sendData(query);
    }

    public void addToScore(UUID playerUuid, int amount) {
        String query = "UPDATE `" + this.getScoreTable() + "` SET `score`=score+" + amount + " WHERE `playerUUID`='"
                + playerUuid.toString() + "'";
        sendData(query);


    }

    public void incrementScore(UUID playerUuid) {
        addToScore(playerUuid, 1);
    }

    public void setScore(UUID playerUuid, int score) {
        String query = "UPDATE `" + this.getScoreTable() + "` SET `score`= '" + score + "' WHERE `playerUUID`='"
                + playerUuid.toString() + "'";
        sendData(query);
    }

    public int getScore(UUID playerUuid) {

        int score = 0;
        try {
            Statement statement = this.getStatement();
            ResultSet datas = statement.executeQuery("SELECT * FROM `" + this.getScoreTable() + "` WHERE `playerUUID` = '" +
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
        String query = "UPDATE `" + this.getScoreTable() + "` SET `score`=score-" + amount + " WHERE `playerUUID`='"
                + playerUuid.toString() + "'";
        sendData(query);
    }
}
