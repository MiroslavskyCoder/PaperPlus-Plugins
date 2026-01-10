package com.webx.potions;

import org.bukkit.plugin.java.JavaPlugin;

public class PotionsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Potions plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Potions plugin disabled!");
    }
}
