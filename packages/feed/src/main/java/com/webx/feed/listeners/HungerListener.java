package com.webx.feed.listeners;

import com.webx.feed.FeedPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class HungerListener implements Listener {
    private final FeedPlugin plugin;
    
    public HungerListener(FeedPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.getConfig().getBoolean("auto-feed-on-join", false)) {
            plugin.getFeedManager().feedPlayer(event.getPlayer());
        }
    }
}
