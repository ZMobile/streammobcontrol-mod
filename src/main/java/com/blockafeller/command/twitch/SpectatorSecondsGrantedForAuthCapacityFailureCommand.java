package com.blockafeller.command.twitch;

import com.blockafeller.config.ConfigManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SpectatorSecondsGrantedForAuthCapacityFailureCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("spectatorsecondsgrantedforauthcapacityfailure")
                .requires(source -> source.hasPermissionLevel(2))  // Requires OP permissions (level 2 or higher)
                .then(CommandManager.argument("seconds", IntegerArgumentType.integer())  // Accepts a boolean value
                        .executes(context -> {
                            int seconds = IntegerArgumentType.getInteger(context, "seconds");

                            ConfigManager.getConfig().setSpectatorSecondsGrantedForAuthCapacityFailure(seconds);
                            ConfigManager.saveConfig();

                            // Inform the player about the new state
                            context.getSource().sendFeedback(() -> Text.literal("Set spectator seconds granted for auth capacity failure to: " + seconds), false);

                            // Return success
                            return 1;
                        })));
    }
}
