package com.blockafeller.trait.loot;

import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.morph.MorphService;
import com.blockafeller.morph.MorphUtil;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.ServerWorldAccess;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CustomDeathDrops {

    public static void register() {
        // Register the death event with ServerEntityEvents
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, damage) -> {
            if (entity instanceof ServerPlayerEntity serverPlayer && ((PlayerExtension) serverPlayer).isInhabiting()) {
                System.out.println("Handling custom drops for: " + serverPlayer);
                handleCustomDrops(serverPlayer, source);
                //MorphService.returnPlayerToLobby(serverPlayer.getServer(), serverPlayer);
            }
            return true;
        });
    }

    private static void handleCustomDrops(ServerPlayerEntity player, DamageSource damageSource) {
        System.out.println("Item in slot 8: " + player.getInventory().getStack(8));
        System.out.println("Was picked up: " + wasPickedUp(player.getInventory().getStack(8)));
        // Step 1: Drop picked-up items first (if any)
        dropPickedUpItems(player);

        // Step 2: Clear the player's inventory to reset drops
        player.getInventory().clear();
        player.currentScreenHandler.sendContentUpdates(); // Explicitly sync the inventory here

        // Step 3: Identify the appropriate loot table based on morph state
        String lootTableId = getMobLootTableId(player);

        if (lootTableId != null) {
            // Step 4: Drop mob-specific loot
            dropMobLoot(player, lootTableId, damageSource);
        }

        // Step 5: Drop chance-based main hand tool
        dropChanceBasedSlot(player, 0, 0.085); // 8.5% chance for the first hotbar slot
        dropChanceBasedSlot(player, 40, 0.085); // 8.5% chance for the off-hand slot
    }

    private static String getMobLootTableId(ServerPlayerEntity player) {
        // Get the morph type from PlayerExtension
        String morphType = ((PlayerExtension) player).getInhabitedMobType().toString();
        // Example morphType: "minecraft:zombie"

        // Split the identifier to modify it into the correct loot table path
        String[] parts = morphType.split(":");
        if (parts.length == 2) {
            return parts[0] + ":entities/" + parts[1]; // "minecraft:entities/zombie"
        }
        return null;
    }

    private static void dropMobLoot(ServerPlayerEntity player, String lootTableId, DamageSource damageSource) {
        System.out.println("Dropping custom loot for: " + lootTableId);
        System.out.println("Damage source: " + damageSource.getAttacker());
        MinecraftServer server = player.getServer();
        if (server != null) {
            // Get the loot table for the mob
            LootTable lootTable = server.getLootManager().getLootTable(new Identifier(lootTableId));

            // Create a LootContextParameterSet
            LootContextParameterSet parameterSet = new LootContextParameterSet.Builder(player.getServerWorld())
                    .add(LootContextParameters.ORIGIN, player.getPos())
                    .add(LootContextParameters.THIS_ENTITY, player)
                    .addOptional(LootContextParameters.DAMAGE_SOURCE, damageSource)
                    .build(LootContextTypes.ENTITY);


            // Generate and drop loot
            List<ItemStack> loot = lootTable.generateLoot(parameterSet);
            if (lootTableId.equals("minecraft:entities/creeper")
                    && (damageSource.getAttacker() instanceof SkeletonEntity || (
                    damageSource.getAttacker() instanceof PlayerEntity playerAttacker && ((PlayerExtension) playerAttacker).isInhabiting() && ((PlayerExtension) playerAttacker).getInhabitedMobType().toString().equals("minecraft:skeleton")))) {
                Item[] c418Discs = new Item[]{
                        Items.MUSIC_DISC_13,
                        Items.MUSIC_DISC_CAT,
                        Items.MUSIC_DISC_BLOCKS,
                        Items.MUSIC_DISC_CHIRP,
                        Items.MUSIC_DISC_FAR,
                        Items.MUSIC_DISC_MALL,
                        Items.MUSIC_DISC_MELLOHI,
                        Items.MUSIC_DISC_STAL,
                        Items.MUSIC_DISC_STRAD,
                        Items.MUSIC_DISC_WARD,
                        Items.MUSIC_DISC_11,
                        Items.MUSIC_DISC_WAIT
                };

                // Select a random disc
                Random random = new Random();
                Item disc = c418Discs[random.nextInt(c418Discs.length)];
                loot.add(new ItemStack(disc));
            }
            for (ItemStack stack : loot) {
                ItemEntity itemEntity = new ItemEntity(player.getWorld(), player.getX(), player.getY(), player.getZ(), stack);
                player.getWorld().spawnEntity(itemEntity);
                System.out.println("Loot item: " + stack);
            }
        }
    }

    private static void dropPickedUpItems(ServerPlayerEntity player) {
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (!stack.isEmpty() && wasPickedUp(stack)) {
                System.out.println("Dropping picked-up item: " + stack);
                player.dropStack(stack);
            }
        }
    }

    private static void dropChanceBasedSlot(ServerPlayerEntity player, int slotIndex, double chance) {
        // Get the item stack at the specified slot index
        ItemStack stack = player.getInventory().getStack(slotIndex);

        // Check if the slot is not empty and the random chance condition is met
        if (!stack.isEmpty() && Math.random() < chance) {
            // Drop the item at the player's position
            removeNbtTag(stack, "PickedUp");
            player.dropStack(stack);

            // Remove the item from the player's inventory after dropping
            player.getInventory().setStack(slotIndex, ItemStack.EMPTY);
        }
    }


    public static void removeNbtTag(ItemStack stack, String tagKey) {
        if (stack.hasNbt()) {
            NbtCompound nbt = stack.getNbt();
            if (nbt != null && nbt.contains(tagKey)) {
                nbt.remove(tagKey);  // Remove the tag by key
            }
        }
    }

    private static boolean wasPickedUp(ItemStack stack) {
        // Check if the stack has the "PickedUp" NBT tag
        System.out.println("Checking if item was picked up: " + stack);
        boolean pickedUp = !(stack.hasNbt() && (!Objects.requireNonNull(stack.getNbt()).getBoolean("PickedUp") || MorphUtil.isDoNotInteractItem(stack)));
        System.out.println("Item was picked up: " + pickedUp);
        return pickedUp;
    }

    // Utility method to mark an item as picked up
    public static void markAsPickedUp(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putBoolean("PickedUp", true);
    }
}