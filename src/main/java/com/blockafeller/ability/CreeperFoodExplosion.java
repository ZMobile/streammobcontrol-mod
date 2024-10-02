package com.blockafeller.ability;

import com.blockafeller.extension.PlayerExtension;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class CreeperFoodExplosion {

    public static void register() {
        // Register a server tick event to monitor player food levels
        ServerTickEvents.END_SERVER_TICK.register(CreeperFoodExplosion::monitorPlayerFoodLevels);
    }

    /**
     * Monitor all players and explode them if their food level is full while they are morphed into a creeper.
     */
    private static void monitorPlayerFoodLevels(MinecraftServer server) {
        server.getPlayerManager().getPlayerList().forEach(player -> {
            // Check if the player is a creeper
            if (isCreeper(player)) {
                // If the player's food level is full (20), trigger an explosion
                if (player.getHungerManager().getFoodLevel() >= 20) {
                    triggerExplosion(player);
                }
            }
        });
    }

    /**
     * Check if the player is currently morphed into a creeper.
     */
    private static boolean isCreeper(PlayerEntity player) {
        // Use your custom Identity check or similar method to determine if the player is a creeper
        if (!((PlayerExtension) player).isInhabiting()) {
            return false;
        }
        return ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:creeper");
    }

    /**
     * Trigger an explosion at the player's location and kill them instantly.
     */
    private static void triggerExplosion(PlayerEntity player) {
        World world = player.getWorld();
        // Play the explosion sound and create an explosion at the player's position
        Explosion explosion = world.createExplosion(player, player.getX(), player.getY(), player.getZ(), 3.0f, World.ExplosionSourceType.MOB);

        // Kill the player in the process
        player.damage(world.getDamageSources().explosion(explosion), Float.MAX_VALUE);
    }
}
