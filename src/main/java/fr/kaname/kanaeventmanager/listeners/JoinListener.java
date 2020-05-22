package fr.kaname.kanaeventmanager.listeners;

import fr.kaname.kanabungeetp.KanaBungeeTP;
import fr.kaname.kanaeventmanager.KanaEventManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {

    private KanaEventManager plugin;
    private KanaBungeeTP kanaBungeeTP;

    public JoinListener(KanaEventManager kanaEventManager) {
        this.plugin = kanaEventManager;
        this.kanaBungeeTP = kanaEventManager.getKbtpPlugin();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        if (event.getPlayer().isOp()) {
            checkLatestVersion(event.getPlayer());
        }

        if (plugin.getServerOpenState() != null && plugin.getServerOpenState().equalsIgnoreCase("slot")) {
            this.updateParticipant();
            event.setJoinMessage(plugin.getPrefix() + ChatColor.AQUA + event.getPlayer().getDisplayName() + " a rejoins l'event "+ plugin.getActualEventName() +" (" + plugin.getEventPlayerCount() + "/" + plugin.getSlot() + ")");
        } else if (plugin.getActualEventName() != null) {
            event.setJoinMessage(plugin.getPrefix() + ChatColor.AQUA + event.getPlayer().getDisplayName() + " a rejoins l'event " + plugin.getActualEventName());
            this.updateParticipant();
        } else {
            event.setJoinMessage(ChatColor.BLUE + "[+] " + ChatColor.AQUA + event.getPlayer().getDisplayName());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(plugin.getPrefix() + ChatColor.AQUA + event.getPlayer().getDisplayName() + " est parti !");
        this.updateParticipant();
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
            plugin.getLogger().info(player.getDisplayName());

            if (!player.isOp() || !player.hasPermission("kanaeventmanager.event.bypass")) {
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
            plugin.getEventOwner().sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Le nombre de joueur requis a été atteind\n" +
                    "tapez §6/event launch pour commencer");
        }
    }
}
