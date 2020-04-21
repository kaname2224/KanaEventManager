package fr.kaname.kanaeventmanager.listeners;

import fr.kaname.kanaeventmanager.KanaEventManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;

public class AutocompleteListener implements Listener {

    private KanaEventManager plugin;

    public AutocompleteListener(KanaEventManager kanaEventManager) {
        this.plugin = kanaEventManager;
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        String command = event.getBuffer().replace("/", "");
        List<String> eventAliases = plugin.getCommand("manageEvent").getAliases();
        List<String> complete = new ArrayList<>();

        List<String> args1Complete = new ArrayList<>();
        args1Complete.add("create");
        args1Complete.add("reload");

        if (event.getSender() instanceof Player) {
            Player player = (Player) event.getSender();
            for (String aliase : eventAliases) {
                if (command.startsWith(aliase)) {
                    for (String arg : args1Complete) {
                        String cmdComplete = aliase + " " + arg;
                        if (cmdComplete.contains(command)) {
                            complete.add(arg);
                        }
                    }
                }
            }
            event.setCompletions(complete);
        }
    }
}