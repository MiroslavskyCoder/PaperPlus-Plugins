package com.webx.friendfeed;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class FriendFeedPlugin extends JavaPlugin {
    
    private boolean enabled;
    private boolean requirePermission;
    private String permission;
    private int restoreHunger;
    private double restoreSaturation;
    private boolean giveEffects;
    private List<EffectConfig> effects;
    private int cooldown;
    private double range;
    private Map<String, String> messages;
    private Map<UUID, Long> cooldowns;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfiguration();
        
        cooldowns = new HashMap<>();
        
        getServer().getPluginManager().registerEvents(new FeedListener(this), this);
        
        getLogger().info("FriendFeed plugin enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("FriendFeed plugin disabled!");
    }
    
    public void loadConfiguration() {
        reloadConfig();
        enabled = getConfig().getBoolean("enabled", true);
        requirePermission = getConfig().getBoolean("require-permission", false);
        permission = getConfig().getString("permission", "friendfeed.use");
        restoreHunger = getConfig().getInt("restore-hunger", 10);
        restoreSaturation = getConfig().getDouble("restore-saturation", 5.0);
        giveEffects = getConfig().getBoolean("give-effects", true);
        cooldown = getConfig().getInt("cooldown", 30);
        range = getConfig().getDouble("range", 5.0);
        
        effects = new ArrayList<>();
        if (getConfig().contains("effects")) {
            for (Map<?, ?> effectMap : getConfig().getMapList("effects")) {
                String type = (String) effectMap.get("type");
                int duration = (Integer) effectMap.get("duration");
                int amplifier = (Integer) effectMap.get("amplifier");
                
                PotionEffectType effectType = PotionEffectType.getByName(type);
                if (effectType != null) {
                    effects.add(new EffectConfig(effectType, duration, amplifier));
                }
            }
        }
        
        messages = new HashMap<>();
        if (getConfig().contains("messages")) {
            for (String key : getConfig().getConfigurationSection("messages").getKeys(false)) {
                messages.put(key, getConfig().getString("messages." + key));
            }
        }
    }
    
    public boolean isFriendFeedEnabled() {
        return enabled;
    }
    
    public boolean isRequirePermission() {
        return requirePermission;
    }
    
    public String getPermission() {
        return permission;
    }
    
    public int getRestoreHunger() {
        return restoreHunger;
    }
    
    public double getRestoreSaturation() {
        return restoreSaturation;
    }
    
    public boolean isGiveEffects() {
        return giveEffects;
    }
    
    public List<EffectConfig> getEffects() {
        return effects;
    }
    
    public int getCooldown() {
        return cooldown;
    }
    
    public double getRange() {
        return range;
    }
    
    public String getMessage(String key) {
        return messages.getOrDefault(key, "§cMessage not found: " + key);
    }
    
    public Map<UUID, Long> getCooldowns() {
        return cooldowns;
    }
    
    public static class EffectConfig {
        private final PotionEffectType type;
        private final int duration;
        private final int amplifier;
        
        public EffectConfig(PotionEffectType type, int duration, int amplifier) {
            this.type = type;
            this.duration = duration;
            this.amplifier = amplifier;
        }
        
        public PotionEffectType getType() {
            return type;
        }
        
        public int getDuration() {
            return duration;
        }
        
        public int getAmplifier() {
            return amplifier;
        }
    }
}
