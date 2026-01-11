package com.webx.feed.commands;

import com.webx.feed.FeedPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FeedAllCommand implements CommandExecutor {
    private final FeedPlugin plugin;
    
    public FeedAllCommand(FeedPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("feed.admin")) {
            sender.sendMessage("§cNo permission!");
            return true;
        }
        
        plugin.getFeedManager().feedAll(Bukkit.getOnlinePlayers());
        sender.sendMessage("§aAll players have been fed!");
        return true;
    }
}
