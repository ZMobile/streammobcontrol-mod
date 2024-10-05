package com.blockafeller.mixin;

import com.blockafeller.util.StreamerUtil;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    private void onHandleChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        // Get the player sending the message
        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) (Object) this;
        ServerPlayerEntity player = handler.player;

        // Custom logic to decide if the player is allowed to chat
        if (!StreamerUtil.isStreamer(player)) {
            // Block the chat message and notify the player
            player.sendMessage(Text.literal("You are not allowed to use chat!"), false);
            ci.cancel(); // Cancel the chat message, preventing it from being sent
        }
    }
}
