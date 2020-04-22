package fr.kaname.kanaeventmanager;

import fr.kaname.kanabungeetp.KanaBungeeTP;
import fr.kaname.kanaeventmanager.listeners.AutocompleteListener;
import fr.kaname.kanaeventmanager.listeners.JoinListener;
import fr.kaname.kanaeventmanager.managers.DatabaseManager;
import fr.kaname.kanaeventmanager.managers.ServersManagers;
import fr.kaname.kanaeventmanager.managers.eventCommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class KanaEventManager extends JavaPlugin {

    private KanaBungeeTP kbtp;
    private DatabaseManager db;
    private ServersManagers serversManagers;
    private Boolean isEvent;
    private String ActualEventName; // Null if "is event" == false


    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.getLogger().info("Plugin Enabled !");
        this.db = new DatabaseManager(this);
        this.db.ConnectDatabase();
        this.kbtp = (KanaBungeeTP)this.getServer().getPluginManager().getPlugin("KanaBungeeTP");
        this.getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        this.getServer().getPluginManager().registerEvents(new AutocompleteListener(this), this);
        this.getCommand("event").setExecutor(new eventCommandManager(this));
        this.serversManagers = new ServersManagers(this);
    }

    public KanaBungeeTP getKbtpPlugin() {
        return this.kbtp;
    }

    public String getPrefix() {
        return "§9[KanaEventManager] ";
    }

    public DatabaseManager getDatabaseManager() {
        return this.db;
    }

    public ServersManagers getServersManagers() { return this.serversManagers; }

}
