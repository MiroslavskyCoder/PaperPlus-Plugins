package com.webx.leveling.listeners;

import com.webx.leveling.LevelingPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LevelBroadcastListener implements Listener {
    private final LevelingPlugin plugin;
    
    public LevelBroadcastListener(LevelingPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        int level = plugin.getLevelingManager().getLevel(event.getPlayer().getUniqueId());
        if (level > 1) {
            event.getPlayer().sendMessage("§6Welcome back! You are level §f" + level);
        }
    }
}
