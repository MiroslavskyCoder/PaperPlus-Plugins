package com.webx.mining;

import org.bukkit.plugin.java.JavaPlugin;

public class MiningPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Mining plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Mining plugin disabled!");
    }
}
