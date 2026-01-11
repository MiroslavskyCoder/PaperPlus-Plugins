package com.webx.feed.utils;

import org.bukkit.entity.Player;

public class FeedUtils {
    
    public static String getHungerBar(Player player) {
        int foodLevel = player.getFoodLevel();
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i < foodLevel / 2) {
                bar.append("§a🍗");
            } else {
                bar.append("§8🍗");
            }
        }
        return bar.toString();
    }
}
