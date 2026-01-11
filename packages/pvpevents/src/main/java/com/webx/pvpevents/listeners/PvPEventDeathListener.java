package com.webx.pvpevents.listeners;

import com.webx.pvpevents.PvPEventsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PvPEventDeathListener implements Listener {
    private final PvPEventsPlugin plugin;
    
    public PvPEventDeathListener(PvPEventsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Track PvP event kills
    }
}
