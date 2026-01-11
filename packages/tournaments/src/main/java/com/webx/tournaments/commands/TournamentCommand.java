package com.webx.tournaments.commands;

import com.webx.tournaments.TournamentsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TournamentCommand implements CommandExecutor {
    private final TournamentsPlugin plugin;
    
    public TournamentCommand(TournamentsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("§6=== Tournaments ===");
        sender.sendMessage("§7Use /tournament status to check active tournaments");
        
        return true;
    }
}
