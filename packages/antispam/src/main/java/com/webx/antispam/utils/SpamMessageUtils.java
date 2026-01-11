package com.webx.antispam.utils;

public class SpamMessageUtils {
    
    public static String censorSwear(String text) {
        // Simple censoring system
        text = text.replace("badword1", "****");
        text = text.replace("badword2", "****");
        return text;
    }
    
    public static boolean isCaps(String message) {
        int caps = 0;
        for (char c : message.toCharArray()) {
            if (Character.isUpperCase(c)) caps++;
        }
        return caps > message.length() * 0.7;
    }
}
