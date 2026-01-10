package com.webx.antispam.utils;

import com.webx.antispam.AntiSpamPlugin;

public class ConfigManager {
    private final AntiSpamPlugin plugin;
    
    public ConfigManager(AntiSpamPlugin plugin) {
        this.plugin = plugin;
    }
    
    public int getMaxMessages() {
        return plugin.getConfig().getInt("spam.max-messages", 5);
    }
    
    public long getTimeWindow() {
        return plugin.getConfig().getLong("spam.time-window", 10000);
    }
    
    public boolean isPunishEnabled() {
        return plugin.getConfig().getBoolean("spam.punish-enabled", true);
    }
}
