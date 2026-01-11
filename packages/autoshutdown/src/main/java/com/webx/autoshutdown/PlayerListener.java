package com.webx.autoshutdown;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    
    private final AutoShutdownPlugin plugin;
    
    public PlayerListener(AutoShutdownPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Reset timer when player joins
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Check will run on next tick
    }
}
