package com.webx.afk.utils;

import org.bukkit.plugin.Plugin;

public class CommandRegistry {
    private final Plugin plugin;
    
    public CommandRegistry(Plugin plugin) {
        this.plugin = plugin;
    }
    
    public void registerCommands(com.webx.afk.AFKPlugin afkPlugin) {
        plugin.getCommand("afk").setExecutor(new com.webx.afk.commands.AFKStatusCommand(afkPlugin));
        plugin.getCommand("afklist").setExecutor(new com.webx.afk.commands.AFKListCommand(afkPlugin));
        plugin.getCommand("afkset").setExecutor(new com.webx.afk.commands.AFKSetCommand(afkPlugin));
        plugin.getCommand("afkstats").setExecutor(new com.webx.afk.commands.AFKStatsCommand(afkPlugin));
        plugin.getCommand("afkrefresh").setExecutor(new com.webx.afk.commands.AFKReloadCommand(afkPlugin));
        plugin.getCommand("afkkick").setExecutor(new com.webx.afk.commands.AFKKickCommand(afkPlugin));
    }
}
