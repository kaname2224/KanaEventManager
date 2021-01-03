package fr.kaname.kanaeventmanager.object;

import fr.kaname.kanaeventmanager.KanaEventManager;
import java.util.Iterator;
import java.util.List;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PapiExpansion extends PlaceholderExpansion {
    private final KanaEventManager plugin;

    public PapiExpansion(KanaEventManager plugin) {
        this.plugin = plugin;
    }

    public String getIdentifier() {
        return "KanaEventManager";
    }

    public String getAuthor() {
        return "Kaname";
    }

    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    public boolean canRegister() {
        return true;
    }

    public boolean persist() {
        return true;
    }

    public String onPlaceholderRequest(Player player, String params) {
        if (params.contains("playerClass_")) {
            List<playerRank> playerRankList = this.plugin.getDatabaseManager().getScoresList();
            Iterator var4 = playerRankList.iterator();

            playerRank rank;
            do {
                if (!var4.hasNext()) {
                    return "Emplacement vide";
                }

                rank = (playerRank)var4.next();
            } while(!params.equalsIgnoreCase("playerClass_" + rank.getClassement()));

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(rank.getUuid());
            String msg;
            switch(rank.getClassement()) {
                case 1:
                    msg = ChatColor.GOLD + "" + ChatColor.BOLD + offlinePlayer.getName() + " &r&f- &6" + rank.getScore();
                    break;
                case 2:
                    msg = ChatColor.GRAY + "" + ChatColor.BOLD + offlinePlayer.getName() + " &r&f- &8" + rank.getScore();
                    break;
                case 3:
                    msg = ChatColor.YELLOW + "" + ChatColor.BOLD + offlinePlayer.getName() + " &r&f- &e" + rank.getScore();
                    break;
                default:
                    msg = ChatColor.BLUE + offlinePlayer.getName() + " &r&f- &b" + rank.getScore();
            }

            return msg;
        } else {
            return "KEM : unknow placeholder";
        }
    }
}
