package com.webx.jobs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JobCommand implements CommandExecutor {
    private JobManager manager;
    
    public JobCommand(JobManager manager) {
        this.manager = manager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        
        if (args.length == 0) {
            player.sendMessage("§6Current Job: §f" + manager.getJob(player) + " §7(Level " + manager.getJobLevel(player) + ")");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("set")) {
            manager.setJob(player, args.length > 1 ? args[1] : "Miner");
        }
        
        return true;
    }
}
