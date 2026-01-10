package com.webx.afk.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessageManager {
    
    public static String colorize(String text) {
        return text.replace("&", "§");
    }
    
    public static void broadcastAFK(Player player, boolean afk) {
        String message = afk 
            ? "§7[AFK] §f" + player.getName() + " §7is now AFK"
            : "§7" + player.getName() + " §ais back!";
        
        Bukkit.broadcastMessage(colorize(message));
    }
    
    public static void sendToPlayer(Player player, String message) {
        player.sendMessage(colorize(message));
    }
}
