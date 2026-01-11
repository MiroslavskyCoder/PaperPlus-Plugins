package com.webx.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * REST API service for AFK plugin configuration
 * Manages afk.json file through web dashboard
 */
public class AfkService {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Path AFK_CONFIG_PATH = Paths.get("plugins/AFK/afk.json");
    
    /**
     * GET /api/afk/config
     * Returns current AFK configuration
     */
    public static void getAfkConfig(Context ctx) {
        try {
            if (!Files.exists(AFK_CONFIG_PATH)) {
                // Create default config
                AfkConfig defaultConfig = new AfkConfig();
                saveConfig(defaultConfig);
            }
            
            String content = Files.readString(AFK_CONFIG_PATH);
            AfkConfig config = mapper.readValue(content, AfkConfig.class);
            
            ctx.json(Map.of(
                "success", true,
                "config", config
            ));
            
        } catch (IOException e) {
            ctx.status(500).json(Map.of(
                "error", "Failed to read AFK config: " + e.getMessage(),
                "success", false
            ));
        }
    }
    
    /**
     * PUT /api/afk/config
     * Updates AFK configuration
     */
    public static void updateAfkConfig(Context ctx) {
        try {
            AfkConfig config = ctx.bodyAsClass(AfkConfig.class);
            
            // Validate
            if (config.timeoutMinutes <= 0) {
                ctx.status(400).json(Map.of(
                    "error", "Timeout must be greater than 0",
                    "success", false
                ));
                return;
            }
            
            // Save
            saveConfig(config);
            
            ctx.json(Map.of(
                "success", true,
                "message", "AFK config updated successfully"
            ));
            
        } catch (IOException e) {
            ctx.status(500).json(Map.of(
                "error", "Failed to update AFK config: " + e.getMessage(),
                "success", false
            ));
        }
    }
    
    private static void saveConfig(AfkConfig config) throws IOException {
        Files.createDirectories(AFK_CONFIG_PATH.getParent());
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(config);
        Files.writeString(AFK_CONFIG_PATH, json);
    }
    
    /**
     * AFK configuration data model
     */
    public static class AfkConfig {
        public int timeoutMinutes = 5;
        public boolean kickEnabled = false;
        public String prefix = "§7[AFK] §r";
        public String suffix = "";
        public boolean showInTab = true;
        public boolean preventDamage = true;
        public boolean freezePlayer = true;
        public String afkMessage = "§7%player% is now AFK";
        public String backMessage = "§7%player% is no longer AFK";
        
        public AfkConfig() {}
    }
}
