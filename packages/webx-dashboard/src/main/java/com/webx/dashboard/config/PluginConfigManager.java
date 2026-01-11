package com.webx.dashboard.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PluginConfigManager {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_DIR = new File("plugins/WebX/configs");
    
    static {
        if (!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs();
        }
    }
    
    public static <T> T loadConfig(String pluginName, Class<T> configClass) {
        File configFile = new File(CONFIG_DIR, pluginName + ".json");
        
        if (!configFile.exists()) {
            try {
                T defaultConfig = configClass.getDeclaredConstructor().newInstance();
                saveConfig(pluginName, defaultConfig);
                return defaultConfig;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        
        try (FileReader reader = new FileReader(configFile)) {
            return GSON.fromJson(reader, configClass);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static <T> void saveConfig(String pluginName, T config) {
        File configFile = new File(CONFIG_DIR, pluginName + ".json");
        
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Map<String, Object> loadConfigAsMap(String pluginName) {
        File configFile = new File(CONFIG_DIR, pluginName + ".json");
        
        if (!configFile.exists()) {
            return new HashMap<>();
        }
        
        try (FileReader reader = new FileReader(configFile)) {
            return GSON.fromJson(reader, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    
    public static void saveConfigFromMap(String pluginName, Map<String, Object> config) {
        File configFile = new File(CONFIG_DIR, pluginName + ".json");
        
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
