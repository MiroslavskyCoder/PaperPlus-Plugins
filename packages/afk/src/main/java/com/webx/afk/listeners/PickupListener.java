package com.webx.afk.listeners;

import com.webx.afk.AFKPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class PickupListener implements Listener {
    private final AFKPlugin plugin;
    
    public PickupListener(AFKPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.Player) {
            plugin.getAFKManager().updateActivity(((org.bukkit.entity.Player) event.getEntity()).getUniqueId());
        }
    }
}
