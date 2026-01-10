package com.webx.news.listeners;

import com.webx.news.NewsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NewsJoinListener implements Listener {
    private final NewsPlugin plugin;
    
    public NewsJoinListener(NewsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var news = plugin.getNewsManager().getLatestNews(1);
        if (!news.isEmpty()) {
            event.getPlayer().sendMessage("§6Latest News: §f" + news.get(0).getTitle());
        }
    }
}
