package com.webx.shared;

import org.bukkit.entity.Player;
import com.google.gson.*;
import java.util.*;

/**
 * Helper utility for plugins to easily work with SharedPluginDatabase
 * Provides convenience methods for common operations
 */
public class PluginDataHelper {
    private final String pluginName;
    private final SharedPluginDatabase db;
    
    public PluginDataHelper(String pluginName) {
        this.pluginName = pluginName;
        this.db = SharedPluginDatabase.getInstance();
    }
    
    // ========== Plugin-level Operations ==========
    
    public void setString(String key, String value) {
        db.setPluginValue(pluginName, key, new JsonPrimitive(value));
    }
    
    public String getString(String key) {
        JsonElement el = db.getPluginValue(pluginName, key);
        return el != null ? el.getAsString() : null;
    }
    
    public void setInt(String key, int value) {
        db.setPluginValue(pluginName, key, new JsonPrimitive(value));
    }
    
    public int getInt(String key) {
        JsonElement el = db.getPluginValue(pluginName, key);
        return el != null ? el.getAsInt() : 0;
    }
    
    public void setDouble(String key, double value) {
        db.setPluginValue(pluginName, key, new JsonPrimitive(value));
    }
    
    public double getDouble(String key) {
        JsonElement el = db.getPluginValue(pluginName, key);
        return el != null ? el.getAsDouble() : 0.0;
    }
    
    public void setBoolean(String key, boolean value) {
        db.setPluginValue(pluginName, key, new JsonPrimitive(value));
    }
    
    public boolean getBoolean(String key) {
        JsonElement el = db.getPluginValue(pluginName, key);
        return el != null && el.getAsBoolean();
    }
    
    public void setObject(String key, JsonObject obj) {
        db.setPluginValue(pluginName, key, obj);
    }
    
    public JsonObject getObject(String key) {
        JsonElement el = db.getPluginValue(pluginName, key);
        return el != null && el.isJsonObject() ? el.getAsJsonObject() : null;
    }
    
    public void setArray(String key, JsonArray arr) {
        db.setPluginValue(pluginName, key, arr);
    }
    
    public JsonArray getArray(String key) {
        JsonElement el = db.getPluginValue(pluginName, key);
        return el != null && el.isJsonArray() ? el.getAsJsonArray() : null;
    }
    
    // ========== Player-level Operations ==========
    
    public void setPlayerString(String playerUUID, String key, String value) {
        db.setPlayerValue(playerUUID, pluginName + "_" + key, new JsonPrimitive(value));
    }
    
    public String getPlayerString(String playerUUID, String key) {
        JsonElement el = db.getPlayerValue(playerUUID, pluginName + "_" + key);
        return el != null ? el.getAsString() : null;
    }
    
    public void setPlayerInt(String playerUUID, String key, int value) {
        db.setPlayerValue(playerUUID, pluginName + "_" + key, new JsonPrimitive(value));
    }
    
    public int getPlayerInt(String playerUUID, String key) {
        JsonElement el = db.getPlayerValue(playerUUID, pluginName + "_" + key);
        return el != null ? el.getAsInt() : 0;
    }
    
    public void setPlayerDouble(String playerUUID, String key, double value) {
        db.setPlayerValue(playerUUID, pluginName + "_" + key, new JsonPrimitive(value));
    }
    
    public double getPlayerDouble(String playerUUID, String key) {
        JsonElement el = db.getPlayerValue(playerUUID, pluginName + "_" + key);
        return el != null ? el.getAsDouble() : 0.0;
    }
    
    public void setPlayerObject(String playerUUID, String key, JsonObject obj) {
        JsonObject playerData = new JsonObject();
        playerData.add(pluginName + "_" + key, obj);
        db.getPlayerData(playerUUID).add(pluginName + "_" + key, obj);
    }
    
    public JsonObject getPlayerObject(String playerUUID, String key) {
        JsonElement el = db.getPlayerValue(playerUUID, pluginName + "_" + key);
        return el != null && el.isJsonObject() ? el.getAsJsonObject() : null;
    }
    
    public JsonObject getPlayerData(String playerUUID) {
        JsonObject allData = db.getPlayerData(playerUUID);
        JsonObject pluginData = new JsonObject();
        String prefix = pluginName + "_";
        
        for (String k : allData.keySet()) {
            if (k.startsWith(prefix)) {
                pluginData.add(k.substring(prefix.length()), allData.get(k));
            }
        }
        return pluginData;
    }
    
    // ========== Global Operations ==========
    
    public void setGlobalString(String key, String value) {
        db.setGlobalValue(pluginName + "_" + key, new JsonPrimitive(value));
    }
    
    public String getGlobalString(String key) {
        JsonElement el = db.getGlobalValue(pluginName + "_" + key);
        return el != null ? el.getAsString() : null;
    }
    
    public void setGlobalInt(String key, int value) {
        db.setGlobalValue(pluginName + "_" + key, new JsonPrimitive(value));
    }
    
    public int getGlobalInt(String key) {
        JsonElement el = db.getGlobalValue(pluginName + "_" + key);
        return el != null ? el.getAsInt() : 0;
    }
    
    // ========== Database Management ==========
    
    public void save() {
        db.save();
    }
    
    public void reload() {
        db.reload();
    }
    
    public String getDatabasePath() {
        return db.getDatabasePath();
    }
    
    /**
     * Get all plugin-specific data
     */
    public JsonObject getPluginAllData() {
        return db.getPluginData(pluginName);
    }
}
