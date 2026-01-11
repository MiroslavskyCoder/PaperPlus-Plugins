package com.webx.leveling.commands;

import com.webx.leveling.LevelingPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LevelCommand implements CommandExecutor {
    private final LevelingPlugin plugin;
    
    public LevelCommand(LevelingPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        Player player = (Player) sender;
        int level = plugin.getLevelingManager().getLevel(player.getUniqueId());
        player.sendMessage("§6Your Level: §f" + level);
        return true;
    }
}
