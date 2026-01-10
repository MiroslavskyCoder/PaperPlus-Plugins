package com.webx.jumpquests.utils;

import org.bukkit.Location;

public class QuestUtils {
    
    public static double getDistance(Location loc1, Location loc2) {
        return loc1.distance(loc2);
    }
    
    public static boolean isNearby(Location loc1, Location loc2, double distance) {
        return loc1.distance(loc2) <= distance;
    }
}
