package com.webx.customitems;

import org.bukkit.plugin.java.JavaPlugin;

public class CustomItemsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("CustomItems plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("CustomItems plugin disabled!");
    }
}
