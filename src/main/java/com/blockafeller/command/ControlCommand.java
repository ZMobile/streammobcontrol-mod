package com.blockafeller.command;

import com.blockafeller.inventory.InventoryFiller;
import com.blockafeller.extension.PlayerExtension;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.variant.IdentityType;

public class ControlCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {

        dispatcher.register(CommandManager.literal("control")
                .then(CommandManager.argument("player", EntityArgumentType.players())
                        .then(CommandManager.argument("mob_id", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ENTITY_TYPE))
                                .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                .executes(context -> {
                                    try {
                                        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                        Identifier mobId = RegistryEntryArgumentType.getSummonableEntityType(context, "mob_id").registryKey().getValue();

                                        morphPlayerToMob(player, mobId);

                                        ((PlayerExtension) player).setInhabitedMobType(mobId);
                                        ((PlayerExtension) player).setInhabiting(false);
                                        InventoryFiller.fillInventoryWithPapers(player);

                                        ((PlayerExtension) player).setInhabiting(true);
                                        System.out.println("Inhabiting: " + ((PlayerExtension) player).isInhabiting());

                                        // Morph the player into the specified mob
                                        player.sendMessage(Text.of("You are now controlling: " + mobId.toString()), false);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return 1;
                                }))));
    }

    private static void morphPlayerToMob(ServerPlayerEntity player, Identifier mobId) {
        // Retrieve the EntityType based on the Identifier
        EntityType<?> entityType = Registries.ENTITY_TYPE.get(mobId);

        // Create an instance of the mob
        Entity createdEntity = entityType.create(player.getWorld());

        // Check if the created entity is a LivingEntity (which is needed for identity)
        if (createdEntity instanceof LivingEntity livingEntity) {
            // Convert the LivingEntity to IdentityType
            IdentityType<LivingEntity> identityType = new IdentityType<>(livingEntity);
            // Update the player's identity using the Identity mod's method
            PlayerIdentity.updateIdentity(player, identityType, livingEntity);
            // Retrieve the EntityType based on the Identifier
        }
    }
}