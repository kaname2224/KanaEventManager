package fr.kaname.kanaeventmanager.managers;

import fr.kaname.kanaeventmanager.KanaEventManager;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class ScoreManager {

    private KanaEventManager plugin;

    public ScoreManager(KanaEventManager plugin) {
        this.plugin = plugin;
    }

    public void incrementScore(UUID uuid) {
        plugin.getDatabaseManager().incrementScore(uuid);
    }

    public void setScore(UUID uuid, int newScore) {

    }

}
