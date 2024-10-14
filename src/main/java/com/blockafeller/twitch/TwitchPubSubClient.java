package com.blockafeller.twitch;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class TwitchPubSubClient extends WebSocketClient {
    private ServerPlayerEntity authenticatedPlayer;
    private String accessToken;
    private String refreshToken;
    private final String clientId;
    private final String clientSecret;
    private final String streamerTwitchId;
    private final Gson gson = new Gson();
    private Timer pingTimer;
    private long expirationTime; // To track when the token expires

    public TwitchPubSubClient(ServerPlayerEntity player, String clientId, String clientSecret, TwitchTokenData tokenData, String streamerTwitchId) {
        super(URI.create("wss://pubsub-edge.twitch.tv"));
        this.authenticatedPlayer = player;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.accessToken = tokenData.getAccessToken();
        this.refreshToken = tokenData.getRefreshToken();
        this.expirationTime = System.currentTimeMillis() + (tokenData.getExpirationTime() * 1000);
        this.streamerTwitchId = streamerTwitchId;
    }

    private boolean isTokenValid() {
        return System.currentTimeMillis() < expirationTime - (2 * 60 * 1000); // Token valid until 2 minutes before expiry
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("[PubSub] Connected to Twitch PubSub");
        // Ensure token is valid before proceeding
        if (isTokenValid()) {
            sendListenRequest();
            startPingTimer();
        } else {
            System.out.println("[PubSub] Token expired. Refreshing...");
            refreshAccessToken();
        }
    }

    @Override
    public void onMessage(String message) {
        JsonObject jsonMessage = gson.fromJson(message, JsonObject.class);
        String type = jsonMessage.get("type").getAsString();

        switch (type) {
            case "RESPONSE":
                handleResponse(jsonMessage);
                break;
            case "MESSAGE":
                handleMessage(jsonMessage);
                break;
            case "PONG":
                // PONG received; connection is alive
                break;
            case "RECONNECT":
                // Reconnect as instructed
                System.out.println("[PubSub] Reconnect requested by Twitch");
                reconnectWithTokenCheck();
                break;
            default:
                System.out.println("[PubSub] Unknown message type: " + type);
                break;
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("[PubSub] Disconnected: " + reason);
        stopPingTimer();
        // Attempt reconnection with token check
        reconnectWithTokenCheck();
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    private void sendListenRequest() {
        JsonObject listenRequest = new JsonObject();
        listenRequest.addProperty("type", "LISTEN");

        JsonObject data = new JsonObject();
        data.addProperty("auth_token", accessToken);

        JsonArray topics = new JsonArray();
        topics.add("channel-bits-events-v2." + streamerTwitchId);
        topics.add("channel-subscribe-events-v1." + streamerTwitchId);
        data.add("topics", topics);

        listenRequest.add("data", data);
        send(listenRequest.toString());
    }

    private void handleResponse(JsonObject message) {
        String error = message.get("error").getAsString();
        if (error.isEmpty()) {
            authenticatedPlayer.sendMessage(Text.literal("[PubSub] Streamer Authentication Successful! Now listening for bits and subscription events.").formatted(Formatting.GREEN), false);
            authenticatedPlayer.sendMessage(Text.literal("Note: in order to avoid storing Twitch access tokens, authentication will need to be redone when the server is restarted."), false);
            authenticatedPlayer.sendMessage(Text.literal("Type \"/stream shutdown\" to halt streamer authentication and subscription / bits event listening."), false);
            System.out.println("[PubSub] Successfully subscribed to topics.");
        } else {
            System.err.println("[PubSub] Failed to subscribe: " + error);
            // Handle subscription errors if necessary
        }
    }

    private void handleMessage(JsonObject message) {
        JsonObject data = message.getAsJsonObject("data");
        String topic = data.get("topic").getAsString();
        String messageContent = data.get("message").getAsString();
        JsonObject contentJson = gson.fromJson(messageContent, JsonObject.class);

        if (topic.startsWith("channel-bits-events-v2.")) {
            handleBitsEvent(contentJson);
        } else if (topic.startsWith("channel-subscribe-events-v1.")) {
            handleSubscriptionEvent(contentJson);
        }
    }

    private void handleBitsEvent(JsonObject content) {
        JsonObject data = content.getAsJsonObject("data");
        String userId = data.get("user_id").getAsString();
        String userName = data.get("user_name").getAsString();
        int bitsUsed = data.get("bits_used").getAsInt();
        String chatMessage = data.get("chat_message").getAsString();

        System.out.println("[PubSub] Bits Event: " + userName + " cheered " + bitsUsed + " bits.");

        // Implement your logic here
        // Example: rewardPlayer(userId, bitsUsed);
    }

    private void handleSubscriptionEvent(JsonObject content) {
        JsonObject subData = content.getAsJsonObject("data");
        String userId = subData.get("user_id").getAsString();
        String userName = subData.get("display_name").getAsString();
        String subPlan = subData.get("sub_plan").getAsString();
        String subPlanName = subData.get("sub_plan_name").getAsString();
        boolean isGift = subData.get("is_gift").getAsBoolean();

        System.out.println("[PubSub] Subscription Event: " + userName + " subscribed with plan " + subPlanName);

        // Implement your logic here
        // Example: rewardSubscriber(userId, subPlan);
    }

    private void startPingTimer() {
        pingTimer = new Timer();
        pingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendPing();
            }
        }, 0, 4 * 60 * 1000); // Every 4 minutes
    }

    private void stopPingTimer() {
        if (pingTimer != null) {
            pingTimer.cancel();
            pingTimer = null;
        }
    }

    public void sendPing() {
        JsonObject pingMessage = new JsonObject();
        pingMessage.addProperty("type", "PING");
        send(pingMessage.toString());
        System.out.println("[PubSub] PING sent");
    }

    private void reconnectWithTokenCheck() {
        // Check if the access token is about to expire
        if (System.currentTimeMillis() >= expirationTime - (2 * 60 * 1000)) { // Refresh 2 minutes before expiry
            refreshAccessToken();
        }
        reconnect();
    }

    public void reconnect() {
        new Thread(() -> {
        // Implement reconnection logic with appropriate delays
        try {
            this.reconnectBlocking();
            System.out.println("[PubSub] Reconnected to Twitch PubSub");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        }).start();
    }

    private void refreshAccessToken() {
        System.out.println("[PubSub] Refreshing access token...");
        TwitchTokenData newTokenData = TwitchAccessTokenRefresherService.refreshAccessToken(clientId, clientSecret, refreshToken);
        if (newTokenData != null) {
            this.accessToken = newTokenData.getAccessToken();
            this.refreshToken = newTokenData.getRefreshToken();
            this.expirationTime = System.currentTimeMillis() + (newTokenData.getExpirationTime() * 1000); // Set new expiration time
            System.out.println("[PubSub] Access token refreshed.");
        } else {
            System.err.println("[PubSub] Failed to refresh access token.");
        }
    }

    public void shutdown() {
        System.out.println("[PubSub] Shutting down Twitch PubSub listener.");
        stopPingTimer(); // Stop the ping timer
        close(); // Close the WebSocket connection
        authenticatedPlayer.sendMessage(Text.literal("Streamer event listener has been shut down.").formatted(Formatting.RED), false);
    }
}