package com.webx.statistics.commands;

import com.webx.statistics.StatisticsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {
    private final StatisticsPlugin plugin;
    
    public StatsCommand(StatisticsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        Player player = (Player) sender;
        var stats = plugin.getStatisticsManager().getStats(player.getUniqueId());
        
        player.sendMessage("§a=== Your Stats ===");
        player.sendMessage("§6Kills: §f" + stats.getKills());
        player.sendMessage("§6Deaths: §f" + stats.getDeaths());
        player.sendMessage("§6K/D Ratio: §f" + String.format("%.2f", stats.getKDRatio()));
        
        return true;
    }
}
