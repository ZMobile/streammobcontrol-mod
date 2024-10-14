package com.blockafeller.command;

import com.blockafeller.util.StreamerUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class StreamerCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("streamer")
                .requires(source -> source.hasPermissionLevel(2))  // Requires OP permissions (level 2 or higher)
                // "add" sub-command
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(context -> {
                                    ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");

                                    if (player != null) {
                                        addPlayerToStreamerTeam(player);
                                        context.getSource().sendFeedback(() -> Text.literal(player.getEntityName() + " added as a streamer").formatted(Formatting.GREEN), true);
                                    } else {
                                        context.getSource().sendError(Text.literal("Player not found").formatted(Formatting.RED));
                                    }
                                    return 1;
                                })))
                // "remove" sub-command
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(context -> {
                                    ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");

                                    if (player != null) {
                                        removePlayerFromStreamerTeam(player);
                                        context.getSource().sendFeedback(() -> Text.literal(player.getEntityName() + " removed as a streamer").formatted(Formatting.GREEN), false);
                                    } else {
                                        context.getSource().sendError(Text.literal("Player not found").formatted(Formatting.RED));
                                    }
                                    return 1;
                                })))
                // "list" sub-command
                .then(CommandManager.literal("list")
                        .executes(context -> {
                            StringBuilder streamerList = new StringBuilder("Current streamers: ");
                            boolean hasStreamers = false;

                            for (ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
                                if (StreamerUtil.isStreamer(player)) {
                                    streamerList.append(player.getEntityName()).append(" ");
                                    hasStreamers = true;
                                }
                            }

                            if (hasStreamers) {
                                context.getSource().sendFeedback(() -> Text.literal(streamerList.toString()).formatted(Formatting.GREEN), false);
                            } else {
                                context.getSource().sendFeedback(() -> Text.literal("No streamers currently online.").formatted(Formatting.YELLOW), false);
                            }
                            return 1;
                        })));
    }
    /**
     * Adds a player to the "streamers" team.
     */
    private static void addPlayerToStreamerTeam(ServerPlayerEntity player) {
        Scoreboard scoreboard = player.getScoreboard();

        // Get or create the "streamers" team
        Team streamerTeam = scoreboard.getTeam("streamers");
        if (streamerTeam == null) {
            streamerTeam = scoreboard.addTeam("streamers");
            streamerTeam.setDisplayName(Text.literal("Streamers"));
            streamerTeam.setColor(Formatting.GOLD);
        }

        // Add the player to the "streamers" team
        scoreboard.addPlayerToTeam(player.getEntityName(), streamerTeam);
    }

    /**
     * Removes a player from the "streamers" team.
     */
    private static void removePlayerFromStreamerTeam(ServerPlayerEntity player) {
        Scoreboard scoreboard = player.getScoreboard();
        Team streamerTeam = scoreboard.getTeam("streamers");

        if (streamerTeam != null) {
            scoreboard.removePlayerFromTeam(player.getEntityName(), streamerTeam);
        }
    }
}