package com.blockafeller.ability;

import com.blockafeller.extension.PlayerExtension;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.TypedActionResult;

public class AbilityStickListener {
    public static void register() {
        // Register the event listener for item use
        UseItemCallback.EVENT.register((player, world, hand) -> {
            System.out.println("UseItemCallback 1");
            ItemStack stack = player.getStackInHand(hand);
            if (stack.getItem() != Items.STICK) {
                return TypedActionResult.pass(stack);
            }
            System.out.println("UseItemCallback 2");
            // Check if the player is holding a special ability stick (vanilla stick with NBT data)
            if (!((PlayerExtension)player).isInhabiting()) {
                System.out.println("Not inhabiting");
                return TypedActionResult.pass(stack);
            } else if (((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:witch")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:creeper")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:snow_golem")) {
                System.out.println("Inhabiting witch, creeper, or snow golem");
                return TypedActionResult.pass(stack);
            }
            if (isAbilityStick(stack)) {
                System.out.println("Activated Ability Stick");
                // Delegate to the handler when the special stick is right-clicked
                MobAbilityStickHandler.onRightClickWithAbilityStick(world, player, hand);
            }// Pass the action if it's not the special stick
            return TypedActionResult.pass(stack);
        });
    }

    public static boolean isAbilityStick(ItemStack stack) {
        if (stack.getItem() != Items.STICK) {
            return false;
        }
        // Check if the item is a vanilla stick and has the "MobType" NBT tag
        System.out.println("UseItemCallback 3");
        boolean isAbilityStick = MobAbilityStickHandler.getMobType(stack) != null;
        System.out.println("UseItemCallback 4: " + isAbilityStick);
        return isAbilityStick;
    }
}