package com.webx.market;

import org.bukkit.plugin.java.JavaPlugin;

public class MarketPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Market plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Market plugin disabled!");
    }
}
