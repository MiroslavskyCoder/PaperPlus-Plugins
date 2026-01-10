package com.webx.warps.commands;

import com.webx.warps.WarpsPlugin;
import com.webx.warps.models.Warp;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class WarpInfoCommand implements CommandExecutor {
    private final WarpsPlugin plugin;

    public WarpInfoCommand(WarpsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /warpinfo <name>");
            return true;
        }

        String warpName = args[0];
        Warp warp = plugin.getWarpManager().getWarp(warpName);

        if (warp == null) {
            plugin.getMessageManager().send(player, "warp-not-found",
                    Map.of("name", warpName));
            return true;
        }

        player.sendMessage("§6§lWarp Info: §e" + warp.getName());
        player.sendMessage("§7Creator: §f" + Bukkit.getOfflinePlayer(warp.getCreator()).getName());
        player.sendMessage("§7Location: §f" + formatLocation(warp));
        player.sendMessage("§7Status: " + (warp.isEnabled() ? "§aEnabled" : "§cDisabled"));
        if (warp.getCost() > 0) {
            player.sendMessage("§7Cost: §6" + warp.getCost());
        }
        player.sendMessage("§7Usage Count: §f" + warp.getUsageCount());

        return true;
    }

    private String formatLocation(Warp warp) {
        var loc = warp.getLocation();
        return loc.getWorld().getName() + " " +
                loc.getBlockX() + ", " +
                loc.getBlockY() + ", " +
                loc.getBlockZ();
    }
}
