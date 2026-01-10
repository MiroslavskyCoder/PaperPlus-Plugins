package com.webx.vaults;

import org.bukkit.plugin.java.JavaPlugin;

public class VaultsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Vaults plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Vaults plugin disabled!");
    }
}
