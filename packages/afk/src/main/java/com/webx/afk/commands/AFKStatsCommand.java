package com.webx.afk.commands;

import com.webx.afk.AFKPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AFKStatsCommand implements CommandExecutor {
    private final AFKPlugin plugin;
    
    public AFKStatsCommand(AFKPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        
        Player player = (Player) sender;
        var afkPlayer = plugin.getAFKManager().getAFKPlayers().get(player.getUniqueId());
        
        if (afkPlayer == null) {
            player.sendMessage("§cNo AFK data found!");
            return true;
        }
        
        player.sendMessage("§a=== Your AFK Stats ===");
        player.sendMessage("§6Current Status: " + (afkPlayer.isAFK() ? "§cAFK" : "§aOnline"));
        player.sendMessage("§6Times AFK: §f" + afkPlayer.getAFKCounter());
        player.sendMessage("§6Current Duration: §f" + (afkPlayer.getAFKDuration() / 60000) + " minutes");
        
        return true;
    }
}
