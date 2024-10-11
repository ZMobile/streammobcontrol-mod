package com.blockafeller.mixin;

import com.blockafeller.extension.PlayerExtension;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Random;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends LivingEntity {

    protected CreeperEntityMixin(EntityType<? extends CreeperEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * Inject into the dropLoot method to modify the drops for the zombie.
     */
    @Inject(method = "dropEquipment", at = @At("HEAD"), cancellable = true)
    private void modifyDrops(CallbackInfo ci) {
        World world = this.getWorld();

        // Only modify drops if it's on the server side
        if (!world.isClient) {
            // Get the damage source that killed the creeper
            DamageSource damageSource = this.getRecentDamageSource();

            if (damageSource != null) {
                // Get the entity that killed the creeper (e.g., a player or another mob)
                Entity killer = damageSource.getAttacker();

                if (killer instanceof PlayerEntity player && ((PlayerExtension)player).isInhabiting() && ((PlayerExtension)player).getInhabitedMobType().equals(new Identifier("minecraft:skeleton"))) {
                    Item[] c418Discs = new Item[]{
                            Items.MUSIC_DISC_13,
                            Items.MUSIC_DISC_CAT,  
                            Items.MUSIC_DISC_BLOCKS,
                            Items.MUSIC_DISC_CHIRP,
                            Items.MUSIC_DISC_FAR,
                            Items.MUSIC_DISC_MALL,
                            Items.MUSIC_DISC_MELLOHI,
                            Items.MUSIC_DISC_STAL,
                            Items.MUSIC_DISC_STRAD,
                            Items.MUSIC_DISC_WARD,
                            Items.MUSIC_DISC_11,
                            Items.MUSIC_DISC_WAIT
                    };

                    // Select a random disc
                    Random random = new Random();
                    Item disc = c418Discs[random.nextInt(c418Discs.length)];
                    this.dropStack(new ItemStack(disc, 1));
                }
            }
        }
    }
}