package com.webx.bedwarsevent.listeners;

import com.webx.bedwarsevent.BedWarsEventPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BedWarsGameListener implements Listener {
    private final BedWarsEventPlugin plugin;
    
    public BedWarsGameListener(BedWarsEventPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Handle BedWars bed destruction
    }
}
