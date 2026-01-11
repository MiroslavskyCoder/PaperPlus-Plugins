package com.webx.miningevents.commands;

import com.webx.miningevents.MiningEventsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MineEventCommand implements CommandExecutor {
    private final MiningEventsPlugin plugin;
    
    public MineEventCommand(MiningEventsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§6=== Mining Events ===");
        sender.sendMessage("§7Use /mineevent status for current event info");
        
        return true;
    }
}
