package com.webx.farming;

import org.bukkit.plugin.java.JavaPlugin;

public class FarmingPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Farming plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Farming plugin disabled!");
    }
}
