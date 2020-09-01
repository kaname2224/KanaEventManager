package fr.kaname.kanaeventmanager.listeners;

import fr.kaname.kanabungeetp.objects.Servers;
import fr.kaname.kanaeventmanager.KanaEventManager;
import org.bukkit.Bukkit;
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
        List<String> eventAliases = plugin.getCommand("manageevent").getAliases();
        List<String> complete = new ArrayList<>();

        List<String> args1Complete = new ArrayList<>();
        if (event.getSender().hasPermission("kanaeventmanager.event.admin")) {
            args1Complete.add("create");
            args1Complete.add("reload");
            args1Complete.add("start");
            args1Complete.add("stop");
            args1Complete.add("infos");
            args1Complete.add("launch");
            args1Complete.add("listPlayer");
            args1Complete.add("forceReady");
            args1Complete.add("kick");
            args1Complete.add("winner");
        }
        if (event.getSender().hasPermission("kanaeventmanager.command.leave") || event.getSender().hasPermission("kanaeventmanager.event.admin")) {
            args1Complete.add("leave");
        }
        if (event.getSender().hasPermission("kanaeventmanager.command.spawn") || event.getSender().hasPermission("kanaeventmanager.event.admin")) {
            args1Complete.add("spawn");
        }

        List<String> argsStartComplete = new ArrayList<>();
        argsStartComplete.add("-p");
        argsStartComplete.add("-t");

        if (event.getSender() instanceof Player) {
            Player player = (Player) event.getSender();
            for (String aliase : eventAliases) {

                if (!command.startsWith(aliase)) {
                    return;
                }

                if (command.startsWith(aliase)) {
                    for (String arg : args1Complete) {
                        String cmdComplete = aliase + " " + arg;
                        if (cmdComplete.contains(command)) {
                            complete.add(arg);
                        }
                    }
                }

                if(command.startsWith(aliase + " start")) {
                    complete.clear();
                    for (String arg : plugin.getDatabaseManager().getEventList()) {
                        String cmdComplete = aliase + " start " + arg.toLowerCase();

                        if (cmdComplete.equals(command.toLowerCase()) || command.equals(aliase + " start")) {
                            return;
                        }


                        if (cmdComplete.contains(command.toLowerCase())) {
                            complete.add(arg);
                        }

                        if (command.contains(arg)) {
                            for (String arg2 : argsStartComplete) {
                                String cmdComplete2 = aliase + " start " + arg.toLowerCase() + " " + arg2.toLowerCase();

                                if (cmdComplete2.contains(command.toLowerCase())) {
                                    complete.add(arg2);
                                }
                            }
                            break;
                        }
                    }
                }

                if(command.startsWith(aliase + " setLobbyServer")) {
                    complete.clear();
                    for (Servers srv : plugin.getKbtpPlugin().getDatabaseManager().getServersList()) {

                        String serverName = srv.getServerName();

                        String cmdComplete = aliase + " setLobbyServer " + serverName.toLowerCase();
                        if (cmdComplete.toLowerCase().contains(command.toLowerCase())) {
                            complete.add(serverName);
                        }
                    }
                }

                if(command.startsWith(aliase + " setLobbyServer")) {
                    complete.clear();
                    for (Player p : Bukkit.getOnlinePlayers()) {

                        String cmdComplete = aliase + " setLobbyServer " + p.getName().toLowerCase();
                        if (cmdComplete.toLowerCase().contains(command.toLowerCase())) {
                            complete.add(p.getName());
                        }
                    }
                }
                event.setCompletions(complete);
            }
        }
    }
}