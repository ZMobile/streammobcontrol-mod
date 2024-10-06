package com.blockafeller.time;

import com.blockafeller.config.ConfigManager;
import com.blockafeller.config.ModConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class GracePeriodTimeTracker {
    private static int gracePeriodTimeRemaining;
    private static int tickCounter = 0;

    public static void register() {
        // Listen for the end of each server tick to track time
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;

            // Run the decrement logic every 20 ticks (1 second)
            if (tickCounter >= 20) {
                tickCounter = 0; // Reset tick counter after each second
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    handleTimeDecrement(player);
                }
            }
        });
    }

    public static void resetGracePeriodTime() {
        gracePeriodTimeRemaining = ConfigManager.getConfig().getGracePeriodSeconds();
    }

    private static void handleTimeDecrement(ServerPlayerEntity player) {
        if (gracePeriodTimeRemaining > 0) {
            gracePeriodTimeRemaining--;
            System.out.println("Grace period time remaining: " + gracePeriodTimeRemaining);
        }
    }

    public static int getGracePeriodTimeRemaining() {
        return gracePeriodTimeRemaining;
    }
}
