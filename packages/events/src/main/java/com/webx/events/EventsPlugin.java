package com.webx.events;

import org.bukkit.plugin.java.JavaPlugin;

public class EventsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Events plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Events plugin disabled!");
    }
}
