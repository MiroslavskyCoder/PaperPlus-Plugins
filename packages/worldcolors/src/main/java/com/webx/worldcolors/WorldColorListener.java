package com.webx.worldcolors;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class WorldColorListener implements Listener {
    
    private final WorldColorsPlugin plugin;
    
    public WorldColorListener(WorldColorsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.updatePlayerDisplay(event.getPlayer());
    }
    
    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        plugin.updatePlayerDisplay(event.getPlayer());
    }
}
