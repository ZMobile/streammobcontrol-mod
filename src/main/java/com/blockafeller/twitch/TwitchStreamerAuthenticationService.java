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

            MinecraftTwitchMessengerService.sendAuthorizationLink(serverPlayerEntity, twitchAuthorizationInitializationData.getUserCode());
            TwitchTokenData twitchTokenData = TwitchAccessTokenPollingService.pollForAccessToken(clientId, twitchAuthorizationInitializationData.getDeviceCode());
            Gson gson = new Gson();
            System.out.println("Token data: " + gson.toJson(twitchTokenData));
            //{"accessToken":"q5fbg0setb81gke7rtlqadbx6sdejt","refreshToken":"u41hgee1ix7x1ic3u53gpjh8jhy4sr21y7g8nn470lbpru779q","expirationTime":13314}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
