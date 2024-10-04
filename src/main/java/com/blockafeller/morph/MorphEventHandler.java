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
                    morphPlayerToMob(serverPlayer, targetMob, mobId);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });

        // Right-click in air with Reverse Morph Key to transform back
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack heldItem = player.getStackInHand(hand);
            if (player instanceof ServerPlayerEntity && isReverseMorphKey(heldItem)) {
                reverseMorph((ServerPlayerEntity) player);
            }
            return TypedActionResult.pass(heldItem);
        });
    }

    private static boolean isMorphKey(ItemStack stack) {
        return stack.isOf(Items.FIRE_CHARGE) && stack.hasNbt() && stack.getNbt().getBoolean("MorphKey");
    }

    private static boolean isReverseMorphKey(ItemStack stack) {
        return stack.isOf(Items.CLOCK) && stack.hasNbt() && stack.getNbt().getBoolean("ReverseMorphKey");
    }

    private static void morphPlayerToMob(ServerPlayerEntity player, MobEntity targetMob, Identifier mobId) {
        HungerUtils.setPlayerHunger(player, 16, 0f);
        // Step 1: Save player’s inventory and teleport to mob’s position
        player.teleport(targetMob.getX(), targetMob.getY(), targetMob.getZ());

        // Step 2: Match player’s facing direction to the mob
        player.setYaw(targetMob.getYaw());
        player.setPitch(targetMob.getPitch());

        // Step 3: Equip the player with the mob’s items
        player.getInventory().clear(); // Clear player’s inventory
        player.getInventory().setStack(0, targetMob.getMainHandStack());
        player.getInventory().setStack(40, targetMob.getOffHandStack()); // Off-hand
        player.getInventory().armor.set(0, targetMob.getEquippedStack(EquipmentSlot.HEAD));
        player.getInventory().armor.set(1, targetMob.getEquippedStack(EquipmentSlot.CHEST));
        player.getInventory().armor.set(2, targetMob.getEquippedStack(EquipmentSlot.LEGS));
        player.getInventory().armor.set(3, targetMob.getEquippedStack(EquipmentSlot.FEET));


        // Step 4: Set morph status and game mode
        ((PlayerExtension) player).setInhabitedMobType(mobId);
        InventoryFiller.fillInventoryWithPapers(player);
        ((PlayerExtension) player).setInhabiting(true);
        System.out.println("Mob id: " + mobId);
        morphPlayerToMob(player, mobId);
        player.changeGameMode(GameMode.ADVENTURE);
        player.setHealth(targetMob.getHealth());


        // Step 5: Despawn the target mob
        targetMob.remove(Entity.RemovalReason.DISCARDED);
    }

    private static void reverseMorph(ServerPlayerEntity player) {
        // Step 1: Create a new mob at the player’s location with the same attributes
        Identifier mobId = ((PlayerExtension) player).getInhabitedMobType();
        MobEntity newMob = (MobEntity) Registries.ENTITY_TYPE.get(mobId).create(player.getWorld());

        if (newMob != null) {
            newMob.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());

            // Step 2: Transfer player’s equipment to the mob
            newMob.equipStack(EquipmentSlot.MAINHAND, player.getInventory().getStack(0));
            newMob.equipStack(EquipmentSlot.OFFHAND, player.getInventory().getStack(40));
            newMob.equipStack(EquipmentSlot.HEAD, player.getInventory().armor.get(0));
            newMob.equipStack(EquipmentSlot.CHEST, player.getInventory().armor.get(1));
            newMob.equipStack(EquipmentSlot.LEGS, player.getInventory().armor.get(2));
            newMob.equipStack(EquipmentSlot.FEET, player.getInventory().armor.get(3));

            // Step 3: Spawn the mob in the world
            player.getWorld().spawnEntity(newMob);

            // Step 4: Clear player inventory and reset morph status
            player.getInventory().clear();
            ((PlayerExtension) player).setInhabitedMobType(null);
            ((PlayerExtension) player).setInhabiting(false);

            // Step 5: Set player to Spectator mode
            player.changeGameMode(GameMode.SPECTATOR);
        }
    }
    // Other methods remain unchanged...

    private static void morphPlayerToMob(ServerPlayerEntity player, Identifier mobId) {
        System.out.println("Morphing player to mob: " + mobId);
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
        }
    }
}
