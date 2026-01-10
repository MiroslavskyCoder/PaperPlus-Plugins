package com.webx.statistics.listeners;

import com.webx.statistics.StatisticsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinStatsListener implements Listener {
    private final StatisticsPlugin plugin;
    
    public JoinStatsListener(StatisticsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Initialize player stats on join
        plugin.getStatisticsManager().getStats(event.getPlayer().getUniqueId());
    }
}
