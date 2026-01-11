package com.webx.achievements;

import org.bukkit.plugin.java.JavaPlugin;

public class AchievementsPlugin extends JavaPlugin {

    private AchievementManager achievementManager;

    @Override
    public void onEnable() {
        this.achievementManager = new AchievementManager(this);
        getLogger().info("Achievements plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Achievements plugin disabled!");
    }

    public AchievementManager getAchievementManager() {
        return achievementManager;
    }
}
