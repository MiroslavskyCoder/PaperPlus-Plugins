package com.webx.customitems;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CustomItemsCommand implements CommandExecutor {
    private CustomItemsManager manager;
    
    public CustomItemsCommand(CustomItemsManager manager) {
        this.manager = manager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage("§6=== Custom Items ===");
        manager.getAllItems().forEach(item -> 
            sender.sendMessage("§b" + item.id + "§7 - §f" + item.displayName)
        );
        return true;
    }
}
