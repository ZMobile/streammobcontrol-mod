package com.blockafeller.twitch.authentication;

import com.blockafeller.config.ConfigManager;
import com.blockafeller.twitch.*;
import com.blockafeller.twitch.memory.PlayerAuthDataManager;
import com.blockafeller.twitch.memory.PlayerAuthData;
import com.google.gson.Gson;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TwitchUserAuthenticationService {
    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void authenticateUser(ServerPlayerEntity serverPlayerEntity) {
        executor.submit(() -> {
            try {
                serverPlayerEntity.sendMessage(Text.literal("Initiating Authentication Process..."));
                if (PlayerAuthDataManager.getPlayerAuthDataMap().hasAuthData(serverPlayerEntity.getUuid())) {
                    serverPlayerEntity.sendMessage(Text.literal("Cancelled: You are already authenticated."));
                    return;
                }

                String clientId = ConfigManager.getConfig().getTwitchAppClientId();
                if (clientId == null) {
                    serverPlayerEntity.sendMessage(Text.literal("Failed to retrieve Twitch App Client ID."));
                    return;
                }

                TwitchAuthorizationInitializationData twitchAuthorizationInitializationData = TwitchDeviceAuthorizationInitializationService.requestViewerAuthentication();

                if (twitchAuthorizationInitializationData.getDeviceCode() == null) {
                    serverPlayerEntity.sendMessage(Text.literal("Failed to retrieve device code."));
                    return;
                }

                MinecraftTwitchMessengerService.sendAuthorizationLink(serverPlayerEntity, twitchAuthorizationInitializationData.getUserCode());
                TwitchAccessTokenPollingService.pollForAccessToken(clientId, twitchAuthorizationInitializationData.getDeviceCode())
                        .thenAccept(tokenData -> {
                            if (tokenData != null && tokenData.getResponseCode() == 200) {
                                System.out.println("Token data: " + new Gson().toJson(tokenData));
                                PlayerAuthData playerAuthData = TwitchUserIdFetcherService.fetchUserTwitchId(clientId, tokenData.getAccessToken());
                                System.out.println("Player auth data: " + new Gson().toJson(playerAuthData));
                                playerAuthData.setResponseCode(null);
                                PlayerAuthDataManager.addPlayerAuthData(serverPlayerEntity.getUuid(), playerAuthData);
                                System.out.println("This gets called 4");
                                serverPlayerEntity.sendMessage(Text.literal("Authentication Successful!"));
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
        });
    }
}
