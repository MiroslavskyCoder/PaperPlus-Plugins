package com.webx.enchanting;

import org.bukkit.plugin.java.JavaPlugin;

public class EnchantingPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Enchanting plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Enchanting plugin disabled!");
    }
}
