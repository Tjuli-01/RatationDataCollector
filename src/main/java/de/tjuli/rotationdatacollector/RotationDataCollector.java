package de.tjuli.rotationdatacollector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RotationDataCollector extends JavaPlugin {

    public static final int SAVE_INTERVAL_PACKETS = 300; //15 seconds
    public static RotationDataCollector INSTANCE;

    @Override
    public void onEnable() {

        INSTANCE = this;
        registerRotationEvent();
        getLogger().info("RotationDataCollector has been enabled!");
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("RotationDataCollector has been disabled!");
    }

    public void registerRotationEvent() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.LOOK, PacketType.Play.Client.POSITION_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                StructureModifier<Float> floats = event.getPacket().getFloat();
                float yaw = floats.read(0); // Yaw rotation
                float pitch = floats.read(1); // Pitch rotation
                RotationDataManager.addRotationData(player, yaw, pitch);
            }
        });
    }


}
