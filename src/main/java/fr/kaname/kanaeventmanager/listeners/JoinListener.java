package fr.kaname.kanaeventmanager.listeners;

import fr.kaname.kanaeventmanager.KanaEventManager;
import fr.kaname.kanaeventmanager.object.eventObject;
import fr.kaname.kanaeventmanager.object.playerRank;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JoinListener implements Listener {

    private KanaEventManager plugin;

    public JoinListener(KanaEventManager kanaEventManager) {
        this.plugin = kanaEventManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        List<UUID> listUUID = plugin.getDatabaseManager().getPlayerStored();

        if (!listUUID.contains(event.getPlayer().getUniqueId())) {
            plugin.getDatabaseManager().createPlayerScore(event.getPlayer());
        }

        if (event.getPlayer().isOp() || event.getPlayer().hasPermission("kanaeventmanager.event.admin")) {
            checkLatestVersion(event.getPlayer());
        } else {
            event.getPlayer().getInventory().clear();
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
            for (PotionEffect effect : event.getPlayer().getActivePotionEffects()) {
                event.getPlayer().removePotionEffect(effect.getType());
            }
            plugin.sendSpawn(event.getPlayer());
        }

        if (plugin.getActualEventName() != null) {
            eventObject actualEvent = plugin.getDatabaseManager().getEvent(plugin.getActualEventName());

            if (plugin.getServerOpenState().equalsIgnoreCase("slot")) {
                this.updateParticipant();
                event.setJoinMessage(plugin.getPrefix() + ChatColor.AQUA + event.getPlayer().getDisplayName() + " a rejoint l'event " + plugin.getActualEventName() + " (" + plugin.getEventPlayerCount() + "/" + plugin.getSlot() + ")");
            } else if (plugin.getServerOpenState().equalsIgnoreCase("time")) {
                event.setJoinMessage(plugin.getPrefix() + ChatColor.AQUA + event.getPlayer().getDisplayName() + " a rejoint l'event " + plugin.getActualEventName());
                this.updateParticipant();
            } else {
                event.setJoinMessage(plugin.getPrefix() + ChatColor.AQUA + event.getPlayer().getDisplayName() + " a rejoint l'event " + plugin.getActualEventName());
            }

        } else {
                event.setJoinMessage(ChatColor.BLUE + "[+] " + ChatColor.AQUA + event.getPlayer().getDisplayName());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(plugin.getPrefix() + ChatColor.AQUA + event.getPlayer().getDisplayName() + " est parti !");
        this.deleteParticipent(event.getPlayer());
    }

    private void deleteParticipent(Player player) {
        if (plugin.getPlayerList().contains(player.getUniqueId())) {
            plugin.getPlayerList().remove(player.getUniqueId());
        }
    }

    private void checkLatestVersion(Player player) {
        String Latest = plugin.getDatabaseManager().GetPluginLatestVersion();
        String Current = plugin.getDescription().getVersion();

        if (!Latest.equalsIgnoreCase(Current)) {
            TextComponent msg = new TextComponent(ChatColor.BLUE + "========= KanaEventManager =========\n" +
                    ChatColor.AQUA + "  Nouvelle version disponible : " + Latest + "\n" +
                    ChatColor.AQUA + "  Version Actuelle : " + Current + "\n" +
                    ChatColor.AQUA + "  Cliquez ");

            TextComponent link = new TextComponent("ICI" + ChatColor.AQUA);
            link.setColor(net.md_5.bungee.api.ChatColor.BLUE);

            link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                    "https://devblog.webcord.fr/plugins/?plugin=KanaEventManager&lang=FR_fr"));

            msg.addExtra(link);
            msg.addExtra(ChatColor.AQUA + " pour la télécharger" + "\n" +
                    ChatColor.BLUE + "===================================");

            player.spigot().sendMessage(msg);

        }
    }

    private void updateParticipant() {

        plugin.resetEventPlayerCount();
        plugin.resetPlayerList();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOp() || !player.hasPermission("kanaeventmanager.event.admin")) {
                plugin.addEventPlayerCount();
                plugin.getPlayerList().add(player.getUniqueId());
            }
        }

        if (!plugin.getPlayerList().isEmpty() && plugin.getPlayerList().size() >= plugin.getSlot() && plugin.getServerOpenState().equals("slot")) {

            String eventServerName = plugin.getConfig().getString("BungeeCord.eventServerName");

            plugin.setEvent(false);
            plugin.getKbtpPlugin().closeServer(eventServerName);
            plugin.setServerEventOpen(false);
            plugin.setServerOpenState("ready");
            plugin.getEventOwner().sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Le nombre de joueur requis a été atteinds\n" +
                    "tapez §6/event launch pour commencer");
        }
    }
}
