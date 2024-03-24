package de.tjuli.rotationdatacollector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class RotationDataCollector extends JavaPlugin {

    public static final int SAVE_INTERVAL_PACKETS = 300; //15 Sekunden
    public static RotationDataCollector INSTANCE;
    private final Map<Player, RotationDataManager.RotationData> previousRotations = new HashMap<>();

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
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.LOOK, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.POSITION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                RotationDataManager.RotationData previousRotation = previousRotations.get(player);

                float deltaYaw = 0;
                float deltaPitch = 0;
                float currentYaw;
                float currentPitch;


                if (previousRotation == null) {
                    if (event.getPacketType() == PacketType.Play.Client.LOOK || event.getPacketType() == PacketType.Play.Client.POSITION_LOOK) {
                        StructureModifier<Float> floats = event.getPacket().getFloat();
                        currentYaw = floats.read(0); // Yaw rotation
                        currentPitch = floats.read(1); // Pitch rotation
                        previousRotations.put(player, new RotationDataManager.RotationData(0, 0, currentYaw, currentPitch));

                    }
                    return;
                }
                float previousYaw = previousRotation.getYawX();
                float previousPitch = previousRotation.getPitchY();
                currentYaw = previousYaw; // If its just a movement packet the yaw does not change
                currentPitch = previousPitch; // If its just a movement packet the pitch does not change


                if (event.getPacketType() == PacketType.Play.Client.LOOK || event.getPacketType() == PacketType.Play.Client.POSITION_LOOK) {
                    StructureModifier<Float> floats = event.getPacket().getFloat();
                    currentYaw = floats.read(0); // Yaw rotation
                    currentPitch = floats.read(1); // Pitch rotation

                    deltaYaw = currentYaw - previousYaw;
                    deltaPitch = currentPitch - previousPitch;

                    previousRotations.put(player, new RotationDataManager.RotationData(0, 0, currentYaw, currentPitch));
                }

                RotationDataManager.addRotationData(player, deltaYaw, deltaPitch, currentYaw, currentPitch);
            }
        });
    }


}
