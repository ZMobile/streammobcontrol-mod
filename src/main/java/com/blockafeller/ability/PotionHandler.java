package com.blockafeller.ability;

import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.inventory.ProjectileRefill;
import draylar.identity.api.platform.IdentityConfig;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.TypedActionResult;

public class PotionHandler {
    public static void registerPotionHandler() {
        // Register the event listener for item use
        UseItemCallback.EVENT.register((player, world, hand) -> {
            System.out.println("UseItemCallback 1");
            ItemStack stack = player.getStackInHand(hand);
            System.out.println("UseItemCallback 2");
            // Check if the player is holding a special ability stick (vanilla stick with NBT data)
            if (((PlayerExtension) player).isInhabiting() && ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:witch") && (stack.getItem() == Items.POTION)) {
                applyPotionCooldown((ServerPlayerEntity) player);
                ProjectileRefill.refillPotion((ServerPlayerEntity) player);
            }// Pass the action if it's not the special stick
            return TypedActionResult.pass(stack);
        });
    }

    private static void applyPotionCooldown(ServerPlayerEntity player) {
        int cooldown = IdentityConfig.getInstance().getAbilityCooldownMap().getOrDefault(((PlayerExtension)player).getInhabitedMobType().toString(), 20);
        player.getItemCooldownManager().set(Items.SPLASH_POTION, cooldown);
        player.getItemCooldownManager().set(Items.POTION, cooldown);
        player.getItemCooldownManager().set(Items.LINGERING_POTION, cooldown);// Apply cooldown to splash potions
    }
}
