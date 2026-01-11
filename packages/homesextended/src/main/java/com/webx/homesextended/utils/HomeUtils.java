package com.webx.homesextended.utils;

import org.bukkit.Location;

public class HomeUtils {
    
    public static String locationToString(Location loc) {
        return String.format("%d,%d,%d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
    
    public static String getWorldName(Location loc) {
        return loc.getWorld() != null ? loc.getWorld().getName() : "Unknown";
    }
}
