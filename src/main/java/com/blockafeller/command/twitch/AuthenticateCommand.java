package com.blockafeller.command.twitch;

import com.blockafeller.twitch.TwitchStreamerAuthenticationService;
import com.blockafeller.twitch.TwitchUserAuthenticationService;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import net.minecraft.server.network.ServerPlayerEntity;

public class AuthenticateCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("authenticate")
                .requires(source -> source.hasPermissionLevel(2))  // Requires OP permissions (level 2 or higher)

                // Streamer authentication sub-command
                .then(CommandManager.literal("streamer")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player != null) {
                                TwitchStreamerAuthenticationService.authenticateStreamer(player);
                            }
                            return 1;
                        }))

                .then(CommandManager.literal("viewer")
                        .requires(source -> source.hasPermissionLevel(0))  // Viewers need no OP permissions
                        .executes(context -> {
                            TwitchUserAuthenticationService.authenticateUser(context.getSource().getPlayer());
                            return 1;
                        })));
    }
}
