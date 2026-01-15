package com.webx.tournaments.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TournamentBracketCommand implements CommandExecutor {
    // TODO: Show tournament bracket to player
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage("§bTournament bracket (stub)");
        return true;
    }
}
