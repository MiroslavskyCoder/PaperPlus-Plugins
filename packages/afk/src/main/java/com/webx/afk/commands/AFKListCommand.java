package com.webx.afk.commands;

import com.webx.afk.AFKPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AFKListCommand implements CommandExecutor {
    private final AFKPlugin plugin;
    
    public AFKListCommand(AFKPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int afkCount = plugin.getAFKManager().getAFKCount();
        sender.sendMessage("§a=== AFK Players: " + afkCount + " ===");
        
        plugin.getAFKManager().getAFKPlayers().forEach((uuid, afkPlayer) -> {
            if (afkPlayer.isAFK()) {
                Player player = plugin.getServer().getPlayer(uuid);
                if (player != null) {
                    long minutes = afkPlayer.getAFKDuration() / 60000;
                    sender.sendMessage("§f" + player.getName() + " §8- §7" + minutes + " minutes");
                }
            }
        });
        
        return true;
    }
}
