package com.webx.speedrun.commands;

import com.webx.speedrun.SpeedRunPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedRunCommand implements CommandExecutor {
    private final SpeedRunPlugin plugin;
    
    public SpeedRunCommand(SpeedRunPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        Player player = (Player) sender;
        player.sendMessage("§aSpeedRun started! Good luck!");
        plugin.getSpeedRunManager().recordRunTime(player.getName());
        
        return true;
    }
}
