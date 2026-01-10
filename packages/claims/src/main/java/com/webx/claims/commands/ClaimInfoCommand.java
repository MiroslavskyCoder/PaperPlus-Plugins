package com.webx.claims.commands;

import com.webx.claims.ClaimsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClaimInfoCommand implements CommandExecutor {
    private final ClaimsPlugin plugin;
    
    public ClaimInfoCommand(ClaimsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        sender.sendMessage("§6Standing claim info:");
        sender.sendMessage("§7No claim at this location.");
        
        return true;
    }
}
