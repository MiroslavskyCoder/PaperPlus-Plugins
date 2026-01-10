package com.webx.jobs;

import org.bukkit.plugin.java.JavaPlugin;

public class JobsPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("Jobs plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Jobs plugin disabled!");
    }
}
