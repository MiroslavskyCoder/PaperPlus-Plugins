package com.webx.afk.commands;

import com.webx.afk.AFKPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AFKSetCommand implements CommandExecutor {
    private final AFKPlugin plugin;
    
    public AFKSetCommand(AFKPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("afk.admin")) {
            sender.sendMessage("§cNo permission!");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /afkset <player> <true|false>");
            return true;
        }
        
        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }
        
        boolean afk = Boolean.parseBoolean(args[1]);
        plugin.getAFKManager().setAFK(target.getUniqueId(), afk);
        
        sender.sendMessage("§a" + target.getName() + " AFK status set to: " + afk);
        
        return true;
    }
}
