package fr.kaname.kanaeventmanager.listeners;

import fr.kaname.kanabungeetp.objects.Servers;
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
        args1Complete.add("start");
        args1Complete.add("stop");
        args1Complete.add("setEventServer");
        args1Complete.add("infos");
        args1Complete.add("launch");
        args1Complete.add("listPlayer");

        List<String> argsStartComplete = new ArrayList<>();
        argsStartComplete.add("-p");
        argsStartComplete.add("-t");

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

                if(command.startsWith(aliase + " setEventServer")) {
                    complete.clear();
                    for (Servers srv : plugin.getKbtpPlugin().getDatabaseManager().getServersList()) {

                        String serverName = srv.getServerName();

                        String cmdComplete = aliase + " setEventServer " + serverName.toLowerCase();
                        if (cmdComplete.toLowerCase().contains(command.toLowerCase())) {
                            complete.add(serverName);
                        }
                    }
                }
            }
            event.setCompletions(complete);
        }
    }
}