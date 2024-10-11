package com.blockafeller.twitch;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerAuthData {
    private final Map<UUID, String> minecraftUuidToTwitchUuidMap;

    public PlayerAuthData() {
        minecraftUuidToTwitchUuidMap = new HashMap<>();
    }

    public void addAuthData(UUID minecraftUuid, String twitchUuid) {
        minecraftUuidToTwitchUuidMap.put(minecraftUuid, twitchUuid);
    }

    public String getTwitchUuid(UUID minecraftUuid) {
        return minecraftUuidToTwitchUuidMap.get(minecraftUuid);
    }
}
