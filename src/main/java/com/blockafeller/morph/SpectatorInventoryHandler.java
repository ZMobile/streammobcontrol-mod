package com.blockafeller.morph;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import java.util.HashSet;
import java.util.Set;

import static com.blockafeller.morph.MorphUtil.createMorphKey;
import static com.blockafeller.morph.MorphUtil.createReverseMorphKey;

public class SpectatorInventoryHandler {

    private static final Set<ServerPlayerEntity> playersInSpectator = new HashSet<>();

    public static void registerSpectatorInventoryEvents() {
        // Register an event to modify the player's spectator inventory when switching to spectator mode
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            if (player.interactionManager.getGameMode() == GameMode.SPECTATOR) {
                handleSpectatorModeInventory(player);
            }
        });

        // Use the server tick event to monitor players' game modes
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.interactionManager.getGameMode() == GameMode.SPECTATOR && !playersInSpectator.contains(player)) {
                    // Player switched to spectator mode, update their inventory
                    handleSpectatorModeInventory(player);
                    playersInSpectator.add(player);
                } else if (player.interactionManager.getGameMode() != GameMode.SPECTATOR) {
                    // Player is no longer in spectator mode, remove them from tracking
                    playersInSpectator.remove(player);
                }
            }
        });
    }

    private static void handleSpectatorModeInventory(ServerPlayerEntity player) {
        if (player.interactionManager.getGameMode() == GameMode.SPECTATOR) {
            // Add the Morph Key and Reverse Morph Key to specific slots in the spectator inventory
            ItemStack morphKey = createMorphKey();
            ItemStack reverseMorphKey = createReverseMorphKey();

            // Place the Morph Key in slot 0 and Reverse Morph Key in slot 1, next to the teleport option
            player.getInventory().setStack(0, morphKey); // Slot 0: Morph Key
            player.getInventory().setStack(1, reverseMorphKey); // Slot 1: Reverse Morph Key
        }
    }
}