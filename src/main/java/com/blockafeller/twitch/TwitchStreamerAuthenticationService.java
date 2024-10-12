package com.blockafeller.twitch;

import com.blockafeller.config.ConfigManager;
import com.google.gson.Gson;
import net.minecraft.server.network.ServerPlayerEntity;

public class TwitchStreamerAuthenticationService {
    public static void authenticateStreamer(ServerPlayerEntity serverPlayerEntity) {
        try {
            String clientId = ConfigManager.getConfig().getTwitchAppClientId();
            if (clientId == null) {
                return;
            }

            TwitchAuthorizationInitializationData twitchAuthorizationInitializationData = TwitchDeviceAuthorizationInitializationService.requestStreamerAuthentication();

            if (twitchAuthorizationInitializationData.getDeviceCode() == null) {
                return;
            }

            MinecraftTwitchMessengerService.sendAuthorizationLinkToStreamer(serverPlayerEntity, twitchAuthorizationInitializationData.getUserCode());
            TwitchTokenData twitchTokenData = TwitchAccessTokenPollingService.pollForAccessToken(clientId, twitchAuthorizationInitializationData.getDeviceCode());
            Gson gson = new Gson();
            System.out.println("Token data: " + gson.toJson(twitchTokenData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
