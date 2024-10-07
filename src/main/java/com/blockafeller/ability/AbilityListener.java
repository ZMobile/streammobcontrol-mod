package com.blockafeller.ability;

import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.morph.MorphUtil;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypedActionResult;

public class AbilityListener {
    public static void register() {
        // Register the event listener for item use
        UseItemCallback.EVENT.register((player, world, hand) -> {
            System.out.println("UseItemCallback 1");
            ItemStack stack = player.getStackInHand(hand);
            System.out.println("UseItemCallback 2");
            // Check if the player is holding a special ability stick (vanilla stick with NBT data)
            if (!((PlayerExtension) player).isInhabiting()) {
                System.out.println("Not inhabiting");
                return TypedActionResult.pass(stack);
            } else if (((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:witch")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:creeper")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:snow_golem")) {
                System.out.println("Inhabiting witch, creeper, or snow golem");
                return TypedActionResult.pass(stack);
            }
            if (MorphUtil.isAbilityStick(stack)) {
                System.out.println("Activated Ability Stick");
                // Delegate to the handler when the special stick is right-clicked
                MobAbilityStickHandler.onRightClickWithAbilityStick(world, player, hand);
            }// Pass the action if it's not the special stick
            return TypedActionResult.pass(stack);
        });
    }
}