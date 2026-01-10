package com.webx.guilds.listeners;

import com.webx.guilds.GuildsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class GuildJoinListener implements Listener {
    private final GuildsPlugin plugin;
    
    public GuildJoinListener(GuildsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // Check if player is in guild and display guild info
    }
}
