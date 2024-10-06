package com.blockafeller.command;

import com.blockafeller.config.ConfigManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class GracePeriodCommand {// A variable to hold the current state of kickcycle
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Register the "kickcycle" command with a boolean argument ("true" or "false")
        dispatcher.register(CommandManager.literal("graceperiod")
                .requires(source -> source.hasPermissionLevel(2))  // Requires OP permissions (level 2 or higher)
                .then(CommandManager.argument("seconds", IntegerArgumentType.integer())  // Accepts a boolean value
                        .executes(context -> {
                            // Retrieve the boolean value from the command argument
                            int seconds = IntegerArgumentType.getInteger(context, "seconds");

                            ConfigManager.getConfig().setGracePeriodSeconds(seconds);
                            ConfigManager.saveConfig();

                            // Inform the player about the new state
                            context.getSource().sendFeedback(() -> Text.literal("Grace period set to: " + seconds + " seconds"), false);

                            // Return success
                            return 1;
                        })));
    }
}

