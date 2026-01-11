package com.webx.guilds.commands;

import com.webx.guilds.GuildsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuildCommand implements CommandExecutor {
    private final GuildsPlugin plugin;
    
    public GuildCommand(GuildsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        sender.sendMessage("§cUsage: /guild <create|join|leave|info>");
        return true;
    }
}
