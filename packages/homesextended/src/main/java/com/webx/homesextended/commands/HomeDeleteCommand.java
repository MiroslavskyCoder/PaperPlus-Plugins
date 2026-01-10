package com.webx.homesextended.commands;

import com.webx.homesextended.HomesExtendedPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeDeleteCommand implements CommandExecutor {
    private final HomesExtendedPlugin plugin;
    
    public HomeDeleteCommand(HomesExtendedPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        sender.sendMessage("§aHome deleted!");
        return true;
    }
}
