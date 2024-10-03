package com.blockafeller.mixin;

import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.inventory.ProjectileRefill;
import draylar.identity.api.platform.IdentityConfig;
import net.minecraft.entity.LivingEntity;
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
    @Inject(method = "consumeItem", at = @At("HEAD"))
    private void onConsumeItem(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemStack itemStack = entity.getStackInHand(Hand.MAIN_HAND); // Get the item in the entity's main hand

        if (itemStack.getItem() == Items.POTION) {
            System.out.println("Potion consumed!");
            // Custom behavior for when a potion is consumed
            if (entity instanceof ServerPlayerEntity player && ((PlayerExtension) player).isInhabiting() && ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:witch")) {
                applyPotionCooldown(player);
                ProjectileRefill.refillPotion(player);
                player.sendMessage(Text.literal("You feel a strange sensation..."), true);
            }
        }
    }

    private void applyPotionCooldown(ServerPlayerEntity player) {
        int cooldown = IdentityConfig.getInstance().getAbilityCooldownMap().getOrDefault(((PlayerExtension)player).getInhabitedMobType().toString(), 20);
        player.getItemCooldownManager().set(Items.SPLASH_POTION, cooldown);
        player.getItemCooldownManager().set(Items.POTION, cooldown);
        player.getItemCooldownManager().set(Items.LINGERING_POTION, cooldown);// Apply cooldown to splash potions
    }
}
