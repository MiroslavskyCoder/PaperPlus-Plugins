package com.webx.leveling.listeners;

import com.webx.leveling.LevelingPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class MiningExpListener implements Listener {
    private final LevelingPlugin plugin;
    
    public MiningExpListener(LevelingPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        plugin.getLevelingManager().addExp(event.getPlayer(), 5);
    }
}
