package fr.kaname.kanaeventmanager.managers;

import fr.kaname.kanaeventmanager.KanaEventManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EventManager {

    private KanaEventManager plugin;

    public EventManager(KanaEventManager kanaEventManager) {
        this.plugin = kanaEventManager;
    }

    public void launchEvent(Player player) {
        String eventName = plugin.getActualEventName();
        String eventState = plugin.getServerOpenState();
        if (eventName != null && eventState.equals("ready") && !plugin.isEvent()) {
            player.sendMessage(plugin.getPrefix() + ChatColor.AQUA + "Lancement de l'event");
            plugin.setEvent(true);

            for (UUID uuid : plugin.getPlayerList()) {
                Player participant = Bukkit.getPlayer(uuid);
                if (participant != null) {
                    participant.sendMessage("Go !");
                }
            }


        } else if (eventName != null && !eventState.equals("ready")) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "L'event n'est pas encore prêt attendez le temps imparti ou les joueurs restants" +
                    "\nPour forcer le lancement tapez §6/event forceready");
        } else if (plugin.isEvent()) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Un event est déjà en cours arretez le avec §6/event stop");
        } else {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Il n'y a pas d'event de sélectionné pour faire un event faîtes la commande §4/event start");
        }
    }
}
