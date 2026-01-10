package com.webx.afk.listeners;

import com.webx.afk.AFKPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractionListener implements Listener {
    private final AFKPlugin plugin;
    
    public InteractionListener(AFKPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        plugin.getAFKManager().updateActivity(event.getPlayer().getUniqueId());
    }
}
