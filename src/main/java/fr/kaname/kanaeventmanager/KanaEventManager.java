package fr.kaname.kanaeventmanager;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.kaname.kanabungeetp.KanaBungeeTP;
import fr.kaname.kanaeventmanager.listeners.AutocompleteListener;
import fr.kaname.kanaeventmanager.listeners.JoinListener;
import fr.kaname.kanaeventmanager.managers.*;
import fr.kaname.kanaeventmanager.object.PapiExpansion;
import fr.kaname.kanaeventmanager.object.eventObject;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private PluginMessageManager pluginMessageManager;
    private ScoreManager scoreManager;
    private boolean isBetaEvent = false;
    private PapiExpansion placeholderExpansion;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.placeholderExpansion = new PapiExpansion(this);
        this.placeholderExpansion.register();
        this.getLogger().info("Plugin Enabled !");
        this.db = new DatabaseManager(this);
        this.db.ConnectDatabase();
        this.kbtp = (KanaBungeeTP)this.getServer().getPluginManager().getPlugin("KanaBungeeTP");
        this.getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        this.getServer().getPluginManager().registerEvents(new AutocompleteListener(this), this);
        this.getCommand("event").setExecutor(new eventCommandManager(this));
        this.serversManagers = new ServersManagers(this);
        this.eventManager = new EventManager(this);
        this.pluginMessageManager = new PluginMessageManager(this);
        this.scoreManager = new ScoreManager(this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    public PapiExpansion getPlaceholderExpansion() {
        return placeholderExpansion;
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

    public PluginMessageManager getPluginMessageManager() {
        return this.pluginMessageManager;
    }

    public ScoreManager getScoreManager() {
        return this.scoreManager;
    }

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

    public boolean isBetaEvent() {
        return isBetaEvent;
    }

    public void sendSpawn(Player player) {

        FileConfiguration config = this.getConfig();

        String worldName = config.getString("SpawnPoint.world");

        double locX = config.getDouble("SpawnPoint.locX");
        double locY = config.getDouble("SpawnPoint.locY");
        double locZ = config.getDouble("SpawnPoint.locZ");
        float pitch = Float.parseFloat(Objects.requireNonNull(config.getString("SpawnPoint.pitch")));
        float yaw = Float.parseFloat(Objects.requireNonNull(config.getString("SpawnPoint.yaw")));
        assert worldName != null;
        World world = Bukkit.getWorld(worldName);

        Location loc = new Location(world, locX, locY, locZ, yaw, pitch);


        player.teleport(loc);
        player.sendMessage(this.getPrefix() + ChatColor.AQUA + "vous avez été renvoyé au spawn");
    }

    public void sendBroadcast(Player player, String eventName, boolean isBetaEvent) {

        String betaEventWord = this.getConfig().getString("BetaEventWord");

        eventObject event = this.getDatabaseManager().getEvent(eventName);
        if (event == null) {
            player.sendMessage(this.getPrefix() + ChatColor.RED + "Cet event n'a pas été trouvé !");
            return;
        }
        String text = event.getBroadcast().replace("&", "§");

        String bc = getConfig().getString("Broadcast").replace("&", "§");

        bc = bc.replace("{EventName}", event.getDisplayName());
        bc = bc.replace("{Broadcast}", text);

        if (isBetaEvent && betaEventWord != null) {
            bc = bc.replace("{isBetaEvent}", betaEventWord);
        } else {
            bc = bc.replace("{isBetaEvent}", "");
        }

        this.getLogger().info(bc);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Message");
        out.writeUTF("ALL");
        out.writeUTF(bc);

        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

    public void sendWinBroadcast(Player player, String text) {

        text = text.replace("&", "§");

        this.getLogger().info(text);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Message");
        out.writeUTF("ALL");
        out.writeUTF(text);

        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }

    public void setBetaEvent(boolean isBetaEvent) {
        this.isBetaEvent = isBetaEvent;
    }
}
