package com.webx.levels;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LevelsCommand implements CommandExecutor {
    private LevelsManager manager;
    
    public LevelsCommand(LevelsManager manager) {
        this.manager = manager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        
        int level = manager.getLevel(player);
        player.sendMessage("§6Your Level: §f" + level);
        
        return true;
    }
}
