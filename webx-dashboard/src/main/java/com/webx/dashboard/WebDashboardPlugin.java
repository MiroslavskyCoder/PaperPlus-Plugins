package com.webx.dashboard;

import com.webx.dashboard.api.WebApiServer;
import com.webx.dashboard.config.DashboardConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class WebDashboardPlugin extends JavaPlugin {
    private static WebDashboardPlugin instance;
    private WebApiServer apiServer;
    private DashboardConfig dashboardConfig;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize configuration
        dashboardConfig = new DashboardConfig(this);
        
        // Start web server
        int port = getConfig().getInt("api.port", 8080);
        String host = getConfig().getString("api.host", "0.0.0.0");
        
        try {
            apiServer = new WebApiServer(this, host, port);
            apiServer.start();
            
            getLogger().info("Web Dashboard API started on " + host + ":" + port);
            getLogger().info("Access dashboard at: http://localhost:" + port + "/dashboard");
        } catch (Exception e) {
            getLogger().severe("Failed to start Web API server: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        if (apiServer != null) {
            apiServer.stop();
            getLogger().info("Web Dashboard API stopped");
        }
    }
    
    public static WebDashboardPlugin getInstance() {
        return instance;
    }
    
    public DashboardConfig getDashboardConfig() {
        return dashboardConfig;
    }
}
