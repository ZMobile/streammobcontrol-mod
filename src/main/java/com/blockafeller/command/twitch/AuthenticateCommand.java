package com.blockafeller.command.twitch;

import com.blockafeller.config.ConfigManager;
import com.blockafeller.config.ModConfig;
import com.blockafeller.twitch.authentication.TwitchStreamerAuthenticationService;
import com.blockafeller.twitch.authentication.TwitchUserAuthenticationService;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class AuthenticateCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("authenticate")// Requires OP permissions (level 2 or higher)
                // Streamer authentication sub-command
                .then(CommandManager.literal("streamer")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player != null) {
                                TwitchStreamerAuthenticationService.authenticateStreamer(player);
                                context.getSource().sendFeedback(() -> Text.literal("Streamer authentication process has been initiated by: " + player.getEntityName()), true);
                            }
                            return 1;
                        }))

                .then(CommandManager.literal("true")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            ConfigManager.getConfig().setAuthenticateViewers(true);
                            ConfigManager.saveConfig();
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player != null) {
                                context.getSource().sendFeedback(() -> Text.literal("Viewer authentication requests enabled"), true);
                            }
                            return 1;
                        }))

                .then(CommandManager.literal("false")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> {
                            ConfigManager.getConfig().setAuthenticateViewers(false);
                            ConfigManager.saveConfig();
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player != null) {
                                context.getSource().sendFeedback(() -> Text.literal("Viewer authentication requests disabled"), true);
                            }
                            return 1;
                        }))



                .then(CommandManager.literal("viewer")
                        .executes(context -> {
                            TwitchUserAuthenticationService.authenticateUser(context.getSource().getPlayer());
                            return 1;
                        })));

    }
}
