package com.webx.chatformatting.managers;

import org.bukkit.entity.Player;

public class ChatFormattingManager {
    
    public String formatMessage(Player player, String message) {
        String prefix = player.hasPermission("chat.vip") ? "§e[VIP]§r " : "";
        return prefix + "§f" + player.getName() + ": §7" + message;
    }
    
    public String colorizeText(String text) {
        return text.replace("&", "§");
    }
}
