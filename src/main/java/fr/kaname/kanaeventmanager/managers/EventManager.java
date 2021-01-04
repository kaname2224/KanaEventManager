package fr.kaname.kanaeventmanager.managers;

import fr.kaname.kanaeventmanager.KanaEventManager;
import fr.kaname.kanaeventmanager.object.eventObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class EventManager {

    private KanaEventManager plugin;
    private Map<String, List<String>> rewardsMap = new HashMap<>();
    private String rewardMsg;
    private int rewardPing;

    public EventManager(KanaEventManager kanaEventManager) {
        this.plugin = kanaEventManager;
        this.getConfigRewards();
    }

    private void getConfigRewards() {

        this.rewardsMap.clear();
        ConfigurationSection rewardsSection = plugin.getConfig().getConfigurationSection("rewards");
        this.rewardMsg = plugin.getConfig().getString("SingleWinBroadcast");
        this.rewardPing = plugin.getConfig().getInt("rewardsPing");

        if (rewardsSection == null) {
            return;
        }

        for (String section : rewardsSection.getKeys(false)) {

            List<String> rewardsOption = new ArrayList<>();

            String command = rewardsSection.getString(section + ".command");
            String displayName = rewardsSection.getString(section + ".displayName");

            rewardsOption.add(command);
            rewardsOption.add(displayName);

            this.rewardsMap.put(section, rewardsOption);

        }

    }

    public void launchEvent(Player player) {
        String eventName = plugin.getActualEventName();
        String eventState = plugin.getServerOpenState();
        if (eventName != null && eventState.equals("ready") && !plugin.isEvent()) {
            player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Lancement de l'event");
            plugin.setEvent(true);

            eventObject event = plugin.getDatabaseManager().getEvent(eventName);
            plugin.getLogger().info(event.getBroadcast());
            Location loc = new Location(player.getWorld(), event.getLocX(), event.getLocY(), event.getLocZ());

            plugin.getEventOwner().teleport(loc);

            for (UUID uuid : plugin.getPlayerList()) {
                Player participant = Bukkit.getPlayer(uuid);
                if (participant != null) {
                    participant.teleport(loc);
                }
            }

            plugin.setServerOpenState("In progress");

        } else if (eventName != null && !eventState.equals("ready")) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "L'event n'est pas encore prêt attendez le temps imparti ou les joueurs restants" +
                    "\nPour forcer le lancement tapez §6/event forceready");
        } else if (plugin.isEvent()) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Un event est déjà en cours arretez le avec §6/event stop");
        } else {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Il n'y a pas d'event de sélectionné pour faire un event faîtes la commande §4/event start");
        }
    }

    public void stopEvent(Player player) {

        plugin.setEvent(false);
        plugin.setActualEventName(null);
        plugin.setServerEventOpen(false);
        plugin.setServerOpenState(null);
        player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Fermeture de l'event et éjections des joueurs");
        plugin.getKbtpPlugin().closeServer(plugin.getConfig().getString("BungeeCord.eventServerName"));

        for (UUID uuid : plugin.getPlayerList()) {
            Player participant = Bukkit.getPlayer(uuid);
            if (participant != null) {
                participant.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Téléporation au Lobby !");
                plugin.getKbtpPlugin().send_server(participant, plugin.getConfig().getString("BungeeCord.lobbyServerName"));
            }
        }

        plugin.resetPlayerList();
        player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Event fermé !");
    }

    public void setWinners(List<OfflinePlayer> winners, Player sender, List<String> rewards) {

        if (!plugin.isEvent()) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Il n'y a pas d'event en cours");
            return;
        }

        List<String> commandsList = new ArrayList<>();
        List<String> rewardDisplayNameList = new ArrayList<>();

        for (String rewardFull : rewards) {
            String rewardKey = rewardFull.split("-")[0];
            int rewardamount = Integer.parseInt(rewardFull.split("-")[1]);
            commandsList.add(this.rewardsMap.get(rewardKey).get(0));
            rewardDisplayNameList.add(this.rewardsMap.get(rewardKey).get(1));
        }

        String eventName = plugin.getActualEventName();

        this.stopEvent(plugin.getEventOwner());

        for (OfflinePlayer winner : winners) {
            plugin.getDatabaseManager().incrementScore(winner);
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {

                for (String command : commandsList) {
                    plugin.getPluginMessageManager().sendBukkitCommand("say " + command, sender);
                }

            }, 20*rewardPing);
        }

        this.rewardMsg = this.rewardMsg.replace("{EventName}", eventName);

        plugin.sendWinBroadcast(sender, this.rewardMsg);

    }
}
