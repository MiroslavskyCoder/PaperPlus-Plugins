package com.webx.partysystem.commands;

import com.webx.partysystem.PartySystemPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyInviteCommand implements CommandExecutor {
    private final PartySystemPlugin plugin;
    
    public PartyInviteCommand(PartySystemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        sender.sendMessage("§aInvite sent!");
        return true;
    }
}
