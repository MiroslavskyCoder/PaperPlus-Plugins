package com.webx.bedwarsevent.utils;

import com.webx.bedwarsevent.models.BedWarsGame;

public class BedWarsUtils {
    
    public static String getGameStatusColor(BedWarsGame game) {
        return game.isActive() ? "§a" : "§c";
    }
}
