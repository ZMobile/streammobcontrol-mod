package com.blockafeller.ability;

import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class IdentityPacketOverride {

    // Reference the identity sync packet identifier from the Identity mod
    private static final Identifier IDENTITY_REQUEST = new Identifier("identity", "request");
    private static final Identifier IDENTITY_SYNC = new Identifier("identity", "identity_sync");

    public static void register() {
        // Register the custom packet handler
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, IDENTITY_REQUEST, IdentityPacketOverride::onIdentitySync);
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, IDENTITY_SYNC, IdentityPacketOverride::onIdentitySync);
    }

    private static void onIdentitySync(PacketByteBuf buf, NetworkManager.PacketContext context) {
        PlayerEntity player = context.getPlayer();

        if (player instanceof ServerPlayerEntity) {
            // Your custom logic here
            // For example, you could log the identity change or prevent it:
            System.out.println("Identity sync packet intercepted and overridden for player: " + player.getDisplayName().getString());
            player.sendMessage(Text.literal("Mob identity change via the client is not allowed here."), false);

            // You could also manipulate the identity information received from the client
            // Use `buf.read/write` methods to access the data if needed, e.g.,
            // Identifier identityId = buf.readIdentifier();
            // System.out.println("Received identity change for: " + identityId);

            // Prevent the identity sync from happening (optional, based on your use case)
            // Custom logic can go here
        }
    }
}