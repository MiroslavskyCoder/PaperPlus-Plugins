package com.webx.news.commands;

import com.webx.news.NewsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class NewsCommand implements CommandExecutor {
    private final NewsPlugin plugin;
    
    public NewsCommand(NewsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§6=== Latest News ===");
        
        for (var news : plugin.getNewsManager().getLatestNews(5)) {
            sender.sendMessage("§f" + news.getTitle());
            sender.sendMessage("§7" + news.getContent());
        }
        
        return true;
    }
}
