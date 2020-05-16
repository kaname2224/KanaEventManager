package fr.kaname.kanaeventmanager.managers;

import fr.kaname.kanabungeetp.KanaBungeeTP;
import fr.kaname.kanaeventmanager.KanaEventManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class eventCommandManager implements CommandExecutor {

    private KanaBungeeTP kanaBungeeTP;
    private KanaEventManager plugin;

    public eventCommandManager(KanaEventManager plugin) {
        this.plugin = plugin;
        this.kanaBungeeTP = plugin.getKbtpPlugin();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {

        if (cmd.getName().equalsIgnoreCase("manageEvent") && args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.reloadConfig();
                sender.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Config Reloaded");
            }
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equals("manageEvent") && args.length >= 1) {
                if (args[0].equalsIgnoreCase("launch")) {
                    plugin.getEventManager().launchEvent(player);
                }
                if (args[0].equalsIgnoreCase("stop")) {
                   plugin.getEventManager().stopEvent(player);
                }

                if (args[0].equalsIgnoreCase("create") && args.length >= 3) {
                    String name = args[1];
                    if (!plugin.getDatabaseManager().getEventList().contains(name)) {
                        String broadcast = "";
                        for (int i = 3; i <= args.length; i++) {
                            if (i == args.length) {
                                broadcast += args[i - 1];
                            } else {
                                broadcast += args[i - 1] + " ";
                            }
                        }
                        Location location = player.getLocation();
                        plugin.getLogger().info("Creating new event : " + name);
                        player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Creating new event : " + name);
                        plugin.getDatabaseManager().createEvent(name, broadcast, location.getX(), location.getY(), location.getZ());
                    } else {
                        player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "This event name is already in use !");
                    }

                }

                if (args[0].equalsIgnoreCase("setLobbyServer")) {

                    if (args.length >= 2) {
                        try {
                            String serverName = plugin.getKbtpPlugin().getDatabaseManager().getServer(args[1]).getServerName();
                            plugin.getConfig().set("BungeeCord.lobbyServerName", serverName);
                            plugin.saveConfig();
                            plugin.reloadConfig();
                            player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Le nouveau lobby a bien été changé");
                        } catch (NullPointerException e) {
                            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Ce serveur n'existe pas !");
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Usage : /event setLobbyServer [ServerName]");
                    }

                }

                if (args[0].equalsIgnoreCase("start") && args.length >= 2) {

                    if (plugin.isEvent()) {
                        player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Un event est déjà en cours arretez le avec §6/event stop");
                        return false;
                    }

                    String arg = null;
                    int amount = -1;
                    String eventName = args[1];
                    plugin.setEventOwner(player);

                    if (args.length >= 4) {
                        arg = args[2];
                        try {
                            amount = Integer.parseInt(args[3], 10);
                        } catch (NumberFormatException exept) {
                            amount = -1;
                            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Vous devez spécifier un nombre entier positif ! Pas du texte !");
                            return false;
                        }

                        if (!arg.equalsIgnoreCase("-p") && !arg.equalsIgnoreCase("-t")) {
                            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Vous devez spécifier un argument -p ou " +
                                    "-t faîtes §6/event help §cPour plus d'informations");
                        }

                        if (amount < 0) {
                            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Vous ne pouvez pas définir un nombre négatif");
                            return false;
                        } else if (amount > 0) {

                            player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Lancement de l'Event...");

                            if (arg.equalsIgnoreCase("-t")) {
                                plugin.getServersManagers().ScheduleOpenedServer(amount, player, eventName);
                            } else if (arg.equalsIgnoreCase("-p")) {
                                plugin.getServersManagers().SlotOpenedServer(amount, player, eventName);
                            }

                        } else {
                            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Vous ne pouvez pas définir un nombre null");
                            return false;
                        }
                    }

                }
                if (args[0].equalsIgnoreCase("infos")) {
                    if (plugin.getServerOpenState() == null) {
                        player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Il n'y a pas d'event actuellement !");
                        return false;
                    } else {
                        String eventInfos = "";
                        eventInfos += "§9==== §bEvent's Infos §9====\n";
                        eventInfos += "§9Nom : §b" + plugin.getActualEventName() + "\n";
                        eventInfos += "§9Status : §b" + plugin.getServerOpenState() + "\n";
                        eventInfos += "§9Nombre de joueurs : §b" + plugin.getEventPlayerCount() + "\n";
                        eventInfos += "§9Créateur de l'Event : §b" + plugin.getEventOwner().getDisplayName() + "\n";
                        eventInfos += "§9=====================";

                        player.sendMessage(eventInfos);
                    }
                }
                if (args[0].equalsIgnoreCase("listPlayer")) {
                    if (plugin.getServerOpenState() == null) {
                        player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Il n'y a pas d'event actuellement !");
                        return false;
                    } else {
                        String eventInfos = "";
                        eventInfos += "§9==== §bEvent's Player §9====\n";
                        for (UUID uuid : plugin.getPlayerList()) {
                            Player pl = Bukkit.getPlayer(uuid);
                            eventInfos += "§b" + pl.getDisplayName() + "\n";
                        }
                        eventInfos += "§9=====================";

                        player.sendMessage(eventInfos);
                    }
                }
                if (args[0].equalsIgnoreCase("forceReady")) {
                    if(plugin.getActualEventName() != null) {
                        plugin.getServersManagers().forceStartEvent(player);
                    } else {
                        player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Il n'y a pas d'event actuellement !");
                    }
                }

            } else if (cmd.getName().equals("manageEvent")) {
                player.sendMessage(ChatColor.RED + "Args missing");
                player.sendMessage(ChatColor.AQUA + "DEBUG : Afficher l'aide ici !");
            }
        }
        return false;
    }
}
