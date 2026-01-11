package com.webx.warps.commands;

import com.webx.warps.WarpsPlugin;
import com.webx.warps.models.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class WarpCommand implements CommandExecutor {
    private final WarpsPlugin plugin;

    public WarpCommand(WarpsPlugin plugin) {
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

        if (args.length == 0) {
            plugin.getGUIManager().openWarpsGUI(player);
            return true;
        }

        String warpName = args[0];
        Warp warp = plugin.getWarpManager().getWarp(warpName);

        if (warp == null) {
            plugin.getMessageManager().send(player, "warp-not-found",
                    Map.of("name", warpName));
            return true;
        }

        if (!warp.canUse(player)) {
            plugin.getMessageManager().send(player, "no-permission");
            return true;
        }

        if (plugin.getCooldownManager().hasCooldown(player)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player);
            plugin.getMessageManager().send(player, "cooldown",
                    Map.of("time", String.valueOf(remaining)));
            return true;
        }

        if (plugin.getEconomyManager().isEnabled()) {
            double cost = warp.getCost() > 0 ? warp.getCost() : plugin.getEconomyManager().getCost();
            if (!plugin.getEconomyManager().charge(player, cost)) {
                plugin.getMessageManager().send(player, "insufficient-funds");
                return true;
            }
        }

        plugin.getTeleportManager().teleport(player, warp.getLocation(), warpName);
        plugin.getCooldownManager().setCooldown(player);
        warp.incrementUsage();

        return true;
    }
}
