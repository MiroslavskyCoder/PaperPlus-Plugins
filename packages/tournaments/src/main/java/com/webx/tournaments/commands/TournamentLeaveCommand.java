package com.webx.tournaments.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TournamentLeaveCommand implements CommandExecutor {
    // TODO: Player leaves a tournament
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage("§eYou left the tournament! (stub)");
        return true;
    }
}
