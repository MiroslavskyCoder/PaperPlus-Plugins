package com.webx.achievements;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AchievementsCommand implements CommandExecutor {
    private AchievementsPlugin plugin;
    
    public AchievementsCommand(AchievementsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage("§6=== All Available Achievements ===");
        plugin.getAchievementManager().getAllAchievements().forEach(ach -> 
            sender.sendMessage("§b" + ach.getId() + " §7- §f" + ach.getDescription())
        );
        return true;
    }
}
