package com.webx.miningevents.listeners;

import com.webx.miningevents.MiningEventsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class OreBreakListener implements Listener {
    private final MiningEventsPlugin plugin;
    
    public OreBreakListener(MiningEventsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onOreBreak(BlockBreakEvent event) {
        // Apply mining event multiplier
    }
}
