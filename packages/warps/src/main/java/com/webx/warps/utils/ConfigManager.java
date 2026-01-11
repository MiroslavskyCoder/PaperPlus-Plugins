package com.webx.warps.utils;

import com.webx.warps.WarpsPlugin;
import org.bukkit.ChatColor;

public class ConfigManager {
    private final WarpsPlugin plugin;

    public ConfigManager(WarpsPlugin plugin) {
        this.plugin = plugin;
    }

    public String getColoredString(String path) {
        String value = plugin.getConfig().getString(path, "");
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    public void reload() {
        plugin.reloadConfig();
    }
}
