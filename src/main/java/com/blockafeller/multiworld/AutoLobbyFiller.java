package com.blockafeller.multiworld;

import com.blockafeller.ability.CreeperFoodExplosion;
import com.blockafeller.extension.PlayerExtension;
import me.isaiah.multiworld.command.TpCommand;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.List;

public class AutoLobbyFiller {


        /**
         * Registers a repeating task to check and teleport non-streamer players to the lobby.
         */
        public static void registerRepeatingTask() {
            // Run the check on every server tick
            ServerTickEvents.START_SERVER_TICK.register(AutoLobbyFiller::checkAndTeleportNonStreamers);
        }

        /**
         * Check if players are non-streamers and meet the conditions to teleport them to the lobby.
         */
        public static void checkAndTeleportNonStreamers(MinecraftServer server) {
            // Get all online players on the server
            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();

            for (ServerPlayerEntity player : players) {
                // Check if the player meets the teleportation criteria
                if (!isStreamer(player) && !isSpectator(player) && !isInhabitingMob(player) && !isInStreamLobby(player)) {
                    player.changeGameMode(GameMode.ADVENTURE);
                    teleportToLobby(server, player);
                }
            }
        }

    private static boolean isInStreamLobby(ServerPlayerEntity player) {
        // Get the player's current world ID
        String currentWorldId = player.getWorld().getRegistryKey().getValue().toString();
        // Compare to the target world ID
        return "stream:lobby".equals(currentWorldId);
    }

        /**
         * Check if a player is a streamer.
         */
        private static boolean isStreamer(ServerPlayerEntity player) {
            // Check if the player is part of the "streamers" team
            Scoreboard scoreboard = player.getScoreboard();
            Team team = scoreboard.getPlayerTeam(player.getEntityName());

            return team != null && team.getName().equals("streamers");
        }

    /**
         * Check if the player is in Spectator mode.
         */
        private static boolean isSpectator(ServerPlayerEntity player) {
            // Access the interaction manager from ServerPlayerEntity to check game mode
            return player.interactionManager.getGameMode() == GameMode.SPECTATOR;
        }

        /**
         * Check if the player is inhabiting a mob.
         */
        private static boolean isInhabitingMob(PlayerEntity player) {
            return player instanceof PlayerExtension && ((PlayerExtension) player).isInhabiting();
        }

        /**
         * Teleport the player to the stream lobby using /mw tp command.
         */
        private static void teleportToLobby(MinecraftServer minecraftServer, ServerPlayerEntity player) {
            // Execute the command /mw tp <player> stream:lobby
            String playerName = player.getEntityName();
            //String command = String.format("mw tp %s stream:lobby", playerName);

            TpCommand.run(minecraftServer, player, new String[]{playerName, "stream:lobby"});
        }
    }
