package com.blockafeller.command;

import com.blockafeller.twitch.authentication.TwitchStreamerAuthenticationService;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class StreamShutdownCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("stream")
                .then(CommandManager.literal("shutdown")
                        .requires(source -> source.hasPermissionLevel(2))  // Requires OP permissions (level 2 or higher)
                        .executes(context -> {
                            // Inform the player about the new state
                            TwitchStreamerAuthenticationService.shutdown(context.getSource().getPlayer());
                            context.getSource().sendFeedback(() -> Text.literal("Shutting down the pub sub stream event listening. Streamer authentication is now cancelled. To reauthenticate, type \"/authenticate streamer\""), true);
                            // Return success
                            return 1;
                        })
                )
        );
    }
}
