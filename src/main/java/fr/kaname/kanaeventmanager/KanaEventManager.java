package fr.kaname.kanaeventmanager;

import fr.kaname.kanabungeetp.KanaBungeeTP;
import fr.kaname.kanaeventmanager.managers.DatabaseManager;
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
    }

    public KanaBungeeTP getKbtpPlugin() {
        return kbtp;
    }

}
