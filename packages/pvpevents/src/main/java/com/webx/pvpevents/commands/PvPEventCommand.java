package com.webx.pvpevents.commands;

import com.webx.pvpevents.PvPEventsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PvPEventCommand implements CommandExecutor {
    private final PvPEventsPlugin plugin;
    
    public PvPEventCommand(PvPEventsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§6=== Active PvP Events ===");
        
        for (var event : plugin.getPvpEventManager().getAllEvents()) {
            String status = event.isActive() ? "§aActive" : "§cInactive";
            sender.sendMessage("§f" + event.getName() + " " + status);
        }
        
        return true;
    }
}
