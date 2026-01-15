package com.webx.tournaments.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TournamentAdminCommand implements CommandExecutor {
    // TODO: Admin commands: create, start, stop, remove tournaments
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage("§c[Admin] Tournament admin command (stub)");
        return true;
    }
}
