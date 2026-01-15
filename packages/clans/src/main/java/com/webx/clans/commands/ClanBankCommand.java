package com.webx.clans.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanBankCommand implements CommandExecutor {
    // TODO: Wire to ClanBankManager, handle /clan bank <deposit|withdraw|balance>
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        // TODO: Implement command logic
        return true;
    }
}
