package com.webx.bedwarsevent.commands;

import com.webx.bedwarsevent.BedWarsEventPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BedWarsCommand implements CommandExecutor {
    private final BedWarsEventPlugin plugin;
    
    public BedWarsCommand(BedWarsEventPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§6=== BedWars Event ===");
        sender.sendMessage("§f" + plugin.getBedWarsManager().getGames().size() + " games available");
        
        return true;
    }
}
