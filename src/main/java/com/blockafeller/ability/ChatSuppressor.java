package com.blockafeller.ability;

import com.blockafeller.util.StreamerUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ChatSuppressor {
    public static void register() {
        // Use ServerChatEvents.ALLOW to check and suppress chat messages conditionally
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("minecraft", "chat"), (server, player, handler, buf, responseSender) -> {
            //SignedMessage chatMessage = SignedMessage.fromPacket(buf);
            server.execute(() -> {
                if (!StreamerUtil.isStreamer(player)) {
                    // Block the chat message and notify the player
                    player.sendMessage(Text.literal("You are not allowed to use chat!"), false);
                } else {
                    // Otherwise, broadcast the message normally
                    //server.getPlayerManager().broadcast(chatMessage.getContent(), false);
                }
            });
        });
    }
}
