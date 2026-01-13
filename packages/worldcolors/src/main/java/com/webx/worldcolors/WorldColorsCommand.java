package com.webx.worldcolors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@SuppressWarnings("deprecation")
public class WorldColorsCommand implements CommandExecutor {
    
    private final WorldColorsPlugin plugin;
    
    public WorldColorsCommand(WorldColorsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("worldcolors.reload")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission!");
                return true;
            }
            
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "WorldColors configuration reloaded!");
            return true;
        }
        
        sender.sendMessage(ChatColor.GOLD + "WorldColors v1.0.0 by LXXV");
        sender.sendMessage(ChatColor.GRAY + "Use /worldcolors reload to reload config");
        return true;
    }
}
