package fr.kaname.kanaeventmanager.object;

import fr.kaname.kanaeventmanager.KanaEventManager;
import java.util.Iterator;
import java.util.List;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PapiExpansion extends PlaceholderExpansion {
    private final KanaEventManager plugin;

    private String firstPlayerPlaceholder;
    private String secondPlayerPlaceholder;
    private String thirdlayerPlaceholder;
    private String defaultPlayerPlaceholder;

    public PapiExpansion(KanaEventManager plugin) {
        this.plugin = plugin;
        ConfigurationSection placeholderConfig = plugin.getConfig().getConfigurationSection("placeholder");
        if (placeholderConfig != null) {
            this.firstPlayerPlaceholder = placeholderConfig.getString("first-player");
            this.secondPlayerPlaceholder = placeholderConfig.getString("second-player");
            this.thirdlayerPlaceholder = placeholderConfig.getString("third-player");
            this.defaultPlayerPlaceholder = placeholderConfig.getString("default");
        } else {
            this.firstPlayerPlaceholder = ChatColor.GOLD + "" + ChatColor.BOLD + "{playerName} &r&f- &3{playerScore}";
            this.secondPlayerPlaceholder = ChatColor.GRAY + "" + ChatColor.BOLD + "{playerName} &r&f- &8{playerScore}";
            this.thirdlayerPlaceholder = ChatColor.YELLOW + "" + ChatColor.BOLD + "{playerName} &r&f- &e{playerScore}";
            this.defaultPlayerPlaceholder = ChatColor.BLUE + "" + ChatColor.BOLD + "{playerName} &r&f- &b{playerScore}";
        }

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
            if (offlinePlayer.getName() == null) {
                return "Erreur avec l'uuid : " + rank.getUuid().toString();
            }
            String msg;
            switch(rank.getClassement()) {
                case 1:
                    msg = firstPlayerPlaceholder;
                    break;
                case 2:
                    msg = secondPlayerPlaceholder;
                    break;
                case 3:
                    msg = thirdlayerPlaceholder;
                    break;
                default:
                    msg = defaultPlayerPlaceholder;
            }
            msg = msg.replace("{playerName}", offlinePlayer.getName());
            msg = msg.replace("{playerScore}", String.valueOf(rank.getScore()));
            return msg;
        } else {
            return "KEM : unknow placeholder";
        }
    }
}
