package fr.kaname.kanaeventmanager.listeners;

import fr.kaname.kanabungeetp.objects.Servers;
import fr.kaname.kanaeventmanager.KanaEventManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.*;

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
        List<String> rewardsList = new ArrayList<>(plugin.getEventManager().getRewardsMap().keySet());

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
            args1Complete.add("broadcast");
            args1Complete.add("bc");
            args1Complete.add("teleport");
            args1Complete.add("score");
            args1Complete.add("tp");
            args1Complete.add("delete");
            args1Complete.add("PluginStatus");
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

        List<String> argsScoreComplete = new ArrayList<>();
        argsScoreComplete.add("set");
        argsScoreComplete.add("add");
        argsScoreComplete.add("remove");

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

                if (command.startsWith(aliase + " teleport") || command.startsWith(aliase + " tp")) {
                    complete.clear();
                    command = command.replace("tp", "teleport");
                    command = command.toLowerCase();
                    for (String arg : plugin.getDatabaseManager().getEventList()) {
                        String cmdComplete = aliase + " teleport " + arg;
                        cmdComplete = cmdComplete.toLowerCase();

                        if (cmdComplete.equals(command.toLowerCase()) || command.equals(aliase + " teleport")) {
                            return;
                        }

                        if (cmdComplete.startsWith(command)) {
                            complete.add(arg);
                        }
                    }
                }

                if (command.startsWith(aliase + " kick")) {
                    complete.clear();
                    for (Player arg : Bukkit.getOnlinePlayers()) {
                        String cmdComplete = aliase + " kick " + arg.getName();
                        cmdComplete = cmdComplete.toLowerCase();

                        if (cmdComplete.equals(command.toLowerCase()) || command.equals(aliase + " kick")) {
                            return;
                        }

                        if (cmdComplete.startsWith(command)) {
                            complete.add(arg.getName());
                        }
                    }
                }

                if (command.startsWith(aliase + " start")) {
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

                if(command.startsWith(aliase + " winner")) {
                    complete.clear();

                    List<String> args = Arrays.asList(command.split(" "));
                    List<Player> playerList = new ArrayList<>(Bukkit.getOnlinePlayers());

                    int currentIndex = args.size() - 1;
                    String lastArg = args.get(currentIndex).toLowerCase();

                    if (!command.contains("rewards")) {

                        complete.add("rewards");

                        for (Player p : playerList) {
                            if (p.getName().toLowerCase().startsWith(lastArg)) {
                                complete.add(p.getName());
                            }
                        }

                        /*String argString = "[";
                        for (String arg : args) {
                            argString += arg + ", ";
                        }
                        argString += "]";

                        player.sendMessage(argString);
                        player.sendMessage("LAST => " + lastPseudo);
                        */
                    } else {
                        lastArg = args.get(currentIndex).toLowerCase();
                        for (String rewardKey : rewardsList) {
                            if (rewardKey.toLowerCase().startsWith(lastArg)) {
                                complete.add(rewardKey);
                            }
                        }
                    }

                }

                if (command.startsWith(aliase + " delete")) {
                    complete.clear();
                    for (String eventName : plugin.getDatabaseManager().getEventList()) {
                        String cmdComplete = aliase + " delete " + eventName.toLowerCase();
                        if (cmdComplete.startsWith(command.toLowerCase())) {
                            complete.add(eventName);
                        }
                    }
                }

                if (command.startsWith(aliase + " score")) {
                    complete.clear();
                    for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                        String cmdComplete = aliase + " score " + offlinePlayer.getName().toLowerCase();
                        if (cmdComplete.startsWith(command.toLowerCase())) {
                            complete.add(offlinePlayer.getName());
                        }

                        if (command.toLowerCase().startsWith(cmdComplete)) {
                            complete.clear();
                            for (String argScore : argsScoreComplete) {
                                String cmdComplete2 = cmdComplete + " " + argScore.toLowerCase();
                                if (cmdComplete2.startsWith(command.toLowerCase())) {
                                    complete.add(argScore);
                                }
                            }
                        }

                    }
                }

                if (command.startsWith(aliase + " broadcast") || command.startsWith(aliase + " bc")) {
                    complete.clear();
                    for (String eventName : plugin.getDatabaseManager().getEventList()) {
                        String cmdComplete = aliase + " broadcast " + eventName.toLowerCase();
                        String cmdComplete2 = aliase + " bc " + eventName.toLowerCase();


                        if (cmdComplete.startsWith(command.toLowerCase()) || cmdComplete2.startsWith(command.toLowerCase())) {
                            complete.add(eventName);
                        }

                        if (command.startsWith(cmdComplete) || command.startsWith(cmdComplete2)) {
                            complete.clear();
                            String cmdComplete3 = aliase + " broadcast " + eventName.toLowerCase() + " -b";
                            String cmdComplete4 = aliase + " bc " + eventName.toLowerCase() + " -b";

                            if (cmdComplete3.startsWith(command.toLowerCase()) || cmdComplete4.startsWith(command.toLowerCase())) {
                                complete.add("-b");
                            }
                        }
                    }
                }
                event.setCompletions(complete);
            }
        }
    }
}