package com.blockafeller.command.twitch;

import com.blockafeller.twitch.TwitchStreamerAuthenticationService;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class AuthenticateCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("authenticate")
                .requires(source -> source.hasPermissionLevel(2))  // Requires OP permissions (level 2 or higher)
                .then(CommandManager.literal("streamer")  // Create the "start" sub-command
                        .executes(context -> {
                            TwitchStreamerAuthenticationService.authenticateStreamer(context.getSource().getPlayer());
                            return 1;
                        })));
    }
}
