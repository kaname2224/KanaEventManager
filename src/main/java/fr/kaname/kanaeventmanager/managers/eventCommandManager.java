package fr.kaname.kanaeventmanager.managers;

import fr.kaname.kanabungeetp.KanaBungeeTP;
import fr.kaname.kanaeventmanager.KanaEventManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            } else if (cmd.getName().equals("manageEvent")) {
                player.sendMessage(ChatColor.RED + "Args missing");
                player.sendMessage(ChatColor.AQUA + "DEBUG : Afficher l'aide ici !");
            }
        }
        return false;
    }
}
