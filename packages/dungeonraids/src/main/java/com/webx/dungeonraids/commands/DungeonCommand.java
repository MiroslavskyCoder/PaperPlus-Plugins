package com.webx.dungeonraids.commands;

import com.webx.dungeonraids.DungeonRaidsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DungeonCommand implements CommandExecutor {
    private final DungeonRaidsPlugin plugin;
    
    public DungeonCommand(DungeonRaidsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§6=== Available Dungeons ===");
        
        // List dungeons
        sender.sendMessage("§f1. Forest Dungeon §7(Difficulty: 5)");
        sender.sendMessage("§f2. Cave Dungeon §7(Difficulty: 8)");
        
        return true;
    }
}
