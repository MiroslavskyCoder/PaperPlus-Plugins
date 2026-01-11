package com.webx.api.endpoints;

import com.google.gson.Gson;
import io.javalin.http.Context;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * AFK System Management API Endpoint
 * Allows configuring AFK settings through Web Dashboard
 */
public class AfkEndpoint {
    private final JavaPlugin plugin;
    private final Gson gson;
    private final Path afkConfigFile;
    
    public AfkEndpoint(JavaPlugin plugin, Gson gson) {
        this.plugin = plugin;
        this.gson = gson;
        
        // AFK config file in AFK plugin folder
        File afkPluginFolder = new File(plugin.getDataFolder().getParentFile(), "AFK");
        afkPluginFolder.mkdirs();
        this.afkConfigFile = afkPluginFolder.toPath().resolve("afk.json");
        
        // Create default AFK config if not exists
        if (!Files.exists(afkConfigFile)) {
            createDefaultAfkConfig();
        }
    }
    
    private void createDefaultAfkConfig() {
        AfkConfig defaultConfig = new AfkConfig();
        defaultConfig.timeout = 10;
        defaultConfig.kickEnabled = false;
        defaultConfig.kickTimeout = 30;
        defaultConfig.prefix = "§7[AFK] ";
        defaultConfig.suffix = "";
        defaultConfig.broadcastEnabled = true;
        defaultConfig.autoResumeEnabled = true;
        
        saveAfkConfig(defaultConfig);
        plugin.getLogger().info("Created default AFK configuration");
    }
    
    /**
     * GET /api/afk
     * Get AFK configuration
     */
    public void getAfkConfig(Context ctx) {
        AfkConfig config = loadAfkConfig();
        ctx.json(Map.of(
            "success", true,
            "message", "Success",
            "data", config
        ));
    }
    
    /**
     * PUT /api/afk
     * Update AFK configuration
     */
    public void updateAfkConfig(Context ctx) {
        try {
            AfkConfig newConfig = gson.fromJson(ctx.body(), AfkConfig.class);
            
            // Validate
            if (newConfig.timeout < 1) {
                ctx.status(400).json(Map.of(
                    "success", false,
                    "message", "Timeout must be at least 1 minute"
                ));
                return;
            }
            
            if (newConfig.kickEnabled && newConfig.kickTimeout < 1) {
                ctx.status(400).json(Map.of(
                    "success", false,
                    "message", "Kick timeout must be at least 1 minute"
                ));
                return;
            }
            
            saveAfkConfig(newConfig);
            
            ctx.json(Map.of(
                "success", true,
                "message", "AFK configuration updated successfully",
                "data", newConfig
            ));
            
            plugin.getLogger().info("Updated AFK configuration: timeout=" + newConfig.timeout + 
                                  ", kickEnabled=" + newConfig.kickEnabled);
            
        } catch (Exception e) {
            ctx.status(400).json(Map.of(
                "success", false,
                "message", "Invalid configuration data: " + e.getMessage()
            ));
        }
    }
    
    /**
     * GET /api/afk/players
     * Get list of AFK players
     */
    public void getAfkPlayers(Context ctx) {
        // This would require integration with AFK plugin
        // For now, return empty list
        ctx.json(Map.of(
            "success", true,
            "message", "Success",
            "data", new Object[]{}
        ));
    }
    
    private AfkConfig loadAfkConfig() {
        try (Reader reader = new FileReader(afkConfigFile.toFile())) {
            AfkConfig config = gson.fromJson(reader, AfkConfig.class);
            return config != null ? config : createDefaultConfig();
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to load AFK config: " + e.getMessage());
            return createDefaultConfig();
        }
    }
    
    private AfkConfig createDefaultConfig() {
        AfkConfig config = new AfkConfig();
        config.timeout = 10;
        config.kickEnabled = false;
        config.kickTimeout = 30;
        config.prefix = "§7[AFK] ";
        config.suffix = "";
        config.broadcastEnabled = true;
        config.autoResumeEnabled = true;
        return config;
    }
    
    private void saveAfkConfig(AfkConfig config) {
        try (Writer writer = new FileWriter(afkConfigFile.toFile())) {
            gson.toJson(config, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save AFK config: " + e.getMessage());
        }
    }
    
    public static class AfkConfig {
        public int timeout;              // Minutes until AFK
        public boolean kickEnabled;       // Whether to kick AFK players
        public int kickTimeout;          // Minutes until kick after AFK
        public String prefix;            // Chat/tab prefix for AFK players
        public String suffix;            // Chat/tab suffix for AFK players
        public boolean broadcastEnabled; // Broadcast AFK status
        public boolean autoResumeEnabled; // Auto-resume when player moves
    }
}
