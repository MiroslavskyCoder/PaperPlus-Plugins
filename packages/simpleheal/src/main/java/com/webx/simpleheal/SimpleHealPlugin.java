package com.webx.simpleheal;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SimpleHealPlugin extends JavaPlugin {
    
    private boolean healHealth;
    private boolean healFood;
    private boolean clearEffects;
    private boolean clearFire;
    private int cooldown;
    
    private Map<String, String> messages;
    private Map<UUID, Long> cooldowns;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfiguration();
        
        cooldowns = new HashMap<>();
        
        getCommand("heal").setExecutor(new HealCommand(this));
        getCommand("healall").setExecutor(new HealAllCommand(this));
        
        getLogger().info("SimpleHeal plugin enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("SimpleHeal plugin disabled!");
    }
    
    public void loadConfiguration() {
        reloadConfig();
        healHealth = getConfig().getBoolean("heal-health", true);
        healFood = getConfig().getBoolean("heal-food", true);
        clearEffects = getConfig().getBoolean("clear-effects", true);
        clearFire = getConfig().getBoolean("clear-fire", true);
        cooldown = getConfig().getInt("cooldown", 60);
        
        messages = new HashMap<>();
        for (String key : getConfig().getConfigurationSection("messages").getKeys(false)) {
            messages.put(key, getConfig().getString("messages." + key));
        }
    }
    
    public boolean isHealHealth() {
        return healHealth;
    }
    
    public boolean isHealFood() {
        return healFood;
    }
    
    public boolean isClearEffects() {
        return clearEffects;
    }
    
    public boolean isClearFire() {
        return clearFire;
    }
    
    public int getCooldown() {
        return cooldown;
    }
    
    public String getMessage(String key) {
        return messages.getOrDefault(key, "§cMessage not found: " + key);
    }
    
    public Map<UUID, Long> getCooldowns() {
        return cooldowns;
    }
}
