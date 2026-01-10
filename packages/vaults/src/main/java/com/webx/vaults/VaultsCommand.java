package com.webx.vaults;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VaultsCommand implements CommandExecutor {
    private VaultsManager manager;
    
    public VaultsCommand(VaultsManager manager) {
        this.manager = manager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        
        if (args.length == 0) {
            player.sendMessage("§6Vault Commands:");
            player.sendMessage("§b/vault create <name>");
            player.sendMessage("§b/vault store");
            player.sendMessage("§b/vault access");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("create")) {
            manager.createVault(player, args.length > 1 ? args[1] : "Default");
        }
        
        return true;
    }
}
