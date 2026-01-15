package com.webx.tournaments.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TournamentJoinCommand implements CommandExecutor {
    // TODO: Player joins a tournament
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage("§aYou joined the tournament! (stub)");
        return true;
    }
}
