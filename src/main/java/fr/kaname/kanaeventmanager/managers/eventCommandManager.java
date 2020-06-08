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

        if (cmd.getName().equalsIgnoreCase("manageevent") && args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.reloadConfig();
                sender.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Config Reloaded");
            }
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("manageevent") && args.length >= 1 && player.hasPermission("kanaeventmanager.event.admin")) {
                if (args[0].equalsIgnoreCase("launch")) {
                    plugin.getEventManager().launchEvent(player);
                }
                if (args[0].equalsIgnoreCase("stop")) {
                   plugin.getEventManager().stopEvent(player);
                }
                if (args[0].equalsIgnoreCase("create") && args.length >= 4) {
                    String name = args[1];

                    if (!plugin.getDatabaseManager().getEventList().contains(name)) {

                        int index = 2;
                        String displayName = "";

                        if (args[2].startsWith("\"")) {
                            plugin.getLogger().info("TRUE");

                            for (int i = index; i < args.length; i++) {

                                if ((i + 1) >= args.length) {
                                    player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Pour créer un event faîtes §9/event name \"display name\" \"broadcast\"");
                                    return false;
                                }

                                if (args[i].endsWith("\"")) {
                                    displayName += args[i];
                                    index = i + 1;
                                    break;
                                } else {
                                    displayName += args[i] + " ";
                                }
                            }

                        } else {
                            player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Pour créer un event faîtes §9/event name \"display name\" \"broadcast\"");
                            return false;
                        }

                        String broadcast = "";

                        if (!args[index].startsWith("\"")) {
                            player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Pour créer un event faîtes §9/event name \"display name\" \"broadcast\"");
                            return false;
                        }

                        for (int i = index; i < args.length; i++) {
                            if (i + 1 == args.length) {
                                broadcast += args[i];
                            } else {
                                broadcast += args[i] + " ";
                            }
                        }

                        Location location = player.getLocation();
                        plugin.getLogger().info("Creating new event : " + name);
                        player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Creating new event : " + name);

                        broadcast = broadcast.replace("\"", "");
                        displayName = displayName.replace("\"", "");

                        broadcast = broadcast.replace("'", "\\'");
                        displayName = displayName.replace("'", "\\'");

                        plugin.getLogger().info(broadcast);
                        plugin.getLogger().info(displayName);

                        plugin.getDatabaseManager().createEvent(name, displayName, broadcast, location.getX(), location.getY(), location.getZ());
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

                            plugin.sendBroadcast(player, eventName);

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
                if (args[0].equalsIgnoreCase("setSpawnPoint")) {

                    Location pos = player.getLocation();

                    double locX = pos.getX();
                    double locY = pos.getY();
                    double locZ = pos.getZ();

                    float pitch = pos.getPitch();
                    float yaw = pos.getYaw();

                    String world = pos.getWorld().getName();


                    plugin.getConfig().set("SpawnPoint.locX", locX);
                    plugin.getConfig().set("SpawnPoint.locY", locY);
                    plugin.getConfig().set("SpawnPoint.locZ", locZ);
                    plugin.getConfig().set("SpawnPoint.pitch", pitch);
                    plugin.getConfig().set("SpawnPoint.yaw", yaw);
                    plugin.getConfig().set("SpawnPoint.world", world);

                    plugin.saveConfig();

                    player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Le spawn a bien été définie à votre position");

                }

                if (args[0].equalsIgnoreCase("spawn")) {
                    if (args.length >= 2) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target != null) {
                            plugin.sendSpawn(target);
                        } else {
                            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "ce joueur n'a pas été trouvé");
                        }
                    } else {
                        plugin.sendSpawn(player);
                    }
                }

                if (args[0].equalsIgnoreCase("leave")) {
                    String serverName = plugin.getConfig().getString("BungeeCord.lobbyServerName");
                    plugin.getKbtpPlugin().send_server(player, serverName);
                }

                if (args[0].equalsIgnoreCase("kick") && args.length >= 2) {
                    player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Vous avez expulsé " + args[1]);
                    String lobbyServerName = plugin.getConfig().getString("BungeeCord.lobbyServerName");
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target != null) {
                        plugin.getKbtpPlugin().send_server(target, lobbyServerName);
                    } else {
                        player.sendMessage(plugin.getPrefix() + ChatColor.RED + "ce joueur n'a pas été trouvé");
                    }
                }

            } else if (cmd.getName().equals("manageEvent") && args.length >= 1 && !player.hasPermission("kanaeventmanager.event.admin")) {

                if (args[0].equalsIgnoreCase("spawn") && player.hasPermission("kanaeventmanager.command.spawn")) {
                    plugin.sendSpawn(player);
                } else if (args[0].equalsIgnoreCase("leave") && player.hasPermission("kanaeventmanager.command.leave")) {
                    String serverName = plugin.getConfig().getString("BungeeCord.lobbyServerName");
                    plugin.getKbtpPlugin().send_server(player, serverName);
                } else {
                    player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Vous n'avez pas la permission d'éxécuter cette commande");
                }

            }
                else if (cmd.getName().equals("manageEvent")) {
                    if (player.hasPermission("kanaeventmanager.event.admin")) {
                        player.sendMessage(ChatColor.RED + "Args missing");
                        player.sendMessage(ChatColor.AQUA + "DEBUG : Afficher l'aide ici !");
                    } else {
                        player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Vous n'avez pas la permission d'éxécuter cette commande");
                    }
            }
        }
        return false;
    }
}
