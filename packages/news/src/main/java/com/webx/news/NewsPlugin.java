package com.webx.news;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class NewsPlugin extends JavaPlugin implements Listener {
    private static NewsPlugin instance;
    private List<NewsItem> news = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        
        getCommand("news").setExecutor((sender, cmd, label, args) -> {
            if (args.length == 0) {
                showNews(sender);
                return true;
            }
            
            if (!sender.hasPermission("news.admin")) {
                sender.sendMessage("§cNo permission!");
                return true;
            }
            
            if (args[0].equalsIgnoreCase("add") && args.length > 1) {
                addNews(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                sender.sendMessage("§aNews added!");
            } else if (args[0].equalsIgnoreCase("remove") && args.length > 1) {
                removeNews(Integer.parseInt(args[1]));
                sender.sendMessage("§aNews removed!");
            }
            
            return true;
        });
        
        getLogger().info("News Plugin enabled!");
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (getConfig().getBoolean("show-on-join", true)) {
            showNews(event.getPlayer());
        }
    }
    
    private void showNews(org.bukkit.command.CommandSender sender) {
        if (news.isEmpty()) {
            sender.sendMessage("§cNo news available!");
            return;
        }
        
        sender.sendMessage("§6=== Latest News ===");
        for (int i = 0; i < Math.min(5, news.size()); i++) {
            NewsItem item = news.get(i);
            sender.sendMessage("§f" + (i + 1) + ". " + item.message);
        }
    }
    
    private void addNews(String message) {
        news.add(0, new NewsItem(message, System.currentTimeMillis()));
    }
    
    private void removeNews(int index) {
        if (index > 0 && index <= news.size()) {
            news.remove(index - 1);
        }
    }
    
    public static NewsPlugin getInstance() {
        return instance;
    }
    
    private static class NewsItem {
        String message;
        long timestamp;
        
        NewsItem(String message, long timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}
