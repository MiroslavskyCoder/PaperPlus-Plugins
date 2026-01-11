package com.webx.tournaments;

import org.bukkit.plugin.java.JavaPlugin;

public class TournamentsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Tournaments plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Tournaments plugin disabled!");
    }
}
