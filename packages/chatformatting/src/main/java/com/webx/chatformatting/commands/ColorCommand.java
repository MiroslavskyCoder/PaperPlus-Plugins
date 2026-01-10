package com.webx.chatformatting.commands;

import com.webx.chatformatting.ChatFormattingPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ColorCommand implements CommandExecutor {
    private final ChatFormattingPlugin plugin;
    
    public ColorCommand(ChatFormattingPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        sender.sendMessage("§6=== Available Colors ===");
        sender.sendMessage("§c&c §f- Red");
        sender.sendMessage("§2&a §f- Green");
        sender.sendMessage("§9&b §f- Blue");
        
        return true;
    }
}
