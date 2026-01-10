package com.webx.marketplace.commands;

import com.webx.marketplace.MarketplacePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MarketCommand implements CommandExecutor {
    private final MarketplacePlugin plugin;
    
    public MarketCommand(MarketplacePlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        Player player = (Player) sender;
        player.sendMessage("§6=== Market Listings ===");
        
        for (var item : plugin.getMarketplaceManager().getListings()) {
            player.sendMessage("§f" + item.getItemName() + " - §6$" + item.getPrice());
        }
        
        return true;
    }
}
