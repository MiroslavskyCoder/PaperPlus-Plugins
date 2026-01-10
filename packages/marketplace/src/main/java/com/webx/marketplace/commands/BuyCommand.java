package com.webx.marketplace.commands;

import com.webx.marketplace.MarketplacePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuyCommand implements CommandExecutor {
    private final MarketplacePlugin plugin;
    
    public BuyCommand(MarketplacePlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        sender.sendMessage("§aPurchase completed!");
        return true;
    }
}
