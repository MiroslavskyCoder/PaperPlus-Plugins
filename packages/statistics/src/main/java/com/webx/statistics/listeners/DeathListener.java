package com.webx.statistics.listeners;

import com.webx.statistics.StatisticsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener implements Listener {
    private final StatisticsPlugin plugin;
    
    public DeathListener(StatisticsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        plugin.getStatisticsManager().recordDeath(event.getEntity().getUniqueId());
        if (event.getEntity().getKiller() != null) {
            plugin.getStatisticsManager().recordKill(event.getEntity().getKiller().getUniqueId(), event.getEntity().getUniqueId());
        }
    }
}
