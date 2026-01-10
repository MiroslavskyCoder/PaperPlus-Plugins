package com.webx.afk.commands;

import com.webx.afk.AFKPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AFKReloadCommand implements CommandExecutor {
    private final AFKPlugin plugin;
    
    public AFKReloadCommand(AFKPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("afk.reload")) {
            sender.sendMessage("§cNo permission!");
            return true;
        }
        
        plugin.reloadConfig();
        sender.sendMessage("§aAFK plugin reloaded!");
        
        return true;
    }
}
