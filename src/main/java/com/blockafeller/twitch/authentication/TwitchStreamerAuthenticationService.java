package com.blockafeller.twitch.authentication;

import com.blockafeller.config.ConfigManager;
import com.blockafeller.twitch.*;
import com.blockafeller.twitch.memory.PlayerAuthData;
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
            if (twitchAuthorizationInitializationData.getReponseCode() != 200) {
                serverPlayerEntity.sendMessage(Text.literal("Failed to  initialize authorization."));
                return;
            }

            MinecraftTwitchMessengerService.sendAuthorizationLink(serverPlayerEntity, twitchAuthorizationInitializationData.getUserCode());
            TwitchAccessTokenPollingService.pollForAccessToken(clientId, twitchAuthorizationInitializationData.getDeviceCode())
                    .thenAccept(tokenData -> {
                        if (tokenData != null && tokenData.getResponseCode() == 200) {
                            System.out.println("Access token: " + tokenData.getAccessToken());
                            PlayerAuthData playerAuthData = TwitchUserIdFetcherService.fetchUserTwitchId(clientId, tokenData.getAccessToken());
                            if (playerAuthData == null || playerAuthData.getResponseCode() != 200) {
                                serverPlayerEntity.sendMessage(Text.literal("Failed to retrieve user ID."));
                                if (playerAuthData != null) {
                                    serverPlayerEntity.sendMessage(Text.literal("Response code: " + playerAuthData.getResponseCode()));
                                }
                                return;
                            }

                            String clientSecret = ConfigManager.getConfig().getTwitchAppClientSecret();
                            TwitchPubSubClient twitchPubSubClient = new TwitchPubSubClient(serverPlayerEntity, clientId, clientSecret, tokenData, playerAuthData.getTwitchUserId());
                            twitchPubSubClient.connect();
                        } else {
                            serverPlayerEntity.sendMessage(Text.literal("Failed to retrieve access token."));
                            if (tokenData != null) {
                                serverPlayerEntity.sendMessage(Text.literal("Response code: " + tokenData.getResponseCode()));
                            }
                        }
                    })
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void shutdown(ServerPlayerEntity serverPlayerEntity) {
        if (twitchPubSubClient != null) {
            twitchPubSubClient.shutdown();
            twitchPubSubClient = null;
        } else {
            serverPlayerEntity.sendMessage(Text.literal("Streamer is not authenticated."));
        }
    }
}
