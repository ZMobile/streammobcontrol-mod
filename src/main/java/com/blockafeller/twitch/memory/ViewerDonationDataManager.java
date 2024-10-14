package com.blockafeller.twitch.memory;

import com.blockafeller.util.gson.LocalDateTimeTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class ViewerDonationDataManager {
    public static final String VIEWER_DONATION_DATA_FILE_NAME = "viewer_donation_data.json";
    private static ViewerDonationDataMap viewerDonationDataMap = new ViewerDonationDataMap();
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new LocalDateTimeTypeAdapterFactory())
            .create();

    public static ViewerDonationDataMap getViewerDonationDataMap() {
        return viewerDonationDataMap;
    }

    // Synchronize loading and saving within one operation
    public static void putViewerDonationData(String twitchUserId, ViewerDonationData viewerDonationData) {
        viewerDonationDataMap.putViewerDonationData(twitchUserId, viewerDonationData);

        // Save the modified data back to the file
        saveViewerDonationData();
    }

    public static void loadViewerDonationData() {
        File viewerDonationDataFile = getViewerDonationDataFile();

        if (!viewerDonationDataFile.exists()) {
            // Copy default viewerDonationData from resources
            try (InputStream in = ViewerDonationDataManager.class.getClassLoader().getResourceAsStream(VIEWER_DONATION_DATA_FILE_NAME)) {
                if (in != null) {
                    Files.copy(in, viewerDonationDataFile.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Load the viewerDonationData
        try (FileReader reader = new FileReader(viewerDonationDataFile)) {
            viewerDonationDataMap = gson.fromJson(reader, ViewerDonationDataMap.class);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            viewerDonationDataMap = new ViewerDonationDataMap();
            saveViewerDonationData();
        }
    }

    public static void saveViewerDonationData() {
        try (FileWriter writer = new FileWriter(getViewerDonationDataFile())) {
            gson.toJson(viewerDonationDataMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getViewerDonationDataFile() {
        return new File(FabricLoader.getInstance().getConfigDir().toFile(), VIEWER_DONATION_DATA_FILE_NAME);
    }
}
