package com.webx.insurance;

import org.bukkit.plugin.java.JavaPlugin;

public class InsurancePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Insurance plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Insurance plugin disabled!");
    }
}
