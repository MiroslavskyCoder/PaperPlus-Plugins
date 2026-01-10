package com.webx.marketplace.listeners;

import com.webx.marketplace.MarketplacePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MarketplaceJoinListener implements Listener {
    private final MarketplacePlugin plugin;
    
    public MarketplaceJoinListener(MarketplacePlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§6Welcome to Marketplace! Type /market to view items.");
    }
}
