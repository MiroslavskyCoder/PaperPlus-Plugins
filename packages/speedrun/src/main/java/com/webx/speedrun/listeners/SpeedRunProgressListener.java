package com.webx.speedrun.listeners;

import com.webx.speedrun.SpeedRunPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class SpeedRunProgressListener implements Listener {
    private final SpeedRunPlugin plugin;
    
    public SpeedRunProgressListener(SpeedRunPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        // Track speedrun progress
    }
}
