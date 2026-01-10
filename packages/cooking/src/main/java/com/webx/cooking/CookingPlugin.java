package com.webx.cooking;

import org.bukkit.plugin.java.JavaPlugin;

public class CookingPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Cooking plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Cooking plugin disabled!");
    }
}
