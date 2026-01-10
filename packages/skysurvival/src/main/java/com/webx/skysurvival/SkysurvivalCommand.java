package com.webx.skysurvival;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkysurvivalCommand implements CommandExecutor {
    private SkysurvivalManager manager;
    
    public SkysurvivalCommand(SkysurvivalManager manager) {
        this.manager = manager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        
        if (args.length == 0) {
            var island = manager.getIsland(player);
            if (island != null) {
                player.sendMessage("§6Your Sky Island - Level " + island.level);
            } else {
                player.sendMessage("§cNo island found. Use /skysurvival create");
            }
            return true;
        }
        
        if (args[0].equalsIgnoreCase("create")) {
            manager.createIsland(player);
        }
        
        return true;
    }
}
