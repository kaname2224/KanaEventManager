package fr.kaname.kanaeventmanager.managers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.kaname.kanaeventmanager.KanaEventManager;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PluginMessageManager {

    private final KanaEventManager plugin;

    public PluginMessageManager(KanaEventManager plugin) {
        this.plugin = plugin;
    }

    public void sendBukkitCommand(String command, Player sender) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF("ALL");
        out.writeUTF("DispatchCommand");

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);

        try {
            msgout.writeUTF(command);
            msgout.writeUTF("StopDispatch");

        } catch (IOException exception){
            exception.printStackTrace();

        }

        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());

        sender.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());

    }
}
