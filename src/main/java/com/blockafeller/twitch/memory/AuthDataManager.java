package com.blockafeller.twitch.memory;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;

public class AuthDataManager {
    public static final String PLAYER_AUTH_DATA_FILE_NAME = "player_auth_data.json";
    private static PlayerAuthData playerAuthData;

    public static void loadPlayerAuthData() {
        File playerAuthDataFile = getPlayerAuthDataFile();

        if (!playerAuthDataFile.exists()) {
            // Copy default playerAuthData from resources
            try (InputStream in = AuthDataManager.class.getClassLoader().getResourceAsStream(PLAYER_AUTH_DATA_FILE_NAME)) {
                if (in != null) {
                    Files.copy(in, playerAuthDataFile.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Load the playerAuthData
        try (FileReader reader = new FileReader(playerAuthDataFile)) {
            playerAuthData = new Gson().fromJson(reader, PlayerAuthData.class);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            playerAuthData = new PlayerAuthData();
            savePlayerAuthData();
        }
    }

    public static void savePlayerAuthData() {
        try (FileWriter writer = new FileWriter(getPlayerAuthDataFile())) {
            new Gson().toJson(playerAuthData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PlayerAuthData getPlayerAuthData() {
        return playerAuthData;
    }

    private static File getPlayerAuthDataFile() {
        return new File(FabricLoader.getInstance().getConfigDir().toFile(), PLAYER_AUTH_DATA_FILE_NAME);
    }
}