package com.webx.claims.commands;

import com.webx.claims.ClaimsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClaimCommand implements CommandExecutor {
    private final ClaimsPlugin plugin;
    
    public ClaimCommand(ClaimsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        Player player = (Player) sender;
        sender.sendMessage("§cUsage: /claim <create|remove|list>");
        return true;
    }
}
