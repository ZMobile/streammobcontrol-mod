package com.blockafeller.mixin;

import com.blockafeller.morph.MorphUtil;
import com.blockafeller.util.StreamerUtil;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
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

    @Inject(method = "onPlayerAction", at = @At("HEAD"), cancellable = true)
    private void onPlayerAction(PlayerActionC2SPacket packet, CallbackInfo ci) {
        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) (Object) this;
        ServerPlayerEntity player = handler.player;

        // Check if the player is trying to drop an item
        if ((packet.getAction() == PlayerActionC2SPacket.Action.DROP_ITEM || packet.getAction() == PlayerActionC2SPacket.Action.DROP_ALL_ITEMS)
        && MorphUtil.isDoNotInteractItem(player.getMainHandStack())) {
            // Cancel the item drop action
            System.out.println("Preventing item drop from player: " + player.getEntityName());
            ci.cancel();
        }
    }

    @Inject(method = "onUpdateSelectedSlot", at = @At("HEAD"))
    private void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket packet, CallbackInfo ci) {
        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) (Object) this;
        ServerPlayerEntity player = handler.player;

        // Get the slot index from the packet
        int selectedSlot = packet.getSelectedSlot();

        // Debug: Print the slot index
        System.out.println("Player " + player.getEntityName() + " changed selected slot to " + selectedSlot);

        // Manually update the server's selected slot value
        player.getInventory().selectedSlot = selectedSlot;
    }
}
