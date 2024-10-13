package com.blockafeller.command.twitch;

import com.blockafeller.config.ConfigManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class PunishSpectatorsForIntentionallyFailingToCompleteAuthCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("punishspectatorsforintentionallyfailingtocompleteauth")
                .requires(source -> source.hasPermissionLevel(2))  // Requires OP permissions (level 2 or higher)
                .then(CommandManager.argument("enabled", BoolArgumentType.bool())  // Accepts a boolean value
                        .executes(context -> {
                            // Retrieve the boolean value from the command argument
                            boolean enabled = BoolArgumentType.getBool(context, "enabled");

                            ConfigManager.getConfig().setPunishSpectatorsForIntentionallyFailingToCompleteAuth(enabled);
                            ConfigManager.saveConfig();

                            // Inform the player about the new state
                            context.getSource().sendFeedback(() -> Text.literal("Punish spectators for intentionally failing to complete auth set to: " + enabled), false);

                            // Return success
                            return 1;
                        })));
    }
}
