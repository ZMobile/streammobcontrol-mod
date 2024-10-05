package com.blockafeller.time;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTimeDataManager {
    private static final Map<UUID, PlayerTimeData> playerDataMap = new HashMap<>();

    public static void registerEvents() {
        // Load player data when they join the server
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            PlayerTimeData data = loadPlayerData(player.getUuid(), player.getServerWorld());
            playerDataMap.put(player.getUuid(), data);
        });

        // Save player data when they disconnect
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            savePlayerData(player.getUuid(), player.getServerWorld());
            playerDataMap.remove(player.getUuid());
        });

        // Optionally: Save data periodically (e.g., every few ticks)
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (UUID playerUuid : playerDataMap.keySet()) {
                ServerWorld world = server.getOverworld(); // Assume overworld for simplicity
                savePlayerData(playerUuid, world);
            }
        });
    }

    private static PlayerTimeData loadPlayerData(UUID playerUuid, ServerWorld world) {
        // Load the player's custom time data using the world's persistent storage
        return world.getPersistentStateManager().getOrCreate(
                PlayerTimeData::fromNbt,    // How to deserialize data
                PlayerTimeData::new,        // What to do if no data exists
                playerUuid.toString()       // Unique ID for each player's data
        );
    }

    private static void savePlayerData(UUID playerUuid, ServerWorld world) {
        PlayerTimeData data = playerDataMap.get(playerUuid);
        if (data != null) {
            world.getPersistentStateManager().set(playerUuid.toString(), data);
            data.markDirty();
        }
    }

    // Getter for player-specific time data
    public static PlayerTimeData getPlayerTimeData(UUID playerUuid) {
        return playerDataMap.get(playerUuid);
    }

    // Adds or retrieves the PlayerTimeData for a specific player UUID
    public static PlayerTimeData getOrCreatePlayerTimeData(UUID playerUuid, ServerWorld world) {
        return playerDataMap.computeIfAbsent(playerUuid, id -> loadPlayerData(id, world));
    }


}