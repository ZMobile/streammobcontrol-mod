package com.blockafeller.config;

import com.blockafeller.util.gson.LocalDateTimeTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;

public class ConfigManager {
    private static final String CONFIG_FILE_NAME = "mob_control_mod_config.json";
    private static ModConfig config;
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new LocalDateTimeTypeAdapterFactory())
            .create();

    public static void loadConfig() {
        File configFile = getConfigFile();

        if (!configFile.exists()) {
            // Copy default config from resources
            try (InputStream in = ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
                if (in != null) {
                    Files.copy(in, configFile.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Load the config
        try (FileReader reader = new FileReader(configFile)) {
            config = gson.fromJson(reader, ModConfig.class);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            config = new ModConfig();
            saveConfig();
        }
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(getConfigFile())) {
            gson.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ModConfig getConfig() {
        return config;
    }

    private static File getConfigFile() {
        return new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_FILE_NAME);
    }
}
