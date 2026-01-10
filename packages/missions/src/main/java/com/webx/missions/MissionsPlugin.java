package com.webx.missions;

import org.bukkit.plugin.java.JavaPlugin;

public class MissionsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Missions plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Missions plugin disabled!");
    }
}
