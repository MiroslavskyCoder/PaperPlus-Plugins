package com.webx.claims.utils;

import org.bukkit.Location;

public class ClaimsUtils {
    
    public static String getRegionName(Location loc) {
        return "Region_" + loc.getBlockX() + "_" + loc.getBlockZ();
    }
    
    public static boolean isInClaim(Location loc1, Location loc2, Location target) {
        return target.getBlockX() >= Math.min(loc1.getBlockX(), loc2.getBlockX()) &&
               target.getBlockX() <= Math.max(loc1.getBlockX(), loc2.getBlockX()) &&
               target.getBlockZ() >= Math.min(loc1.getBlockZ(), loc2.getBlockZ()) &&
               target.getBlockZ() <= Math.max(loc1.getBlockZ(), loc2.getBlockZ());
    }
}
