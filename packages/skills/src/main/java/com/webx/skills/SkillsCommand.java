package com.webx.skills;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillsCommand implements CommandExecutor {
    private SkillsManager manager;
    
    public SkillsCommand(SkillsManager manager) {
        this.manager = manager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        
        sender.sendMessage("§6=== Your Skills ===");
        manager.getSkills(player).forEach((skill, xp) -> 
            sender.sendMessage("§b" + skill + ": §f" + xp + " XP")
        );
        
        return true;
    }
}
