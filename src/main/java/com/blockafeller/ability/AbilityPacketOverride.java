package com.blockafeller.ability;

import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class AbilityPacketOverride {

    // Replace "draylar.identity.network.ServerNetworking.USE_ABILITY" with the correct identifier
    private static final Identifier USE_ABILITY = new Identifier("identity", "use_ability");

    public static void register() {
        // Override the packet handler with a no-op implementation
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, USE_ABILITY, AbilityPacketOverride::onUseAbility);
    }

    private static void onUseAbility(PacketByteBuf buf, NetworkManager.PacketContext context) {
        // Handle the packet but do nothing
        PlayerEntity player = context.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            // Custom logic here, if needed, or simply prevent the ability execution
            System.out.println("Use Ability packet intercepted and overridden.");
        }
    }
}
