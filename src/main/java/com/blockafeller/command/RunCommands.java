package com.blockafeller.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class RunCommands {
    /*public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerRunCommand(dispatcher);
        });
    }

    private static void registerRunCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("run")
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("playerName", StringArgumentType.string())
                                .executes(context -> {
                                    String playerName = StringArgumentType.getString(context, "playerName");
                                    ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(playerName);

                                    if (player != null) {
                                        RunSet.add(player.getUuid());
                                        context.getSource().sendFeedback(() -> Text.literal(playerName + " added as a Run").formatted(Formatting.GREEN), false);
                                    } else {
                                        context.getSource().sendError(Text.literal("Player not found").formatted(Formatting.RED));
                                    }
                                    return 1;
                                })))
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("playerName", StringArgumentType.string())
                                .executes(context -> {
                                    String playerName = StringArgumentType.getString(context, "playerName");
                                    ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(playerName);

                                    if (player != null) {
                                        RunSet.remove(player.getUuid());
                                        context.getSource().sendFeedback(() -> Text.literal(playerName + " removed as a Run").formatted(Formatting.GREEN), false);
                                    } else {
                                        context.getSource().sendError(Text.literal("Player not found").formatted(Formatting.RED));
                                    }
                                    return 1;
                                }))));
    }*/
}
