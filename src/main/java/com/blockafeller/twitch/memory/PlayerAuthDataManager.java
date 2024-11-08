package com.blockafeller.twitch.memory;

import com.blockafeller.util.gson.LocalDateTimeTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.util.UUID;

public class PlayerAuthDataManager {
    public static final String PLAYER_AUTH_DATA_FILE_NAME = "player_auth_data.json";
    private static PlayerAuthDataMap playerAuthDataMap = new PlayerAuthDataMap();
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new LocalDateTimeTypeAdapterFactory())
            .create();

    // Synchronize loading and saving within one operation
    public static void addPlayerAuthData(UUID minecraftUuid, PlayerAuthData playerAuthData) {
        playerAuthDataMap.addAuthData(minecraftUuid, playerAuthData);

        // Save the modified data back to the file
        savePlayerAuthData();
    }

    public static PlayerAuthDataMap getPlayerAuthDataMap() {
        return playerAuthDataMap;
    }

    public static void loadPlayerAuthData() {
        File playerAuthDataFile = getPlayerAuthDataFile();

        if (!playerAuthDataFile.exists()) {
            // Copy default playerAuthData from resources
            try (InputStream in = PlayerAuthDataManager.class.getClassLoader().getResourceAsStream(PLAYER_AUTH_DATA_FILE_NAME)) {
                if (in != null) {
                    Files.copy(in, playerAuthDataFile.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Load the playerAuthData
        try (FileReader reader = new FileReader(playerAuthDataFile)) {
            playerAuthDataMap = gson.fromJson(reader, PlayerAuthDataMap.class);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            playerAuthDataMap = new PlayerAuthDataMap();
            savePlayerAuthData();
        }
    }

    public static void savePlayerAuthData() {
        try (FileWriter writer = new FileWriter(getPlayerAuthDataFile())) {
            gson.toJson(playerAuthDataMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static File getPlayerAuthDataFile() {
        return new File(FabricLoader.getInstance().getConfigDir().toFile(), PLAYER_AUTH_DATA_FILE_NAME);
    }
}
