package fr.kaname.kanaeventmanager.listeners;

import fr.kaname.kanabungeetp.KanaBungeeTP;
import fr.kaname.kanaeventmanager.KanaEventManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {

    private KanaEventManager plugin;
    private KanaBungeeTP kanaBungeeTP;

    public JoinListener(KanaEventManager kanaEventManager) {
        this.plugin = kanaEventManager;
        this.kanaBungeeTP = kanaEventManager.getKbtpPlugin();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(plugin.getPrefix() + ChatColor.AQUA + event.getPlayer().getDisplayName() + " a rejoins l'event");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(plugin.getPrefix() + ChatColor.AQUA + event.getPlayer().getDisplayName() + " est parti !");
    }
}
