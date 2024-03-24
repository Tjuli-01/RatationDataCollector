package de.tjuli.rotationdatacollector;

import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RotationDataManager {

    private static final Map<Player, SampleList<RotationData>> rotationDataMap = new HashMap<>();

    public static void addRotationData(Player player, float yaw, float pitch) {
        if (rotationDataMap.get(player) != null) {
            SampleList<RotationData> rotationDataSampleList = rotationDataMap.get(player);
            rotationDataSampleList.add(new RotationData(yaw, pitch));
            if (rotationDataSampleList.isCollected()) {
                saveRotationDataFile(player);
            }

        } else {
            rotationDataMap.put(player, new SampleList<>(RotationDataCollector.SAVE_INTERVAL_PACKETS));
        }
    }

    public static String getAndClearRotationData(Player player) {
        StringBuilder result = new StringBuilder();
        SampleList<RotationData> rotationDataList = rotationDataMap.get(player);
        if (rotationDataList == null) {
            return null;
        }

        result.append("[");
        for (RotationData rotationData : rotationDataList) {
            result.append("{").append(rotationData.getYaw()).append(", ").append(rotationData.getPitch()).append("}, ");
        }

        result.append("]");


        rotationDataMap.get(player).clear();
        return result.toString();

    }

    private static void saveRotationDataFile(Player player) {

        File dataFile = new File(RotationDataCollector.INSTANCE.getDataFolder(),  "rotation_data.txt");

        try (FileWriter writer = new FileWriter(dataFile, true)) {
            String rotationData = RotationDataManager.getAndClearRotationData(player);
            if (rotationData == null) {
                return;
            }
            // Save rotation data to file
            if (!rotationData.isEmpty()) {
                writer.write(rotationData + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static float clamp180(float value) {

        value %= 360F;

        if (value >= 180.0F) value -= 360.0F;

        if (value < -180.0F) value += 360.0F;

        return value;
    }

    public static class RotationData {
        private final float yaw;
        private final float pitch;

        public RotationData(float yawX, float pitchY) {
            this.yaw = clamp180(yawX);
            this.pitch = pitchY;
        }
        public float getYaw() {
            return yaw;
        }

        public float getPitch() {
            return pitch;
        }


    }
}
