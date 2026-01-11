package com.webx.fishing;

import org.bukkit.plugin.java.JavaPlugin;

public class FishingPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Fishing plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Fishing plugin disabled!");
    }
}
