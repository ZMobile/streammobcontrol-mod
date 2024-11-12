package com.blockafeller.time;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class TimeUtils {
    public static void setDayTime(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            if (world.getRegistryKey() == World.OVERWORLD) {
                world.setTimeOfDay(1000); // Set time to day (1000 is morning)
            }
        }
    }
}
