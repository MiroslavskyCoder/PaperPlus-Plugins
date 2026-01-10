package com.webx.pvpbase;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class PvPBasePlugin extends JavaPlugin {
    private MatchManager matchManager;
    private ClassRegistry classRegistry;

    @Override
    public void onEnable() {
        getLogger().info("PvPBase enabled");

        // Initialize managers
        this.matchManager = new MatchManager();
        this.classRegistry = new ClassRegistry();

        // Register /match command
        PluginCommand matchCmd = getCommand("match");
        if (matchCmd != null) {
            matchCmd.setExecutor(new MatchCommand(this));
        } else {
            getLogger().warning("Command 'match' not found in plugin.yml");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("PvPBase disabled");
    }

    public MatchManager getMatchManager() { return matchManager; }
    public ClassRegistry getClassRegistry() { return classRegistry; }
}
