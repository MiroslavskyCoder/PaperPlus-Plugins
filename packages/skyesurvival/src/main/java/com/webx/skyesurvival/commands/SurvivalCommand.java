package com.webx.skyesurvival.commands;

import com.webx.skyesurvival.SkyeSurvivalPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SurvivalCommand implements CommandExecutor {
    private final SkyeSurvivalPlugin plugin;
    
    public SurvivalCommand(SkyeSurvivalPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        Player player = (Player) sender;
        int resources = plugin.getSurvivalManager().getResources(player.getUniqueId());
        player.sendMessage("§6Your Resources: §f" + resources);
        
        return true;
    }
}
