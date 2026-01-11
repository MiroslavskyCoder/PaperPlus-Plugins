package com.webx.levels;

import org.bukkit.plugin.java.JavaPlugin;

public class LevelsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Levels plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Levels plugin disabled!");
    }
}
