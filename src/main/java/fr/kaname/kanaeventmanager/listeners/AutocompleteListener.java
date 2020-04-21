package fr.kaname.kanaeventmanager.listeners;

import fr.kaname.kanaeventmanager.KanaEventManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.List;

public class AutocompleteListener implements Listener {

    private KanaEventManager plugin;

    public AutocompleteListener(KanaEventManager kanaEventManager) {
        this.plugin = kanaEventManager;
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        String command = event.getBuffer();
        List<String> ali = plugin.getCommand("manageEvent").getAliases();
        if (event.getSender() instanceof Player) {
            Player player = (Player)event.getSender();
            player.sendMessage(ali.toString());
        }
    }
}
