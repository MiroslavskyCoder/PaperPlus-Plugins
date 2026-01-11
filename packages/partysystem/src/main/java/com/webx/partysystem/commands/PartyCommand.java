package com.webx.partysystem.commands;

import com.webx.partysystem.PartySystemPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyCommand implements CommandExecutor {
    private final PartySystemPlugin plugin;
    
    public PartyCommand(PartySystemPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /party <create|join|leave>");
            return true;
        }
        
        if (args[0].equals("create")) {
            plugin.getPartyManager().createParty((Player) sender);
            sender.sendMessage("§aParty created!");
        }
        
        return true;
    }
}
