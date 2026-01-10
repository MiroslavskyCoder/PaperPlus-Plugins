package com.webx.antispam;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class AntiSpamPlugin extends JavaPlugin implements Listener {
    private static AntiSpamPlugin instance;
    private Map<UUID, ChatData> playerChatData = new HashMap<>();
    
    private int maxMessagesPerSecond;
    private int messageWarnThreshold;
    private Pattern spamPattern;
    private int minWordLength;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        maxMessagesPerSecond = getConfig().getInt("max-messages-per-second", 3);
        messageWarnThreshold = getConfig().getInt("warn-threshold", 5);
        minWordLength = getConfig().getInt("min-word-length", 3);
        
        String spamRegex = getConfig().getString("spam-pattern", "[a-zA-Z]{5,}");
        spamPattern = Pattern.compile(spamRegex);
        
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("AntiSpam Plugin enabled!");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String message = event.getMessage();
        
        ChatData data = playerChatData.computeIfAbsent(uuid, k -> new ChatData());
        
        // Проверка на spam (повторяющиеся буквы)
        if (isSpamMessage(message)) {
            event.setCancelled(true);
            player.sendMessage("§cYour message was blocked (spam detected)");
            return;
        }
        
        // Проверка на rate limit
        long now = System.currentTimeMillis();
        if (now - data.lastMessageTime < 1000 && data.messagesInSecond >= maxMessagesPerSecond) {
            event.setCancelled(true);
            data.warnCount++;
            player.sendMessage("§cSpamming detected! Warn: " + data.warnCount + "/" + messageWarnThreshold);
            
            if (data.warnCount >= messageWarnThreshold) {
                player.kickPlayer("§cToo many spam violations!");
            }
            return;
        }
        
        // Обновляем счетчик сообщений
        if (now - data.lastMessageTime > 1000) {
            data.messagesInSecond = 1;
            data.lastMessageTime = now;
        } else {
            data.messagesInSecond++;
        }
    }
    
    private boolean isSpamMessage(String message) {
        // Проверка на повторяющиеся символы
        for (int i = 0; i < message.length() - 4; i++) {
            if (message.charAt(i) == message.charAt(i+1) && 
                message.charAt(i) == message.charAt(i+2) &&
                message.charAt(i) == message.charAt(i+3)) {
                return true; // 4+ одинаковых символа подряд = spam
            }
        }
        return false;
    }
    
    public static AntiSpamPlugin getInstance() {
        return instance;
    }
    
    private static class ChatData {
        long lastMessageTime = 0;
        int messagesInSecond = 0;
        int warnCount = 0;
    }
}
