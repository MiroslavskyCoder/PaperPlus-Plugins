package com.webx.mounts;

import org.bukkit.plugin.java.JavaPlugin;

public class MountsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Mounts plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Mounts plugin disabled!");
    }
}
