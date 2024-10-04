package com.blockafeller.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Optional;

import static com.blockafeller.morph.MorphUtil.createMorphKey;
import static com.blockafeller.morph.MorphUtil.createReverseMorphKey;

public class MorphKeyCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(CommandManager.literal("givemorphkey")
                .then(CommandManager.argument("player", EntityArgumentType.players()) // Target player(s)
                        .then(CommandManager.argument("key_type", StringArgumentType.string()) // morph/reverse
                                .executes(MorphKeyCommands::executeGiveKeyCommand))));
    }

    private static int executeGiveKeyCommand(CommandContext<ServerCommandSource> context) {
        try {
            // Get the player(s) from the command arguments
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
            String keyType = StringArgumentType.getString(context, "key_type");

            for (ServerPlayerEntity player : players) {
                // Determine which key to create based on `key_type`
                if (keyType.equalsIgnoreCase("morph")) {
                    // Create and give the generic Morph Key to the player
                    ItemStack morphKey = createMorphKey();
                    player.getInventory().insertStack(morphKey);
                    context.getSource().sendFeedback(() -> Text.literal("Gave a Morph Key to " + player.getName().getString()), false);
                } else if (keyType.equalsIgnoreCase("reverse")) {
                    // Create and give the Reverse Morph Key to the player
                    ItemStack reverseMorphKey = createReverseMorphKey();
                    player.getInventory().insertStack(reverseMorphKey);
                    context.getSource().sendFeedback(() -> Text.literal("Gave a Reverse Morph Key to " + player.getName().getString()), false);
                } else {
                    context.getSource().sendError(Text.literal("Invalid key type: " + keyType));
                    return 0;
                }
            }
            return 1;
        } catch (Exception e) {
            context.getSource().sendError(Text.literal("An error occurred: " + e.getMessage()));
            return 0;
        }
    }
}