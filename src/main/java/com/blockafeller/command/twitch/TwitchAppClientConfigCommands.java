package com.blockafeller.command.twitch;

import com.blockafeller.config.ConfigManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class TwitchAppClientConfigCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Register the "/setTwitchApp clientId <client-id>" command
        dispatcher.register(CommandManager.literal("settwitchapp")
                        .requires(source -> source.hasPermissionLevel(2))  // Requires OP permissions (level 2 or higher)
                        // Subcommand "clientId"
                        .then(CommandManager.literal("clientId")
                                .then(CommandManager.argument("client-id", StringArgumentType.string())  // Accepts a string value
                                        .executes(context -> {
                                            // Retrieve the client-id from the command argument
                                            String clientId = StringArgumentType.getString(context, "client-id");

                                            ConfigManager.getConfig().setTwitchAppClientId(clientId);  // Save the client ID in config
                                            ConfigManager.saveConfig();  // Save the config

                                            // Inform the player about the new client ID
                                            context.getSource().sendFeedback(() -> Text.literal("Client ID set to: " + clientId), false);

                                            // Return success
                                            return 1;
                                        })))
                // Subcommand "clientSecret"
                .then(CommandManager.literal("clientSecret")
                        .then(CommandManager.argument("client-secret", StringArgumentType.string())  // Accepts a string value
                                .executes(context -> {
                                    // Retrieve the client-secret from the command argument
                                    String clientSecret = StringArgumentType.getString(context, "client-secret");

                                    ConfigManager.getConfig().setTwitchAppClientSecret(clientSecret);  // Save the client secret in config
                                    ConfigManager.saveConfig();  // Save the config

                                    // Inform the player about the new client secret
                                    context.getSource().sendFeedback(() -> Text.literal("Client Secret set successfully."), false);

                                    // Return success
                                    return 1;
                                }))));
    }
}