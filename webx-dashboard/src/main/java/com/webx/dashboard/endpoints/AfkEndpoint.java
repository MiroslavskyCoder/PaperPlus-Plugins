package com.webx.dashboard.endpoints;

import com.google.gson.Gson;
import com.webx.dashboard.WebDashboardPlugin;
import com.webx.dashboard.api.WebApiServer.Response;
import io.javalin.http.Context;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AfkEndpoint {
    private final WebDashboardPlugin plugin;
    private final Gson gson;
    private final Path afkConfigFile;
    
    public AfkEndpoint(WebDashboardPlugin plugin, Gson gson) {
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
        defaultConfig.prefix = "§7[AFK] ";
        defaultConfig.suffix = "";
        
        saveAfkConfig(defaultConfig);
    }
    
    public void getAfkConfig(Context ctx) {
        AfkConfig config = loadAfkConfig();
        ctx.json(new Response(true, "Success", config));
    }
    
    public void updateAfkConfig(Context ctx) {
        try {
            AfkConfig newConfig = gson.fromJson(ctx.body(), AfkConfig.class);
            
            // Validate
            if (newConfig.timeout < 1) {
                ctx.status(400).json(new Response(false, "Timeout must be at least 1 minute"));
                return;
            }
            
            saveAfkConfig(newConfig);
            
            ctx.json(new Response(true, "AFK configuration updated successfully", newConfig));
        } catch (Exception e) {
            ctx.status(400).json(new Response(false, "Invalid configuration data: " + e.getMessage()));
        }
    }
    
    private AfkConfig loadAfkConfig() {
        try (Reader reader = new FileReader(afkConfigFile.toFile())) {
            return gson.fromJson(reader, AfkConfig.class);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to load AFK config: " + e.getMessage());
            AfkConfig defaultConfig = new AfkConfig();
            defaultConfig.timeout = 10;
            defaultConfig.kickEnabled = false;
            defaultConfig.prefix = "§7[AFK] ";
            return defaultConfig;
        }
    }
    
    private void saveAfkConfig(AfkConfig config) {
        try (Writer writer = new FileWriter(afkConfigFile.toFile())) {
            gson.toJson(config, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save AFK config: " + e.getMessage());
        }
    }
    
    public static class AfkConfig {
        public int timeout;
        public boolean kickEnabled;
        public String prefix;
        public String suffix;
    }
}
