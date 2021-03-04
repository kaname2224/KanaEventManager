package fr.kaname.kanaeventmanager.managers;

import fr.kaname.kanaeventmanager.KanaEventManager;
import fr.kaname.kanaeventmanager.object.eventObject;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import javax.swing.plaf.metal.MetalBorders;
import java.util.*;

public class EventManager {

    private KanaEventManager plugin;
    private Map<String, List<String>> rewardsMap = new HashMap<>();
    private String singleRewardMsg;
    private String mulpipleRewardMsg;
    private String lastRewardLinkWord;
    private int rewardPing;

    public EventManager(KanaEventManager kanaEventManager) {
        this.plugin = kanaEventManager;
        this.getConfigRewards();
    }

    private void getConfigRewards() {

        this.rewardsMap.clear();
        ConfigurationSection rewardsSection = plugin.getConfig().getConfigurationSection("rewards");
        this.singleRewardMsg = plugin.getConfig().getString("SingleWinBroadcast");
        this.mulpipleRewardMsg = plugin.getConfig().getString("MultipleWinBroadcast");
        this.rewardPing = plugin.getConfig().getInt("rewardsPing");
        this.lastRewardLinkWord = plugin.getConfig().getString("RewardsLinkWord");

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

    public Map<String, List<String>> getRewardsMap() {
        return rewardsMap;
    }

    public void deleteEvent(String eventName) {
        plugin.getDatabaseManager().deleteEvent(eventName);
    }

    public void TeleportToEvent(Player player, String eventName) {

        eventObject event = plugin.getDatabaseManager().getEvent(eventName);
        if (event != null) {
            Location eventLoc = new Location(player.getWorld(), event.getLocX(), event.getLocY(), event.getLocZ());
            player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Téléportation à l'event " + event.getDisplayName());
            player.teleport(eventLoc);
        } else {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "L'event n'a pas été trouvé !");
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

    public void stopEvent(Player player, boolean isWinnerCommand) {

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

        String rewardMsg;
        String eventName = plugin.getActualEventName();

        plugin.getDatabaseManager().logEvent(true, eventName, plugin.getEventOwner(), winners, rewards, plugin.isBetaEvent());

        Map<String, Integer> commandsList = new HashMap<>();
        StringBuilder rewardsString = new StringBuilder();

        for (String rewardFull : rewards) {
            String rewardKey = rewardFull.split("-")[0];
            String rewardDisplayName = this.rewardsMap.get(rewardKey).get(1);
            int rewardAmount = Integer.parseInt(rewardFull.split("-")[1]);
            commandsList.put(this.rewardsMap.get(rewardKey).get(0), rewardAmount);

            boolean isLastReward = rewards.lastIndexOf(rewardFull) == rewards.size() - 1;

            if (isLastReward && rewards.size() > 1) {
                rewardsString.append(this.lastRewardLinkWord).append(" ").append(rewardAmount).append(" ").append(rewardDisplayName);
            } else {
                rewardsString.append(rewardAmount).append(" ").append(rewardDisplayName).append(" ");
            }

        }

        StringBuilder multipleWinnerMsg = new StringBuilder();

        for (OfflinePlayer winner : winners) {

            plugin.getScoreManager().incrementScore(winner.getUniqueId());
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {


                for (String command : commandsList.keySet()) {
                    command = command.replace("{amount}", String.valueOf(commandsList.get(command)));
                    command = command.replace("{playerName}", Objects.requireNonNull(winner.getName()));
                    plugin.getPluginMessageManager().sendBukkitCommand(command, sender);
                }

            }, 20L * rewardPing);

            boolean isLastPlayer = winners.lastIndexOf(winner) == winners.size() - 1;

            if (!isLastPlayer) {
                multipleWinnerMsg.append(winner.getName()).append(" ");
            } else {
                multipleWinnerMsg.append(this.lastRewardLinkWord).append(" ").append(winner.getName());
            }
        }

        this.stopEvent(plugin.getEventOwner(), true);

        if (winners.size() > 1) {
            rewardMsg = this.mulpipleRewardMsg;
        } else {
            rewardMsg = this.singleRewardMsg;
        }

        rewardMsg = rewardMsg.replace("{PlayerName}", Objects.requireNonNull(winners.get(0).getName()));
        rewardMsg = rewardMsg.replace("{PlayersList}", multipleWinnerMsg);
        rewardMsg = rewardMsg.replace("{EventName}", eventName);
        rewardMsg = rewardMsg.replace("{rewards}", rewardsString);

        plugin.sendWinBroadcast(sender, rewardMsg);

    }
}
