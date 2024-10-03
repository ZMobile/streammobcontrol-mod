package com.blockafeller.mixin;

import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.inventory.ProjectileRefill;
import draylar.identity.api.platform.IdentityConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Inject(method = "setStack", at = @At("HEAD"), cancellable = true)
    private void preventInventoryModification(int slot, ItemStack stack, CallbackInfo ci) {
        System.out.println("Item set in inventory!");
        System.out.println("Item: " + stack.getItem().getName().getString());
        PlayerEntity player = ((PlayerInventory) (Object) this).player;
        if (player instanceof PlayerExtension playerExtension && playerExtension.isInhabiting()) {
            // If the current stack is a tripwire hook, cancel any modification attempts
            //or if the slot already has a tripwire hook
            if (playerExtension.getInhabitedMobType().toString().equals("minecraft:witch") && (stack.getItem() == Items.POTION || stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.LINGERING_POTION)) {
                System.out.println("Blocked glass bottle modification in inventory");
                //ci.cancel(); // Cancel direct modification of the slot
            } else if (playerExtension.getInhabitedMobType().toString().equals("minecraft:snow_golem") && (stack.getItem() == Items.SNOWBALL)) {
                System.out.println("Blocked glass bottle modification in inventory");
                //ci.cancel(); // Cancel direct modification of the slot
            } else if ((stack.getItem() == Items.PAPER && stack.getItem().getName().getString().startsWith("Do not interact"))
                    || (player.getInventory().getStack(slot).getItem() == Items.PAPER && player.getInventory().getStack(slot).getName().getString().startsWith("Do not interact"))) {
                System.out.println("Blocked tripwire hook modification in inventory");
                ci.cancel(); // Cancel direct modification of the slot
            }
        }
        /*if (stack.getItem() == Items.GLASS_BOTTLE) {
            // Ensure we're on the server side
            if (((PlayerExtension) player).isInhabiting() && ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:witch") && !player.getWorld().isClient && player instanceof ServerPlayerEntity serverPlayer) {
                // Custom behavior for when an empty glass bottle is added
                applyPotionCooldown(serverPlayer);
                ProjectileRefill.refillPotion(serverPlayer);
                //serverPlayer.sendMessage(Text.literal("You feel a strange sensation..."), true);
            }
        }*/
    }


    @Inject(method = "swapSlotWithHotbar", at = @At("HEAD"), cancellable = true)
    private void preventHotkeySwap(int slot, CallbackInfo ci) {
        PlayerEntity player = ((PlayerInventory) (Object) this).player;

        if (player instanceof PlayerExtension playerExtension && playerExtension.isInhabiting()) {
            /*ItemStack stack = player.getInventory().getStack(slot);
            if ((stack.getItem() == Items.PAPER || stack.getItem() == Items.ARROW) && stack.getName().getString().startsWith("Do not interact")) {*/
                System.out.println("Blocked tripwire hook hotkey swap");
                ci.cancel(); // Cancel the hotkey swap
            //}
        }
    }

    /*@Inject(method = "addStack(ILnet/minecraft/item/ItemStack;)I", at = @At("HEAD"))
    private void onAddStack(int slot, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        System.out.println("Item added to inventory!");
        if (stack.getItem() == Items.GLASS_BOTTLE) {
            PlayerInventory inventory = (PlayerInventory) (Object) this;
            PlayerEntity player = inventory.player;

            // Ensure we're on the server side
            if (((PlayerExtension) player).isInhabiting() && ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:witch") && !player.getWorld().isClient && player instanceof ServerPlayerEntity serverPlayer) {
                // Custom behavior for when an empty glass bottle is added
                applyPotionCooldown(serverPlayer);
                ProjectileRefill.refillPotion(serverPlayer);
                //serverPlayer.sendMessage(Text.literal("You feel a strange sensation..."), true);
            }
        }
    }*/

    private void applyPotionCooldown(ServerPlayerEntity player) {
        int cooldown = IdentityConfig.getInstance().getAbilityCooldownMap()
                .getOrDefault(((PlayerExtension) player).getInhabitedMobType().toString(), 20);
        player.getItemCooldownManager().set(Items.SPLASH_POTION, cooldown);
        player.getItemCooldownManager().set(Items.POTION, cooldown);
        player.getItemCooldownManager().set(Items.LINGERING_POTION, cooldown);
    }
}