package com.blockafeller.command.twitch;

import com.blockafeller.config.ConfigManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class BitsPerMobTimeMinuteCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("bitspermobtimeminute")
                .requires(source -> source.hasPermissionLevel(2))  // Requires OP permissions (level 2 or higher)
                .then(CommandManager.argument("bits", IntegerArgumentType.integer())  // Accepts a boolean value
                        .executes(context -> {
                            int bits = IntegerArgumentType.getInteger(context, "bits");

                            ConfigManager.getConfig().setBitsPerMobTimeMinute(bits);
                            ConfigManager.saveConfig();

                            // Inform the player about the new state
                            context.getSource().sendFeedback(() -> Text.literal("Set bits per mob time minute to: " + bits + " for a minute of mob time."), false);

                            // Return success
                            return 1;
                        })));
    }
}
