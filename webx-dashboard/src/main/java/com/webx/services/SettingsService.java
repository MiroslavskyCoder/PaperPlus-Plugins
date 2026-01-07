package com.webx.services;

import com.webx.api.models.SettingsConfig;
import com.webx.api.models.SettingsConfig.RedisConfig;
import com.webx.api.models.SettingsConfig.SQLConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SettingsService {
    
    private final JavaPlugin plugin;
    private final ObjectMapper objectMapper;
    private final File settingsFile;
    private SettingsConfig config;
    
    public SettingsService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.objectMapper = new ObjectMapper();
        this.settingsFile = new File(plugin.getDataFolder(), "settings.json");
        
        // Ensure data folder exists
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        loadSettings();
    }
    
    public void loadSettings() {
        if (settingsFile.exists()) {
            try {
                String content = new String(Files.readAllBytes(settingsFile.toPath()));
                this.config = objectMapper.readValue(content, SettingsConfig.class);
                plugin.getLogger().info("Settings loaded from file");
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to load settings: " + e.getMessage());
                this.config = new SettingsConfig();
                saveSettings();
            }
        } else {
            this.config = new SettingsConfig();
            saveSettings();
        }
    }
    
    public void saveSettings() {
        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(config);
            Files.write(settingsFile.toPath(), json.getBytes());
            plugin.getLogger().info("Settings saved successfully");
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save settings: " + e.getMessage());
        }
    }
    
    public SettingsConfig getSettings() {
        return config;
    }
    
    public void updateSettings(SettingsConfig newConfig) {
        this.config = newConfig;
        saveSettings();
    }
    
    public boolean testSQLConnection(SQLConfig sqlConfig) {
        try {
            Class.forName("org.postgresql.Driver");
            String url = String.format("jdbc:postgresql://%s:%d/%s?sslmode=%s",
                    sqlConfig.host,
                    sqlConfig.port,
                    sqlConfig.database,
                    sqlConfig.ssl ? "require" : "disable"
            );
            
            java.sql.Connection conn = java.sql.DriverManager.getConnection(
                    url,
                    sqlConfig.username,
                    sqlConfig.password
            );
            
            if (conn != null) {
                conn.close();
                return true;
            }
        } catch (ClassNotFoundException e) {
            plugin.getLogger().warning("PostgreSQL driver not found: " + e.getMessage());
        } catch (Exception e) {
            plugin.getLogger().warning("SQL connection test failed: " + e.getMessage());
        }
        return false;
    }
    
    public boolean testRedisConnection(RedisConfig redisConfig) {
        try {
            redis.clients.jedis.Jedis jedis = new redis.clients.jedis.Jedis(
                    redisConfig.host,
                    redisConfig.port,
                    3000
            );
            
            if (!redisConfig.password.isEmpty()) {
                jedis.auth(redisConfig.password);
            }
            
            jedis.select(redisConfig.db);
            String pong = jedis.ping();
            jedis.close();
            
            return "PONG".equals(pong);
        } catch (Exception e) {
            plugin.getLogger().warning("Redis connection test failed: " + e.getMessage());
        }
        return false;
    }
}
