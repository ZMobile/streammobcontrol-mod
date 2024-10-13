package com.blockafeller.twitch.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerAuthDataMap {
    private final Map<UUID, PlayerAuthData> minecraftUuidToTwitchUuidMap;

    public PlayerAuthDataMap() {
        minecraftUuidToTwitchUuidMap = new HashMap<>();
    }

    public void addAuthData(UUID minecraftUuid, PlayerAuthData playerAuthData) {
        minecraftUuidToTwitchUuidMap.put(minecraftUuid, playerAuthData);
    }

    public PlayerAuthData getAuthData(UUID minecraftUuid) {
        return minecraftUuidToTwitchUuidMap.get(minecraftUuid);
    }

    public boolean hasAuthData(UUID minecraftUuid) {
        return minecraftUuidToTwitchUuidMap.containsKey(minecraftUuid);
    }
}
