package com.webx.warps.commands;

import com.webx.warps.WarpsPlugin;
import com.webx.warps.models.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpsCommand implements CommandExecutor {
    private final WarpsPlugin plugin;

    public WarpsCommand(WarpsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if (!plugin.getPermissionManager().canUseWarps(player)) {
            plugin.getMessageManager().send(player, "no-permission");
            return true;
        }

        List<Warp> warps = plugin.getWarpManager().getWarpsForPlayer(player);

        if (warps.isEmpty()) {
            player.sendMessage("§cNo warps available!");
            return true;
        }

        player.sendMessage("§6§lAvailable Warps:");
        for (Warp warp : warps) {
            player.sendMessage("§e  - §6" + warp.getName());
        }

        return true;
    }
}
