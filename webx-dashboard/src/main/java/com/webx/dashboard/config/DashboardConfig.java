package com.webx.dashboard.config;

import com.webx.dashboard.WebDashboardPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class DashboardConfig {
    private final WebDashboardPlugin plugin;
    private final FileConfiguration config;
    
    public DashboardConfig(WebDashboardPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        
        // Set defaults
        config.addDefault("api.port", 8080);
        config.addDefault("api.host", "0.0.0.0");
        config.addDefault("api.auth.enabled", false);
        config.addDefault("api.auth.key", "change-me");
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }
    
    public int getPort() {
        return config.getInt("api.port", 8080);
    }
    
    public String getHost() {
        return config.getString("api.host", "0.0.0.0");
    }
    
    public boolean isAuthEnabled() {
        return config.getBoolean("api.auth.enabled", false);
    }
    
    public String getAuthKey() {
        return config.getString("api.auth.key", "change-me");
    }
}
