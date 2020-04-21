package fr.kaname.kanaeventmanager.managers;

import fr.kaname.kanaeventmanager.KanaEventManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ServersManagers{

    private KanaEventManager plugin;
    private String eventServerName;

    public ServersManagers(KanaEventManager kanaEventManager) {
        this.plugin = kanaEventManager;
        this.eventServerName = kanaEventManager.getConfig().getString("BungeeCord.eventServerName");
    }

    public void ScheduleOpenedServer(int delay, Player player){

        plugin.getKbtpPlugin().openServer(eventServerName);
        player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + " Event server open for " + delay + " seconds");

        new BukkitRunnable() {

            @Override
            public void run() {
                plugin.getKbtpPlugin().closeServer(eventServerName);
            }

        }.runTaskLater(plugin, delay * 20);
    }

}
