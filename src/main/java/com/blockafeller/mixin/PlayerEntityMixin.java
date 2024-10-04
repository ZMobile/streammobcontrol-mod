package com.blockafeller.mixin;

import com.blockafeller.ability.AbilityStickListener;
import com.blockafeller.inventory.InventoryFiller;
import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.inventory.ProjectileRefill;
import draylar.identity.api.platform.IdentityConfig;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PlayerExtension {
    @Unique
    private boolean inhabiting = false;

    @Unique
    private Identifier inhabitedMobType = null;

    @Override
    public boolean isInhabiting() {
        return inhabiting;
    }

    @Override
    public void setInhabiting(boolean value) {
        this.inhabiting = value;
    }

    @Override
    public Identifier getInhabitedMobType() {
        return inhabitedMobType;
    }

    @Override
    public void setInhabitedMobType(Identifier mobType) {
        this.inhabitedMobType = mobType;
    }

    @Inject(method = "dropItem*", at = @At("HEAD"), cancellable = true)
    private void preventItemDrop(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if (!stack.isEmpty()
                 && this.inhabiting
                && (((stack.getItem() == Items.PAPER || stack.getItem() == Items.ARROW || AbilityStickListener.isAbilityStick(stack)) && stack.getName().getString().startsWith("Do not interact")) || (stack.getItem() == Items.COMPASS && stack.getItem().getName().getString().startsWith("Leave Morph")))) {
            InventoryFiller.fillInventoryWithPapers((ServerPlayerEntity) (Object) this);
            System.out.println("Blocked tripwire hook drop");
            cir.setReturnValue(null); // Prevent dropping tripwire hooks
        }
        Map<String, Item> desiredPotions = Map.of(
                "Potion of Slowness", Items.SPLASH_POTION,
                "Potion of Weakness", Items.SPLASH_POTION,
                "Potion of Poison", Items.SPLASH_POTION,
                "Potion of Harming", Items.SPLASH_POTION,
                "Healing Potion (discard to reset)", Items.POTION,
                "Fire Resistance Potion (discard to reset)", Items.POTION,
                "Swiftness Potion (discard to reset)", Items.POTION
        );
        if (this.inhabiting && this.getInhabitedMobType().toString().equals("minecraft:witch") && (desiredPotions.containsKey(stack.getName().getString())) || (stack.getItem() == Items.GLASS_BOTTLE)) {
            InventoryFiller.fillInventoryWithPapers((ServerPlayerEntity) (Object) this);
            System.out.println("Blocked potion drop");
            cir.setReturnValue(null); // Prevent dropping potions
        }
        if (this.inhabiting && this.getInhabitedMobType().toString().equals("minecraft:snow_golem") && stack.getItem() == Items.SNOWBALL) {
            InventoryFiller.fillInventoryWithPapers((ServerPlayerEntity) (Object) this);
            System.out.println("Blocked glass bottle drop");
            cir.setReturnValue(null); // Prevent dropping glass bottles
        }
        if (this.inhabiting && this.getInhabitedMobType().toString().equals("minecraft:creeper") && stack.getItem() == Items.PUFFERFISH) {
            InventoryFiller.fillInventoryWithPapers((ServerPlayerEntity) (Object) this);
            System.out.println("Blocked glass bottle drop");
            cir.setReturnValue(null); // Prevent dropping glass bottles
        }
    }

    @Inject(method = "playSound(Lnet/minecraft/sound/SoundEvent;FF)V", at = @At("HEAD"), cancellable = true)
    private void preventEatingSoundAsCreeper(SoundEvent sound, float volume, float pitch, CallbackInfo ci) {
        if (this.inhabiting && this.inhabitedMobType.toString().equals("minecraft:creeper") && sound == SoundEvents.ENTITY_GENERIC_EAT) {
            ci.cancel();
        }
    }

    @Inject(method = "eatFood", at = @At("HEAD"), cancellable = true)
    private void triggerExplosionIfCreeper(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (this.inhabiting && this.inhabitedMobType.toString().equals("minecraft:creeper")) {
            triggerExplosion((PlayerEntity) (Object) this);
            cir.setReturnValue(stack);
        }
    }

    /**
     * Trigger an explosion at the player's location and kill them instantly.
     */
    private static void triggerExplosion(PlayerEntity player) {
        World world = player.getWorld();
        // Play the explosion sound and create an explosion at the player's position
        Explosion explosion = world.createExplosion(player, player.getX(), player.getY(), player.getZ(), 3.0f, World.ExplosionSourceType.MOB);

        // Kill the player in the process
        player.damage(world.getDamageSources().explosion(explosion), Float.MAX_VALUE);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (this.inhabiting) {
            // Check if the inventory hot bar contains an empty slot or a glass bottle
            if (this.inhabitedMobType.toString().equals("minecraft:witch")) {
                boolean hasEmptySlot = false;
                for (int i = 0; i < 7; i++) {
                    ItemStack stack = ((PlayerEntity) (Object) this).getInventory().getStack(i);
                    if (stack.isEmpty() || stack.getItem() == Items.GLASS_BOTTLE) {
                        hasEmptySlot = true;
                        break;
                    }
                }
                if (hasEmptySlot) {
                    ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
                    applyPotionCooldown(player);
                    ProjectileRefill.refillPotion(player);
                }
            } else if (this.inhabitedMobType.toString().equals("minecraft:snow_golem")) {
                ItemStack stack = ((PlayerEntity) (Object) this).getInventory().getStack(0);
                //if stack has less than 64:
                if (stack.getCount() < 64) {
                    stack.setCount(64);
                }
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
