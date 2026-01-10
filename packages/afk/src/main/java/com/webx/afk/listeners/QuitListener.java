package com.webx.afk.listeners;

import com.webx.afk.AFKPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {
    private final AFKPlugin plugin;
    
    public QuitListener(AFKPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getAFKManager().removePlayer(event.getPlayer().getUniqueId());
    }
}
