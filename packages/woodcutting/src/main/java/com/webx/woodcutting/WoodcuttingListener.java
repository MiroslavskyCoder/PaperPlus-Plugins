package com.webx.woodcutting;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Material;

public class WoodcuttingListener implements Listener {
    private WoodcuttingManager manager;
    
    public WoodcuttingListener(WoodcuttingManager manager) {
        this.manager = manager;
    }
    
    @EventHandler
    public void onLogBreak(BlockBreakEvent event) {
        if (event.getBlock().getType().toString().contains("LOG")) {
            manager.chopLog(event.getPlayer());
        }
    }
}
