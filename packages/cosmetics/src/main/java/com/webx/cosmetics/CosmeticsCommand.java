package com.webx.cosmetics;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CosmeticsCommand implements CommandExecutor {
    private CosmeticsManager manager;
    
    public CosmeticsCommand(CosmeticsManager manager) {
        this.manager = manager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        
        if (args.length == 0) {
            player.sendMessage("§6Your Cosmetics:");
            manager.getCosmetics(player).forEach(c -> player.sendMessage("  §a✓ " + c));
            return true;
        }
        
        if (args[0].equalsIgnoreCase("buy")) {
            manager.purchaseCosmetic(player, args.length > 1 ? args[1] : "default");
        }
        
        return true;
    }
}
