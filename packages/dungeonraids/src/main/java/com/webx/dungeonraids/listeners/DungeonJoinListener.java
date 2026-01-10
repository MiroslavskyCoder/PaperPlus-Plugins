package com.webx.dungeonraids.listeners;

import com.webx.dungeonraids.DungeonRaidsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DungeonJoinListener implements Listener {
    private final DungeonRaidsPlugin plugin;
    
    public DungeonJoinListener(DungeonRaidsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§6Dungeons available! Type /dungeon to explore.");
    }
}
