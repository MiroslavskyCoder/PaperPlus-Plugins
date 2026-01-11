package com.webx.seasons.commands;

import com.webx.seasons.SeasonsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SeasonCommand implements CommandExecutor {
    private final SeasonsPlugin plugin;
    
    public SeasonCommand(SeasonsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        var currentSeason = plugin.getSeasonManager().getCurrentSeason();
        
        if (currentSeason != null) {
            sender.sendMessage("§6Current Season: §f" + currentSeason.getName());
            sender.sendMessage("§7" + currentSeason.getDescription());
        } else {
            sender.sendMessage("§cNo active season");
        }
        
        return true;
    }
}
