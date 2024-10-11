package com.blockafeller.twitch;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class TwitchAccessTokenPollingService {
    public TwitchTokenData pollForAccessToken(String clientId, String deviceCode) throws InterruptedException, IOException {
        boolean authorized = false;
        long startTime = System.currentTimeMillis();
        long expiresInMillis = /*expiresIn*/ 60 * 1000;
        int interval = 5;
        TwitchTokenData tokenData = null;
        while (!authorized && (System.currentTimeMillis() - startTime) < expiresInMillis) {
            Thread.sleep(interval * 1000);

            URL tokenUrl = new URL("https://id.twitch.tv/oauth2/token");
            HttpURLConnection tokenConn = (HttpURLConnection) tokenUrl.openConnection();
            tokenConn.setRequestMethod("POST");
            tokenConn.setDoOutput(true);
            tokenConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String tokenParams = "client_id=" + URLEncoder.encode(clientId, "UTF-8") +
                    "&device_code=" + URLEncoder.encode(deviceCode, "UTF-8") +
                    "&grant_type=urn:ietf:params:oauth:grant-type:device_code";

            OutputStream tokenOs = tokenConn.getOutputStream();
            tokenOs.write(tokenParams.getBytes(StandardCharsets.UTF_8));
            tokenOs.flush();
            tokenOs.close();

            int responseCode = tokenConn.getResponseCode();
            if (responseCode == 200) {
                // Successful authorization
                BufferedReader tokenIn = new BufferedReader(new InputStreamReader(tokenConn.getInputStream()));
                StringBuilder tokenResponse = new StringBuilder();
                String tokenInputLine;
                while ((tokenInputLine = tokenIn.readLine()) != null) {
                    tokenResponse.append(tokenInputLine);
                }
                tokenIn.close();

                Gson gson = new Gson();
                JsonObject jsonResponse = gson.fromJson(tokenResponse.toString(), JsonObject.class);
                String accessToken = jsonResponse.get("access_token").getAsString();

// Handle optional "refresh_token"
                String refreshToken = null;
                if (jsonResponse.has("refresh_token") && !jsonResponse.get("refresh_token").isJsonNull()) {
                    refreshToken = jsonResponse.get("refresh_token").getAsString();
                }

                int tokenExpiresIn = jsonResponse.get("expires_in").getAsInt();

                tokenData = new TwitchTokenData(accessToken, refreshToken, tokenExpiresIn);

                authorized = true;
                // Notify the user
            } else {
                // Handle errors
                BufferedReader errorIn = new BufferedReader(new InputStreamReader(tokenConn.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String errorInputLine;
                while ((errorInputLine = errorIn.readLine()) != null) {
                    errorResponse.append(errorInputLine);
                }
                errorIn.close();

                Gson gson = new Gson();
                JsonObject errorJson = gson.fromJson(errorResponse.toString(), JsonObject.class);
                String error = errorJson.get("error").getAsString();

                if ("authorization_pending".equals(error)) {
                    // Continue polling
                } else if ("slow_down".equals(error)) {
                    // Increase interval by 5 seconds
                    interval += 5;
                } else if ("access_denied".equals(error) || "expired_token".equals(error)) {
                    // Stop polling and inform the user
                    System.out.println("[Mod] Authentication failed or expired. Please try again.");
                    break;
                } else {
                    // Other errors
                    System.out.println("[Mod] Error during authentication: " + error);
                    break;
                }
            }
        }

        if (!authorized) {
            // Timed out
            System.out.println("[Mod] Authentication timed out. Please initiate the process again.");
        }
        return tokenData;
    }

    /*Access token response:
    {
  "access_token": "ACCESS_TOKEN",
  "refresh_token": "REFRESH_TOKEN", // May not be provided for all flows
  "expires_in": 3600, // Token validity in seconds
  "scope": ["list", "of", "scopes"],
  "token_type": "bearer"
}
     */
}
