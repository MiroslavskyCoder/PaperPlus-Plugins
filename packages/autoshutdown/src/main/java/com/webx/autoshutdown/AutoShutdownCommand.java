package com.webx.autoshutdown;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AutoShutdownCommand implements CommandExecutor {
    
    private final AutoShutdownPlugin plugin;
    
    public AutoShutdownCommand(AutoShutdownPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("autoshutdown.admin")) {
            sender.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "AutoShutdown v1.0.0");
            sender.sendMessage(ChatColor.GRAY + "/autoshutdown <enable|disable|status>");
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "enable":
                plugin.setEnabled(true);
                sender.sendMessage(ChatColor.GREEN + "AutoShutdown enabled!");
                break;
            case "disable":
                plugin.setEnabled(false);
                sender.sendMessage(ChatColor.YELLOW + "AutoShutdown disabled!");
                break;
            case "status":
                if (plugin.isEnabled()) {
                    long emptySince = plugin.getEmptyServerSince();
                    if (emptySince == -1) {
                        sender.sendMessage(ChatColor.GREEN + "AutoShutdown: Active (server has players)");
                    } else {
                        long minutes = (System.currentTimeMillis() - emptySince) / 60000;
                        sender.sendMessage(ChatColor.YELLOW + "AutoShutdown: Empty for " + minutes + "/" + plugin.getTimeout() + " minutes");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "AutoShutdown: Disabled");
                }
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown command!");
        }
        
        return true;
    }
}
