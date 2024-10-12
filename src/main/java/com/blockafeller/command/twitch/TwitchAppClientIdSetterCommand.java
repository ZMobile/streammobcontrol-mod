package com.blockafeller.command.twitch;

import com.blockafeller.config.ConfigManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class TwitchAppClientIdSetterCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Register the "/setappclientid" command with a string argument
        dispatcher.register(CommandManager.literal("setappclientid")
                .requires(source -> source.hasPermissionLevel(2))  // Requires OP permissions (level 2 or higher)
                .then(CommandManager.argument("client-id", StringArgumentType.string())  // Accepts a string value
                        .executes(context -> {
                            // Retrieve the client-id from the command argument
                            String clientId = StringArgumentType.getString(context, "client-id");

                            ConfigManager.getConfig().setTwitchAppClientId(clientId); // Save the client ID in config
                            ConfigManager.saveConfig();  // Save the config

                            // Inform the player about the new client ID
                            context.getSource().sendFeedback(() -> Text.literal("Client ID set to: " + clientId), false);

                            // Return success
                            return 1;
                        })));
    }
}