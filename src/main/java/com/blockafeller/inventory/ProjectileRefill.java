package com.blockafeller.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class ProjectileRefill {
    public static void refillArrows(ServerPlayerEntity player) {
        ItemStack itemStack = new ItemStack(Items.ARROW, 64);
        itemStack.setCustomName((Text.literal("Do not interact - Slot 27")));
        player.getInventory().setStack(27, itemStack);
    }

    public static void refillTridents(ServerPlayerEntity player, PersistentProjectileEntity trident) {
        // Get the item currently in slot 0
        ItemStack currentStack = player.getInventory().getStack(0);

        // If the slot is occupied, drop the item in the world
        if (!currentStack.isEmpty()) {
            dropItem(player, currentStack);
            player.getInventory().setStack(0, ItemStack.EMPTY); // Clear the slot
        }

        // Create the new trident ItemStack
        ItemStack itemStack = new ItemStack(Items.TRIDENT, 1);

        // Copy all NBT data from the original trident entity to the new item stack
        NbtCompound tridentNbt = new NbtCompound();
        trident.writeNbt(tridentNbt);  // Get the NBT data from the trident entity
        itemStack.setNbt(tridentNbt);
        itemStack.setCustomName(trident.getCustomName());

        // Place the new trident in slot 0
        player.getInventory().setStack(0, itemStack);
    }

    /**
     * Helper method to drop an item in the world at the player's position.
     */
    private static void dropItem(ServerPlayerEntity player, ItemStack itemStack) {
        // Get the player's current position
        Vec3d position = player.getPos();

        // Drop the item at the player's position
        player.dropItem(itemStack, false);  // `false` means the item will not be automatically picked up by the player
    }

    public static void refillPotion(ServerPlayerEntity player) {
        PlayerInventory inventory = player.getInventory();

        // 1. Drop items in slots 1-7 and offhand if not empty
        for (int slot = 1; slot <= 7; slot++) {
            if (!inventory.getStack(slot).isEmpty()) {
                dropItem(player, inventory.getStack(slot));
                inventory.setStack(slot, ItemStack.EMPTY);
            }
        }

        // Drop the offhand item if not empty
        if (!player.getOffHandStack().isEmpty()) {
            player.dropStack(player.getOffHandStack());
            player.setStackInHand(player.getActiveHand(), ItemStack.EMPTY);
        }

        // 2. Refill slots 1-4 with throwable potions
        inventory.setStack(0, createNamedPotion(Items.SPLASH_POTION, "Potion of Slowness"));
        inventory.setStack(1, createNamedPotion(Items.SPLASH_POTION, "Potion of Weakness"));
        inventory.setStack(2, createNamedPotion(Items.SPLASH_POTION, "Potion of Poison"));
        inventory.setStack(3, createNamedPotion(Items.SPLASH_POTION, "Potion of Slowness"));

        // 3. Refill slots 5-7 with other potion types
        inventory.setStack(4, createNamedPotion(Items.POTION, "Healing Potion"));
        inventory.setStack(5, createNamedPotion(Items.POTION, "Fire Resistance Potion"));
        inventory.setStack(6, createNamedPotion(Items.POTION, "Swiftness Potion"));
    }

    private static ItemStack createNamedPotion(Item potionType, String customName) {
        ItemStack potionStack = new ItemStack(potionType, 1);
        potionStack.setCustomName(Text.literal(customName));
        return potionStack;
    }

    public static void refillSnowballs(ServerPlayerEntity player) {
        ItemStack itemStack = new ItemStack(Items.SNOWBALL, 64);
        //itemStack.setCustomName((Text.literal("Do not interact - Slot 27")));
        player.getInventory().setStack(27, itemStack);
    }
}
