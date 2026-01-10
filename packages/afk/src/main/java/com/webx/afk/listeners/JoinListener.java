package com.webx.afk.listeners;

import com.webx.afk.AFKPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private final AFKPlugin plugin;
    
    public JoinListener(AFKPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getAFKManager().updateActivity(event.getPlayer().getUniqueId());
    }
}
