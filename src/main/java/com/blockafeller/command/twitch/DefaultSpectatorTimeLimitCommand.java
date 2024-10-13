package com.blockafeller.command.twitch;

import com.blockafeller.config.ConfigManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class DefaultSpectatorTimeLimitCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("defaultspectatortimelimit")
                .requires(source -> source.hasPermissionLevel(2))  // Requires OP permissions (level 2 or higher)
                .then(CommandManager.argument("seconds", IntegerArgumentType.integer())  // Accepts a boolean value
                        .executes(context -> {
                            int seconds = IntegerArgumentType.getInteger(context, "seconds");

                            ConfigManager.getConfig().setDefaultSpectatorTimeLimit(seconds);
                            ConfigManager.saveConfig();

                            // Inform the player about the new state
                            context.getSource().sendFeedback(() -> Text.literal("Set default spectator time limit to: " + seconds + " seconds."), false);

                            // Return success
                            return 1;
                        })));
    }
}
