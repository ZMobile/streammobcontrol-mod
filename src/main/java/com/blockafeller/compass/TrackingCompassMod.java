package com.blockafeller.compass;

import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.morph.MorphUtil;
import com.blockafeller.util.StreamerUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

public class TrackingCompassMod {
    public static void register() {
        // Register a tick event to run on the server
        ServerTickEvents.START_WORLD_TICK.register(TrackingCompassMod::onWorldTick);
    }

    // Method that runs every world tick
    private static void onWorldTick(World world) {
        if (!world.isClient) {
            for (PlayerEntity player : world.getPlayers()) {
                if (((PlayerExtension) player).isInhabiting()) {
                    updateCompassToNearestPlayer((ServerPlayerEntity) player);
                }
            }
        }
    }

    // Find and update the compass in the player's inventory to point to the nearest player
    private static void updateCompassToNearestPlayer(ServerPlayerEntity player) {
        PlayerEntity nearestPlayer = getNearestStreamer(player);
        if (nearestPlayer == null) return; // No nearby players

        // Find a compass in the player's inventory
        for (ItemStack itemStack : player.getInventory().main) {
            if (itemStack.isOf(Items.COMPASS) && MorphUtil.isDoNotInteractItem(itemStack)) {
                // Set the compass to point to the nearest player's location
                BlockPos nearestPos = nearestPlayer.getBlockPos();

                // Ensure that the compass behaves as a lodestone compass
                itemStack.getOrCreateNbt().putBoolean("LodestoneTracked", true);
                itemStack.getOrCreateNbt().put("LodestonePos", NbtHelper.fromBlockPos(nearestPos));
                itemStack.getOrCreateNbt().putString("LodestoneDimension", player.getWorld().getRegistryKey().getValue().toString());
                itemStack.getOrCreateNbt().putBoolean("DoNotInteract", true);
            }
        }
    }

    // Helper method to find the nearest streamer to the given player
    private static PlayerEntity getNearestStreamer(ServerPlayerEntity player) {
        PlayerEntity nearestPlayer = null;
        double minDistance = Double.MAX_VALUE;

        for (PlayerEntity target : player.getWorld().getPlayers()) {
            // Check if the player is a streamer
            if (!StreamerUtil.isStreamer((ServerPlayerEntity) target)) {
                continue; // Skip if the player is not a streamer
            }

            // Skip if the player is the same, in Creative, Adventure, or Spectator mode
            if (target == player || ((ServerPlayerEntity) target).interactionManager.getGameMode() != GameMode.SURVIVAL) {
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