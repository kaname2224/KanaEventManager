package fr.kaname.kanaeventmanager.managers;

import fr.kaname.kanaeventmanager.KanaEventManager;
import fr.kaname.kanaeventmanager.object.eventObject;
import org.bukkit.configuration.Configuration;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private KanaEventManager plugin;
    private Statement statement;
    private Statement checker;
    private String eventTable;
    private String LastVersion;

    public DatabaseManager(KanaEventManager plugin) {
        this.plugin = plugin;
    }

    private String getEventTable() {
        return eventTable;
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
                "`Broadcast` TEXT NULL," +
                "`LocX` DOUBLE NULL," +
                "`LocY` DOUBLE NULL," +
                "`LocZ` DOUBLE NULL," +
                "PRIMARY KEY (`id`))");
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

    public eventObject getEvent(String eventName) {
        eventObject event = null;
        try {
            ResultSet datas = this.getStatement().executeQuery("SELECT * FROM " + this.getEventTable() + " WHERE `Name` = '" + eventName + "'");
            if (datas.next()) {

                String broadcast = datas.getString(3);
                Double locX = datas.getDouble(4);
                Double locY = datas.getDouble(5);
                Double locZ = datas.getDouble(6);

                event = new eventObject(eventName, broadcast, locX, locY, locZ);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return event;
    }

    public void createEvent(String Name, String Broadcast, Double LocX, Double LocY, Double LocZ) {
        try {
            this.getStatement().execute("INSERT INTO " + this.getEventTable() + "(`Name`, `Broadcast`, `LocX`, `LocY`, `LocZ`)" +
                    "VALUES ('" + Name + "','" + Broadcast + "','" + LocX + "','" + LocY + "','" + LocZ + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
