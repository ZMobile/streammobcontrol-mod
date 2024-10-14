package com.blockafeller.time;
import com.blockafeller.config.ConfigManager;
import com.blockafeller.extension.PlayerExtension;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.HashMap;
import java.util.Map;

public class PlayerTimeBossBarTracker {
    private static final Map<ServerPlayerEntity, ServerBossBar> playerBossBars = new HashMap<>();
    private static int tickCounter = 0;

    public static void register() {
        // Listen for the end of each server tick to track time
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (ConfigManager.getConfig().isMobTimeLimitEnabled()) {
                tickCounter++;

                // Run the decrement logic every 20 ticks (1 second)
                if (tickCounter >= 20) {
                    tickCounter = 0; // Reset tick counter after each second
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        handleTimeDecrement(player);
                    }
                }
            }
        });
    }

    public static ServerBossBar getOrCreateBossBar(ServerPlayerEntity player) {
        return playerBossBars.computeIfAbsent(player, p -> createBossBar(p));
    }

    private static void handleTimeDecrement(ServerPlayerEntity player) {
        // Get the player's TimeManager instance
        PlayerTimeData timeManager = PlayerTimeDataManager.getOrCreatePlayerTimeData(player.getUuid(), player.getServer());

        // Create or update the boss bar
        ServerBossBar bossBar = getOrCreateBossBar(player);

        // Check if the player is in spectator mode or morphed
        if (player.interactionManager.getGameMode() == GameMode.SPECTATOR) {
            if (ConfigManager.getConfig().isKickCycle()) {
                if (timeManager.getSpectatorTime() > 0) {
                    updateBossBar(bossBar, "Spectator Time Remaining: " + timeManager.getSpectatorTime() + "s", (float) timeManager.getSpectatorTime() / timeManager.getTotalSpectatorTime());
                    showBossBar(bossBar, player); // Ensure boss bar is visible
                } else if (timeManager.getMobTime() > 0) {
                    // Switch to mob time when spectator time runs out
                    long mobTimeToTransfer = Math.min(timeManager.getMobTime(), 30);
                    updateBossBar(bossBar, "Transferred " + mobTimeToTransfer + " seconds from Mob Time to Spectator Time.", (float) timeManager.getSpectatorTime() / timeManager.getTotalSpectatorTime());
                    showBossBar(bossBar, player); // Ensure boss bar is visible
                } else {
                    // Both times have run out, switch to lobby or default state
                    //player.changeGameMode(GameMode.SURVIVAL); // Switch back to survival or lobby mode
                    hideBossBar(bossBar, player); // Hide the boss bar
                }
            }
        } else if (isPlayerMorphed(player)) { // Custom condition to check if the player is morphed
            if (timeManager.getMobTime() > 0) {
                updateBossBar(bossBar, "Mob Time Remaining: " + timeManager.getMobTime() + "s", (float) timeManager.getMobTime() / timeManager.getTotalMobTime());
                showBossBar(bossBar, player); // Ensure boss bar is visible
            } else {
                hideBossBar(bossBar, player); // Hide the boss bar
            }
        } else {
            // Player is neither spectating nor morphed; hide the boss bar
            hideBossBar(bossBar, player);
        }
    }

    private static ServerBossBar createBossBar(ServerPlayerEntity player) {
        // Create a new ServerBossBar instance
        ServerBossBar bossBar = new ServerBossBar(
                Text.literal("Time Remaining").formatted(Formatting.GOLD),
                BossBar.Color.RED,
                BossBar.Style.PROGRESS
        );
        return bossBar;
    }

    public static void updateBossBar(ServerBossBar bossBar, String title, float progress) {
        // Update the boss bar title and progress
        bossBar.setName(Text.literal(title));
        bossBar.setPercent(progress);
    }

    public static void showBossBar(ServerBossBar bossBar, ServerPlayerEntity player) {
        // Add the player to the boss bar to make it visible
        if (!bossBar.getPlayers().contains(player)) {
            bossBar.addPlayer(player);
        }
    }

    public static void hideBossBar(ServerBossBar bossBar, ServerPlayerEntity player) {
        // Remove the player from the boss bar to hide it
        if (bossBar.getPlayers().contains(player)) {
            bossBar.removePlayer(player);
        }
    }

    // Helper method to check if a player is morphed
    public static boolean isPlayerMorphed(ServerPlayerEntity player) {
        // Use your existing logic to check if the player is in a morph state
        return ((PlayerExtension) player).isInhabiting();
    }
}