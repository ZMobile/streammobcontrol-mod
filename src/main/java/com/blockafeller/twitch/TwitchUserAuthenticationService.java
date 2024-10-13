package com.blockafeller.twitch;

import com.blockafeller.config.ConfigManager;
import com.blockafeller.twitch.memory.AuthDataManager;
import com.blockafeller.twitch.memory.PlayerAuthData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class TwitchUserAuthenticationService {
    public static void authenticateUser(ServerPlayerEntity serverPlayerEntity) {
        try {
            serverPlayerEntity.sendMessage(Text.literal("Initiating Authentication Process..."));
            if (AuthDataManager.hasPlayerAuthData(serverPlayerEntity.getUuid())) {
                return;
            }

            String clientId = ConfigManager.getConfig().getTwitchAppClientId();
            if (clientId == null) {
                return;
            }

            TwitchAuthorizationInitializationData twitchAuthorizationInitializationData = TwitchDeviceAuthorizationInitializationService.requestViewerAuthentication();

            if (twitchAuthorizationInitializationData.getDeviceCode() == null) {
                return;
            }

            MinecraftTwitchMessengerService.sendAuthorizationLink(serverPlayerEntity, twitchAuthorizationInitializationData.getUserCode());
            TwitchTokenData twitchTokenData = TwitchAccessTokenPollingService.pollForAccessToken(clientId, twitchAuthorizationInitializationData.getDeviceCode());

            PlayerAuthData playerAuthData = TwitchUserIdFetcherService.fetchUserTwitchId(clientId, twitchTokenData.getAccessToken());

            AuthDataManager.addPlayerAuthData(serverPlayerEntity.getUuid(), playerAuthData);
            serverPlayerEntity.sendMessage(Text.literal("Authentication Successful!"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
