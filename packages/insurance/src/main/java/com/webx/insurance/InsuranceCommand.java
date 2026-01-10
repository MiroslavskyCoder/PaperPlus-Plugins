package com.webx.insurance;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InsuranceCommand implements CommandExecutor {
    private InsuranceManager manager;
    
    public InsuranceCommand(InsuranceManager manager) {
        this.manager = manager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        
        if (args.length == 0) {
            player.sendMessage("§6Insurance Options");
            player.sendMessage("§b/insurance buy <amount> <days>");
            player.sendMessage("§b/insurance claim");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("buy")) {
            double amount = Double.parseDouble(args[1]);
            long days = Long.parseLong(args[2]);
            manager.createPolicy(player, amount, days * 86400000L);
        }
        
        if (args[0].equalsIgnoreCase("claim")) {
            manager.claimInsurance(player);
        }
        
        return true;
    }
}
