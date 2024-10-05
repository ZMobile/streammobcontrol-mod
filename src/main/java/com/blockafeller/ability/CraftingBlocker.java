package com.blockafeller.ability;

import com.blockafeller.extension.PlayerExtension;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.HashSet;
import java.util.UUID;

public class CraftingBlocker {
    // Check if crafting is disabled for the given player
    public static boolean isCraftingDisabledForPlayer(PlayerEntity player) {
        return ((PlayerExtension) player).isInhabiting();
    }

    // Register a tick event to monitor player crafting behavior
    public static void registerCraftingBlocker() {
        ServerTickEvents.END_WORLD_TICK.register(CraftingBlocker::onWorldTick);
    }

    // Method that runs every server tick
    private static void onWorldTick(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            // If crafting is disabled for this player, clear the output slot in their crafting inventory
            if (isCraftingDisabledForPlayer(player)) {
                disablePlayerCraftingGrid(player);
            }
        }
    }

    // Method to disable the player's 2x2 crafting grid
    private static void disablePlayerCraftingGrid(ServerPlayerEntity player) {
        ScreenHandler screenHandler = player.currentScreenHandler;

        // Check if the current screen handler is the player's inventory crafting screen
        if (screenHandler != null && screenHandler.getCursorStack().isEmpty()) {
            for (int i = 1; i < screenHandler.slots.size(); i++) {
                if (screenHandler.slots.get(i).inventory instanceof CraftingInventory) {
                    if (!screenHandler.slots.get(i).getStack().isEmpty()) {
                        player.dropItem(screenHandler.slots.get(i).getStack(), false); // Drop the item
                        screenHandler.slots.get(i).setStack(ItemStack.EMPTY); // Clear the slot
                    }
                }
            }
        }
    }
}
