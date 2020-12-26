package fr.kaname.kanaeventmanager.managers;

import fr.kaname.kanaeventmanager.KanaEventManager;
import fr.kaname.kanaeventmanager.object.eventObject;
import fr.kaname.kanaeventmanager.object.playerRank;
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
                "`score` INT(11) NOT NULL DEFAULT 0," +
                "PRIMARY KEY (`playerUUID`))");
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

    public void createEvent(String Name, String displayName, String Broadcast, Double LocX, Double LocY, Double LocZ) {
        try {
            this.getStatement().execute("INSERT INTO `" + this.getEventTable() + "` (`Name`, `DisplayName`, `Broadcast`, `LocX`, `LocY`, `LocZ`)" +
                    "VALUES ('" + Name + "','" + displayName + "','" + Broadcast + "','" + LocX + "','" + LocY + "','" + LocZ + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createPlayerScore(Player player) {
        try {
            this.getStatement().execute("INSERT INTO `" + this.getScoreTable() + "` (`playerUUID`) VALUES ('" +
                    player.getUniqueId().toString() + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void incrementScore(OfflinePlayer player) {
        try {
            this.getStatement().execute("UPDATE `" + this.getScoreTable() + "` SET `score`=score+1 WHERE `playerUUID`='"
                    + player.getUniqueId().toString() + "'");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
