package com.webx.quests.utils;

import com.webx.quests.QuestsPlugin;
import org.bukkit.ChatColor;

public class ConfigManager {
    private final QuestsPlugin plugin;

    public ConfigManager(QuestsPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reloadConfig();
    }
}
