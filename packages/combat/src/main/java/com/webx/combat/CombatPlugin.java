package com.webx.combat;

import org.bukkit.plugin.java.JavaPlugin;

public class CombatPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Combat plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Combat plugin disabled!");
    }
}
