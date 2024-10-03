package com.blockafeller.mixin;

import com.blockafeller.extension.PlayerExtension;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        ExperienceOrbEntity orb = (ExperienceOrbEntity) (Object) this;

        // Define the area to search for nearby players (8 blocks in each direction)
        Box searchBox = orb.getBoundingBox().expand(8.0D, 8.0D, 8.0D);
        List<PlayerEntity> nearbyPlayers = orb.getWorld().getEntitiesByClass(PlayerEntity.class, searchBox, player -> true);

        // Find the closest player that is not excluded from attracting the orb
        PlayerEntity closestPlayer = null;
        double closestDistance = Double.MAX_VALUE;

        for (PlayerEntity player : nearbyPlayers) {
            // If the player should be prevented from attracting XP, skip them
            if (shouldPreventXpAttraction(player)) continue;

            // Calculate the distance to the player
            double distance = player.squaredDistanceTo(new Vec3d(orb.getX(), orb.getY(), orb.getZ()));
            if (distance < closestDistance) {
                closestDistance = distance;
                closestPlayer = player;
            }
        }

        // If no valid player is found, cancel the default tick behavior to stop XP orb movement
        if (closestPlayer == null) {
            ci.cancel();
        } else {
            // Continue normal behavior with the closest non-blocked player
            moveTowardPlayer(orb, closestPlayer);
        }
    }

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void onPlayerCollision(PlayerEntity player, CallbackInfo ci) {
        if (shouldPreventXpAttraction(player)) {
            // Cancel the XP orb pickup event for this player
            ci.cancel();
        }
    }


    /** b
     * Custom method to prevent specific players from attracting XP orbs.
     */
    private boolean shouldPreventXpAttraction(PlayerEntity player) {
        // Example condition: Prevent XP attraction for players using custom logic
        return ((PlayerExtension) player).isInhabiting();
    }

    /**
     * Mimic the internal movement behavior of the XP orb towards the target player.
     */
    private void moveTowardPlayer(ExperienceOrbEntity orb, PlayerEntity player) {
        Vec3d targetPos = new Vec3d(player.getX(), player.getY() + (double) player.getStandingEyeHeight() / 2.0D, player.getZ());
        Vec3d direction = targetPos.subtract(orb.getPos()).normalize();
        orb.setVelocity(direction.multiply(0.2D)); // Controls the XP orb's movement speed toward the player
    }
}