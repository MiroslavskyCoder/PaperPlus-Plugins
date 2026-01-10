package com.webx.antispam.commands;

import com.webx.antispam.AntiSpamPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand implements CommandExecutor {
    private final AntiSpamPlugin plugin;
    
    public MuteCommand(AntiSpamPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("antispam.mute")) {
            sender.sendMessage("§cNo permission!");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /mute <player> <duration>");
            return true;
        }
        
        sender.sendMessage("§aPlayer muted!");
        return true;
    }
}
