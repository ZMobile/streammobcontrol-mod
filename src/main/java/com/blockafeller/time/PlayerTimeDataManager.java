package com.blockafeller.time;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTimeDataManager {

    // The global data container to store all players' time data
    private static GlobalPlayerTimeData globalPlayerTimeData;

    // Unique identifier for the global player data in the server's PersistentState
    private static final String GLOBAL_DATA_NAME = "global_player_time_data";

    // In-memory cache for quick access during gameplay
    private static final Map<UUID, PlayerTimeData> playerDataMap = new HashMap<>();

    // Register the event listeners for player join, disconnect, and server ticks
    public static void register() {
        // Load the global data when the server starts
        ServerTickEvents.START_SERVER_TICK.register(PlayerTimeDataManager::loadGlobalData);

        // Load individual player data when they join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            UUID playerUuid = player.getUuid();

            // Load the player-specific data from the global data store
            PlayerTimeData data = globalPlayerTimeData.getOrCreatePlayerData(playerUuid);
            playerDataMap.put(playerUuid, data);
        });

        // Save individual player data when they disconnect
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            UUID playerUuid = player.getUuid();

            // Save the specific player's data to the global state and remove from in-memory cache
            savePlayerData(playerUuid);
            playerDataMap.remove(playerUuid);
        });

        // Save all player data periodically (e.g., at the end of each server tick)
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            saveGlobalData(server);
        });
    }

    // Load the global data when the server starts
    private static void loadGlobalData(MinecraftServer server) {
        if (globalPlayerTimeData == null) {
            // Load or create the global player data from the server's PersistentStateManager
            globalPlayerTimeData = server.getOverworld().getPersistentStateManager().getOrCreate(
                    GlobalPlayerTimeData::fromNbt,
                    GlobalPlayerTimeData::new,
                    GLOBAL_DATA_NAME
            );
        }
    }

    // Save the global data at the end of each server tick or when players disconnect
    private static void saveGlobalData(MinecraftServer server) {
        if (globalPlayerTimeData != null) {
            globalPlayerTimeData.markDirty();
            server.getOverworld().getPersistentStateManager().set(GLOBAL_DATA_NAME, globalPlayerTimeData);
        }
    }

    // Save the data for an individual player
    private static void savePlayerData(UUID playerUuid) {
        PlayerTimeData data = playerDataMap.get(playerUuid);
        if (data != null && globalPlayerTimeData != null) {
            globalPlayerTimeData.putPlayerData(playerUuid, data);
        }
    }

    // Get the PlayerTimeData for a specific player UUID
    public static PlayerTimeData getPlayerTimeData(UUID playerUuid) {
        return playerDataMap.get(playerUuid);
    }

    // Retrieve or create player-specific time data
    public static PlayerTimeData getOrCreatePlayerTimeData(UUID playerUuid, MinecraftServer server) {
        if (playerDataMap.containsKey(playerUuid)) {
            return playerDataMap.get(playerUuid);
        } else {
            loadGlobalData(server);
            return globalPlayerTimeData.getOrCreatePlayerData(playerUuid);
        }
    }

    // Global data container for storing all player time data
    public static class GlobalPlayerTimeData extends PersistentState {
        private final Map<UUID, PlayerTimeData> playerDataMap = new HashMap<>();

        // Default constructor
        public GlobalPlayerTimeData() {}

        // Load data from NBT when the global state is initialized
        public static GlobalPlayerTimeData fromNbt(NbtCompound nbt) {
            GlobalPlayerTimeData globalData = new GlobalPlayerTimeData();
            NbtList playerList = nbt.getList("PlayerData", NbtElement.COMPOUND_TYPE);
            for (NbtElement element : playerList) {
                NbtCompound playerNbt = (NbtCompound) element;
                UUID playerUuid = UUID.fromString(playerNbt.getString("UUID"));
                PlayerTimeData playerData = PlayerTimeData.fromNbt(playerNbt);
                globalData.playerDataMap.put(playerUuid, playerData);
            }
            return globalData;
        }

        // Save data to NBT when the global state is persisted
        @Override
        public NbtCompound writeNbt(NbtCompound nbt) {
            NbtList playerList = new NbtList();
            for (Map.Entry<UUID, PlayerTimeData> entry : playerDataMap.entrySet()) {
                NbtCompound playerNbt = new NbtCompound();
                playerNbt.putString("UUID", entry.getKey().toString());
                entry.getValue().writeNbt(playerNbt);
                playerList.add(playerNbt);
            }
            nbt.put("PlayerData", playerList);
            return nbt;
        }

        // Get or create a new PlayerTimeData instance for a specific UUID
        public PlayerTimeData getOrCreatePlayerData(UUID playerUuid) {
            return playerDataMap.computeIfAbsent(playerUuid, id -> new PlayerTimeData());
        }

        // Put a specific player's data into the global map
        public void putPlayerData(UUID playerUuid, PlayerTimeData data) {
            playerDataMap.put(playerUuid, data);
        }
    }
}