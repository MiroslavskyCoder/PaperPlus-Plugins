package com.webx.showhealth;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class ShowHealthPlugin extends JavaPlugin {
    private HealthDisplayListener healthListener;

    @Override
    public void onEnable() {
        getLogger().info("ShowHealth enabled");

        // Initialize health display listener
        this.healthListener = new HealthDisplayListener(this);
        getServer().getPluginManager().registerEvents(healthListener, this);

        // Register /togglehealth command
        PluginCommand cmd = getCommand("togglehealth");
        if (cmd != null) {
            cmd.setExecutor(new HealthCommand(this));
        } else {
            getLogger().warning("Command 'togglehealth' not found in plugin.yml");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("ShowHealth disabled");
    }

    public HealthDisplayListener getHealthListener() {
        return healthListener;
    }
}
