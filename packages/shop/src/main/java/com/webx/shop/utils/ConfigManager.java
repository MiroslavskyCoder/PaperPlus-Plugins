package com.webx.shop.utils;

import com.webx.shop.ShopPlugin;
import org.bukkit.ChatColor;

public class ConfigManager {
    private final ShopPlugin plugin;

    public ConfigManager(ShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
    }
}
