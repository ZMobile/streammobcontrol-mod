package com.blockafeller.mixin.projectile;

import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.inventory.ProjectileRefill;
import draylar.identity.api.platform.IdentityConfig;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionEntity.class)
public abstract class PotionEntityMixin {

    @Inject(method = "onCollision", at = @At("HEAD"))
    private void onCollision(HitResult hitResult, CallbackInfo ci) {
        PotionEntity potion = (PotionEntity) (Object) this;
        if (potion.getOwner() instanceof ServerPlayerEntity player && ((PlayerExtension) potion.getOwner()).isInhabiting() && ((PlayerExtension) potion.getOwner()).getInhabitedMobType().toString().equals("minecraft:witch")) {
            applyPotionCooldown(player);
                ProjectileRefill.refillPotion(player);
        }
    }

    @Inject(method = "onBlockHit", at = @At("HEAD"))
    private void onBlockHit(BlockHitResult blockHitResult, CallbackInfo ci) {
        PotionEntity potion = (PotionEntity) (Object) this;
        if (potion.getOwner() instanceof ServerPlayerEntity player && ((PlayerExtension) potion.getOwner()).isInhabiting() && ((PlayerExtension) potion.getOwner()).getInhabitedMobType().toString().equals("minecraft:witch")) {
            applyPotionCooldown(player);
            ProjectileRefill.refillPotion(player);
        }
    }

    /**
     * Applies a cooldown to the potion.
     */
    private void applyPotionCooldown(ServerPlayerEntity player) {
        int cooldown = IdentityConfig.getInstance().getAbilityCooldownMap().getOrDefault(((PlayerExtension)player).getInhabitedMobType().toString(), 20);
        player.getItemCooldownManager().set(Items.SPLASH_POTION, cooldown);
        player.getItemCooldownManager().set(Items.POTION, cooldown);
        player.getItemCooldownManager().set(Items.LINGERING_POTION, cooldown);// Apply cooldown to splash potions
    }
}
