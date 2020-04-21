package fr.kaname.kanaeventmanager;

import fr.kaname.kanabungeetp.KanaBungeeTP;
import fr.kaname.kanaeventmanager.listeners.JoinListener;
import fr.kaname.kanaeventmanager.managers.DatabaseManager;
import fr.kaname.kanaeventmanager.managers.eventCommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class KanaEventManager extends JavaPlugin {

    KanaBungeeTP kbtp;
    DatabaseManager db;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.getLogger().info("Plugin Enabled !");
        this.db = new DatabaseManager(this);
        this.db.ConnectDatabase();
        this.getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        this.getCommand("event").setExecutor(new eventCommandManager(this));
    }

    public KanaBungeeTP getKbtpPlugin() {
        return kbtp;
    }

    public String getPrefix() {
        return "§9[KanaEventManager] ";
    }

    public DatabaseManager getDatabaseManager() {
        return this.db;
    }

}
