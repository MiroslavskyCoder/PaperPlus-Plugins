package com.webx.afk.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
    private final Plugin plugin;
    
    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
    }
    
    public long getAFKTimeout() {
        return plugin.getConfig().getLong("afk-timeout-minutes", 5) * 60 * 1000;
    }
    
    public String getAFKPrefix() {
        return plugin.getConfig().getString("afk-prefix", "&7[AFK] &r");
    }
    
    public String getAFKSuffix() {
        return plugin.getConfig().getString("afk-suffix", " &7(AFK)");
    }
    
    public boolean isKickAFKEnabled() {
        return plugin.getConfig().getBoolean("kick-afk", false);
    }
    
    public long getKickAFKAfter() {
        return plugin.getConfig().getLong("kick-afk-after-minutes", 30) * 60 * 1000;
    }
}
