package com.blockafeller.twitch;

import com.blockafeller.config.ConfigManager;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TwitchDeviceAuthorizationInitializationService {
    private static String streamerScopes = "bits:read channel:read:redemptions user:read:email";
    private static String viewerScopes = "user:read:email";

    public static TwitchAuthorizationInitializationData requestAuthentication(String scopes) throws Exception {
        String clientId = ConfigManager.getConfig().getTwitchAppClientId();
        if (clientId == null) {
            throw new Exception("Twitch App Client ID is not set in the config file.");
        }
        URL url = new URL("https://id.twitch.tv/oauth2/device");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String params = "client_id=" + URLEncoder.encode(clientId, "UTF-8") +
                "&scope=" + URLEncoder.encode(scopes, "UTF-8");

        OutputStream os = conn.getOutputStream();
        os.write(params.getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.close();

// Read the response
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

// Parse the JSON response
        // Parse the JSON response using Gson
        Gson gson = new Gson();
        JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
        System.out.println("Response: " + response.toString());

        String deviceCode = jsonResponse.get("device_code").getAsString();
        String userCode = jsonResponse.get("user_code").getAsString();
        String verificationUri = jsonResponse.get("verification_uri").getAsString();
        int expiresIn = jsonResponse.get("expires_in").getAsInt();
        int interval = jsonResponse.get("interval").getAsInt();
        return new TwitchAuthorizationInitializationData(deviceCode, expiresIn, interval, userCode, verificationUri);
    }

    public static TwitchAuthorizationInitializationData requestStreamerAuthentication() throws Exception {
        return requestAuthentication(streamerScopes);
    }

    public static TwitchAuthorizationInitializationData requestViewerAuthentication() throws Exception {
        return requestAuthentication(viewerScopes);
    }
}