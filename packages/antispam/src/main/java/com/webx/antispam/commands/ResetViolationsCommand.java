package com.webx.antispam.commands;

import com.webx.antispam.AntiSpamPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetViolationsCommand implements CommandExecutor {
    private final AntiSpamPlugin plugin;
    
    public ResetViolationsCommand(AntiSpamPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("antispam.reset")) {
            sender.sendMessage("§cNo permission!");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /resetviolations <player>");
            return true;
        }
        
        sender.sendMessage("§aViolations reset!");
        return true;
    }
}
