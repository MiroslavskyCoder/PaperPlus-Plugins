package com.webx.afk.utils;

import org.bukkit.plugin.Plugin;

public class EventRegistry {
    private final Plugin plugin;
    
    public EventRegistry(Plugin plugin) {
        this.plugin = plugin;
    }
    
    public void registerListeners(com.webx.afk.AFKPlugin afkPlugin) {
        plugin.getServer().getPluginManager().registerEvents(new com.webx.afk.listeners.MovementListener(afkPlugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new com.webx.afk.listeners.ChatListener(afkPlugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new com.webx.afk.listeners.InteractionListener(afkPlugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new com.webx.afk.listeners.BlockListener(afkPlugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new com.webx.afk.listeners.JoinListener(afkPlugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new com.webx.afk.listeners.QuitListener(afkPlugin), plugin);
    }
}
