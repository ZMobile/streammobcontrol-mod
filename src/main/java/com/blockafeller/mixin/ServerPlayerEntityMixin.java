package com.blockafeller.mixin;

import com.blockafeller.util.StreamerUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
    private void onPlayerDeath(DamageSource source, CallbackInfo info) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        if (!StreamerUtil.isStreamer(player)) {
            // Cancel the default death message
            info.cancel();

            // Optionally, send a custom message instead
            //player.getServer().getPlayerManager().broadcast(Text.literal(player.getName().getString() + " met a mysterious end..."), false);
        }
    }
}
