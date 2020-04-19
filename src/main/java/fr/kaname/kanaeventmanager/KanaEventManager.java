package fr.kaname.kanaeventmanager;

import fr.kaname.kanabungeetp.KanaBungeeTP;
import org.bukkit.plugin.java.JavaPlugin;

public class KanaEventManager extends JavaPlugin {

    KanaBungeeTP kbtp;

    @Override
    public void onEnable() {
        this.getLogger().info("Plugin Enabled !");
    }
}
