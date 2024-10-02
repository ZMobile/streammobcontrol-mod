package com.blockafeller.mixin;

import com.blockafeller.extension.PlayerExtension;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Inject(method = "setStack", at = @At("HEAD"), cancellable = true)
    private void preventInventoryModification(int slot, ItemStack stack, CallbackInfo ci) {
        PlayerEntity player = ((PlayerInventory) (Object) this).player;
        if (player instanceof PlayerExtension playerExtension && playerExtension.isInhabiting()) {
            // If the current stack is a tripwire hook, cancel any modification attempts
            //or if the slot already has a tripwire hook
            if ((stack.getItem() == Items.PAPER && stack.getItem().getName().getString().startsWith("Do not interact"))
                    || (player.getInventory().getStack(slot).getItem() == Items.PAPER && player.getInventory().getStack(slot).getName().getString().startsWith("Do not interact"))) {
                System.out.println("Blocked tripwire hook modification in inventory");
                ci.cancel(); // Cancel direct modification of the slot
            }
        }
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
}