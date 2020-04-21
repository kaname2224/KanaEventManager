package fr.kaname.kanaeventmanager.listeners;

import fr.kaname.kanabungeetp.KanaBungeeTP;
import fr.kaname.kanaeventmanager.KanaEventManager;
import org.bukkit.event.Listener;

public class JoinListener implements Listener {

    private KanaEventManager plugin;
    private KanaBungeeTP kanaBungeeTP;

    public JoinListener(KanaEventManager kanaEventManager) {
        this.plugin = kanaEventManager;
        this.kanaBungeeTP = kanaEventManager.getKbtpPlugin();
    }
}
