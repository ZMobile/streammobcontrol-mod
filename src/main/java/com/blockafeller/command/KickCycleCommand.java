package com.blockafeller.command;

import com.blockafeller.config.ConfigManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class KickCycleCommand {// A variable to hold the current state of kickcycle
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Register the "kickcycle" command with a boolean argument ("true" or "false")
        dispatcher.register(CommandManager.literal("kickcycle")
                .requires(source -> source.hasPermissionLevel(2))  // Requires OP permissions (level 2 or higher)
                .then(CommandManager.argument("enabled", BoolArgumentType.bool())  // Accepts a boolean value
                        .executes(context -> {
                            // Retrieve the boolean value from the command argument
                            boolean enabled = BoolArgumentType.getBool(context, "enabled");

                            ConfigManager.getConfig().setKickCycle(enabled);
                            ConfigManager.saveConfig();

                            // Inform the player about the new state
                            context.getSource().sendFeedback(() -> Text.literal("Kick Cycle has been set to: " + enabled), false);

                            // Return success
                            return 1;
                        })));
    }
}
