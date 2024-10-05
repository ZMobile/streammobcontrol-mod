package com.blockafeller.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

public class ProjectileRefill {
    public static void refillArrows(ServerPlayerEntity player) {
        ItemStack itemStack = new ItemStack(Items.ARROW, 64);
        itemStack.setCustomName((Text.literal("Do not interact - Slot 27")));
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putBoolean("DoNotInteract", true);
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

        // Define the potions we want to keep
        Map<String, Item> desiredPotions = Map.of(
                "Potion of Slowness", Items.SPLASH_POTION,
                "Potion of Weakness", Items.SPLASH_POTION,
                "Potion of Poison", Items.SPLASH_POTION,
                "Potion of Harming", Items.SPLASH_POTION,
                "Healing Potion", Items.POTION,
                "Fire Resistance Potion", Items.POTION,
                "Swiftness Potion", Items.POTION
        );

        // 1. Drop items in slots 1-7 and offhand if not matching desired potions
        for (int slot = 0; slot <= 6; slot++) {
            ItemStack stack = inventory.getStack(slot);

            if (!isDesiredPotion(stack, desiredPotions)) {
                //dropItem(player, stack);
                inventory.setStack(slot, ItemStack.EMPTY);
            }
        }

        // Drop the offhand item if it does not match the desired potions
        if (!isDesiredPotion(player.getOffHandStack(), desiredPotions)) {
            //player.dropStack(player.getOffHandStack());
            player.setStackInHand(player.getActiveHand(), ItemStack.EMPTY);
        }

        // 2. Refill slots 1-4 with specific splash potions
        inventory.setStack(0, createNamedPotion(Items.POTION, "Healing Potion", Potions.HEALING));
        inventory.setStack(1, createNamedPotion(Items.SPLASH_POTION, "Potion of Weakness", Potions.WEAKNESS));
        inventory.setStack(2, createNamedPotion(Items.SPLASH_POTION, "Potion of Poison", Potions.POISON));
        inventory.setStack(3, createNamedPotion(Items.SPLASH_POTION, "Potion of Harming", Potions.HARMING));

        // 3. Refill slots 5-7 with other potion types
        inventory.setStack(4, createNamedPotion(Items.SPLASH_POTION, "Potion of Slowness", Potions.SLOWNESS));
        inventory.setStack(5, createNamedPotion(Items.POTION, "Fire Resistance Potion", Potions.FIRE_RESISTANCE));
        inventory.setStack(6, createNamedPotion(Items.POTION, "Swiftness Potion", Potions.SWIFTNESS));
    }

    private static boolean isDesiredPotion(ItemStack stack, Map<String, Item> desiredPotions) {
        if (stack.isEmpty()) {
            return false;
        }

        // Check if the item is a potion and if it has the correct custom name
        Item item = stack.getItem();
        String customName = stack.hasCustomName() ? stack.getName().getString() : "";

        return desiredPotions.containsKey(customName) && desiredPotions.get(customName).equals(item);
    }

    private static ItemStack createNamedPotion(Item potionType, String customName, Potion potionEffect) {
        // Create a new potion stack with the correct type
        ItemStack potionStack = new ItemStack(potionType);
        NbtCompound nbt = potionStack.getOrCreateNbt();
        nbt.putBoolean("DoNotInteract", true);
        // Set the potion type (e.g., Slowness, Weakness, Poison, etc.)
        PotionUtil.setPotion(potionStack, potionEffect);

        // Set the custom name for display purposes
        potionStack.setCustomName(Text.literal(customName));

        return potionStack;
    }


    public static void refillSnowballs(ServerPlayerEntity player) {
        ItemStack itemStack = new ItemStack(Items.SNOWBALL, 64);
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putBoolean("DoNotInteract", true);
        //itemStack.setCustomName((Text.literal("Do not interact - Slot 27")));
        player.getInventory().setStack(27, itemStack);
    }
}
