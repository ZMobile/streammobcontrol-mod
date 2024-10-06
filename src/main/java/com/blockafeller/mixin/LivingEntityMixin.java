package com.blockafeller.mixin;

import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.inventory.ProjectileRefill;
import draylar.identity.api.platform.IdentityConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {


    /*@Inject(method = "sendPickup", at = @At("HEAD"), cancellable = true)
    private void onSendPickup(Entity item, int count, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Check if the entity is a morphed ServerPlayerEntity using Identity
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;

            // Check if the player is morphed using Identity mod
            if (((PlayerExtension) player).isInhabiting()) {
                ItemStack stack = ((ItemEntity) item).getStack();

                // Allow pickup if the item is armor
                if (stack.getItem() instanceof ArmorItem) {
                    ArmorItem armorItem = (ArmorItem) stack.getItem();

                    // Equip the armor manually and remove the item entity from the world
                    player.equipStack(armorItem.getSlotType(), stack);
                    ((ItemEntity) item).remove(Entity.RemovalReason.DISCARDED); // Remove the item entity after pickup
                    System.out.println("Morphed player picked up and equipped: " + stack.getItem().getTranslationKey());
                    ci.cancel(); // Cancel further handling of this pickup to avoid conflicts
                }
            }
        }
    }*/
}
