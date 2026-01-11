package com.webx.antispam.commands;

import com.webx.antispam.AntiSpamPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ViolationsCommand implements CommandExecutor {
    private final AntiSpamPlugin plugin;
    
    public ViolationsCommand(AntiSpamPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /violations <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }
        
        int violations = plugin.getSpamManager().getViolationCount(target.getUniqueId());
        sender.sendMessage("§6" + target.getName() + "§f has §c" + violations + "§f spam violations");
        
        return true;
    }
}
