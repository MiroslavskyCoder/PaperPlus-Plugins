package com.webx.potions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PotionsCommand implements CommandExecutor {
    private PotionsManager manager;
    
    public PotionsCommand(PotionsManager manager) {
        this.manager = manager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        
        if (args.length < 1) {
            player.sendMessage("§cUsage: /potion <type> [duration]");
            return true;
        }
        
        int duration = args.length > 1 ? Integer.parseInt(args[1]) : 60;
        manager.applyBuff(player, args[0], duration);
        
        return true;
    }
}
