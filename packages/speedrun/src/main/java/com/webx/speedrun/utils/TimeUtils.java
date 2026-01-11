package com.webx.speedrun.utils;

public class TimeUtils {
    
    public static String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}
