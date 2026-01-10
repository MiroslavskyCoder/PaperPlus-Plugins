package com.webx.afk.commands;

import com.webx.afk.AFKPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AFKKickCommand implements CommandExecutor {
    private final AFKPlugin plugin;
    
    public AFKKickCommand(AFKPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("afk.kick")) {
            sender.sendMessage("§cNo permission!");
            return true;
        }
        
        int kicked = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (plugin.getAFKManager().isAFK(player.getUniqueId())) {
                player.kickPlayer("§cKicked for being AFK!");
                kicked++;
            }
        }
        
        sender.sendMessage("§aKicked §f" + kicked + "§a AFK players!");
        
        return true;
    }
}
