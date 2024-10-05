package com.blockafeller.morph;

import com.blockafeller.ability.MobAbilityStickHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
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
        ItemStack reverseMorphKey = new ItemStack(Items.COMPASS);
        reverseMorphKey.getOrCreateNbt().putBoolean("ReverseMorphKey", true);
        NbtCompound nbt = reverseMorphKey.getOrCreateNbt();
        nbt.putBoolean("DoNotInteract", true);
        reverseMorphKey.setCustomName(Text.literal("Reverse Morph Key"));
        return reverseMorphKey;
    }

    public static ItemStack createDoorBreakingNugget() {
        // Create an iron nugget with custom NBT tags
        ItemStack doorBreakingNugget = new ItemStack(Items.IRON_NUGGET);

        // Get or create the NBT tag for this item
        NbtCompound nbt = doorBreakingNugget.getOrCreateNbt();

        // Add a custom property to indicate this is a Door Breaker
        nbt.putBoolean("DoorBreaker", true);

        // Create and populate the CanDestroy list for Adventure mode
        nbt.put("CanDestroy", createCanDestroyTag());

        // Set a custom name for the item
        doorBreakingNugget.setCustomName(Text.literal("Door Breaker"));

        return doorBreakingNugget;
    }

    // Helper method to create the CanDestroy NBT list for doors
    private static NbtList createCanDestroyTag() {
        NbtList canDestroyList = new NbtList();

        // Add the list of door block identifiers to the CanDestroy NBT list
        canDestroyList.add(NbtString.of("minecraft:oak_door"));
        canDestroyList.add(NbtString.of("minecraft:birch_door"));
        canDestroyList.add(NbtString.of("minecraft:spruce_door"));
        canDestroyList.add(NbtString.of("minecraft:jungle_door"));
        canDestroyList.add(NbtString.of("minecraft:acacia_door"));
        canDestroyList.add(NbtString.of("minecraft:dark_oak_door"));
        canDestroyList.add(NbtString.of("minecraft:crimson_door"));
        canDestroyList.add(NbtString.of("minecraft:warped_door"));

        return canDestroyList;
    }

    public static boolean isMorphKey(ItemStack stack) {
        return stack.isOf(Items.FIRE_CHARGE) && stack.hasNbt() && stack.getNbt().getBoolean("MorphKey");
    }

    public static boolean isReverseMorphKey(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().getBoolean("ReverseMorphKey");
    }

    public static boolean isAbilityStick(ItemStack stack) {
        return stack.isOf(Items.STICK) && stack.hasNbt() && MobAbilityStickHandler.getMobType(stack) != null;
    }

    public static boolean isSpectateKey(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().getBoolean("SpectateKey");
    }

    public static boolean isDoNotInteractItem(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().getBoolean("DoNotInteract");
    }
}
