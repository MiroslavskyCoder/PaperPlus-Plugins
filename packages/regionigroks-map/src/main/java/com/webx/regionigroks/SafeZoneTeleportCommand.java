package com.webx.regionigroks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SafeZoneTeleportCommand implements CommandExecutor {
    private final RegionigroksMapPlugin plugin;

    public SafeZoneTeleportCommand(RegionigroksMapPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Эта команда доступна только игрокам");
            return true;
        }

        Player player = (Player) sender;

        // Find SafeZone region
        Optional<Region> safeZoneOpt = plugin.getRegionManager().getRegionByName("SafeZone");
        if (!safeZoneOpt.isPresent()) {
            player.sendMessage(ChatColor.RED + "Безопасная зона не найдена");
            return true;
        }

        Region safeZone = safeZoneOpt.get();
        
        // Get world
        World world = Bukkit.getWorld("world");
        if (world == null) {
            player.sendMessage(ChatColor.RED + "Мир не найден");
            return true;
        }

        // Create teleport location at center of SafeZone
        Location teleportLoc = new Location(
            world,
            safeZone.getCenterX() + 0.5,
            world.getHighestBlockYAt(safeZone.getCenterX(), safeZone.getCenterZ()) + 1,
            safeZone.getCenterZ() + 0.5
        );

        // Teleport player
        player.teleport(teleportLoc);
        player.sendMessage(ChatColor.GREEN + "✓ Вы телепортированы в безопасную зону");

        return true;
    }
}
