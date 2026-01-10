package com.webx.guilds.utils;

import java.util.UUID;

public class GuildUtils {
    
    public static String getGuildPrefix(String name) {
        return "[" + name.substring(0, Math.min(3, name.length())).toUpperCase() + "]";
    }
}
