package com.webx.claims.listeners;

import com.webx.claims.ClaimsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ClaimProtectionListener implements Listener {
    private final ClaimsPlugin plugin;
    
    public ClaimProtectionListener(ClaimsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Check if block is in claim
    }
}
