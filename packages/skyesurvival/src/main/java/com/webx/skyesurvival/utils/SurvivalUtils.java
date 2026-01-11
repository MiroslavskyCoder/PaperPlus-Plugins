package com.webx.skyesurvival.utils;

import java.util.UUID;

public class SurvivalUtils {
    
    public static String getResourceBar(int resources) {
        int bars = resources / 10;
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < bars; i++) {
            bar.append("§a█");
        }
        for (int i = bars; i < 10; i++) {
            bar.append("§8█");
        }
        return bar.toString();
    }
}
