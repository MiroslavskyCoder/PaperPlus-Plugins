package com.webx.cosmetics;

import org.bukkit.plugin.java.JavaPlugin;

public class CosmeticsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Cosmetics plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Cosmetics plugin disabled!");
    }
}
