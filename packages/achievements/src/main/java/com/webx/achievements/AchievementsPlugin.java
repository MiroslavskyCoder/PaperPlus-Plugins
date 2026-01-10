package com.webx.achievements;

import org.bukkit.plugin.java.JavaPlugin;

public class AchievementsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Achievements plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Achievements plugin disabled!");
    }
}
