package com.webx.shared;

import com.google.gson.*;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Shared database for all plugins - centralized JSON storage at {CACHE}/lxxv_plugins_server.json
 * Provides thread-safe access to plugin data with automatic persistence
 */
public class SharedPluginDatabase {
    private static final String CACHE_DIR_PROPERTY = "lxxv.cache.dir";
    private static final String DEFAULT_CACHE = "./cache";
    private static final String DATABASE_FILENAME = "lxxv_plugins_server.json";
    
    private static SharedPluginDatabase instance;
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    private JsonObject rootData;
    private Path databasePath;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    
    private SharedPluginDatabase() {
        initializeDatabase();
    }
    
    /**
     * Get singleton instance of SharedPluginDatabase
     */
    public static SharedPluginDatabase getInstance() {
        if (instance == null) {
            synchronized (SharedPluginDatabase.class) {
                if (instance == null) {
                    instance = new SharedPluginDatabase();
                }
            }
        }
        return instance;
    }
    
    /**
     * Initialize or load the database
     */
    private void initializeDatabase() {
        try {
            String cacheDir = System.getProperty(CACHE_DIR_PROPERTY, DEFAULT_CACHE);
            Path cachePath = Paths.get(cacheDir);
            
            // Create cache directory if it doesn't exist
            if (!Files.exists(cachePath)) {
                Files.createDirectories(cachePath);
            }
            
            databasePath = cachePath.resolve(DATABASE_FILENAME);
            
            // Load existing database or create new one
            if (Files.exists(databasePath)) {
                String content = new String(Files.readAllBytes(databasePath), StandardCharsets.UTF_8);
                rootData = JsonParser.parseString(content).getAsJsonObject();
            } else {
                rootData = new JsonObject();
                rootData.add("plugins", new JsonObject());
                rootData.add("players", new JsonObject());
                rootData.add("global", new JsonObject());
                rootData.add("timestamp", new JsonPrimitive(System.currentTimeMillis()));
                save();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize shared database", e);
        }
    }
    
    /**
     * Get or create plugin data section
     */
    public JsonObject getPluginData(String pluginName) {
        lock.readLock().lock();
        try {
            JsonObject plugins = rootData.getAsJsonObject("plugins");
            if (!plugins.has(pluginName)) {
                lock.readLock().unlock();
                lock.writeLock().lock();
                try {
                    if (!plugins.has(pluginName)) {
                        plugins.add(pluginName, new JsonObject());
                        save();
                    }
                    return plugins.getAsJsonObject(pluginName);
                } finally {
                    lock.writeLock().unlock();
                    lock.readLock().lock();
                }
            }
            return plugins.getAsJsonObject(pluginName);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Get or create player data section
     */
    public JsonObject getPlayerData(String playerUUID) {
        lock.readLock().lock();
        try {
            JsonObject players = rootData.getAsJsonObject("players");
            if (!players.has(playerUUID)) {
                lock.readLock().unlock();
                lock.writeLock().lock();
                try {
                    if (!players.has(playerUUID)) {
                        players.add(playerUUID, new JsonObject());
                        save();
                    }
                    return players.getAsJsonObject(playerUUID);
                } finally {
                    lock.writeLock().unlock();
                    lock.readLock().lock();
                }
            }
            return players.getAsJsonObject(playerUUID);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Get global data section (shared across all plugins)
     */
    public JsonObject getGlobalData() {
        lock.readLock().lock();
        try {
            return rootData.getAsJsonObject("global");
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Set value in plugin data
     */
    public void setPluginValue(String pluginName, String key, JsonElement value) {
        lock.writeLock().lock();
        try {
            JsonObject pluginData = getPluginData(pluginName);
            pluginData.add(key, value);
            save();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Get value from plugin data
     */
    public JsonElement getPluginValue(String pluginName, String key) {
        lock.readLock().lock();
        try {
            JsonObject pluginData = getPluginData(pluginName);
            return pluginData.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Set value in player data
     */
    public void setPlayerValue(String playerUUID, String key, JsonElement value) {
        lock.writeLock().lock();
        try {
            JsonObject playerData = getPlayerData(playerUUID);
            playerData.add(key, value);
            save();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Get value from player data
     */
    public JsonElement getPlayerValue(String playerUUID, String key) {
        lock.readLock().lock();
        try {
            JsonObject playerData = getPlayerData(playerUUID);
            return playerData.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Set value in global data
     */
    public void setGlobalValue(String key, JsonElement value) {
        lock.writeLock().lock();
        try {
            JsonObject globalData = getGlobalData();
            globalData.add(key, value);
            save();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Get value from global data
     */
    public JsonElement getGlobalValue(String key) {
        lock.readLock().lock();
        try {
            return getGlobalData().get(key);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Save database to disk
     */
    public void save() {
        lock.writeLock().lock();
        try {
            rootData.addProperty("timestamp", System.currentTimeMillis());
            String jsonContent = gson.toJson(rootData);
            Files.write(databasePath, jsonContent.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save shared database", e);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Reload database from disk
     */
    public void reload() {
        lock.writeLock().lock();
        try {
            initializeDatabase();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Get database file path
     */
    public String getDatabasePath() {
        return databasePath.toString();
    }
    
    /**
     * Get entire root data (for exports/backups)
     */
    public JsonObject getAllData() {
        lock.readLock().lock();
        try {
            return rootData.deepCopy();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Clear all data and reinitialize
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            rootData = new JsonObject();
            rootData.add("plugins", new JsonObject());
            rootData.add("players", new JsonObject());
            rootData.add("global", new JsonObject());
            rootData.add("timestamp", new JsonPrimitive(System.currentTimeMillis()));
            save();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
