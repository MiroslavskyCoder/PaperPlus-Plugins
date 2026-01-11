package com.webx.clans.utils;

import com.webx.clans.ClansPlugin;
import org.bukkit.ChatColor;

public class ConfigManager {
    private final ClansPlugin plugin;

    public ConfigManager(ClansPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
    }
}
