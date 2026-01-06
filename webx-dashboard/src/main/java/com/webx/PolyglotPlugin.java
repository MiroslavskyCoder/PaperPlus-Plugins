package com.webx;

import com.webx.player.AuthManager;
import com.webx.player.PlayerProfile;
import com.webx.services.DatabaseManager;
import com.webx.services.RedisManager;
import com.webx.services.SystemMonitorService;

import org.bukkit.Location;
import org.bukkit.World;
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
    private RedisManager redisManager;
    private DatabaseManager dbManager;
    private AuthManager authManager;

    private final Map<UUID, PlayerProfile> onlineProfiles = new HashMap<>();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        
        String redisHost = getConfig().getString("Redis.Host", "localhost");
        int redisPort = getConfig().getInt("Redis.Port", 6379);
        String redisPass = getConfig().getString("Redis.Password", "");
        
        redisManager = new RedisManager(this, redisHost, redisPort, redisPass);
        
        String dbHost = getConfig().getString("PostgreSQL.Host", "localhost");
        int dbPort = getConfig().getInt("PostgreSQL.Port", 5432);
        String dbName = getConfig().getString("PostgreSQL.Database", "minecraft");
        String dbUser = getConfig().getString("PostgreSQL.User", "user");
        String dbPass = getConfig().getString("PostgreSQL.Password", "password");
        
        dbManager = new DatabaseManager(this, dbHost, dbPort, dbName, dbUser, dbPass);
        
        authManager = new AuthManager(redisManager);
        
        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("WebxDashboard Plugin Enabled.");


        monitorService = new SystemMonitorService(this);
        monitorService.startMonitoring();
    }

    @Override
    public void onDisable() { 
        getLogger().info("Saving profiles to PostgreSQL...");
        for (PlayerProfile profile : onlineProfiles.values()) {
            profile.saveToDatabase(this.dbManager);
        }
        onlineProfiles.clear();
 
        if (redisManager != null) {
            redisManager.shutdown();
        }
        if (dbManager != null) {
            dbManager.shutdown();
        }
    } 

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        org.bukkit.entity.Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        if (authManager.isAuthorized(player)) {
            getLogger().info(player.getName() + " Find authorized profile. Loading data..."); 
            
            PlayerProfile profile = new PlayerProfile(player); 
            profile.setAuthenticated(true);
            onlineProfiles.put(uuid, profile);
            
            player.teleport(new Location(player.getWorld(), profile.x, profile.y, profile.z, profile.yaw, profile.pitch));
            
        } else { 
            getLogger().info(player.getName() + " Required authorization. Waiting for successful login.");
            PlayerProfile profile = new PlayerProfile(player);
            onlineProfiles.put(uuid, profile);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        
        PlayerProfile profile = onlineProfiles.remove(uuid);
        
        if (profile != null) { 
            profile.saveToDatabase(this.dbManager); 
            authManager.clearAuthorization(event.getPlayer()); 
        }
    }
}