package com.webx.horrorenginex;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Manages configuration for HorrorEngineX
 */
public class HorrorConfigManager {
    
    private final HorrorEngineXPlugin plugin;
    private FileConfiguration config;
    
    private boolean horrorEventsEnabled = true;
    private boolean soundEffectsEnabled = true;
    private boolean atmosphericEffectsEnabled = true;
    private boolean joinMessageEnabled = true;
    
    public HorrorConfigManager(HorrorEngineXPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    
    /**
     * Load configuration from file
     */
    public void loadConfig() {
        // Load values from config.yml
        horrorEventsEnabled = config.getBoolean("horror-events.enabled", true);
        soundEffectsEnabled = config.getBoolean("effects.sounds.enabled", true);
        atmosphericEffectsEnabled = config.getBoolean("effects.atmospheric.enabled", true);
        joinMessageEnabled = config.getBoolean("messages.join-message", true);
        
        plugin.getLogger().info("Configuration loaded successfully");
    }
    
    /**
     * Save current configuration
     */
    public void saveConfig() {
        config.set("horror-events.enabled", horrorEventsEnabled);
        config.set("effects.sounds.enabled", soundEffectsEnabled);
        config.set("effects.atmospheric.enabled", atmosphericEffectsEnabled);
        config.set("messages.join-message", joinMessageEnabled);
        plugin.saveConfig();
    }
    
    // Getters
    public boolean isHorrorEventsEnabled() {
        return horrorEventsEnabled;
    }
    
    public boolean isSoundEffectsEnabled() {
        return soundEffectsEnabled;
    }
    
    public boolean isAtmosphericEffectsEnabled() {
        return atmosphericEffectsEnabled;
    }
    
    public boolean isJoinMessageEnabled() {
        return joinMessageEnabled;
    }
    
    public FileConfiguration getConfig() {
        return config;
    }
    
    // Setters
    public void setHorrorEventsEnabled(boolean enabled) {
        horrorEventsEnabled = enabled;
        config.set("horror-events.enabled", enabled);
    }
    
    public void setSoundEffectsEnabled(boolean enabled) {
        soundEffectsEnabled = enabled;
        config.set("effects.sounds.enabled", enabled);
    }
    
    public void setAtmosphericEffectsEnabled(boolean enabled) {
        atmosphericEffectsEnabled = enabled;
        config.set("effects.atmospheric.enabled", enabled);
    }
    
    public void setJoinMessageEnabled(boolean enabled) {
        joinMessageEnabled = enabled;
        config.set("messages.join-message", enabled);
    }
}
