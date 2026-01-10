package com.webx.cooking;

import org.bukkit.plugin.java.JavaPlugin;

public class CookingPlugin extends JavaPlugin {

    private CookingManager cookingManager;

    @Override
    public void onEnable() {
        cookingManager = new CookingManager();
        getServer().getPluginManager().registerEvents(new CookingListener(cookingManager), this);
        getLogger().info("Cooking plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Cooking plugin disabled!");
    }
}
