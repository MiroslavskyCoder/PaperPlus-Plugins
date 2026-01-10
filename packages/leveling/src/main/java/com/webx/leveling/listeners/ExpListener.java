package com.webx.leveling.listeners;

import com.webx.leveling.LevelingPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class ExpListener implements Listener {
    private final LevelingPlugin plugin;
    
    public ExpListener(LevelingPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            plugin.getLevelingManager().addExp(event.getEntity().getKiller(), 10);
        }
    }
}
