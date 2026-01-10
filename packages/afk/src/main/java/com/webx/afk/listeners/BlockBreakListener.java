package com.webx.afk.listeners;

import com.webx.afk.AFKPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private final AFKPlugin plugin;
    
    public BlockBreakListener(AFKPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        plugin.getAFKManager().updateActivity(event.getPlayer().getUniqueId());
    }
}
