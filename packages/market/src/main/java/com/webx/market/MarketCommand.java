package com.webx.market;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MarketCommand implements CommandExecutor {
    private MarketManager manager;
    
    public MarketCommand(MarketManager manager) {
        this.manager = manager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage("§6=== Market Prices ===");
        manager.getAllMarketItems().forEach(item -> 
            sender.sendMessage("§b" + item.name + "§7: Buy §f$" + item.buyPrice + " §7Sell §f$" + item.sellPrice)
        );
        return true;
    }
}
