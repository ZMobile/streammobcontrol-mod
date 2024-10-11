package com.blockafeller.twitch;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class TwitchPubSubClient extends WebSocketClient {

    private final String accessToken;
    private final String streamerTwitchId;
    private final Gson gson = new Gson();
    private Timer pingTimer;

    public TwitchPubSubClient(URI serverUri, String accessToken, String streamerTwitchId) {
        super(serverUri);
        this.accessToken = accessToken;
        this.streamerTwitchId = streamerTwitchId;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("[PubSub] Connected to Twitch PubSub");

        // Send LISTEN command to subscribe to topics
        sendListenRequest();

        // Start sending PING messages every 4 minutes
        startPingTimer();
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
                reconnect();
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
        // Implement reconnection logic if desired
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

    public void reconnect() {
        // Implement reconnection logic with appropriate delays
        try {
            this.reconnectBlocking();
            System.out.println("[PubSub] Reconnected to Twitch PubSub");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}