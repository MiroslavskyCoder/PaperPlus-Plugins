package com.webx.achievements;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AchievementCommand implements CommandExecutor {
    private AchievementsPlugin plugin;
    
    public AchievementCommand(AchievementsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is for players only!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sender.sendMessage("§6=== Your Achievements ===");
            var achievements = plugin.getAchievementManager().getPlayerAchievements(player.getUniqueId());
            achievements.forEach(id -> sender.sendMessage("§a✓ " + id));
            return true;
        }
        
        if (args[0].equalsIgnoreCase("stats")) {
            int total = plugin.getAchievementManager().getAllAchievements().size();
            int unlocked = plugin.getAchievementManager().getPlayerAchievements(player.getUniqueId()).size();
            sender.sendMessage("§6Progress: " + unlocked + "/" + total);
            return true;
        }
        
        return false;
    }
}
