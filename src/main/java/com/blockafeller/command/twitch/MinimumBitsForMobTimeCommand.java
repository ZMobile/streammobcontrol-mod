package com.blockafeller.command.twitch;

import com.blockafeller.config.ConfigManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class MinimumBitsForMobTimeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("minimumbitsformobtime")
                .requires(source -> source.hasPermissionLevel(2))  // Requires OP permissions (level 2 or higher)
                .then(CommandManager.argument("bits", IntegerArgumentType.integer())  // Accepts a boolean value
                        .executes(context -> {
                            int bits = IntegerArgumentType.getInteger(context, "bits");

                            ConfigManager.getConfig().setMinimumBitsForMobTime(bits);
                            ConfigManager.saveConfig();

                            // Inform the player about the new state
                            context.getSource().sendFeedback(() -> Text.literal("Set minimum bits for mob time to: " + bits), false);

                            // Return success
                            return 1;
                        })));
    }
}
