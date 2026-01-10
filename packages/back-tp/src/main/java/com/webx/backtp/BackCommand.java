package com.webx.backtp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand implements CommandExecutor {
    private final BackTpPlugin plugin;
    private final BackManager backManager;
    private final TeleportManager teleportManager;

    public BackCommand(BackTpPlugin plugin, BackManager backManager, TeleportManager teleportManager) {
        this.plugin = plugin;
        this.backManager = backManager;
        this.teleportManager = teleportManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Только игроки могут использовать эту команду!");
            return true;
        }

        if (!plugin.getConfig().getBoolean("back.enabled", true)) {
            player.sendMessage(ChatColor.RED + "Команда /back отключена!");
            return true;
        }

        if (!backManager.hasDeathLocation(player)) {
            String msg = plugin.getConfig().getString("messages.back.no-death-location", 
                "&cУ вас нет последней точки смерти!");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            return true;
        }

        Location deathLoc = backManager.getDeathLocation(player);
        teleportManager.teleport(player, deathLoc, "back");
        
        return true;
    }
}
