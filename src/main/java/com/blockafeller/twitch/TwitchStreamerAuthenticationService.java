package com.blockafeller.twitch;

import com.blockafeller.config.ConfigManager;
import com.blockafeller.twitch.memory.AuthDataManager;
import com.google.gson.Gson;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class TwitchStreamerAuthenticationService {
    private static TwitchPubSubClient twitchPubSubClient = null;

    public static void authenticateStreamer(ServerPlayerEntity serverPlayerEntity) {
        if (twitchPubSubClient != null) {
            serverPlayerEntity.sendMessage(Text.literal("Streamer is already authenticated."));
            return;
        }
        try {
            serverPlayerEntity.sendMessage(Text.literal("Initiating Authentication Process..."));

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

            //TwitchPubSubClient twitchPubSubClient = new TwitchPubSubClient(twitchTokenData.getAccessToken());
            //{"accessToken":"q5fbg0setb81gke7rtlqadbx6sdejt","refreshToken":"u41hgee1ix7x1ic3u53gpjh8jhy4sr21y7g8nn470lbpru779q","expirationTime":13314}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
