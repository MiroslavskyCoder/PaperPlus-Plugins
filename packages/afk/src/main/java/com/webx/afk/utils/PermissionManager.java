package com.webx.afk.utils;

import org.bukkit.entity.Player;

public class PermissionManager {
    
    public static boolean canCheckAFK(Player player) {
        return player.hasPermission("afk.check") || player.hasPermission("afk.admin");
    }
    
    public static boolean canSetAFK(Player player) {
        return player.hasPermission("afk.admin");
    }
    
    public static boolean canKickAFK(Player player) {
        return player.hasPermission("afk.kick");
    }
    
    public static boolean canReloadAFK(Player player) {
        return player.hasPermission("afk.reload");
    }
}
