package com.blockafeller.twitch;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class TwitchAccessTokenRefresherService {
    public static TwitchTokenData refreshAccessToken(String clientId, String clientSecret, String refreshToken) {
        TwitchTokenData tokenData = null;
        try {
            // Create the connection
            URL url = new URL("https://id.twitch.tv/oauth2/token");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Build the request parameters
            String params = "client_id=" + URLEncoder.encode(clientId, "UTF-8") +
                    "&client_secret=" + URLEncoder.encode(clientSecret, "UTF-8") +
                    "&grant_type=refresh_token" +
                    "&refresh_token=" + URLEncoder.encode(refreshToken, "UTF-8");

            // Send the request
            OutputStream os = conn.getOutputStream();
            os.write(params.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            // Get the response code
            int responseCode = conn.getResponseCode();
            System.out.println("HTTP Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder tokenResponse = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    tokenResponse.append(inputLine);
                }
                in.close();

                // Parse the JSON response using Gson
                Gson gson = new Gson();
                JsonObject jsonResponse = gson.fromJson(tokenResponse.toString(), JsonObject.class);

                String newAccessToken = jsonResponse.get("access_token").getAsString();
                String newRefreshToken = jsonResponse.has("refresh_token") ? jsonResponse.get("refresh_token").getAsString() : null;
                int newExpiresIn = jsonResponse.get("expires_in").getAsInt();

                // Update stored tokens
                tokenData = new TwitchTokenData(newAccessToken, newRefreshToken, newExpiresIn);

                // Output the new tokens (or use them as needed)
                System.out.println("New Access Token: " + newAccessToken);
                System.out.println("New Refresh Token: " + newRefreshToken);
                System.out.println("Expires In: " + newExpiresIn + " seconds");
            } else {
                // Handle error responses
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String errorLine;
                StringBuilder errorResponse = new StringBuilder();
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorReader.close();

                // Parse the error response
                Gson gson = new Gson();
                JsonObject errorJson = gson.fromJson(errorResponse.toString(), JsonObject.class);

                String error = errorJson.get("error").getAsString();
                String errorDescription = errorJson.get("message").getAsString(); // Adjust based on actual error fields

                // Output the error details
                System.out.println("Error: " + error);
                System.out.println("Description: " + errorDescription);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokenData;
    }

}
