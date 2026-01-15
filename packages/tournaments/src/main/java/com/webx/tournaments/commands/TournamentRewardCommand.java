package com.webx.tournaments.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TournamentRewardCommand implements CommandExecutor {
    // TODO: Show or claim tournament rewards
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage("§6Tournament rewards (stub)");
        return true;
    }
}
