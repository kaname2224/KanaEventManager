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


        String playerName = args[1];
        UUID playerUUID = plugin.getDatabaseManager().getPlayerUuid(playerName);

        if (args.length >= 4) {
            if (args[2].equalsIgnoreCase("add")) {
                int amount = Integer.parseInt(args[3]);
                if (amount < 0) {
                    sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Vous devez spécifiez un nombre supérieur à 0");
                    return;
                }
                this.addToScore(playerUUID, amount);
                sender.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Vous avez rajouté " + amount +
                        " au score de " + playerName);

            } else if (args[2].equalsIgnoreCase("remove")) {
                int amount = Integer.parseInt(args[3]);
                if (amount < 0) {
                    sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Vous devez spécifiez un nombre supérieur à 0");
                } else if (plugin.getDatabaseManager().getScore(playerUUID) - amount < 0) {
                    sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Le joueur n'a pas assez de points");
                } else {
                    this.removeToScore(playerUUID, amount);
                    sender.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Vous avez retiré " + amount +
                            " au score de " + playerName);
                }

            } else if (args[2].equalsIgnoreCase("set")) {
                int amount = Integer.parseInt(args[3]);
                this.setScore(playerUUID, amount);
                sender.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Vous avez défini le score de " +
                        playerName + " à " + amount);
            }

        } else if (args.length == 2){


            if (playerUUID != null) {
                int score = plugin.getDatabaseManager().getScore(playerUUID);
                sender.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Le score de " + playerName + " est " + score);
            } else {
                sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Le joueur n'a pas été trouvé");
            }
        } else {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Mauvaise syntaxe\n" +
                    "/event score [Player] => Voir le score d'un joueur\n" +
                    "/event score [Player] [add|remove|set] [amount] => Définir le score d'un joueur");
        }

    }

    private void removeToScore(UUID uuid, int amount) {
        plugin.getDatabaseManager().removeToScore(uuid, amount);
    }

    public void incrementScore(UUID uuid) {
        plugin.getDatabaseManager().incrementScore(uuid);
    }

    public int getScore(UUID uuid) {
        return plugin.getDatabaseManager().getScore(uuid);
    }

    public void setScore(UUID uuid, int newScore) {
        plugin.getDatabaseManager().setScore(uuid, newScore);
    }

    public void addToScore(UUID uuid, int amount) {
        plugin.getDatabaseManager().addToScore(uuid, amount);
    }

}
