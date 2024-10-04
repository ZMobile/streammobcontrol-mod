package com.blockafeller.time;

import com.blockafeller.extension.PlayerExtension;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.world.GameMode;

import java.util.HashMap;
import java.util.Map;

public class PlayerTimeTracker {
    private static int tickCounter = 0;

    public static void registerTickEvent() {
        // Listen for the end of each server tick to track time
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;

            // Run the decrement logic every 20 ticks (1 second)
            if (tickCounter >= 20) {
                tickCounter = 0; // Reset tick counter after each second
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    handleTimeDecrement(player);
                }
            }
        });
    }

    private static void handleTimeDecrement(ServerPlayerEntity player) {
        // Get the player's TimeManager instance
        PlayerTimeData timeManager = PlayerTimeDataManager.getPlayerTimeData(player.getUuid());

        if (timeManager == null) {
            return; // No time manager available for this player
        }

        // Check if the player is in spectator mode
        if (player.interactionManager.getGameMode() == GameMode.SPECTATOR) {
            // Decrement spectator time
            if (timeManager.getSpectatorTime() > 0) {
                timeManager.decrementSpectatorTime(1);
                //player.sendMessage(Text.literal("Spectator Time Remaining: " + timeManager.getSpectatorTime() + " seconds"), true);
                //isplayActionBar(player, "Spectator Time Remaining: " + timeManager.getSpectatorTime() + "s");
            } else if (timeManager.getMobTime() > 0) {
                // Switch to mob time when spectator time runs out
                long mobTimeToTransfer = Math.min(timeManager.getMobTime(), 30);
                timeManager.decrementMobTime(mobTimeToTransfer);
                timeManager.setSpectatorTime(mobTimeToTransfer);
                //player.sendMessage(Text.literal("Transferred " + mobTimeToTransfer + " seconds from Mob Time to Spectator Time."), true);
                //displayActionBar(player, "Transferred " + mobTimeToTransfer + " seconds to Spectator Time.");
            } else {
                // Both times have run out, switch to lobby or default state
                player.changeGameMode(GameMode.SURVIVAL); // Switch back to survival or lobby mode
                //player.sendMessage(Text.literal("Your spectator and mob time have expired!"), true);
                //displayActionBar(player, "Your spectator and mob time have expired!");
                // Optionally teleport to lobby or remove from morph state
            }
        } else if (isPlayerMorphed(player)) { // Custom condition to check if the player is morphed
            // Decrement mob time
            if (timeManager.getMobTime() > 0) {
                timeManager.decrementMobTime(1);
                //player.sendMessage(Text.literal("Mob Time Remaining: " + timeManager.getMobTime() + " seconds"), true);
                //displayActionBar(player, "Mob Time Remaining: " + timeManager.getMobTime() + "s");
            } else {
                // Mob time ran out, switch back to lobby or spectator
                ((PlayerExtension) player).setInhabiting(false); // Clear morph status
                player.changeGameMode(GameMode.SURVIVAL); // Switch back to survival or spectator mode
                //player.sendMessage(Text.literal("Your mob time has expired!"), true);
                //displayActionBar(player, "Your mob time has expired!");
                // Optionally teleport to lobby or perform other actions
            }
        }
    }

    private static void displayActionBar(ServerPlayerEntity player, String message) {
        // Create a TitleS2CPacket for action bar messages
        TitleS2CPacket actionBarPacket = new TitleS2CPacket(Text.literal(message).formatted(Formatting.GOLD));
        player.networkHandler.sendPacket(actionBarPacket);
    }


    // Helper method to check if a player is morphed
    private static boolean isPlayerMorphed(ServerPlayerEntity player) {
        // Use your existing logic to check if the player is in a morph state
        return ((PlayerExtension) player).isInhabiting();
    }
}