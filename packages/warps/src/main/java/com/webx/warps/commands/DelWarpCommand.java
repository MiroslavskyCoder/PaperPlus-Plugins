package com.webx.warps.commands;

import com.webx.warps.WarpsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class DelWarpCommand implements CommandExecutor {
    private final WarpsPlugin plugin;

    public DelWarpCommand(WarpsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if (!plugin.getPermissionManager().canAdminWarps(player)) {
            plugin.getMessageManager().send(player, "no-permission");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /delwarp <name>");
            return true;
        }

        String warpName = args[0];

        boolean deleted = plugin.getWarpManager().deleteWarp(warpName);

        if (deleted) {
            plugin.getMessageManager().send(player, "warp-deleted",
                    Map.of("name", warpName));
        } else {
            plugin.getMessageManager().send(player, "warp-not-found",
                    Map.of("name", warpName));
        }

        return true;
    }
}
