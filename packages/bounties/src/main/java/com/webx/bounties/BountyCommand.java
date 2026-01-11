package com.webx.bounties;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class BountyCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is for players only!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("bounties.set")) {
            player.sendMessage("§cNo permission!");
            return true;
        }
        
        if (args.length < 2) {
            player.sendMessage("§cUsage: /bounty <player> <amount>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("§cPlayer not found!");
            return true;
        }
        
        try {
            int amount = Integer.parseInt(args[1]);
            player.sendMessage("§aSet bounty on " + target.getName() + " for $" + amount);
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid amount!");
        }
        
        return true;
    }
}
