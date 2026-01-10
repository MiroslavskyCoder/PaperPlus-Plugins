package com.webx.chatformatting.utils;

public class ChatColorUtils {
    
    public static String stripColor(String text) {
        return text.replaceAll("§[0-9a-fk-or]", "");
    }
    
    public static String getColorName(char code) {
        return switch(code) {
            case '0' -> "Black";
            case 'c' -> "Red";
            case 'a' -> "Green";
            case 'b' -> "Blue";
            case 'e' -> "Yellow";
            default -> "Unknown";
        };
    }
}
