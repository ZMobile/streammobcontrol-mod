package com.blockafeller.command.twitch;

import com.blockafeller.config.ConfigManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class MinimumSubTierForMorphingCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("minimumsubtierformorphing")
                .requires(source -> source.hasPermissionLevel(2))  // Requires OP permissions (level 2 or higher)
                .then(CommandManager.argument("subTier", IntegerArgumentType.integer())  // Accepts a boolean value
                        .executes(context -> {
                            int subTier = IntegerArgumentType.getInteger(context, "subTier");

                            ConfigManager.getConfig().setMinimumSubTierForMorphing(subTier);
                            ConfigManager.saveConfig();

                            // Inform the player about the new state
                            context.getSource().sendFeedback(() -> Text.literal("Minimum Sub Tier for morphing set to: " + subTier), false);

                            // Return success
                            return 1;
                        })));
    }
}
