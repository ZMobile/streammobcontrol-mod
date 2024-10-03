package com.blockafeller.mixin.projectile;

import com.blockafeller.inventory.ProjectileRefill;
import com.blockafeller.extension.PlayerExtension;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends ProjectileEntity {

    public PersistentProjectileEntityMixin() {
        super(null, null);
    }

    /**
     * This method is called when the projectile hits an entity.
     * We can use this to adjust properties, such as making the projectile non-pickupable.
     */
    @Inject(method = "onEntityHit", at = @At("HEAD"))
    private void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        PersistentProjectileEntity projectile = (PersistentProjectileEntity) (Object) this;
        // Prevent the projectile from being picked up by setting the pickup type
        if (projectile.getOwner() instanceof ServerPlayerEntity && ((PlayerExtension) projectile.getOwner()).isInhabiting()) {
            projectile.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
            if (projectile instanceof ArrowEntity) {
                ProjectileRefill.refillArrows((ServerPlayerEntity) projectile.getOwner());
            } else if (projectile instanceof TridentEntity) {
                ProjectileRefill.refillTridents((ServerPlayerEntity) projectile.getOwner(), projectile);
            }
        }
    }

    /**
     * This method is called when the projectile hits a block.
     * We use this to adjust pickup properties when the projectile sticks to a block.
     */
    @Inject(method = "onBlockHit", at = @At("HEAD"))
    private void onBlockHit(BlockHitResult blockHitResult, CallbackInfo ci) {
        PersistentProjectileEntity projectile = (PersistentProjectileEntity) (Object) this;
        // Prevent the projectile from being picked up by setting the pickup type
        if (projectile.getOwner() instanceof ServerPlayerEntity && ((PlayerExtension) projectile.getOwner()).isInhabiting()) {
            projectile.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
            if (projectile instanceof ArrowEntity) {
                ProjectileRefill.refillArrows((ServerPlayerEntity) projectile.getOwner());
            } else if (projectile instanceof TridentEntity) {
                ProjectileRefill.refillTridents((ServerPlayerEntity) projectile.getOwner(), projectile);
            }
        }
    }
}