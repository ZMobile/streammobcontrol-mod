package com.blockafeller.compass;

import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.morph.MorphUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class TrackingCompassMod {
    public void register() {
        // Register a tick event to run on the server
        ServerTickEvents.START_WORLD_TICK.register(this::onWorldTick);
    }

    // Method that runs every world tick
    private void onWorldTick(World world) {
        if (!world.isClient) {
            for (PlayerEntity player : world.getPlayers()) {
                if (((PlayerExtension) player).isInhabiting()) {
                    updateCompassToNearestPlayer((ServerPlayerEntity) player);
                }
            }
        }
    }

    // Find and update the compass in the player's inventory to point to the nearest player
    private void updateCompassToNearestPlayer(ServerPlayerEntity player) {
        PlayerEntity nearestPlayer = getNearestPlayer(player);
        if (nearestPlayer == null) return; // No nearby players

        // Find a compass in the player's inventory
        for (ItemStack itemStack : player.getInventory().main) {
            if (itemStack.isOf(Items.COMPASS) && MorphUtil.isDoNotInteractItem(itemStack)) {
                // Set the compass to point to the nearest player's location
                BlockPos nearestPos = nearestPlayer.getBlockPos();
                itemStack.getOrCreateNbt().putBoolean("LodestoneTracked", true);
                itemStack.getOrCreateNbt().putInt("LodestonePosX", nearestPos.getX());
                itemStack.getOrCreateNbt().putInt("LodestonePosY", nearestPos.getY());
                itemStack.getOrCreateNbt().putInt("LodestonePosZ", nearestPos.getZ());
                itemStack.getOrCreateNbt().putString("LodestoneDimension", player.getWorld().getRegistryKey().getValue().toString());
            }
        }
    }

    // Helper method to find the nearest player to the given player

    private PlayerEntity getNearestPlayer(ServerPlayerEntity player) {
        PlayerEntity nearestPlayer = null;
        double minDistance = Double.MAX_VALUE;

        for (PlayerEntity target : player.getWorld().getPlayers()) {
            // Skip if the player is the same, in Creative, Adventure, or Spectator mode
            if (target == player || !(target instanceof ServerPlayerEntity) || ((ServerPlayerEntity) target).interactionManager.getGameMode() != GameMode.SURVIVAL) {
                continue;
            }

            // Calculate the distance and update if this is the nearest Survival mode player
            double distance = player.squaredDistanceTo(target);
            if (distance < minDistance) {
                minDistance = distance;
                nearestPlayer = target;
            }
        }

        return nearestPlayer;
    }
}
