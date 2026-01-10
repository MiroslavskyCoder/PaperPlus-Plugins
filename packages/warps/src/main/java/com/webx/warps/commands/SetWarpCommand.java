package com.webx.warps.commands;

import com.webx.warps.WarpsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class SetWarpCommand implements CommandExecutor {
    private final WarpsPlugin plugin;

    public SetWarpCommand(WarpsPlugin plugin) {
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
            player.sendMessage("§cUsage: /setwarp <name>");
            return true;
        }

        String warpName = args[0];

        if (plugin.getWarpManager().exists(warpName)) {
            player.sendMessage("§cWarp already exists!");
            return true;
        }

        boolean created = plugin.getWarpManager().createWarp(
                warpName,
                player.getLocation(),
                player.getUniqueId()
        );

        if (created) {
            plugin.getMessageManager().send(player, "warp-created",
                    Map.of("name", warpName));
        } else {
            player.sendMessage("§cFailed to create warp!");
        }

        return true;
    }
}
