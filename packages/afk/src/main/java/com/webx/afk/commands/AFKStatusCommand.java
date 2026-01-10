package com.webx.afk.commands;

import com.webx.afk.AFKPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AFKStatusCommand implements CommandExecutor {
    private final AFKPlugin plugin;
    
    public AFKStatusCommand(AFKPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length > 0 && player.hasPermission("afk.check.others")) {
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§cPlayer not found!");
                return true;
            }
            
            boolean isAFK = plugin.getAFKManager().isAFK(target.getUniqueId());
            player.sendMessage("§6" + target.getName() + " is " + (isAFK ? "§cAFK" : "§aOnline"));
            return true;
        }
        
        boolean isAFK = plugin.getAFKManager().isAFK(player.getUniqueId());
        player.sendMessage("§6You are " + (isAFK ? "§cAFK" : "§aOnline"));
        
        return true;
    }
}
