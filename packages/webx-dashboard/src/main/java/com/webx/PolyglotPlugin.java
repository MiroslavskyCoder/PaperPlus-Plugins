package com.webx;

import com.webx.player.AuthManager;
import com.webx.player.PlayerProfile;
import com.webx.services.DatabaseManager;
import com.webx.services.RedisManager;
import com.webx.services.SystemMonitorService;
import com.webx.services.SettingsService;
import com.webx.api.models.SettingsConfig;
import com.webx.api.RouterProvider;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin; 
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PolyglotPlugin extends JavaPlugin implements Listener {

    private SystemMonitorService monitorService;
    private SettingsService settingsService;
    private RedisManager redisManager;
    private DatabaseManager dbManager;
    private AuthManager authManager;
    private RouterProvider routerProvider;

    private final Map<UUID, PlayerProfile> onlineProfiles = new HashMap<>();

    public SettingsService getSettingsService() {
        return settingsService;
    }

    public DatabaseManager getDatabaseManager() {
        return dbManager;
    }

    public RedisManager getRedisManager() {
        return redisManager;
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        
        // Load settings to check if Auth Player is enabled
        settingsService = new SettingsService(this);
        SettingsConfig settings = settingsService.getSettings();
        
        // Initialize Redis, Database and AuthManager only if Auth Player is enabled
        if (settings.authPlayer.isAuthPlayerEnabled) {
            getLogger().info("Auth Player enabled - initializing Redis and Database...");
            
            String redisHost = getConfig().getString("Redis.Host", "localhost");
            int redisPort = getConfig().getInt("Redis.Port", 6379);
            String redisPass = getConfig().getString("Redis.Password", "");
            
            try {
                redisManager = new RedisManager(this, redisHost, redisPort, redisPass);
            } catch (Exception e) {
                getLogger().warning("Failed to initialize Redis: " + e.getMessage());
                getLogger().warning("Auth Player will not work properly without Redis.");
            }
            
            String dbHost = getConfig().getString("PostgreSQL.Host", "localhost");
            int dbPort = getConfig().getInt("PostgreSQL.Port", 5432);
            String dbName = getConfig().getString("PostgreSQL.Database", "minecraft");
            String dbUser = getConfig().getString("PostgreSQL.User", "user");
            String dbPass = getConfig().getString("PostgreSQL.Password", "password");
            
            try {
                dbManager = new DatabaseManager(this, dbHost, dbPort, dbName, dbUser, dbPass);
            } catch (Exception e) {
                getLogger().warning("Failed to initialize Database: " + e.getMessage());
                getLogger().warning("Auth Player will not work properly without Database.");
            }
            
            if (redisManager != null && dbManager != null) {
                authManager = new AuthManager(redisManager, dbManager);
                getLogger().info("Auth Player system initialized successfully.");
            } else {
                getLogger().warning("Auth Player system not initialized due to connection errors.");
            }
        } else {
            getLogger().info("Auth Player disabled - skipping Redis and Database initialization.");
        }
        
        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("WebxDashboard Plugin Enabled.");

        // Start monitoring service
        monitorService = new SystemMonitorService(this);
        monitorService.startMonitoring();

        // Start Web Server for API and WebSocket
        routerProvider = new RouterProvider(this);
        getLogger().info("Web Server API/WebSocket started on port 9092");
    }

    @Override
    public void onDisable() { 
        if (dbManager != null) {
            getLogger().info("Saving profiles to PostgreSQL...");
            for (PlayerProfile profile : onlineProfiles.values()) {
                profile.saveToDatabase(this.dbManager);
            }
        }
        onlineProfiles.clear();
 
        if (redisManager != null) {
            redisManager.shutdown();
        }
        if (dbManager != null) {
            dbManager.shutdown();
        }
        if (routerProvider != null) {
            routerProvider.stopWebServer();
        }
    } 

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        org.bukkit.entity.Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        // Skip auth check if Auth Player is disabled
        if (authManager != null && authManager.isAuthorized(player)) {
            getLogger().info(player.getName() + " Find authorized profile. Loading data..."); 
            
            PlayerProfile profile = new PlayerProfile(player); 
            profile.setAuthenticated(true);
            onlineProfiles.put(uuid, profile);
            
            player.teleport(new Location(player.getWorld(), profile.x, profile.y, profile.z, profile.yaw, profile.pitch));
            
        } else if (authManager != null) { 
            getLogger().info(player.getName() + " Required authorization. Waiting for successful login.");
            PlayerProfile profile = new PlayerProfile(player);
            onlineProfiles.put(uuid, profile);
        } else {
            // Auth Player disabled - allow direct join
            PlayerProfile profile = new PlayerProfile(player);
            profile.setAuthenticated(true);
            onlineProfiles.put(uuid, profile);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        
        PlayerProfile profile = onlineProfiles.remove(uuid);
        
        if (profile != null) { 
            if (dbManager != null) {
                profile.saveToDatabase(this.dbManager);
            }
            if (authManager != null) {
                authManager.clearAuthorization(event.getPlayer());
            }
        }
    }
}