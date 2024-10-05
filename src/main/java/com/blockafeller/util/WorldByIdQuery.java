package com.blockafeller.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class WorldByIdQuery {
    public static ServerWorld getWorldById(MinecraftServer server, String worldId) {
        // Parse the world ID string into a Namespace and Path (e.g., "minecraft:overworld")
        Identifier identifier = new Identifier(worldId);

        // Get the RegistryKey<World> for the given ID
        RegistryKey<World> worldRegistryKey = RegistryKey.of(RegistryKeys.WORLD, identifier);

        // Retrieve the ServerWorld instance from the server using the RegistryKey
        return server.getWorld(worldRegistryKey);
    }

    public static boolean isPlayerInWorld(ServerPlayerEntity player, String worldId) {
        // Get the current world ID of the player as an Identifier
        Identifier currentWorldId = player.getWorld().getRegistryKey().getValue();

        // Compare the current world ID to the specified world ID
        return currentWorldId.toString().equals(worldId);
    }
}
