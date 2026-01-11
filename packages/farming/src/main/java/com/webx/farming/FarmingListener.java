package com.webx.farming;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.Material;

public class FarmingListener implements Listener {
    private FarmingManager manager;
    
    public FarmingListener(FarmingManager manager) {
        this.manager = manager;
    }
    
    @EventHandler
    public void onCropBreak(BlockBreakEvent event) {
        if (isCrop(event.getBlock().getType())) {
            manager.harvestCrop(event.getPlayer());
        }
    }
    
    private boolean isCrop(Material mat) {
        return mat == Material.WHEAT || mat == Material.BEETROOTS || mat == Material.CARROTS || mat == Material.POTATOES;
    }
}
