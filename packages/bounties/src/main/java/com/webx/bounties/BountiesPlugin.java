package com.webx.bounties;

import org.bukkit.plugin.java.JavaPlugin;

public class BountiesPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Bounties plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Bounties plugin disabled!");
    }
}
