package com.webx.warps.utils;

import com.webx.warps.WarpsPlugin;
import org.bukkit.entity.Player;

public class PermissionManager {
    private final WarpsPlugin plugin;

    public PermissionManager(WarpsPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean hasPermission(Player player, String permission) {
        return player.hasPermission(permission);
    }

    public boolean canUseWarps(Player player) {
        return player.hasPermission("warps.use");
    }

    public boolean canAdminWarps(Player player) {
        return player.hasPermission("warps.admin");
    }

    public boolean canBypassCooldown(Player player) {
        return player.hasPermission("warps.bypass.cooldown");
    }

    public boolean canBypassCost(Player player) {
        return player.hasPermission("warps.bypass.cost");
    }
}
