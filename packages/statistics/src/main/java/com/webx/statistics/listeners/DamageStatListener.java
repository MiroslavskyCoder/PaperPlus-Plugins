package com.webx.statistics.listeners;

import com.webx.statistics.StatisticsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageStatListener implements Listener {
    private final StatisticsPlugin plugin;
    
    public DamageStatListener(StatisticsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        // Track damage statistics
    }
}
