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

    public void ScheduleOpenedServer(int delay, Player player, String eventName){

        plugin.getKbtpPlugin().openServer(eventServerName);
        player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Event server open for " + delay + " seconds");

        plugin.setServerOpenState("time");
        plugin.setServerEventOpen(true);
        plugin.setActualEventName(eventName);
        plugin.setEvent(false);

        new BukkitRunnable() {

            @Override
            public void run() {
                plugin.getKbtpPlugin().closeServer(eventServerName);
                player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Event server closed type §6/event launch §bto start event !");
                plugin.setServerEventOpen(false);
                plugin.setServerOpenState("ready");
            }

        }.runTaskLater(plugin, delay * 20);
    }

    public void SlotOpenedServer(int slot, Player player, String eventName) {

        plugin.setServerOpenState("slot");
        plugin.setServerEventOpen(true);
        plugin.setActualEventName(eventName);
        plugin.setEvent(false);
        plugin.setSlot(slot);

        plugin.getKbtpPlugin().openServer(eventServerName);
        player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Event server open for " + slot + " players");
    }

    public void forceStartEvent(Player player) {
        plugin.setEvent(false);
        plugin.getKbtpPlugin().closeServer(eventServerName);
        plugin.setServerEventOpen(false);
        plugin.setServerOpenState("ready");
        player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Event server closed type §6/event launch §bto start event !");
    }

}
