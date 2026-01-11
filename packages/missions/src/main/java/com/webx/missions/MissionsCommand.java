package com.webx.missions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MissionsCommand implements CommandExecutor {
    private MissionsManager manager;
    
    public MissionsCommand(MissionsManager manager) {
        this.manager = manager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        
        sender.sendMessage("§6Active Missions:");
        manager.getActiveMissions(player).forEach(mission -> 
            sender.sendMessage("§b" + mission.name + "§7 - §f" + mission.description)
        );
        
        return true;
    }
}
