package com.blockafeller.mixin.projectile;

import com.blockafeller.inventory.ProjectileRefill;
import com.blockafeller.extension.PlayerExtension;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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
         if (projectile.getOwner() instanceof ServerPlayerEntity player && ((PlayerExtension) projectile.getOwner()).isInhabiting()) {
             //double originalDamage = projectile.getDamage();  // Get the original damage
              if ((((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:skeleton")
                     || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:stray")
             ||((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:pillager")
             || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:piglin")) && projectile instanceof ArrowEntity) {
                 System.out.println("Skeleton arrow damage adjusting");
                 System.out.println("Original damage: " + projectile.getDamage());
                 double skeletonDamage = getSkeletonArrowDamage(player.getServerWorld());
                 double damageModifier = projectile.getDamage() / 9;
                 double damage = skeletonDamage * damageModifier;
                 System.out.println("Damange modifier: " + damageModifier);
                 System.out.println("Skeleton arrow damage: " + damage);
                 projectile.setDamage(damage);
             }

             projectile.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
             if (projectile instanceof ArrowEntity) {
                 ProjectileRefill.refillArrows((ServerPlayerEntity) projectile.getOwner());
             } else if (projectile instanceof TridentEntity) {
                 ProjectileRefill.refillTridents((ServerPlayerEntity) projectile.getOwner(), projectile);
             }
        }
    }

    private double getSkeletonArrowDamage(ServerWorld world) {
        switch (world.getDifficulty()) {
            case EASY:
                return 2.0 + (world.random.nextFloat() * 2.0);  // 2.0 to 4.0 damage
            case NORMAL:
                return 3.0 + (world.random.nextFloat());  // 3.0 to 4.0 damage
            case HARD:
                return 4.0 + (world.random.nextFloat());  // 4.0 to 5.0 damage
            default:
                return 2.0;  // Default to 2.0 if somehow difficulty is not recognized
        }
    }

    private double applyStatusEffectModifiers(LivingEntity entity, double baseDamage) {
        // Check for Strength effect
        if (entity.hasStatusEffect(StatusEffects.STRENGTH)) {
            int amplifier = entity.getStatusEffect(StatusEffects.STRENGTH).getAmplifier();
            baseDamage += 3 * (amplifier + 1);  // Each level adds 3 damage
        }

        // Check for Weakness effect
        if (entity.hasStatusEffect(StatusEffects.WEAKNESS)) {
            int amplifier = entity.getStatusEffect(StatusEffects.WEAKNESS).getAmplifier();
            baseDamage -= 4 * (amplifier + 1);  // Each level reduces damage by 4
        }

        // Prevent negative damage values
        return Math.max(0.0, baseDamage);
    }

    private double getCustomProjectileDamage(ServerPlayerEntity player, PersistentProjectileEntity projectile, double baseDamage) {
        double customDamage = baseDamage;

        // Check if the player is using a bow or crossbow
        if (projectile instanceof ArrowEntity) {
            ItemStack bowStack = getPlayerRangedWeapon(player);
            if (bowStack.getItem() instanceof BowItem) {
                customDamage = calculateBowDamage(bowStack, baseDamage);
            } else if (bowStack.getItem() instanceof CrossbowItem) {
                customDamage = calculateCrossbowDamage(bowStack, baseDamage);
            }
        }

        return customDamage;
    }

    private ItemStack getPlayerRangedWeapon(ServerPlayerEntity player) {
        ItemStack mainHandStack = player.getMainHandStack();
        ItemStack offHandStack = player.getOffHandStack();

        if (mainHandStack.getItem() instanceof BowItem || mainHandStack.getItem() instanceof CrossbowItem) {
            return mainHandStack;
        }

        if (offHandStack.getItem() instanceof BowItem || offHandStack.getItem() instanceof CrossbowItem) {
            return offHandStack;
        }

        return ItemStack.EMPTY;
    }

    /**
     * Calculates the damage of an arrow shot by a bow, factoring in enchantments like Power.
     */
    private double calculateBowDamage(ItemStack bowStack, double baseDamage) {
        int powerLevel = EnchantmentHelper.getLevel(Enchantments.POWER, bowStack);
        if (powerLevel > 0) {
            baseDamage += powerLevel * 0.5f + 0.5f;
        }
        return baseDamage;
    }

    /**
     * Calculates the damage of an arrow shot by a crossbow, factoring in enchantments like Power.
     */
    private double calculateCrossbowDamage(ItemStack crossbowStack, double baseDamage) {
        int powerLevel = EnchantmentHelper.getLevel(Enchantments.POWER, crossbowStack);
        if (powerLevel > 0) {
            baseDamage += powerLevel * 0.5f + 0.5f;
        }
        return baseDamage;
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