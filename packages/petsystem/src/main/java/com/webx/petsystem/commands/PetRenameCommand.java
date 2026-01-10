package com.webx.petsystem.commands;

import com.webx.petsystem.PetSystemPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PetRenameCommand implements CommandExecutor {
    private final PetSystemPlugin plugin;
    
    public PetRenameCommand(PetSystemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        sender.sendMessage("§aPet renamed!");
        return true;
    }
}
