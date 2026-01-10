package com.webx.dungeonraids.utils;

public class DifficultyUtils {
    
    public static String getDifficultyColor(int difficulty) {
        if (difficulty <= 3) return "§a";
        if (difficulty <= 6) return "§e";
        return "§c";
    }
    
    public static String getDifficultyName(int difficulty) {
        return switch(difficulty) {
            case 1, 2, 3 -> "Easy";
            case 4, 5, 6 -> "Normal";
            case 7, 8, 9 -> "Hard";
            default -> "Expert";
        };
    }
}
