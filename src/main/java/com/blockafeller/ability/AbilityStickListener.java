package com.blockafeller.ability;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.TypedActionResult;

public class AbilityStickListener {
    public static void registerAbilityStickListener() {
        // Register the event listener for item use
        UseItemCallback.EVENT.register((player, world, hand) -> {
            System.out.println("UseItemCallback 1");
            ItemStack stack = player.getStackInHand(hand);
            System.out.println("UseItemCallback 2");
            // Check if the player is holding a special ability stick (vanilla stick with NBT data)
            if (isAbilityStick(stack)) {
                // Delegate to the handler when the special stick is right-clicked
                MobAbilityStickHandler.onRightClickWithAbilityStick(world, player, hand);
            }// Pass the action if it's not the special stick
            return TypedActionResult.pass(stack);
        });
    }

    private static boolean isAbilityStick(ItemStack stack) {
        // Check if the item is a vanilla stick and has the "MobType" NBT tag
        System.out.println("UseItemCallback 3");
        boolean isAbilityStick = stack.getItem() == Items.STICK && MobAbilityStickHandler.getMobType(stack) != null;
        System.out.println("UseItemCallback 4: " + isAbilityStick);
        return isAbilityStick;
    }
}