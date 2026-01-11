package com.webx.mobcatch;

import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MobCatchPlugin extends JavaPlugin {
    
    private boolean enabled;
    private boolean requirePermission;
    private String permission;
    private Set<EntityType> allowedMobs;
    private Set<EntityType> blacklistedMobs;
    private Map<String, String> messages;
    private String eggNameFormat;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfiguration();
        
        getServer().getPluginManager().registerEvents(new MobCatchListener(this), this);
        
        getLogger().info("MobCatch plugin enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("MobCatch plugin disabled!");
    }
    
    public void loadConfiguration() {
        reloadConfig();
        enabled = getConfig().getBoolean("enabled", true);
        requirePermission = getConfig().getBoolean("require-permission", true);
        permission = getConfig().getString("permission", "mobcatch.use");
        eggNameFormat = getConfig().getString("egg-name", "§6Captured %mob%");
        
        allowedMobs = new HashSet<>();
        for (String mobName : getConfig().getStringList("allowed-mobs")) {
            try {
                allowedMobs.add(EntityType.valueOf(mobName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                getLogger().warning("Unknown mob type: " + mobName);
            }
        }
        
        blacklistedMobs = new HashSet<>();
        for (String mobName : getConfig().getStringList("blacklisted-mobs")) {
            try {
                blacklistedMobs.add(EntityType.valueOf(mobName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                getLogger().warning("Unknown mob type: " + mobName);
            }
        }
        
        messages = new HashMap<>();
        if (getConfig().contains("messages")) {
            for (String key : getConfig().getConfigurationSection("messages").getKeys(false)) {
                messages.put(key, getConfig().getString("messages." + key));
            }
        }
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public boolean isRequirePermission() {
        return requirePermission;
    }
    
    public String getPermission() {
        return permission;
    }
    
    public Set<EntityType> getAllowedMobs() {
        return allowedMobs;
    }
    
    public Set<EntityType> getBlacklistedMobs() {
        return blacklistedMobs;
    }
    
    public String getMessage(String key) {
        return messages.getOrDefault(key, "§cMessage not found: " + key);
    }
    
    public String getEggNameFormat() {
        return eggNameFormat;
    }
}
