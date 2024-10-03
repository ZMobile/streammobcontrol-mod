package com.blockafeller.mixin.projectile;

import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.inventory.ProjectileRefill;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowballEntity.class)
public class SnowballEntityMixin {
    @Inject(method = "onCollision", at = @At("HEAD"))
    private void onCollision(HitResult hitResult, CallbackInfo ci) {
        SnowballEntity snowballEntity = (SnowballEntity) (Object) this;
        if (snowballEntity.getOwner() instanceof ServerPlayerEntity player && ((PlayerExtension) snowballEntity.getOwner()).isInhabiting() && ((PlayerExtension) snowballEntity.getOwner()).getInhabitedMobType().toString().equals("minecraft:snow_golem")) {
            ProjectileRefill.refillSnowballs(player);
        }
    }

    @Inject(method = "onEntityHit", at = @At("HEAD"))
    private void onBlockHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        SnowballEntity snowballEntity = (SnowballEntity) (Object) this;
        if (snowballEntity.getOwner() instanceof ServerPlayerEntity player && ((PlayerExtension) snowballEntity.getOwner()).isInhabiting() && ((PlayerExtension) snowballEntity.getOwner()).getInhabitedMobType().toString().equals("minecraft:snow_golem")) {
            ProjectileRefill.refillPotion(player);
        }
    }

}
