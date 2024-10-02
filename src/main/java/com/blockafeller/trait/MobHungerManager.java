package com.blockafeller.trait;

import com.blockafeller.extension.PlayerExtension;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.Map;

public class MobHungerManager {

    // Track the last stable food level for each player who is morphed into any mob
    private static final Map<PlayerEntity, Integer> lastFoodLevels = new HashMap<>();

    public static void register() {
        // Register a server tick event to monitor player food levels
        //ServerTickEvents.END_SERVER_TICK.register(MobHungerManager::managePlayerHunger);
    }

    /**
     * Monitor each player on the server and restore the food level if it decreases without eating.
     */
    private static void managePlayerHunger(MinecraftServer server) {
        server.getPlayerManager().getPlayerList().forEach(player -> {
            // Check if the player is morphed into any mob type
            if (isMorphed(player)) {
                int currentFoodLevel = player.getHungerManager().getFoodLevel();

                // If the player has no tracked food level yet, initialize it
                if (!lastFoodLevels.containsKey(player)) {
                    lastFoodLevels.put(player, currentFoodLevel);
                }

                // Get the last stable food level
                int lastStableLevel = lastFoodLevels.get(player);

                // If the food level has gone down and the player is not eating, restore it
                if (currentFoodLevel < lastStableLevel && !player.isUsingItem()) {
                    player.getHungerManager().setFoodLevel(lastStableLevel);
                }

                // Update the tracked stable food level if the player is eating (food level increases)
                if (player.isUsingItem()) {
                    lastFoodLevels.put(player, currentFoodLevel);
                }
            } else {
                // Remove the player from the tracking map if they are no longer morphed
                lastFoodLevels.remove(player);
            }
        });
    }

    /**
     * Check if the player is currently morphed into any mob.
     */
    private static boolean isMorphed(PlayerEntity player) {
        // Use your custom check for detecting if the player is morphed (e.g., Identity mod)
        // Replace this with the actual check for your mod
        return ((PlayerExtension) player).isInhabiting();
    }
}
