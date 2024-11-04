package com.blockafeller.twitch;

import com.blockafeller.config.ConfigManager;
import com.blockafeller.twitch.memory.ViewerDonationData;
import com.blockafeller.twitch.memory.ViewerDonationDataManager;
import com.blockafeller.twitch.memory.ViewerDonationDataMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;

public class TwitchSubscriptionCheckerService {

    public static void getTwitchSubscriptions(String accessToken, String userId) throws Exception {
        String clientId = ConfigManager.getConfig().getTwitchAppClientId();
        if (clientId == null) {
            throw new Exception("Twitch App Client ID is not set in the config file.");
        }

        URL url = new URL("https://api.twitch.tv/helix/subscriptions?broadcaster_id=" + userId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Client-Id", clientId);

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse the JSON response using Gson
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
            JsonArray subscriptions = jsonResponse.getAsJsonArray("data");

            for (int i = 0; i < subscriptions.size(); i++) {
                JsonObject sub = subscriptions.get(i).getAsJsonObject();
                String userName = sub.get("user_name").getAsString();
                String userIdSub = sub.get("user_id").getAsString();
                String tier = sub.get("tier").getAsString();
                String expiresAt = sub.get("expires_at").getAsString();

                // Convert expiration timestamp to LocalDateTime
                LocalDateTime expirationTime = LocalDateTime.parse(expiresAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                // Handle subscription event
                handleSubscriptionEvent(sub, userIdSub, userName, tier, expirationTime);

                System.out.println("User: " + userName + " (ID: " + userIdSub + ")");
                System.out.println("Tier: " + tier);
                System.out.println("Expiration: " + expirationTime);
                System.out.println("--------------------------");
            }
        } else {
            throw new Exception("GET request failed with response code: " + responseCode);
        }
    }

    private static void handleSubscriptionEvent(JsonObject subData, String userId, String userName, String subPlan, LocalDateTime expirationTime) {
        String subPlanName = subData.get("sub_plan_name").getAsString();
        boolean isGift = subData.get("is_gift").getAsBoolean();

        String gifteeUserId = userId; // Default to the gifter, but this will change if it's a gift
        String gifteeUserName = userName;

        // If it's a gifted subscription, extract the recipient's information
        if (isGift) {
            gifteeUserId = subData.get("recipient_id").getAsString();
            gifteeUserName = subData.get("recipient_display_name").getAsString();
            System.out.println("[PubSub] Gifted Subscription: " + userName + " gifted a subscription to " + gifteeUserName);
        } else {
            System.out.println("[PubSub] Subscription Event: " + userName + " subscribed with plan " + subPlanName);
        }

        ViewerDonationDataMap viewerDonationDataMap = ViewerDonationDataManager.getViewerDonationDataMap();
        ViewerDonationData viewerDonationData = viewerDonationDataMap.getViewerDonationData(gifteeUserId);
        if (viewerDonationData == null) {
            viewerDonationData = new ViewerDonationData(gifteeUserId);
            viewerDonationDataMap.putViewerDonationData(gifteeUserId, viewerDonationData);
        }

        int subTier = mapSubPlanToTier(subPlan);
        viewerDonationData.addSubscription(subTier);

        ViewerDonationDataManager.saveViewerDonationData();
    }

    private static int mapSubPlanToTier(String subPlan) {
        return switch (subPlan) {
            case "1000" -> 1;
            case "2000" -> 2;
            case "3000" -> 3;
            default -> 0;
        };
    }
}
