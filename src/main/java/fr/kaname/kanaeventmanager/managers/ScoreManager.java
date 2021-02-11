package fr.kaname.kanaeventmanager.managers;

import fr.kaname.kanaeventmanager.KanaEventManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ScoreManager {

    private KanaEventManager plugin;

    public ScoreManager(KanaEventManager plugin) {
        this.plugin = plugin;
    }

    public void scoreCommand(Player sender, Command cmd,  String[] args) {

        if (args.length >= 3) {

            if (args[2].equalsIgnoreCase("add")) {
                sender.sendMessage("score add");

            } else if (args[2].equalsIgnoreCase("remove")) {
                sender.sendMessage("score remove");

            } else if (args[2].equalsIgnoreCase("set")) {
                sender.sendMessage("score set");
            }

        } else {

            String playerName = args[1];
            UUID playerUUID = plugin.getDatabaseManager().getPlayerUuid(playerName);

            if (playerUUID != null) {
                int score = plugin.getDatabaseManager().getScore(playerUUID);
                sender.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Le score de " + playerName + " est " + score);
            } else {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Le joueur n'a pas été trouvé");
            }
        }

    }

    public void incrementScore(UUID uuid) {
        plugin.getDatabaseManager().incrementScore(uuid);
    }

    public int getScore(UUID uuid) {
        return plugin.getDatabaseManager().getScore(uuid);
    }

    public void setScore(UUID uuid, int newScore) {

    }

}
