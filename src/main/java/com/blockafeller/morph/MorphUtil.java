package com.blockafeller.morph;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class MorphUtil {
    public static ItemStack createMorphKey() {
        // Create a simple Morph Key (using Fire Charge as the base item) with no specific mob linked
        ItemStack morphKey = new ItemStack(Items.FIRE_CHARGE);
        morphKey.getOrCreateNbt().putBoolean("MorphKey", true); // Simple NBT tag to identify it as a Morph Key
        morphKey.setCustomName(Text.literal("Morph Key (Right-click a Mob)"));
        return morphKey;
    }

    public static ItemStack createReverseMorphKey() {
        // Create a Reverse Morph Key (using Clock as the base item)
        ItemStack reverseMorphKey = new ItemStack(Items.CLOCK);
        reverseMorphKey.getOrCreateNbt().putBoolean("ReverseMorphKey", true);
        reverseMorphKey.setCustomName(Text.literal("Reverse Morph Key"));
        return reverseMorphKey;
    }
}
