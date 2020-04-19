package fr.kaname.kanaeventmanager.managers;

import fr.kaname.kanaeventmanager.KanaEventManager;
import org.bukkit.configuration.Configuration;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private KanaEventManager plugin;
    private Statement statement;
    private String eventTable;

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

}
