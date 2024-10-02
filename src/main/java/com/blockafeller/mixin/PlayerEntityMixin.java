package com.blockafeller.mixin;

import com.blockafeller.constraints.InventoryFiller;
import com.blockafeller.extension.PlayerExtension;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
        if (!stack.isEmpty() && (stack.getItem() == Items.PAPER || stack.getItem() == Items.ARROW) && stack.getName().getString().startsWith("Do not interact") && this.inhabiting) {
            InventoryFiller.fillInventoryWithPapers((ServerPlayerEntity) (Object) this);
            System.out.println("Blocked tripwire hook drop");
            cir.setReturnValue(null); // Prevent dropping tripwire hooks
        }
    }

    @Inject(method = "playSound(Lnet/minecraft/sound/SoundEvent;FF)V", at = @At("HEAD"), cancellable = true)
    private void preventEatingSoundAsCreeper(SoundEvent sound, float volume, float pitch, CallbackInfo ci) {
        if (this.inhabiting && this.inhabitedMobType.toString().equals("minecraft:creeper") && sound == SoundEvents.ENTITY_GENERIC_EAT) {
            ci.cancel();
        }
    }
}
