package com.webx.pets;

import org.bukkit.plugin.java.JavaPlugin;

public class PetsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Pets plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Pets plugin disabled!");
    }
}
