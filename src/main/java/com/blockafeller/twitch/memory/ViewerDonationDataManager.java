package com.blockafeller.twitch.memory;

import com.google.gson.Gson;
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

    // Synchronize loading and saving within one operation
    public synchronized static void putViewerDonationData(String twitchUserId, ViewerDonationData viewerDonationData) {
        // Ensure the latest data is loaded
        loadViewerDonationData();

        // Add new data
        viewerDonationDataMap.putViewerDonationData(twitchUserId, viewerDonationData);

        // Save the modified data back to the file
        saveViewerDonationData();
    }

    public synchronized static void loadViewerDonationData() {
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
            viewerDonationDataMap = new Gson().fromJson(reader, ViewerDonationDataMap.class);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            viewerDonationDataMap = new ViewerDonationDataMap();
            saveViewerDonationData();
        }
    }

    public synchronized static void saveViewerDonationData() {
        File tempFile = new File(getViewerDonationDataFile().getPath() + ".tmp");
        try (FileWriter writer = new FileWriter(tempFile)) {
            new Gson().toJson(viewerDonationDataMap, writer);
            Files.move(tempFile.toPath(), getViewerDonationDataFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getViewerDonationDataFile() {
        return new File(FabricLoader.getInstance().getConfigDir().toFile(), VIEWER_DONATION_DATA_FILE_NAME);
    }
}
