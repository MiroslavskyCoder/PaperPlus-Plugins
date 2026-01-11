package com.webx.antispam.listeners;

import com.webx.antispam.AntiSpamPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractionSpamListener implements Listener {
    private final AntiSpamPlugin plugin;
    
    public InteractionSpamListener(AntiSpamPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (plugin.getSpamManager().isSpamming(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cInteraction spam detected!");
        }
    }
}
