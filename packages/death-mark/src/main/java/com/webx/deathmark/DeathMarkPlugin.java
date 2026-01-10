package com.webx.deathmark;

import org.bukkit.plugin.java.JavaPlugin;

public class DeathMarkPlugin extends JavaPlugin {
    private DeathChestManager manager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        manager = new DeathChestManager(this);
        getServer().getPluginManager().registerEvents(new DeathListener(this, manager), this);
        getLogger().info("DeathMark 0.1.0 enabled - Death chests system active");
    }

    @Override
    public void onDisable() {
        if (manager != null) {
            manager.cleanup();
        }
        getLogger().info("DeathMark plugin disabled");
    }

    public DeathChestManager getManager() {
        return manager;
    }
}
