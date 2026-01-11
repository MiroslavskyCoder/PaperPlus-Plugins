package com.webx.guildsadvanced;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuildsAdvancedCommand implements CommandExecutor {
    private GuildsAdvancedManager manager;
    
    public GuildsAdvancedCommand(GuildsAdvancedManager manager) {
        this.manager = manager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        
        if (args.length == 0) {
            player.sendMessage("§6Guild Commands: /aguild create <name>");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("create")) {
            manager.createGuild(args.length > 1 ? args[1] : "Default", player);
        }
        
        return true;
    }
}
