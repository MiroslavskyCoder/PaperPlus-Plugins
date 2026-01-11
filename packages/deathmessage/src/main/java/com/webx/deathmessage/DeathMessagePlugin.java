package com.webx.deathmessage;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;

public class DeathMessagePlugin extends JavaPlugin {
    
    private boolean enabled;
    private boolean broadcast;
    private boolean showLocation;
    private boolean showWorld;
    private String messageFormat;
    private Map<String, String> causeMessages;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfiguration();
        
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        
        getLogger().info("DeathMessage plugin enabled!");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("DeathMessage plugin disabled!");
    }
    
    public void loadConfiguration() {
        reloadConfig();
        enabled = getConfig().getBoolean("enabled", true);
        broadcast = getConfig().getBoolean("broadcast", true);
        showLocation = getConfig().getBoolean("show-location", true);
        showWorld = getConfig().getBoolean("show-world", false);
        messageFormat = getConfig().getString("message-format", "§c☠ §7%player% §cdied");
        
        causeMessages = new HashMap<>();
        if (getConfig().contains("causes")) {
            for (String key : getConfig().getConfigurationSection("causes").getKeys(false)) {
                causeMessages.put(key, getConfig().getString("causes." + key));
            }
        }
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public boolean isBroadcast() {
        return broadcast;
    }
    
    public boolean isShowLocation() {
        return showLocation;
    }
    
    public boolean isShowWorld() {
        return showWorld;
    }
    
    public String getMessageFormat() {
        return messageFormat;
    }
    
    public Map<String, String> getCauseMessages() {
        return causeMessages;
    }
}
