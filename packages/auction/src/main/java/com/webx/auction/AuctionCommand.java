package com.webx.auction;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AuctionCommand implements CommandExecutor {
    private AuctionManager manager;
    
    public AuctionCommand(AuctionManager manager) {
        this.manager = manager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sender.sendMessage("§6=== Active Auctions ===");
            manager.getAllListings().forEach(listing -> 
                sender.sendMessage("§b$" + listing.price)
            );
            return true;
        }
        
        if (args[0].equalsIgnoreCase("create")) {
            try {
                long price = Long.parseLong(args[1]);
                manager.createListing(player, player.getInventory().getItemInMainHand(), price);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid price!");
            }
        }
        
        return true;
    }
}
