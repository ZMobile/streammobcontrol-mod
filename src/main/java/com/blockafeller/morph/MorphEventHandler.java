package com.blockafeller.morph;

import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.inventory.InventoryFiller;
import com.blockafeller.trait.hunger.HungerUtils;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.variant.IdentityType;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.GameMode;


public class MorphEventHandler {

    public static void registerMorphEvents() {
        // Right-click a mob with Morph Key to transform
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            System.out.println("Right-clicked entity: " + entity);
            if (player instanceof ServerPlayerEntity serverPlayer && entity instanceof MobEntity targetMob) {
                if (serverPlayer.interactionManager.getGameMode() == GameMode.SPECTATOR) {
                    // Get the type of the mob being right-clicked
                    EntityType<?> mobType = targetMob.getType();
                    Identifier mobId = Registries.ENTITY_TYPE.getId(mobType);

                    // Morph into the mob type dynamically
                    MorphService.morphPlayerToMob(serverPlayer, targetMob, mobId);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });

        // Right-click in air with Reverse Morph Key to transform back
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack heldItem = player.getStackInHand(hand);
            if (player instanceof ServerPlayerEntity) {
                if (MorphUtil.isReverseMorphKey(heldItem)) {
                    MorphService.reverseMorph((ServerPlayerEntity) player);
                } else if (MorphUtil.isSpectateKey(heldItem)) {
                    MorphService.beginSpectating((ServerPlayerEntity) player);
                }
            }
            return TypedActionResult.pass(heldItem);
        });
    }
}
