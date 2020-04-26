package fr.kaname.kanaeventmanager;

import fr.kaname.kanabungeetp.KanaBungeeTP;
import fr.kaname.kanaeventmanager.listeners.AutocompleteListener;
import fr.kaname.kanaeventmanager.listeners.JoinListener;
import fr.kaname.kanaeventmanager.managers.DatabaseManager;
import fr.kaname.kanaeventmanager.managers.EventManager;
import fr.kaname.kanaeventmanager.managers.ServersManagers;
import fr.kaname.kanaeventmanager.managers.eventCommandManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class KanaEventManager extends JavaPlugin {

    private KanaBungeeTP kbtp;
    private DatabaseManager db;
    private ServersManagers serversManagers;
    private boolean isEvent;
    private String ActualEventName; // Null if "isEvent" == false
    private boolean isServerEventOpen;
    private String ServerOpenState; // Null if "isServerEventOpen" == false
    private EventManager eventManager;
    private int slot;
    private int eventPlayerCount;
    private List<UUID> playerList = new ArrayList<>();
    private Player eventOwner;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.getLogger().info("Plugin Enabled !");
        this.db = new DatabaseManager(this);
        this.db.ConnectDatabase();
        this.kbtp = (KanaBungeeTP)this.getServer().getPluginManager().getPlugin("KanaBungeeTP");
        this.getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        this.getServer().getPluginManager().registerEvents(new AutocompleteListener(this), this);
        this.getCommand("event").setExecutor(new eventCommandManager(this));
        this.serversManagers = new ServersManagers(this);
        this.eventManager = new EventManager(this);
    }

    public KanaBungeeTP getKbtpPlugin() {
        return this.kbtp;
    }

    public String getPrefix() {
        return "§9[KanaEventManager] ";
    }

    public DatabaseManager getDatabaseManager() {
        return this.db;
    }

    public ServersManagers getServersManagers() { return this.serversManagers; }

    public boolean isEvent() {
        return isEvent;
    }

    public String getActualEventName() {
        return ActualEventName;
    }

    public boolean isServerEventOpen() {
        return isServerEventOpen;
    }

    public String getServerOpenState() {
        return ServerOpenState;
    }

    public void setEvent(boolean event) {
        isEvent = event;
    }

    public void setActualEventName(String actualEventName) {
        ActualEventName = actualEventName;
    }

    public void setServerEventOpen(boolean serverEventOpen) {
        isServerEventOpen = serverEventOpen;
    }

    public void setServerOpenState(String serverOpenState) {
        ServerOpenState = serverOpenState;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void addEventPlayerCount() { this.eventPlayerCount += 1; }

    public void resetEventPlayerCount() { this.eventPlayerCount = 0; }

    public int getEventPlayerCount() { return this.eventPlayerCount; }

    public void setSlot(int amount) { this.slot = amount; }

    public int getSlot() {return this.slot; }

    public List<UUID> getPlayerList() {
        return playerList;
    }

    public void resetPlayerList() {
        this.playerList.clear();
    }

    public Player getEventOwner() {
        return eventOwner;
    }

    public void setEventOwner(Player eventOwner) {
        this.eventOwner = eventOwner;
    }
}
