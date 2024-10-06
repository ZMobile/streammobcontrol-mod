package com.blockafeller.mixin;

import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.util.StreamerUtil;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void onPlayerDeath(DamageSource source, CallbackInfo info) {
        System.out.println("Player died!");
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        if (!StreamerUtil.isStreamer(player)) {
            System.out.println("Non streamer died!");
            // Cancel the default death message
            info.cancel();

            // Optionally, send a custom message instead
            //player.getServer().getPlayerManager().broadcast(Text.literal(player.getName().getString() + " met a mysterious end..."), false);
        } else {
            System.out.println("Streamer died!");
            System.out.println("Source: " + source.getName());
            if (source.getAttacker() instanceof ServerPlayerEntity attacker) {
                System.out.println("Killed by player!");
                player.getServer().getPlayerManager().broadcast(Text.literal("Congratulations " + attacker.getEntityName() + " for killing " + player.getEntityName() + "!"), false);
            }
        }
    }

    /*@Inject(method = "triggerItemPickedUpByEntityCriteria", at = @At("HEAD"), cancellable = true)
    private void canPickupItem(ItemEntity itemEntity, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // Check if the player is morphed using Identity mod
        if (((PlayerExtension) player).isInhabiting()) {
            ItemStack stack = itemEntity.getStack();

            // Allow pickup if the item is armor
            if (stack.getItem() instanceof ArmorItem) {
                cir.setReturnValue(true); // Allow pickup for armor items
            }
        }
    }*/
}
