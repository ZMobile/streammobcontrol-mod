package com.blockafeller.twitch;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class TwitchUserIdFetcherService {
    class UserResponse {
        List<UserData> data;
    }

    class UserData {
        String id;
        String login;
        @SerializedName("display_name")
        String displayName;
        @SerializedName("profile_image_url")
        String profileImageUrl;
        // Add other fields as needed
    }

    public String fetchUserTwitchId(String clientId, String accessToken) {
        String twitchUserId = null;
        try {
            // Create the URL object
            URL url = new URL("https://api.twitch.tv/helix/users");

            // Open the connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            conn.setRequestMethod("GET");

            // Set the request headers
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Client-Id", clientId);

            // Get the response code
            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder responseContent = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    responseContent.append(inputLine);
                }
                in.close();

                // Parse the JSON response
                Gson gson = new Gson();
                UserResponse userResponse = gson.fromJson(responseContent.toString(), UserResponse.class);

                if (!userResponse.data.isEmpty()) {
                    UserData userData = userResponse.data.get(0);

                    //String twitchLogin = userData.login;
                    //String displayName = userData.displayName;
                    //String profileImageUrl = userData.profileImageUrl;
                    // Now you can link the Twitch ID with the viewer's Minecraft account
                    //linkViewerTwitchAccount(minecraftUUID, twitchUserId, twitchLogin, displayName);
                    twitchUserId = userData.id;

                    // Inform the viewer that their account has been linked
                    System.out.println("[Mod] Your Twitch account has been linked successfully!");
                } else {
                    // Handle the case where no user data is returned
                    System.out.println("[Mod] Failed to retrieve your Twitch user information.");
                }
            } else {
                // Handle non-200 response codes
                BufferedReader errorIn = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String errorLine;
                StringBuilder errorResponse = new StringBuilder();
                while ((errorLine = errorIn.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorIn.close();

                // Log or handle the error
                System.err.println("Error retrieving user information: " + errorResponse.toString());
                System.out.println("[Mod] An error occurred while linking your Twitch account.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[Mod] An unexpected error occurred.");
        }
        return twitchUserId;
    }
}
