package com.webx.skyesurvival.listeners;

import com.webx.skyesurvival.SkyeSurvivalPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ResourceGatherListener implements Listener {
    private final SkyeSurvivalPlugin plugin;
    
    public ResourceGatherListener(SkyeSurvivalPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        plugin.getSurvivalManager().addResource(event.getPlayer().getUniqueId(), 1);
    }
}
