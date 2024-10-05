package com.blockafeller.morph;

import com.blockafeller.ability.MobAbilityStickHandler;
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

    public static ItemStack createSpectateKey() {
        // Create a simple Morph Key (using Fire Charge as the base item) with no specific mob linked
        ItemStack morphKey = new ItemStack(Items.COMPASS);
        morphKey.getOrCreateNbt().putBoolean("SpectateKey", true); // Simple NBT tag to identify it as a Morph Key
        morphKey.setCustomName(Text.literal("Right Click to start searching for a mob"));
        return morphKey;
    }

    public static boolean isMorphKey(ItemStack stack) {
        return stack.isOf(Items.FIRE_CHARGE) && stack.hasNbt() && stack.getNbt().getBoolean("MorphKey");
    }

    public static boolean isReverseMorphKey(ItemStack stack) {
        return stack.isOf(Items.CLOCK) && stack.hasNbt() && stack.getNbt().getBoolean("ReverseMorphKey");
    }

    public static boolean isAbilityStick(ItemStack stack) {
        return stack.isOf(Items.STICK) && stack.hasNbt() && MobAbilityStickHandler.getMobType(stack) != null;
    }

    public static boolean isSpectateKey(ItemStack stack) {
        return stack.isOf(Items.COMPASS) && stack.hasNbt() && stack.getNbt().getBoolean("SpectateKey");
    }
}
