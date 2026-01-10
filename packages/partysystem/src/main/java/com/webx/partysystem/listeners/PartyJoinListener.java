package com.webx.partysystem.listeners;

import com.webx.partysystem.PartySystemPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PartyJoinListener implements Listener {
    private final PartySystemPlugin plugin;
    
    public PartyJoinListener(PartySystemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Check if player is in a party
    }
}
