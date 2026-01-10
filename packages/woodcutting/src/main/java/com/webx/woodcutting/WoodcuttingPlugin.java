package com.webx.woodcutting;

import org.bukkit.plugin.java.JavaPlugin;

public class WoodcuttingPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Woodcutting plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Woodcutting plugin disabled!");
    }
}
